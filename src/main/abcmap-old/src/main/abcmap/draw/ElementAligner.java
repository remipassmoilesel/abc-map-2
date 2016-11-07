package abcmap.draw;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.managers.DrawManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.lists.CoeffComparator;
import abcmap.utils.lists.CoeffComparator.ComparableObject;
import abcmap.utils.lists.CoeffComparator.Order;

public class ElementAligner implements Runnable {

	private ProjectManager projectm;
	private DrawManager drawm;

	private AlignConstants action;
	private ArrayList<LayerElement> elements;

	public ElementAligner(AlignConstants action) {

		this.projectm = MainManager.getProjectManager();
		this.drawm = MainManager.getDrawManager();

		this.action = action;

		// liste des elements à modifier
		this.elements = null;

	}

	public void setElements(ArrayList<LayerElement> elements) {
		this.elements = elements;
	}

	@Override
	public void run() {

		GuiUtils.throwIfOnEDT();

		// projet non initialisé, arret
		if (projectm.isInitialized() == false)
			return;

		// rechercher les elements electionnées au besoin
		if (elements == null) {
			elements = drawm.getSelectedElements();
		}

		// distribuer les objets horizontalement
		if (AlignConstants.DISTRIBUTE_HORIZONTAL.equals(action)) {
			distributeObjectsHorizontally();
		}

		// ou distribuer les objets verticalement
		else if (AlignConstants.DISTRIBUTE_VERTICAL.equals(action)) {
			distributeObjectsVertically();
		}

		// ou aligner les objets
		else {
			alignObjects();
		}

	}

	private void distributeObjectsHorizontally() {

		// obtenir l'ecart ideal
		int gap = getEqualHorizontalSpace(elements);

		// classer les objets en fonctions de leur position X
		ArrayList<ComparableObject> sortedList = CoeffComparator.getNewList();
		for (LayerElement elmt : elements) {
			sortedList.add(CoeffComparator.getComparableFor(elmt, elmt.getPosition().x));
		}

		CoeffComparator.sort(sortedList, Order.ASCENDING);

		// puis les repartir horizontalement dans l'ordre
		int curx = 0;
		boolean first = true;
		for (ComparableObject obj : sortedList) {

			// l'element
			LayerElement elmt = (LayerElement) obj.getObject();
			Rectangle bounds = elmt.getMaximumBounds();

			// tout premier element
			if (first) {
				curx = bounds.x + bounds.width + gap;
				first = false;
				continue;
			}

			// dernier element: sortie de la boucle
			if (sortedList.indexOf(obj) >= sortedList.size() - 1) {
				break;
			}

			// positionnement des objets
			elmt.setPosition(new Point(curx, bounds.y));
			elmt.refreshShape();

			curx += bounds.width + gap;

		}

	}

	private void distributeObjectsVertically() {

		// obtenir l'ecart ideal
		int gap = getEqualVerticalSpace(elements);

		// classer les objets en fonctions de leur position X
		ArrayList<ComparableObject> sortedList = CoeffComparator.getNewList();
		for (LayerElement elmt : elements) {
			sortedList.add(CoeffComparator.getComparableFor(elmt, elmt.getPosition().x));
		}

		CoeffComparator.sort(sortedList, Order.ASCENDING);

		// puis les repartir horizontalement dans l'ordre
		int cury = 0;
		boolean first = true;
		for (ComparableObject obj : sortedList) {

			// l'element
			LayerElement elmt = (LayerElement) obj.getObject();
			Rectangle bounds = elmt.getMaximumBounds();

			// tout premier element
			if (first) {
				cury = bounds.y + bounds.height + gap;
				first = false;
				continue;
			}

			// dernier element: sortie de la boucle
			if (sortedList.indexOf(obj) >= sortedList.size() - 1) {
				break;
			}

			// positionnement des objets
			elmt.setPosition(new Point(bounds.x, cury));
			elmt.refreshShape();

			cury += bounds.height + gap;

		}

	}

	private void alignObjects() {

		// calculer la reference d'alignement
		Integer ref = null;
		if (AlignConstants.ALIGN_TOP.equals(action)) {
			ref = getSmallestY(elements);
		}

		else if (AlignConstants.ALIGN_RIGHT.equals(action)) {
			ref = getGreatestX(elements);
		}

		else if (AlignConstants.ALIGN_BOTTOM.equals(action)) {
			ref = getGreatestY(elements);
		}

		else if (AlignConstants.ALIGN_LEFT.equals(action)) {
			ref = getSmallestX(elements);
		}

		else if (AlignConstants.ALIGN_MIDDLE_HORIZONTAL.equals(action)) {
			ref = getMiddleY(elements);
		}

		else if (AlignConstants.ALIGN_MIDDLE_VERTICAL.equals(action)) {
			ref = getMiddleX(elements);
		}

		// iterer les elements
		for (LayerElement elmt : elements) {

			// la position de l'element
			Rectangle bounds = elmt.getMaximumBounds();

			// aligner en haut
			if (AlignConstants.ALIGN_TOP.equals(action)) {
				elmt.setPosition(new Point(bounds.x, ref));
			}

			// aligner en bas
			else if (AlignConstants.ALIGN_BOTTOM.equals(action)) {
				elmt.setPosition(new Point(bounds.x, ref - bounds.height));
			}

			// aligner à gauche
			else if (AlignConstants.ALIGN_LEFT.equals(action)) {
				elmt.setPosition(new Point(ref, bounds.y));
			}

			// aligner à droite
			else if (AlignConstants.ALIGN_RIGHT.equals(action)) {
				elmt.setPosition(new Point(ref - bounds.width, bounds.y));
			}

			// aligner au milieu horizontal
			else if (AlignConstants.ALIGN_MIDDLE_HORIZONTAL.equals(action)) {
				elmt.setPosition(new Point(bounds.x, ref - bounds.height / 2));
			}

			// aligner au milieu vertical
			else if (AlignConstants.ALIGN_MIDDLE_VERTICAL.equals(action)) {
				elmt.setPosition(new Point(ref - bounds.width / 2, bounds.y));
			}

			// rafraichir la forme
			elmt.refreshShape();

		}

	}

