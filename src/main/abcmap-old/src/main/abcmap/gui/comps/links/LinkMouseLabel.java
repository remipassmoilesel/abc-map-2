package abcmap.gui.comps.links;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import abcmap.draw.links.LinkRessource;
import abcmap.gui.comps.buttons.HtmlLabel;
import net.miginfocom.swing.MigLayout;

/**
 * Etiquette d'affichage d'une ressource de lien
 * <p>
 * S'affiche à proximité de la souris lorsque l'utilisateur survole un objet
 * lié.
 * 
 * @author remipassmoilesel
 *
 */
public class LinkMouseLabel extends JPanel {

	private Color backgroundColor;
	private Border borderStyle;
	private Font fontStyle;
	private LinkRessource link;
	private int maxLinkLenght;
	private HtmlLabel label;

	public LinkMouseLabel() {
		super(new MigLayout("insets 5 10 5 10"));

		// styles
		backgroundColor = new Color(244, 244, 214);
		borderStyle = BorderFactory.createLineBorder(Color.darkGray, 1);
		fontStyle = new Font(Font.DIALOG, Font.PLAIN, 13);

		// etiquette de texte
		label = new HtmlLabel("");
		label.setOpaque(false);
		label.setFont(fontStyle);
		add(label, "center");

		// panneau
		this.setOpaque(true);
		this.setBackground(backgroundColor);
		this.setBorder(borderStyle);

		maxLinkLenght = 30;

	}

	public void reconstruct() {

		// lien null, affichage par defaut puis retour
		if (link == null) {
			setLabelText("Ressource non disponible.");
			revalidate();
			repaint();
			return;
		}

		// extraire puis tronquer le lien si il est trop long
		String location = link.getLocation();
		if (location.length() > maxLinkLenght) {
			location = location.substring(0, 10) + "..."
					+ location.substring(maxLinkLenght - 13, maxLinkLenght);
		}

		// affichage par defaut
		String text = "Lien vers <b>" + location + "</b>"
				+ "<br>Clic droit sur la forme pour ouvrir";

		setLabelText(text);

		// rafraichir
		revalidate();
		repaint();

	}

	public void setLinkRessource(LinkRessource link) {
		this.link = link;
	}

	private void setLabelText(String text) {
		label.setText(text);
	}

}
