package oldtrys.datas;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class ApacheCSVTest {

	public static void main(String[] args) {

		try {
			Reader in = new FileReader("examples/exemple_liste.csv");
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {

				System.out.println("record.size()");
				System.out.println(record.size());

				System.out.println("record.get(10)");
				System.out.println(record.get(10));

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
