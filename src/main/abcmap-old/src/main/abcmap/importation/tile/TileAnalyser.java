package abcmap.importation.tile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abcmap.configuration.Configuration;
import abcmap.draw.shapes.Tile;
import abcmap.exceptions.TileAnalyseException;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.surf.InterestPoint;
import abcmap.surf.Matcher;
import abcmap.utils.Utils;

/**
 * Regroupe les méthodes d'analyse et de positionnement des tuiles
 * 
 * @author remipassmoilesel
 *
 */

public class TileAnalyser {
	
	private ProjectManager projectm;
	private boolean debugMode;

	public TileAnalyser() {
		projectm = MainManager.getProjectManager();
		debugMode = false;
	}

	/**
	 * Analyse une tuile et lui donne eventuellement une position
	 * 
	 * @param t
	 * @return
	 * @throws TileAnalyseException
	 */
	public void analyseTileAndSetPosition(Tile t) throws TileAnalyseException {

		// analyse de la tuile
		Point position = searchTilePosition(t);

		// analyse réussie
		if (position != null) {
			t.setPosition(position);
		}

	}

	/**
	 * Analyse la tuile et retourne une position idéale par rapport aux autres
	 * tuiles du projet. Si la tuile est la toute première, Point(0,0) est
	 * retourné.
	 * 
	 * @param tileA
	 * @return
	 * @throws TileAnalyseException
	 */
	public Point searchTilePosition(Tile tileA) throws TileAnalyseException {

		if (projectm.isInitialized() == false)
			return null;

		// Lister les tuiles. Peut changer d'une analyse à l'autre.
		ArrayList<Tile> tiles = projectm.getAllTiles();

		// Premiere tuile: retour de la position par defaut
		if (tiles.size() == 0) {
			return new Point(0, 0);
		}

		// Recuperer la configuration. Peut changer d'une analyse à l'autre
		Configuration config = MainManager.getConfigurationManager().getConfiguration();

		// Rechercher une tuile correspondante
		Map<InterestPoint, InterestPoint> commonPoints = new HashMap<InterestPoint, InterestPoint>(
				400);

		// tuileB correspondante à tuileA
		Tile correspondingTile = null;

		// points communs de reference
		InterestPoint ptTileA = null;
		InterestPoint ptTileB = null;

		/**
		 * Iterer les tuiles l'envers, à la recherche d'une tuile avec
		 * suffisament de points communs. Commencer par la dernière donne plus
		 * de chances de tomber rapidement sur une tuile correspondante
		 */
		searchTile: for (int i = tiles.size() - 1; i >= 0; i--) {

			Tile tileB = tiles.get(i);

			// extraire les points communs
			commonPoints.clear();
			commonPoints.putAll(getCommonPoints(tileA, tileB));

			// ne continuer que si suffisament de points communs
			if (commonPoints.size() < config.MATCHING_POINTS_THRESHOLD) {
				continue;
			}

			/**
			 * Iterer les points d'interet, à la recherche de points communs
			 */
			for (InterestPoint ip1 : commonPoints.keySet()) {

				// prendre une paire de point d'interets
				InterestPoint ip2 = commonPoints.get(ip1);

				int matching = 0;

				// calculer l'ecart aux deux points de reference
				for (InterestPoint ipTest1 : commonPoints.keySet()) {

					InterestPoint ipTest2 = commonPoints.get(ipTest1);

					// mesurer les distances entre les points
					double dX1 = Math.abs(ipTest1.x - ip1.x);
					double dY1 = Math.abs(ipTest1.y - ip1.y);
					double dX2 = Math.abs(ipTest2.x - ip2.x);
					double dY2 = Math.abs(ipTest2.y - ip2.y);

					// si la distance totale < 0.5, prise en compte comme point
					// de correspondance
					double ttDist = Math.abs((dX1 + dY1) - (dX2 + dY2));
					if (ttDist < 0.5) {

						matching++;

						if (matching >= config.MATCHING_POINTS_THRESHOLD) {
							ptTileA = ip1;
							ptTileB = ip2;
							correspondingTile = tileB;
							break searchTile;
						}
					}
				}

			}

		}

		// aucune correspondance
		if (correspondingTile == null) {
			throw new TileAnalyseException(
					"No corresponding tiles, thresold:  " + config.MATCHING_POINTS_THRESHOLD);
		}

		// calcul de la position optimale sur la carte
		int x = (int) (ptTileB.getX() - ptTileA.getX() + correspondingTile.getPosition().x);
		int y = (int) (ptTileB.getY() - ptTileA.getY() + correspondingTile.getPosition().y);

		return new Point(x, y);
	}

	/**
	 * Recherche les points communs entre les tuiles. <br>
	 * "Allege" la deuxieme tuile en serialisant ses points communs
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws TileAnalyseException
	 * @throws TileAnalyseMemoryException
	 */
	private Map<InterestPoint, InterestPoint> getCommonPoints(Tile t1, Tile t2)
			throws TileAnalyseException {

		Integer surfMode = MainManager.getConfigurationManager().getConfiguration().SURF_MODE;

		List<InterestPoint> ipts1;
		List<InterestPoint> ipts2;

		if (Utils.safeEquals(t1.getSurfMode(), surfMode) == false) {
			ipts1 = t1.surfAnalyse();
		}

		else {
			ipts1 = t1.getIptsList();
		}

		if (Utils.safeEquals(t2.getSurfMode(), surfMode) == false) {
			ipts2 = t2.surfAnalyse();
		}

		else {
			ipts2 = t2.getIptsList();
		}

		if (ipts1 == null || ipts1.size() <= 0) {
			throw new TileAnalyseException("Errors with ipts list 1");
		}
		if (ipts2 == null || ipts2.size() <= 0) {
			throw new TileAnalyseException("Errors with ipts list 2");
		}

		// chercher les points communs entre les deux images
		Map<InterestPoint, InterestPoint> mp = Matcher.findMatches(ipts1, ipts2);
		Map<InterestPoint, InterestPoint> mp2 = Matcher.findMatches(ipts2, ipts1);

		// ne garder que les points communs aux deux analyses
		Map<InterestPoint, InterestPoint> matchedPoints = new HashMap<InterestPoint, InterestPoint>(
				100);
		for (InterestPoint ipt1 : mp.keySet()) {
			InterestPoint ipt2 = mp.get(ipt1);
			if (ipt1 == mp2.get(ipt2))
				matchedPoints.put(ipt1, ipt2);
		}

		return matchedPoints;

	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

}
