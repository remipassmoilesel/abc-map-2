package abcmap.gui.windows.crop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.crypto.dsig.Transform;

import abcmap.gui.windows.AbstractCustomWindow;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

/**
 * Affiche une image en plein écran: <br>
 * - A taille normale si elle est plus petite,<br>
 * - Réduite à la taille de l'écran si elle est plus grande
 * 
 * @author remipassmoilesel
 *
 */
public class FullScreenPictureWindow extends AbstractCustomWindow {

	protected boolean debug = false;

	/** Les dimensions d'affichage */
	protected Dimension displayDim;

	/** Les dimensions originales l'image */
	protected Dimension imageDim;

	/** Les dimensions de l'écran */
	protected Dimension screenDim;

	/** L'image à dessiner */
	protected BufferedImage image;

	/** Couleur de fond du cadre autour de l'image à dessiner */
	protected Color backgroundColor;

	/** Le panneau ou l'image est déssinée */
	protected ImagePane imagePane;

	public FullScreenPictureWindow() {

		GuiUtils.throwIfNotOnEDT();

		// couleur de fond
		backgroundColor = Color.darkGray;

		// determiner la taille de l'ecran
		this.screenDim = Toolkit.getDefaultToolkit().getScreenSize();

		// fenetre en haut a gauche, et de la taille de l'ecran
		this.setLocation(0, 0);
		this.setSize(screenDim);

		// pas de bouton, pas de fermeture, pas de redimensionnement
		this.setUndecorated(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// panneau support
		JPanel contentPane = new JPanel();
		contentPane.setBackground(backgroundColor);
		setContentPane(contentPane);

		// panneau d'image
		this.imagePane = new ImagePane();
		imagePane.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));

		// centrer l'image sur le panneau
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.CENTER;
		gridbag.setConstraints(contentPane, constraints);
		contentPane.setLayout(gridbag);

		contentPane.add(imagePane);

	}

	/**
	 * Peinture de l'element central contenant l'image.
	 * 
	 * @param g2d
	 */
	protected void paintImagePane(Graphics2D g2d) {

		// ameliorer la qualité de rendu
		GuiUtils.applyQualityRenderingHints(g2d);

		// dessiner l'image
		g2d.drawImage(image, 0, 0, displayDim.width, displayDim.height, null);

	}

	public void setImage(BufferedImage img) {

		// conserver les dimensions de l'image orginale
		imageDim = new Dimension(img.getWidth(), img.getHeight());

		// raz des dimensions d'affichage
		displayDim = new Dimension();

		// si l'image est trop grande, mettre à l'echelle l'image et ne
		// conserver que la version réduite
		if (imageDim.width > screenDim.width || imageDim.height > screenDim.height) {
			this.image = Utils.scaleImage(img, screenDim.width, screenDim.height);
		}

		// sinon conserver telle quelle
		else {
			this.image = img;
		}

		// retenir les dimensions de l'image redimensionnée pour affichage
		displayDim.width = this.image.getWidth();
		displayDim.height = this.image.getHeight();

		// mettre à jour le panneau d'affichage de l'image
		refreshImagePane();

		// mettre à jour la fenêtre
		revalidate();
		repaint();
	}

	public void refreshImagePane() {
		imagePane.revalidate();
		imagePane.repaint();
	}

	/**
	 * Retourne le coefficient d'affichage, soit la taille de l'affichage / la
	 * taille de l'image
	 * 
	 * @return
	 */
	public float getDisplayScale() {
		return (float) (displayDim.getWidth() / imageDim.getWidth());
	}

	public Rectangle transformToImageSpace(Rectangle r) {

		// copier le rectangle
		Rectangle rect = new Rectangle(r);

		// echelle de transformation
		float scale = getDisplayScale();

		rect.x = Math.round(rect.x / scale);
		rect.y = Math.round(rect.y / scale);
		rect.width = Math.round(rect.width / scale);
		rect.height = Math.round(rect.height / scale);

		return rect;
	}

	public Rectangle transformToScreenSpace(Rectangle r) {

		// copier le rectangle
		Rectangle rect = new Rectangle(r);

		// echelle de transformation
		float scale = getDisplayScale();

		rect.x = Math.round(rect.x * scale);
		rect.y = Math.round(rect.y * scale);
		rect.width = Math.round(rect.width * scale);
		rect.height = Math.round(rect.height * scale);

		return rect;
	}

	/**
	 * Panneau d'affichage de l'image. Peint à l'exterieur pour faciliter
	 * l'overriding.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	protected class ImagePane extends JPanel {

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			paintImagePane((Graphics2D) g);
		}

		@Override
		public Dimension getPreferredSize() {
			return displayDim;
		}

		@Override
		public Dimension getMaximumSize() {
			return displayDim;
		}

		@Override
		public Dimension getMinimumSize() {
			return displayDim;
		}
	}

}
