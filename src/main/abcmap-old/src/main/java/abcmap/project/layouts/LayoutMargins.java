package abcmap.project.layouts;

import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.LayoutMarginsProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.Utils;

/**
 * Marges de documents en millimetres
 * 
 * @author remipassmoilesel
 * 
 */
public class LayoutMargins implements AcceptPropertiesContainer {

	private int north;
	private int east;
	private int south;
	private int west;

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public LayoutMargins() {
		north = 0;
		east = 0;
		south = 0;
		west = 0;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public LayoutMargins(Integer north, Integer east, Integer south,
			Integer west) {
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public LayoutMargins(LayoutMargins margins) {
		north = margins.north;
		east = margins.east;
		south = margins.south;
		west = margins.west;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public int[] getMargins() {
		return new int[] { getNorth(), getEast(), getSouth(), getWest() };
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public int getNorth() {
		return north;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public void setNorth(Integer north) {
		this.north = north;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public int getEast() {
		return east;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public void setEast(Integer east) {
		this.east = east;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public int getSouth() {
		return south;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public void setSouth(Integer south) {
		this.south = south;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public int getWest() {
		return west;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public void setWest(Integer west) {
		this.west = west;
	}

	/**
	 * Marges en mm
	 * 
	 * @return
	 */
	public void setMargins(Integer[] integers) {

		if (integers.length != 4)
			throw new IllegalArgumentException("Invalid values: "
					+ integers.length);

		north = integers[0];
		east = integers[1];
		south = integers[2];
		west = integers[3];
	}

	@Override
	public boolean equals(Object arg0) {

		if ((arg0 instanceof LayoutMargins) == false)
			return false;

		LayoutMargins mg = (LayoutMargins) arg0;

		int i = 0;
		if (this.getNorth() == mg.getNorth()) {
			i++;
		}
		if (this.getEast() == mg.getEast()) {
			i++;
		}
		if (this.getSouth() == mg.getSouth()) {
			i++;
		}
		if (this.getWest() == mg.getWest()) {
			i++;
		}

		return (i == 4);
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { north, east, south, west };
		Object[] keys = new Object[] { "north", "east", "south", "west" };

		return Utils.toString(this, keys, values);

	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		if (properties == null)
			throw new NullPointerException();

		LayoutMarginsProperties pp = (LayoutMarginsProperties) properties;

		this.north = pp.north;
		this.east = pp.east;
		this.south = pp.south;
		this.west = pp.west;
	}

	@Override
	public PropertiesContainer getProperties() {
		LayoutMarginsProperties properties = new LayoutMarginsProperties();
		properties.north = north;
		properties.east = east;
		properties.south = south;
		properties.west = west;
		return properties;
	}

}
