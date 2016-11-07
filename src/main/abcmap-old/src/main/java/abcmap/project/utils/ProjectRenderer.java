package abcmap.project.utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Area;

import abcmap.gui.comps.display.DrawablePanelElement;
import abcmap.managers.DrawManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.Project;
import abcmap.project.ProjectMetadatas;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.utils.Utils;

/**
 * Classe de rendu de projet pour objet Graphics (Swing)
 * 
 * @author remipassmoilesel
 *
 */
public class ProjectRenderer implements DrawablePanelElement {

	private ProjectManager projectm;

	// couleur de fond
	private Color backgroundColor;

	private Color borderColor;
	private Stroke borderStroke;

	private String renderingMode;
	private boolean drawLayoutFrames;

	private DrawManager drawm;

	public ProjectRenderer(String renderingMode) {

		this.projectm = MainManager.getProjectManager();
		this.drawm = MainManager.getDrawManager();

		// dessiner les cadres d'impression (index)
		this.drawLayoutFrames = false;

		// mode de rendu (impression / ecran)
		this.renderingMode = renderingMode;

		// couleur de fond, derriere le projet
		this.backgroundColor = Color.lightGray;

		// bordure dynamique
		this.borderColor = Color.gray;
		this.borderStroke = new BasicStroke(2);
	}

	public void render(Graphics2D g2d) {

		// projet non initialisé: vider l'ecran puis
		if (projectm.isInitialized() == false) {
			g2d.setPaint(backgroundColor);
			g2d.fill(g2d.getClip());
			return;
		}

		// metadonnees
		ProjectMetadatas metadatas = projectm.getMetadatas();

		Rectangle projectBounds = new Rectangle(0, 0, metadatas.MAP_DIMENSIONS.width, metadatas.MAP_DIMENSIONS.height);

		// arriere plan du projet
		g2d.setPaint(Utils.stringToColor(metadatas.BACKGROUND_COLOR));
		g2d.fill(projectBounds);

		// parcourir et afficher les calques
		for (MapLayer lay : projectm.getLayers()) {
			if (lay.isVisible())
				lay.draw(g2d, renderingMode);
		}

		// dessin des cadres de mise en page
		if (drawLayoutFrames) {

			// dessiner les cadres en transparence
			Graphics2D g2t = (Graphics2D) g2d.create();
			g2t.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, metadatas.LAYOUT_FRAME_OPACITY));

			// transmission de l'echelle
			g2t.setTransform(g2d.getTransform());

			Integer i = 0;
			drawing: for (LayoutPaper lay : projectm.getLayouts()) {

				// ne pas dessiner les pages d'assemblage
				// mais les compter quand meme dans les numeros de pages
				i++;
				if (lay.isAssemblyPage())
					continue drawing;

				// dessin du rectangle
				Rectangle rect = lay.getBoundsOnMap();

				// ajustements par rapport e l'epaisseur du trait
				Integer s = metadatas.LAYOUT_FRAME_THICKNESS;
				rect.x += s / 2;
				rect.y += s / 2;

				rect.width -= s;
				rect.height -= s;

				// dessin du cadre
				g2t.setStroke(new BasicStroke(s));

				// alterner les couleurs
				if (i % 2 == 0)
					g2t.setColor(metadatas.LAYOUT_FRAME_COLOR_1);
				else
					g2t.setColor(metadatas.LAYOUT_FRAME_COLOR_2);

				g2t.draw(rect);

				// dessin du numero de cadre
				Font font = new Font(Font.DIALOG, Font.PLAIN, rect.height / 2);
				g2t.setFont(font);
				FontMetrics fm = g2t.getFontMetrics(font);
				g2t.drawString(i.toString(), rect.x + s, rect.y + s + fm.getHeight());

			}

		}

		// passer la main à l'outil si présent
		if (drawm.getCurrentTool() != null){
			drawm.getCurrentTool().drawOnCanvas(g2d);
		}

		// (re) dessiner l'arriere plan du panel
		// pour ne pas dessiner l'exterieur du projet
		g2d.setPaint(backgroundColor);
		Area bg = new Area(g2d.getClip());
		bg.subtract(new Area(projectBounds));
		g2d.fill(bg);

		// dessiner la bordure dynamique
		g2d.setColor(borderColor);
		g2d.setStroke(borderStroke);
		g2d.draw(projectBounds);

	}

	public void drawLayoutFrames(boolean drawLayoutFrames) {
		this.drawLayoutFrames = drawLayoutFrames;
	}

	@Override
	public Dimension getDimensions() {

		// projet non initialisé, dimensions nulles
		if (projectm.isInitialized() == false) {
			return null;
		}

		Project project = projectm.getProject();
		return project.getMapDimensions();
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

}
