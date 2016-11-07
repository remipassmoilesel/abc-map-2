package oldtrys.datas;

import java.io.File;
import java.io.IOException;

import abcmap.utils.Utils;

public class ImageDimensions {

	public static void main(String[] args) {
		try {
			Utils.getImageDimensions(new File("D:/MES DOCS/layout.pdf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
