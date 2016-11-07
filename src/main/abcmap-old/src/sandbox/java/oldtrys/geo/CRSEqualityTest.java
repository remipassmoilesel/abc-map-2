package oldtrys.geo;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.managers.stub.Managers;

public class CRSEqualityTest {
	public static void main(String[] args) {
		// tests d'égalité entre crs. L'objet CRS ne surcharge pas la méthode
		// equals.

		System.out.println(Managers.getMapManager().getCRS("3308").toString()
				.equals(Managers.getMapManager().getCRS("3308").toString()));

		// creer le systeme
		try {
			CoordinateReferenceSystem system = CRS.decode("EPSG:3308");

			// les CRS sont mis en cache et peuvent être garbage collected
			//
			for (int i = 0; i < 15; i++) {
				System.gc();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			CoordinateReferenceSystem system2 = CRS.decode("EPSG:3308");

			System.out.println(system.equals(system2));
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
