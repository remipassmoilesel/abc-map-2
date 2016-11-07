package abcmap.importation.data.reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import abcmap.configuration.ConfigurationConstants;
import abcmap.exceptions.DataImportException;
import abcmap.geo.Coordinate;
import abcmap.importation.data.DataEntry;
import abcmap.importation.data.DataEntryList;

public class CsvDataParser extends AbstractDataParser {

	private static final String[] supportedExtensions = new String[] { "csv" };

	@Override
	public String[] getSupportedExtensions() {
		return supportedExtensions;
	}

	@Override
	public boolean isSupportedExtension(String extension) {
		return Arrays.asList(supportedExtensions).contains(extension);
	}

	/**
	 * Retourne les lignes du fichier CSV. <br>
	 * Si 'lines' > 0, retourne le nombre de ligne. <br>
	 * Si 'lines' =< 0, retourne toutes les lignes.
	 * 
	 * @param file
	 * @param lines
	 * @return
	 * @throws DataImportException
	 * @throws IOException
	 */
	private List<CSVRecord> getLines(File file, int lines) throws DataImportException, IOException {

		Reader in = new FileReader(file);
		CSVParser parser = null;
		try {

			parser = ConfigurationConstants.DEFAULT_CSV_FORMAT.parse(in);

			// Verification de la taille du fichier. La méthode
			// "parser.getRecordNumber()" renvoi zéro si
			// getRecords n'a pas été appelé. Donc chargement complet du
			// fichier.
			List<CSVRecord> originalRecords = parser.getRecords();

			// fermer la ressource
			in.close();

			// verifier que le fichier ne soit pas trop volumineux
			if (originalRecords.size() > MAX_DATA_PARSING) {
				throw new DataImportException(DataImportException.DATAS_TOO_HEAVY);
			}

			// verifier que le fichier soit suffisament volumineux
			if (originalRecords.size() < HEADERS_LABELS_INDEX + 1) {
				throw new DataImportException(DataImportException.DATAS_TOO_LIGHT);
			}

			// recuperer tous les enregistrements
			if (lines <= 0) {
				return originalRecords;
			}

			// recuperer seulement les enregistrements demandés
			else {

				ArrayList<CSVRecord> records = new ArrayList<>();
				Iterator<CSVRecord> iterator = originalRecords.iterator();
				for (int i = 0; i < lines; i++) {
					records.add(iterator.next());
				}

				// fermer la ressource
				in.close();

				return originalRecords;

			}

		} finally {
			if (parser != null) {
				parser.close();
			}
			in.close();
		}

	}

	@Override
	public ArrayList<String> getHeaders(File file) throws IOException, DataImportException {

		// recuperer la ligne d'entetes
		CSVRecord headersLine = getLines(file, HEADERS_LABELS_INDEX).get(HEADERS_LABELS_INDEX);

		ArrayList<String> headers = new ArrayList<String>();

		int i = 1;
		for (Iterator<String> iterator = headersLine.iterator(); iterator.hasNext();) {

			String header = iterator.next().trim();
			headers.add(header.isEmpty() ? HEADER_DEFAULT_PREFIX + i : header);

			i++;
		}

		return headers;

	}

	@Override
	public DataEntryList parseFile(File file) throws IOException, DataImportException {

		// recuperer les entetes
		ArrayList<String> headers = getHeaders(file);

		// recuperer toutes les entrées
		List<CSVRecord> lines = getLines(file, -1);

		// la liste à retourner
		DataEntryList dataList = new DataEntryList();

		// iterer les lignes
		for (int i = HEADERS_LABELS_INDEX; i < lines.size(); i++) {

			// la ligne courante
			CSVRecord cl = lines.get(i);

			// la ligne est trop petite, arret
			if (cl.size() < MINIMUM_LINE_SIZE) {
				continue;
			}

			// parser les coordonnées
			Coordinate coords = Coordinate.valueOf(cl.get(HEADER_INDEX_LATITUDE),
					cl.get(HEADER_INDEX_LONGITUDE));

			// les coordonnées sont nulles, ignorer la ligne
			if (coords == null) {
				continue;
			}

			// l'objet à ajouter
			DataEntry data = new DataEntry();
			data.setCoords(coords);

			for (int j = HEADER_FIRST_FIELD_INDEX; j < cl.size(); j++) {

				// obtenir le nom du champs ou laisser l'entrée générer un nom
				String name = j < headers.size() ? headers.get(j) : "";

				data.addField(name, cl.get(j));

			}

			dataList.add(data);

		}

		return dataList;
	}

}
