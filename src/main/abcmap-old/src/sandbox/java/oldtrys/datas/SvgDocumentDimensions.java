package oldtrys.datas;

import java.io.File;
import java.io.IOException;

import abcmap.importation.documents.SvgRenderer;

public class SvgDocumentDimensions {

	/**
	 * Tester la mesure de dimensions de documents SVG
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SvgRenderer render = new SvgRenderer();

		File dir = new File("./examples/svg/");
		File[] files = dir.listFiles();

		for (File f : files) {
			System.out.println();
			System.out.println(f.getAbsolutePath());

			try {
				System.out.println(render.getDocumentDimensions(f)[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
