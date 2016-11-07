package abcmap.draw.links;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.comps.geo.MapPanel;
import abcmap.gui.comps.links.LinkMouseLabel;
import abcmap.managers.GuiManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;

public class MapLinkMonitor extends MouseAdapter {

	private ProjectManager projectm;
	private MapManager mapm;

	/**
	 * L'analyse des mouvements ne se fait pas à chaque déplacement. Uniquement
	 * lorsque le pointer est incrémenté jusqu'à cette valeur.
	 */
	private int analyseThreshold;
	private int analysePointer;
	private MapPanel map;
	private LinkMouseLabel linkLabel;
	private int gapBetweenMouseAndLabel;
	private boolean notYetEnteredOnLink;
	private LayerElement lastElmt;
	private GuiManager guim;

	public MapLinkMonitor() {

		projectm = MainManager.getProjectManager();
		mapm = MainManager.getMapManager();
		guim = MainManager.getGuiManager();
		map = mapm.getMapComponent();

		analyseThreshold = 3;
		analysePointer = 0;

		linkLabel = new LinkMouseLabel();
		gapBetweenMouseAndLabel = 15;

		notYetEnteredOnLink = false;
	}

	/**
	 * Lors d'un mouvement de la souris, recherche les liens et modifie
	 * l'affichage.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		// analyse effectuee uniquement tous les threshold
		if (analysePointer < analyseThreshold) {
			analysePointer++;
			return;
		}
		analysePointer = 0;

		// rechercher le lien sous la souris
		LayerElement elmt = getLinkedElementUnderPoint(e.getPoint());

		// pas de lien: raz puis retour
		if (elmt == null) {

			// retablir le precedent curseur si necessaire
			if (Utils.safeEquals(guim.getDrawingCursor(), map.getCursor()) == false) {
				map.setCursor(guim.getDrawingCursor());
			}

			// masquer l'affichage si nécéssaire
			if (linkLabel.isVisible() == true) {
				showLinkLabel(false, null, null);
			}

			// ne plus peindre la marque de lien
			if (lastElmt != null)
				lastElmt.drawLinkMark(false);

			// reinitialiser le flag de curseur
			notYetEnteredOnLink = true;

			// sortie
			return;
		}

		// Un lien est présent: recuperer le lien
		LinkRessource link = elmt.getLinkRessources();

		// verifier le precedent element
		if (elmt != lastElmt) {
			if (lastElmt != null)
				lastElmt.drawLinkMark(false);
			lastElmt = elmt;
		}

		// premier mouvement sur le lien
		if (notYetEnteredOnLink) {

			// modifier le curseur
			map.setCursor(guim.getClickableCursor());

			// peindre la marque de lien
			elmt.drawLinkMark(true);

			notYetEnteredOnLink = false;

		}

		// afficher l'etiquette de renseignement a proximité de la souris
		showLinkLabel(true, link, e.getPoint());

		// rafraichir la carte
		map.refresh();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// ne fonctionne que si le clic droit est utilisé
		if (SwingUtilities.isRightMouseButton(e) == false)
			return;

		// rechercher le lien sous la souris
		LayerElement elmt = getLinkedElementUnderPoint(e.getPoint());

		// pas de lien, retour
		if (elmt == null) {
			return;
		}

		// activer le lien
		ThreadManager.runLater(elmt.getLinkRessources());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// cacher l'etiquette lors d'une interaction
		if (linkLabel.isVisible() == true) {
			showLinkLabel(false, null, null);
		}
	}

	private void showLinkLabel(boolean value, LinkRessource link, Point p) {

		GuiUtils.throwIfNotOnEDT();

		// masquage du composant puis quitter
		if (value == false) {

			linkLabel.setVisible(false);
			map.remove(linkLabel);
			map.refresh();

			return;
		}

		// affichage du composant

		// adapter le composant
		linkLabel.setLinkRessource(link);
		linkLabel.reconstruct();

		// ajouter
		map.add(linkLabel);

		// fixer les dimensions
		Dimension dims = linkLabel.getPreferredSize();
		linkLabel.setBounds(p.x + gapBetweenMouseAndLabel * 2, p.y + gapBetweenMouseAndLabel, dims.width, dims.height);

		linkLabel.setVisible(true);
	}

	/**
	 * Retourne le premier lien correspondant à la position passée en argument.
	 * 
	 * @param e
	 * @return
	 */
	public LayerElement getLinkedElementUnderPoint(Point mousePosition) {

		if (projectm.isInitialized() == false)
			return null;

		// récuperer le calque actif
		MapLayer lay;
		try {
			lay = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			// Log.error(e1);
			return null;
		}

		// parcourir le projet à la recherche de liens
		for (LayerElement elmt : lay.getDrawShapesReversed()) {

			// prendre en compte uniquement les objets possédant un lien
			if (elmt.getLinkRessources() == null)
				continue;

			// position de la souris à l'echelle
			Point p = mapm.getScaledPoint(mousePosition);

			// verifier si l'element est sous la souris
			if (elmt.getInteractionArea().contains(p)) {
				return elmt;
			}
		}

		return null;
	}

}
