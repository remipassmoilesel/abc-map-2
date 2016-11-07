package abcmap.draw.links;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.net.URI;
import java.util.Arrays;

import javax.swing.ImageIcon;

import abcmap.gui.GuiIcons;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.threads.ThreadManager;

public class LinkRessource implements Runnable {

	/** Le lien texte vers la ressource */
	private String location;

	/** L'action a effectuer avec la ressource */
	private LinkAction action;

	/** L'icone a dessiner */
	private ImageIcon mark;

	private GuiManager guim;

	LinkRessource(String location, LinkAction action) {

		guim = MainManager.getGuiManager();

		this.location = location;
		this.action = action;
		this.mark = GuiIcons.LINK_MARK;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof LinkRessource == false)
			return false;

		LinkRessource shp = (LinkRessource) obj;

		Object[] toCompare1 = new Object[] { this.location, this.action };
		Object[] toCompare2 = new Object[] { shp.location, shp.action };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	public void draw(Graphics2D g, int x, int y) {

		// dessiner la marque de lien
		g.drawImage(mark.getImage(), x, y - mark.getIconHeight() / 2, null);
	}

	@Override
	public void run() {

		// ouvrir dans le navigateur
		try {

			Desktop desktop = Desktop.getDesktop();

			if (LinkAction.OPEN_IN_BROWSER.equals(action)) {
				desktop.browse(new URI(location));
			}

			else if (LinkAction.OPEN_ON_DESKTOP.equals(action)) {
				desktop.open(new File(location));
			}

			else if (LinkAction.OPEN_IN_MAILER.equals(action)) {
				desktop.mail(new URI(location));
			}

		} catch (Exception e) {

			// erreur lors de l'ouverture
			Log.error(e);
			showErrorLater();
		}

	}

	public void runLater() {
		ThreadManager.runLater(this);
	}

	private void showErrorLater() {
		guim.showInformationTextFieldDialog(guim.getMainWindow(),
				"Impossible d'ouvrir la ressource spécifiée en lien. Vous pourrez peut être trouver "
						+ "cette ressource manuellement à l'emplacement: ",
				location);
	}

	public String getLocation() {
		return location;
	}

	public LinkAction getAction() {
		return action;
	}

	public void draw(Graphics2D g, Point p) {
		draw(g, p.x, p.y);
	}

	public static Point getDefaultPosition(Point shapeOrigin) {
		shapeOrigin.x += 15;
		return shapeOrigin;
	}

	public static Point getDefaultPosition(int shapeOriginX, int shapeOriginY) {
		Point p = new Point(shapeOriginX, shapeOriginY);
		return getDefaultPosition(p);
	}

}
