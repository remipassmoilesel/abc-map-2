package oldtrys.datas;

import java.io.File;
import java.io.IOException;

import abcmap.exceptions.DataImportException;
import abcmap.importation.data.DataEntry;
import abcmap.importation.data.DataEntryList;
import abcmap.importation.data.reader.CsvDataParser;

public class CSVDataListParserTest {

	public static void main(String[] args) {

		CsvDataParser parser = new CsvDataParser();
		try {

			File file = new File("examples/exemple_liste.csv");

			System.out.println("Headers");
			for (String s : parser.getHeaders(file)) {
				System.out.println(s);
			}

			DataEntryList list = parser.parseFile(file);

			for (DataEntry de : list) {
				System.out.println();
				System.out.println(de);
				for (String k : de.getFieldNames()) {
					System.out.println(k);
					System.out.println(de.getField(k));
				}
			}

		} catch (IOException | DataImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
