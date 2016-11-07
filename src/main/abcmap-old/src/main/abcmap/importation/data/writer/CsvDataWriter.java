package abcmap.importation.data.writer;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.csv.CSVPrinter;

import abcmap.configuration.ConfigurationConstants;
import abcmap.importation.data.DataEntry;
import abcmap.importation.data.DataEntryList;
import abcmap.importation.data.reader.AbstractDataParser;

public class CsvDataWriter extends AbstractDataWriter {

	private static final String[] supportedExtensions = new String[] { "csv" };

	@Override
	public void write(DataEntryList list, File destination) throws IOException {

		// créer le nouveau fichier
		destination.createNewFile();

		// initialize FileWriter object
		FileWriter fileWriter = new FileWriter(destination);
		CSVPrinter csvFilePrinter = null;
		try {
			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, ConfigurationConstants.DEFAULT_CSV_FORMAT);

			// recuperer les entetes de la liste
			ArrayList<String> headers = new ArrayList<>(list.getAllHeaders());
			headers.add(0, AbstractDataParser.LABEL_LATITUDE);
			headers.add(1, AbstractDataParser.LABEL_LONGITUDE);

			// creer le fichier CSV
			csvFilePrinter.printRecord(list.getComments());
			csvFilePrinter.printRecord(headers);

			// parcourir la liste
			for (DataEntry entry : list) {

				// ajout de la position
				Point2D degrees = entry.getCoords().getDegreesPoint();

				// latitude
				csvFilePrinter.print(degrees.getY());

				// longitude
				csvFilePrinter.print(degrees.getX());

				// parcourir les champs
				for (int i = 2; i < headers.size(); i++) {
					String field = entry.getField(headers.get(i));
					csvFilePrinter.print(field != null ? field : "");
				}

				// saut de ligne
				csvFilePrinter.println();

			}

		} finally {
			if (csvFilePrinter != null) {
				csvFilePrinter.close();
			}
			fileWriter.close();
		}

	}

	/**
	 * Sauvegarde un exemple de liste dans le fichier 'destination'
	 * 
	 * @param destination
	 * @throws IOException
	 */
	public static void saveExampleAt(File destination) throws IOException {

		DataEntryList list = new DataEntryList();
		list.addComment(
				"Titre et commentaires. Les coordonnées être représentées de plusieurs manières: "
						+ "0.00, 0° 0.00', 0° 0' 0.00\".");

		int entrys = 5;

		for (int i = 0; i < entrys; i++) {

			DataEntry de = new DataEntry();
			de.setCoords(0.0, 0.0);
			de.addField("categoty", "square");
			de.addField("color", "red");
			de.addField("...", "...");

			list.add(de);

		}

		for (int i = 0; i < entrys; i++) {

			DataEntry de = new DataEntry();
			de.setCoords(0.0, 0.0);
			de.addField("categoty", "ellipse");
			de.addField("color", "#123456");
			de.addField("...", "...");

			list.add(de);

		}

		CsvDataWriter writer = new CsvDataWriter();
		writer.write(list, destination);

	}

	@Override
	public String[] getSupportedExtensions() {
		return supportedExtensions;
	}

	@Override
	public boolean isSupportedExtension(String str) {
		return Arrays.asList(supportedExtensions).contains(str);
	}

}