	/**
	 * Retourne une valeur d'espacement pour distribution égale de la liste
	 * d'objets passée en arguments
	 * 
	 * @param list
	 * @return
	 */
	public static int getEqualHorizontalSpace(ArrayList<LayerElement> list) {

		int minX = 0;
		int maxX = 0;
		int sumWidth = 0;
		boolean first = true;

		for (LayerElement elmt : list) {

			Rectangle cb = elmt.getMaximumBounds();

			// premier element, enregistrer les dimensions
			if (first) {
				minX = cb.x;
				maxX = cb.x + cb.width;
				sumWidth = cb.width;
				first = false;
				continue;
			}

			// elements suivants, comparer avant enregistrement
			if (cb.x < minX)
				minX = cb.x;

			if (cb.x + cb.width > maxX)
				maxX = cb.x + cb.width;

			sumWidth += cb.width;
		}

		// soustraire puis diviser
		return (maxX - minX - sumWidth) / (list.size() - 1);

	}

	/**
	 * Retourne une valeur d'espacement pour distribution égale de la liste
	 * d'objets passée en arguments
	 * 
	 * @param list
	 * @return
	 */
	public static int getEqualVerticalSpace(ArrayList<LayerElement> list) {

		int minY = 0;
		int maxY = 0;
		int sumHeight = 0;
		boolean first = true;

		for (LayerElement elmt : list) {

			Rectangle cb = elmt.getMaximumBounds();

			// premier element, enregistrer les dimensions
			if (first) {
				minY = cb.y;
				maxY = cb.y + cb.height;
				sumHeight = cb.height;
				first = false;
				continue;
			}

			// elements suivants, comparer avant enregistrement
			if (cb.y < minY)
				minY = cb.y;

			if (cb.y + cb.height > maxY)
				maxY = cb.y + cb.height;

			sumHeight += cb.height;
		}

		// soustraire puis diviser
		return (maxY - minY - sumHeight) / (list.size() - 1);

	}

	/**
	 * Retourne la position de l'element le plus en bas de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getMiddleY(ArrayList<LayerElement> list) {

		int minY = 0;
		int maxY = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			Rectangle cb = elmt.getMaximumBounds();

			// premier element, enregistrer la position
			if (first) {
				minY = cb.y;
				maxY = cb.y + cb.height;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que si position plus à gauche
			if (cb.y < minY) {
				minY = cb.y;
			}

			if (cb.y + cb.height > maxY) {
				maxY = cb.y + cb.height;
			}

		}

		return minY + ((maxY - minY) / 2);

	}

	/**
	 * Retourne la position de l'element le plus en bas de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getMiddleX(ArrayList<LayerElement> list) {

		int minX = 0;
		int maxX = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			Rectangle cb = elmt.getMaximumBounds();

			// premier element, enregistrer la position
			if (first) {
				minX = cb.x;
				maxX = cb.x + cb.width;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que si position plus à gauche
			if (cb.x < minX) {
				minX = cb.x;
			}

			if (cb.x + cb.width > maxX) {
				maxX = cb.x + cb.width;
			}

		}

		return minX + ((maxX - minX) / 2);

	}

	/**
	 * Retourne la position de l'element le plus en bas de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getGreatestY(ArrayList<LayerElement> list) {

		int rslt = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			// position courante
			Rectangle cb = elmt.getMaximumBounds();
			int cy = cb.y + cb.height;

			// premier element, enregistrer la position
			if (first) {
				rslt = cy;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que la position la plus basse
			if (cy > rslt) {
				rslt = cy;
			}

		}

		return rslt;

	}

	/**
	 * Retourne la position de l'element le plus en haut de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getSmallestY(ArrayList<LayerElement> list) {

		int rslt = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			// position courante
			int cy = elmt.getPosition().y;

			// premier element, enregistrer la position
			if (first) {
				rslt = cy;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que si position plus à gauche
			if (cy < rslt) {
				rslt = cy;
			}

		}

		return rslt;

	}

	/**
	 * Retourne la position de l'element le plus à droite de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getGreatestX(ArrayList<LayerElement> list) {

		int rslt = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			Rectangle cb = elmt.getMaximumBounds();
			int cx = cb.x + cb.width;

			// premier element, enregistrer la position
			if (first) {
				rslt = cx;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que si position plus à droite
			if (cx > rslt) {
				rslt = cx;
			}

		}

		return rslt;

	}

	/**
	 * Retourne la position de l'element le plus à gauche de la liste.
	 * 
	 * @param list
	 * @return
	 */
	public static int getSmallestX(ArrayList<LayerElement> list) {

		int rslt = 0;
		boolean first = true;

		// parcourir les elements
		for (LayerElement elmt : list) {

			int cx = elmt.getPosition().x;

			// premier element, enregistrer la position
			if (first) {
				rslt = cx;
				first = false;
				continue;
			}

			// elements suivant, n'enregistrer que si position plus à gauche
			if (cx < rslt) {
				rslt = cx;
			}

		}

		return rslt;

	}

}
