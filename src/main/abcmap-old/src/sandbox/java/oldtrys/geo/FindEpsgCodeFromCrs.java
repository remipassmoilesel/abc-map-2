package oldtrys.geo;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.managers.stub.Managers;
import abcmap.utils.PrintUtils;

public class FindEpsgCodeFromCrs {

	public static void main(String[] args) {

		// Retrouver le code EPSG d'un CRS

		CoordinateReferenceSystem crs = Managers.getMapManager().getCRS("3302");

		System.out.println(crs.getName());
		System.out.println(crs.getCoordinateSystem());
		System.out.println(crs.toString());
		PrintUtils.p(crs.getIdentifiers());

		try {
			System.out.println(CRS.lookupIdentifier(crs, true));
		} catch (FactoryException e) {
			e.printStackTrace();
		}
	}

}
