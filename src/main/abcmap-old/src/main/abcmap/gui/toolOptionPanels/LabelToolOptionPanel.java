package abcmap.gui.toolOptionPanels;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;

import javax.swing.JComboBox;

import abcmap.draw.DrawConstants;
import abcmap.draw.shapes.Label;
import abcmap.events.DrawManagerEvent;
import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.geo.GeoInfoMode;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.color.ColorButton;
import abcmap.gui.comps.color.ColorDialogButton;
import abcmap.gui.comps.help.AttentionPanel;
import abcmap.managers.Log;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.gui.GuiUtils;

public class LabelToolOptionPanel extends ToolOptionPanel {

	private static final Integer[] PREDEFINED_SIZES = new Integer[] { 10, 15,
			20, 25, 50, 100, 150 };

	private JComboBox<String> cbFontName;
	private JComboBox cbFontSize;
	private HtmlCheckbox chkStrike;
	private HtmlCheckbox chkUnderline;
	private HtmlCheckbox chkBold;
	private HtmlCheckbox chkItalic;
	private ColorButton colorIndicator;
	private JComboBox cbTextMode;
	private HashMap<String, GeoInfoMode> textModes;

	private Color selectedColor;

	private ColorDialogButton colorDialogButton;

	private HtmlCheckbox chkBorder;

	public LabelToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// combo de choix de la police
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		cbFontName = new JComboBox(fonts);
		cbFontName.addActionListener(new Performer(
				DrawConstants.MODIFY_FONT_FAMILY));

		// Pas de renderer pour ne pas ralentir le programme.
		// cbFontName.setRenderer(new FontComboRenderer());

		GuiUtils.addLabel("Police du texte: ", this, "wrap");
		add(cbFontName, gapLeft + largeWrap);

		// combo de choix de la taille du texte
		cbFontSize = new JComboBox(PREDEFINED_SIZES);
		cbFontSize.setEditable(true);
		cbFontSize.addActionListener(new Performer(
				DrawConstants.MODIFY_TEXT_SIZE));

		GuiUtils.addLabel("Taille du texte: ", this, "wrap");
		add(cbFontSize, gapLeft + largeWrap);

		// couleur du texte
		colorIndicator = new ColorButton(selectedColor);
		colorIndicator.setEnabled(false);

		colorDialogButton = new ColorDialogButton();
		colorDialogButton.getListenerHandler().add(
				new Performer(DrawConstants.MODIFY_TEXT_COLOR));

		GuiUtils.addLabel("Couleur du texte: ", this, "wrap");
		add(colorIndicator, gapLeft);
		add(colorDialogButton, "cell 0 5, " + largeWrap);

		// caracteristiques du texte
		GuiUtils.addLabel("Caractéristiques: ", this, "wrap");

		chkBorder = new HtmlCheckbox("Bordure");
		add(chkBorder, gapLeft + "wrap");
		chkBorder.addActionListener(new Performer(DrawConstants.MODIFY_BORDER));

		chkItalic = new HtmlCheckbox("Italique");
		add(chkItalic, gapLeft + "wrap");
		chkItalic.addActionListener(new Performer(DrawConstants.MODIFY_ITALIC));

		chkBold = new HtmlCheckbox("Gras");
		add(chkBold, gapLeft + "wrap");
		chkBold.addActionListener(new Performer(DrawConstants.MODIFY_BOLD));

		chkUnderline = new HtmlCheckbox("Souligné");
		add(chkUnderline, gapLeft + "wrap");
		chkUnderline.addActionListener(new Performer(
				DrawConstants.MODIFY_UNDERLINE));

		chkStrike = new HtmlCheckbox("Barré");
		add(chkStrike, gapLeft + largeWrap);
		chkStrike.addActionListener(new Performer(DrawConstants.MODIFY_STRIKE));

		// etiquette d'avertissement
		add(new AttentionPanel(), "span," + largeWrap);

		// combo de changement de mode d'affichage
		textModes = getDisplayModes();
		String[] modeNames = textModes.keySet().toArray(
				new String[textModes.size()]);
		cbTextMode = new JComboBox<>(modeNames);
		cbTextMode.addActionListener(new Performer(
				DrawConstants.MODIFY_TEXT_MODE));

