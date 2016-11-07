package oldtrys.datas;

import java.io.File;
import java.io.IOException;

import abcmap.exceptions.DataImportException;
import abcmap.importation.data.DataEntryList;
import abcmap.importation.data.reader.GpxDataParser;
import abcmap.importation.data.writer.CsvDataWriter;

public class GpxToDatalist {

	public static void main(String[] args) {

		GpxDataParser gpx = new GpxDataParser();

		CsvDataWriter writer = new CsvDataWriter();

		File[] files = new File[] { new File("examples/gpx/export.gpx"),
				new File("examples/gpx/export_2.gpx"), };

		for (int i = 0; i < files.length; i++) {
			try {
				DataEntryList list = gpx.parseFile(files[i]);
				writer.write(list, new File("csv_" + i + ".csv"));
			} catch (IOException | DataImportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
