package abcmap.gui.comps.geo;

import abcmap.geo.GeoInfoMode;

public interface HasGeoInformations {

	/**
	 * Retourne un conteneur représentant les types d'informations affichées.
	 * 
	 * @return
	 */
	public GeoInfoMode getGeoInfoMode();

	/**
	 * Afficher des informations géographiques.
	 * 
	 * @param infos
	 */
	public void setGeoInfoMode(GeoInfoMode infos);

	/**
	 * Retourne la taille du texte utilisée pour afficher des informations
	 * géographiques.
	 * 
	 * @return
	 */
	public int getGeoTextSize();

	/**
	 * Affecter la taille du texte utilisée pour afficher des informations
	 * géographiques.
	 * 
	 * @param size
	 */
	public void setGeoTextSize(int size);

}