		GuiUtils.addLabel("Modes d'affichage: ", this, "wrap");
		add(cbTextMode, gapLeft + "span, wrap");

		// mettre à jour les formuaires en fonction du projet, du gestionnaire
		// de dessin et de carte
		FormUpdater formUpdater = new FormUpdater();
		formUpdater.addEventFilter(DrawManagerEvent.class);
		formUpdater.addEventFilter(ProjectEvent.class);
		formUpdater.addEventFilter(MapEvent.class);

		// ecouter le projet et le gest de dessin
		observer.setDefaultUpdatableObject(formUpdater);
		projectm.getNotificationManager().addObserver(this);
		drawm.getNotificationManager().addObserver(this);
		mapm.getNotificationManager().addObserver(this);

		// première mise à jour
		formUpdater.run();

	}

	private HashMap<String, GeoInfoMode> getDisplayModes() {

		HashMap<String, GeoInfoMode> rslt = new HashMap<>();
		rslt.put("Texte libre", GeoInfoMode.NO_INFORMATIONS);

		GeoInfoMode posDD = new GeoInfoMode();
		posDD.setPosDD(true);
		rslt.put("Position en degrés", posDD);

		GeoInfoMode posDMD = new GeoInfoMode();
		posDMD.setPosDMD(true);
		rslt.put("Position en degrés et min. décimales", posDMD);

		GeoInfoMode posDMS = new GeoInfoMode();
		posDMS.setPosDMS(true);
		rslt.put("Position en degrés et min. et secondes", posDMS);

		return rslt;
	}

	/**
	 * Mettre à jour les formulaires
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class FormUpdater extends abcmap.utils.gui.FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer la premiere forme selectionnée
			Label shp = (Label) getFirstSelectedElement(Label.class);

			// pas de selection, mise à jour à partir de la forme temoin
			if (shp == null) {
				shp = drawm.getWitnessLabel();
				if (shp == null)
					return;
			}

			// mise à jour des composants
			updateComponentWithoutFire(cbFontName, shp.getFontFamily());
			updateComponentWithoutFire(cbFontSize, shp.getFontSize());
			updateComponentWithoutFire(colorIndicator, shp.getStroke()
					.getFgColor());
			updateComponentWithoutFire(chkBorder, shp.isBorderActivated());
			updateComponentWithoutFire(chkItalic, shp.isItalic());
			updateComponentWithoutFire(chkBold, shp.isBold());
			updateComponentWithoutFire(chkUnderline, shp.isUnderlined());
			updateComponentWithoutFire(chkStrike, shp.isStrikethrough());
			updateComponentWithoutFire(cbTextMode, shp.getGeoInfoMode());

		}

	}

	private class Performer extends ShapeUpdater {

		public Performer(DrawConstants mode) {
			setMode(mode);
			addShapeFilter(Label.class);
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// si mode color, maj de l'indicateur
			if (DrawConstants.MODIFY_TEXT_COLOR.equals(mode)) {
				colorIndicator.setColor(colorDialogButton.getActiveColor());
			}

			// rassemblement des valeurs a affecter
			ShapeProperties pp = new ShapeProperties();

			pp.font = getSelectedFont();
			pp.size = getSelectedSize();
			pp.geoInfoSize = getSelectedSize();

			pp.stroke = new DrawPropertiesContainer();
			pp.stroke.fgColor = colorIndicator.getColor();

			pp.bold = chkBold.isSelected();
			pp.italic = chkItalic.isSelected();
			pp.strikethrough = chkStrike.isSelected();
			pp.underlined = chkUnderline.isSelected();
			pp.borderActivated = chkBorder.isSelected();

			pp.geoInfoMode = getSelectedMode();

			// proprietes pour application aux elemnts selectionnes
			setProperties(pp);

			// enregistrement des préférences pour futures création
			drawm.updateWitness(Label.class, mode, pp);

		}

	}

	private String getSelectedFont() {
		try {
			return (String) cbFontName.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}
	}

	private String getSelectedMode() {
		try {
			return (String) textModes.get(cbTextMode.getSelectedItem())
					.toString();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}
	}

	private Integer getSelectedSize() {
		try {
			return (Integer) cbFontSize.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}
	}

}
