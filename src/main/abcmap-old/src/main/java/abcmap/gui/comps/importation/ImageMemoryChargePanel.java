package abcmap.gui.comps.importation;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.comps.color.ColorButton;
import abcmap.importation.ImageMemoryIndicator;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Refreshable;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

/**
 * Composant graphique affichant la charge représentée par une image en fonction
 * de sa taille.
 * 
 * @author remipassmoilesel
 *
 */
public class ImageMemoryChargePanel extends JPanel implements Refreshable {

	/** L'indicateur de charge à afficher */
	private ImageMemoryIndicator indicator;

	/** Affiche le titre du nivau */
	private JLabel levelLabel;

	/** Affiche la couleur du niveau */
	private ColorButton colorIndicator;

	/** Affiche un message spécifique si le niveau est elevé */
	private JLabel messageLabel;

	/** Affiche la taille estimée de l'element */
	private JLabel estimatedSizeLabel;

	/** Taille estimée de l'image */
	private double estimatedSizeMp;

	private ImportManager importm;

	public ImageMemoryChargePanel() {

		super(new MigLayout("insets 5"));

		this.importm = MainManager.getImportManager();

		// données
		this.estimatedSizeMp = -1;
		this.indicator = ImageMemoryIndicator.MIN_CHARGE;

		// composants graphiques
		this.levelLabel = new HtmlLabel();
		this.estimatedSizeLabel = new HtmlLabel();

		this.colorIndicator = new ColorButton(Color.red);
		colorIndicator.setEnabled(false);

		this.messageLabel = new HtmlLabel(
				"<b>Attention:</b> utiliser trop de mémoire affectera la stabilité "
						+ "du logiciel. Pensez à scinder votre projet en plusieurs sous-projets "
						+ "si nécéssaire.");

		this.setBorder(BorderFactory.createLineBorder(Color.gray));

		// construction du panneau
		GuiUtils.addLabel("<b>Indicateur de charge mémoire</b>", this,
				"span, wrap", GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);
		add(colorIndicator, "width 30px!");
		add(levelLabel, "wrap");
		add(estimatedSizeLabel, "span, wrap 10px");

		reconstruct();
	}

	public void setMemoryIndicator(ImageMemoryIndicator indicator) {
		this.estimatedSizeMp = -1d;
		this.indicator = indicator;
	}

	public void setIndicationFor(double valueMp) {
		this.estimatedSizeMp = Utils.round(valueMp, 1);
		this.indicator = importm.getMemoryChargeIndicatorFor(estimatedSizeMp);
	}

	public void setMemoryIndicatorFor(double pixelWidth, double pixelHeight) {
		this.estimatedSizeMp = Utils.round(pixelWidth * pixelHeight / 1000000d,
				1);
		this.indicator = importm.getMemoryChargeIndicatorFor(pixelWidth,
				pixelHeight);
	}

	@Override
	public void reconstruct() {

		GuiUtils.throwIfNotOnEDT();

		// pas d'indicateur de charge, vider le panneau et fermer
		if (indicator == null) {
			return;
		}

		// changer la couleur du niveau
		colorIndicator.setColor(indicator.getBgColor());

		// changer le nom du niveau
		levelLabel.setText(indicator.getLevelLabel());

		// afficher la taille estimée
		String estimatedSizeStr = "Taille estimée: ";
		if (estimatedSizeMp > 0) {
			estimatedSizeStr += estimatedSizeMp + " MP";
		} else {
			String strMaxVal = indicator.getMaxVal() < 50 ? String
					.valueOf(indicator.getMaxVal()) : "50";
			estimatedSizeStr += indicator.getMinVal() + " MP < ... < "
					+ strMaxVal + " MP";
		}
		estimatedSizeLabel.setText(estimatedSizeStr);

		// afficher un message d'avertissement en fonction du niveau
		remove(messageLabel);
		if (indicator.getLevel() >= ImageMemoryIndicator.getWeightThreshold()) {
			add(messageLabel, "width 200px!, span, wrap");
		}

		refresh();
	}

	@Override
	public void refresh() {

		colorIndicator.revalidate();
		colorIndicator.repaint();

		estimatedSizeLabel.revalidate();
		estimatedSizeLabel.repaint();

		levelLabel.revalidate();
		levelLabel.repaint();

		messageLabel.revalidate();
		messageLabel.repaint();

		revalidate();
		repaint();
	}

}
