package abcmap.draw.legend;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Label;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;

public class LegendBlock {

	public static LegendBlock buildAndAddTo(MapLayer lay, int x, int y, int width,
			int height, DrawProperties bg) {

		GuiUtils.throwIfOnEDT();

		LegendBlock lb = new LegendBlock();
		lb.setBackgroundStroke(bg);
		lb.setDesiredBounds(new Rectangle(x, y, width, height));
		lb.construct();
		lb.addToLayer(lay);

		return lb;
	}

	/** Les dimensions souhaitées du block de légende */
	private Rectangle desiredBounds;

	/** Les instructions concernant couleur, bordure et style du fond */
	private DrawProperties backgroundStroke;

	/** La largeur en pourcentage de largeur totale des echantillons elements */
	private float sampleRelativeWidth;

	/** L'espacement de référence en pourcentage */
	private float sampleRelativeSpace;

	/** Taille relative de la légende */
	private float textRelativeSize;

	/** La liste de tous les elements a dessiner */
	private ArrayList<LayerElement> elements;

	/** Marges entre le fond et le contenu */
	private int bgMargins;

	/** Hauteur minimale des lignes en pixels */
	private int minLineHeight;

	/** Largeur d'echantillon minimale */
	private int minSampleWidth;

	/** Espace minimal */
	private int minSpace;

	/** le texte par defaut */
	private String textSample;

	private ProjectManager drawm;
	private ProjectManager projectm;

	public LegendBlock() {

		projectm = MainManager.getProjectManager();
		drawm = MainManager.getProjectManager();

		desiredBounds = null;

		sampleRelativeWidth = 0.1f;
		sampleRelativeSpace = 0.03f;
		textRelativeSize = 0.07f;

		minSpace = 10;
		minLineHeight = 20;
		minSampleWidth = 20;
		bgMargins = 10;

		backgroundStroke = new DrawProperties();

		textSample = "Lorem ipsum dolor sit amet...";

		elements = new ArrayList<LayerElement>(20);

	}

	/**
	 * Construire les elements à dessiner et les positionner
	 */
	public void construct() {

		GuiUtils.throwIfOnEDT();

		if (desiredBounds == null)
			throw new IllegalStateException("Must define bounds before");

		// lister tous les echantillons à afficher
		ArrayList<LegendSample> samples = listSamples();

		Rectangle realBounds = new Rectangle(desiredBounds.x, desiredBounds.y, 0, 0);

		// calculer les dimensions de reference des echantillon
		int width = (int) (desiredBounds.width * sampleRelativeWidth);
		int space = (int) (desiredBounds.width * sampleRelativeSpace);
		int fontSize = (int) (desiredBounds.width * textRelativeSize);

		// longueur de reference
		width = width < minSampleWidth ? minSampleWidth : width;

		// espacement de reference
		space = space < minSpace ? minSpace : space;

		// hauteur de la ligne
		int lineHeight = space + width + space;

		// position du début du traçage
		int pointerX = desiredBounds.x + space;
		int pointerY = desiredBounds.y + space;

		// iterer les echantillons
		for (LegendSample sp : samples) {

			// calculer le futur emplacement
			Rectangle bounds = new Rectangle(pointerX, pointerY, width, width);

			// instancier l'echantillon et l'ajouter
			LayerElement sample = sp.getInstance(bounds);
			elements.add(sample);

			// ajouter un texte d'exemple
			Label lbl = new Label();
			lbl.setFontFamily(Font.DIALOG);
			lbl.setFontSize(fontSize);
			lbl.setText(textSample);

			// positionner le texte et rafraichir
			lbl.setPosition(new Point(pointerX + width + space, pointerY));
			lbl.refreshShape();
			elements.add(lbl);

			// adapter le curseur vertical
			pointerY += lineHeight;

			if (pointerY > realBounds.height) {
				realBounds.height = pointerY + lineHeight + space;
			}

			// si trop bas, passer à la colonne suivante
			if (pointerY > desiredBounds.y + desiredBounds.height) {

				// deplacer le curseur horizontal + 1 colonne
				pointerX += width + space + lbl.getMaximumBounds().width + space;

				// deplacer le curseur vertical + ligne
				pointerY = desiredBounds.y;

				// rectifier les dimensions reelles
				realBounds.width += width + space + lbl.getMaximumBounds().width + space;

			}

		}

		// ajouter le fond à la fin en fonction des vraies dimensions
		if (backgroundStroke != null) {

			// creer un rectangle
			abcmap.draw.shapes.Rectangle bgRectangle = new abcmap.draw.shapes.Rectangle();

			// positionner et dimensionner
			bgRectangle.setPosition(new Point(realBounds.x - bgMargins, realBounds.y - bgMargins));
			bgRectangle.setDimensions(
					new Dimension(realBounds.width + bgMargins, realBounds.height + bgMargins));

			// style
			bgRectangle.setStroke(backgroundStroke);

			// rafraichir et ajouter au début
			elements.add(0, bgRectangle);
		}

	}

	public void addToLayer(MapLayer lay) {

		GuiUtils.throwIfOnEDT();

		// ajouter sans notifications
		lay.setNotificationsEnabled(false);

		for (LayerElement elmt : elements) {
			lay.addElement(elmt);
		}

		lay.setNotificationsEnabled(true);

		// notification
		projectm.fireElementsChanged();

	}

	/**
	 * Liste tous les elements différents du projet et créé un échantilllon de
	 * chauque element
	 * 
	 * @return
	 */
	private ArrayList<LegendSample> listSamples() {

		// projet non initialisé: erreur
		if (projectm.isInitialized() == false)
			throw new IllegalStateException();

		ArrayList<LegendSample> rslt = new ArrayList<>();

		// iterer les calques
		for (MapLayer lay : projectm.getLayers()) {

			// iterer les elements
			for (LayerElement elmt : lay.getDrawShapes()) {

				// creer un echantillon
				LegendSample sample = new LegendSample(elmt);

				// ajouter si non présent
				if (rslt.contains(sample) == false) {
					rslt.add(sample);
				}

			}

		}

		return rslt;

	}

	public void setBackgroundStroke(DrawProperties backgroundStroke) {
		this.backgroundStroke = backgroundStroke;
	}

	public void setBgMargins(int bgMargins) {
		this.bgMargins = bgMargins;
	}

	public void setMinLineHeight(int min) {
		this.minLineHeight = min;
	}

	public void setSampleRelativeSpace(float sampleRelativeSpace) {
		this.sampleRelativeSpace = sampleRelativeSpace;
	}

	public void setSampleRelativeWidth(float sampleRelativeWidth) {
		this.sampleRelativeWidth = sampleRelativeWidth;
	}

	public void setDesiredBounds(Rectangle desiredBounds) {
		this.desiredBounds = desiredBounds;
	}

	public ArrayList<LayerElement> getElements() {
		return elements;
	}

}
