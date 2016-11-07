package abcmap.gui.comps.color;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.textfields.DecimalTextField;
import abcmap.managers.Log;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau de selection de couleur. Lorsque qu'une couleur est sélectionnée,
 * 
 * @author remipassmoilesel
 *
 */
public class ColorPicker extends JPanel implements
		HasListenerHandler<ColorEventListener> {

	/** Champs de texte des verts */
	private DecimalTextField txtG;

	/** Champs de texte des rouges */
	private DecimalTextField txtR;

	/** Champs de texte des bleus */
	private DecimalTextField txtB;

	/** La liste des couleurs récentes */
	private ArrayList<ColorButton> recentsColorsBtn;

	/** Couleur active de premier plan */
	private Color fgColor;

	/** Couleur active de second plan */
	private Color bgColor;

	/** Sélection du plan à modifier */
	private ToggleColorButton fgButton;

	/** Sélection du plan à modifier */
	private ToggleColorButton bgButton;

	/** Nombre maximum de couleur récentes affichées */
	private int maxRecentColors;

	/** Couleurs récente par ligne - en commençant par 0 */
	private int colorsPerLine;

	/** Les couleurs récentes sélectionnées par l'utilisateur */
	private ArrayList<Color> recentColors;

	private ListenerHandler<ColorEventListener> listenerHandler;

	public ColorPicker() {
		super(new MigLayout("insets 0, gap 0"));

		this.listenerHandler = new ListenerHandler<ColorEventListener>();

		this.maxRecentColors = 5;
		this.colorsPerLine = 5;
		this.recentColors = new ArrayList<>(maxRecentColors);

		// couleur de premier et second plan
		this.fgColor = Color.blue;
		this.bgColor = Color.white;

		// palette colorée
		ColorPalette cp = new ColorPalette();
		cp.setActiveColor(bgColor);
		cp.addActionListener(new ColorPaletteActionListener());
		add(cp, "gapright 7px");

		// champs textes
		JPanel p4 = new JPanel(new MigLayout("insets 0, gap 3"));
		txtR = (DecimalTextField) GuiStyle.applyStyleTo(
				GuiStyle.RGB_RED_TXTFIELD_STYLE, new DecimalTextField(3));
		txtG = (DecimalTextField) GuiStyle.applyStyleTo(
				GuiStyle.RGB_GREEN_TXTFIELD_STYLE, new DecimalTextField(3));
		txtB = (DecimalTextField) GuiStyle.applyStyleTo(
				GuiStyle.RGB_BLUE_TXTFIELD_STYLE, new DecimalTextField(3));

		KeyListenerUtil.addListener(txtR, new ColorTextFieldListener());
		KeyListenerUtil.addListener(txtG, new ColorTextFieldListener());
		KeyListenerUtil.addListener(txtB, new ColorTextFieldListener());

		// largeur des composants
		String compWidth = "width 35px!,";

		p4.add(txtR, compWidth + "wrap");
		p4.add(txtG, compWidth + "wrap");
		p4.add(txtB, compWidth + "wrap");
		p4.add(new JPanel(), "wrap");

		// boutons blanc, noir, null
		ColorButtonActionListener cbal = new ColorButtonActionListener();
		ColorButton btB = new ColorButton(Color.black);
		ColorButton btW = new ColorButton(Color.white);
		ColorButton btN = new ColorButton(null);
		btB.addActionListener(cbal);
		btW.addActionListener(cbal);
		btN.addActionListener(cbal);

		// bouton de couleur personnalisée
		ColorDialogButton ccb = new ColorDialogButton();
		ccb.getListenerHandler().add(new CustomColorAL());

		p4.add(btW, compWidth + "wrap");
		p4.add(btB, compWidth + "wrap");
		p4.add(btN, compWidth + "wrap");
		p4.add(ccb, compWidth + "wrap");
		add(p4, "wrap");

		// visualisation de la couleur et choix du plan
		JPanel plansp = new JPanel(new MigLayout("insets 3, gap 3"));
		add(plansp, "span, grow, wrap");

		fgButton = new ToggleColorButton(fgColor);
		fgButton.setOpaque(true);

		bgButton = new ToggleColorButton(bgColor);
		bgButton.setOpaque(true);

		// premier plan sélectionné par défaut
		ButtonGroup bg = new ButtonGroup();
		bg.add(fgButton);
		bg.add(bgButton);
		fgButton.setSelected(true);

		GuiUtils.addLabel("1er plan:", plansp, "");
		plansp.add(fgButton, compWidth);

		GuiUtils.addLabel("2eme plan:", plansp, "");
		plansp.add(bgButton, compWidth);

		// conserver les couleurs récentes dans un panneau
		JPanel rcp = new JPanel(new MigLayout("insets 0, gap 3"));
		add(rcp, "span, grow");

		GuiUtils.addLabel("Couleurs récentes:", rcp, "span");

		recentsColorsBtn = new ArrayList<ColorButton>(maxRecentColors);
		for (int i = 1; i <= maxRecentColors; i++) {

			// création du bouton récent
			ColorButton bt = new ColorButton(Color.white);
			bt.addActionListener(cbal);
			recentsColorsBtn.add(bt);

			// ajout
			String cs = i % colorsPerLine == 0 ? compWidth + "wrap" : compWidth;
			rcp.add(bt, cs);

		}

	}

	/**
	 * Ecouter les les clic sur la palette colorée. Met à jour des champs de
	 * texte et assigne la couleur par défaut.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ColorPaletteActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer la source
			ColorPalette src = (ColorPalette) e.getSource();

			// recuperer la couleur active de la palette
			Color c = src.getActiveColor();

			// activer la couleur
			setActiveColor(c);

			// mise à jour des champs de texte
			updateTextFieldsWithoutFire();

			// ajout aux couleurs récentes
			addToRecentColors(c);

			// mise à jour des boutons
			updateColorButtons();

		}
	}

	/**
	 * Ecouter les clics sur les boutons de couleurs. Assigne la couleur active
	 * et met à jour les champs de texte.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ColorButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer la couleur du bouton
			Color c = ((ColorButton) e.getSource()).getColor();

			// activer la couleur
			setActiveColor(c);

			// mettre a jour les champs de texte
			updateTextFieldsWithoutFire();

			// ajouter aux couleurs récentes
			addToRecentColors(c);

			// mettre a jour les boutons
			updateColorButtons();

		}
	}

	/**
	 * Ecouter les changements dans les champs de texte et modifier la couleur
	 * active en consequence.
	 *
	 */
	private class ColorTextFieldListener extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {

			// champs de texte
			DecimalTextField[] txtF = new DecimalTextField[] { txtR, txtG, txtB };

			// valeurs finales
			Integer[] val = new Integer[3];

			for (int i = 0; i < txtF.length; i++) {
				try {

					// recuperer la valeur du champs de texte
					val[i] = txtF[i].getIntegerValue();

					// verifier l'amplitude
					if (val[i] == null || val[i] > 255 || val[i] < 0) {
						throw new InvalidInputException();
					}

				} catch (InvalidInputException | NullPointerException e2) {
					// Log.error(e2);
					return;
				}
			}

			// creer et activer la couleur
			setActiveColor(new Color(val[0], val[1], val[2]));

			// mise à jour des boutons
			updateColorButtons();
		}

	}

	/**
	 * Modifier la couleur active. Envoi un événement.
	 * 
	 * @param color
	 */
	public void setActiveColor(Color color) {

		if (fgButton.isSelected()) {
			setFgColor(color);
		}

		else {
			setBgColor(color);
		}

	}

	/**
	 * Renvoi vrai si le controle de couleur du premier plan est actif, faux si
	 * c'est celui du second plan.
	 * 
	 * @return
	 */
	public boolean isForegroundActive() {
		return fgButton.isSelected();
	}

	/**
	 * Retourne la couleur active
	 * 
	 * @return
	 */
	public Color getActiveColor() {
		if (fgButton.isSelected()) {
			return fgColor;
		} else {
			return bgColor;
		}
	}

	/**
	 * Modifie la couleur de fond selectionnee. Envoi un evenement.
	 * 
	 * @param bgColor
	 */
	public void setBgColor(Color bgColor) {
		setBgColor(bgColor, true);
	}

	/**
	 * Modifie la couleur de fond active et si notify = true envoi un evenement.
	 * 
	 * @param bgColor
	 * @param notify
	 */
	public void setBgColor(Color bgColor, boolean notify) {

		this.bgColor = bgColor;

		if (notify) {
			listenerHandler.fireEvent(new ColorEvent(bgColor, this));
		}
	}

	/**
	 * Modifie la couleur de premier plan selectionnee. Envoi un evenement.
	 * 
	 * @param fgColor
	 */
	public void setFgColor(Color fgColor) {
		setFgColor(fgColor, true);
	}

	/**
	 * Modifie la couleur de premier plan active active et si notify = true
	 * envoi un evenement.
	 * 
	 * @param bgColor
	 * @param notify
	 */
	public void setFgColor(Color fgColor, boolean notify) {

		this.fgColor = fgColor;

		if (notify) {
			listenerHandler.fireEvent(new ColorEvent(fgColor, this));
		}
	}

	/**
	 * Repeint les boutons de couleurs et actualise la liste des couleurs
	 * récentes.
	 */
	public void updateColorButtons() {

		// couleur de premier plan
		fgButton.setColor(fgColor);
		fgButton.repaint();

		// couleur de second plan
		bgButton.setColor(bgColor);
		bgButton.repaint();

		// couleurs récentes
		for (int i = 0; i < recentsColorsBtn.size(); i++) {

			Color color = recentColors.size() > i ? recentColors.get(i) : null;
			ColorButton btn = recentsColorsBtn.get(i);

			btn.setColor(color);
			btn.repaint();

		}

	}

	/**
	 * Ajoute une couleur à la liste des couleurs récentes. Si la couleur est
	 * déjà présente ses précédentes occurences seront supprimées.
	 * 
	 * @param clr
	 */
	public void addToRecentColors(Color clr) {

		// enlever d'eventuelles même couleurs
		while (recentColors.contains(clr)) {
			recentColors.remove(recentColors.indexOf(clr));
		}

		// ajouter la couleur
		recentColors.add(0, clr);

		// supprimer les couleurs en trop
		while (recentColors.size() > maxRecentColors) {
			recentColors.remove(recentColors.size() - 1);
		}

	}

	/**
	 * Met à jour les champs de texte de couleurs avec les valeurs de la couleur
	 * active.
	 * 
	 */
	public void updateTextFieldsWithoutFire() {

		// recuperer la couleur active
		Color c = getActiveColor();

		// transformer les valeurs en chaines
		String r = c != null ? Integer.toString(c.getRed()) : "";
		String g = c != null ? Integer.toString(c.getGreen()) : "";
		String b = c != null ? Integer.toString(c.getBlue()) : "";

		// mettre à jour les champs si necessaire
		GuiUtils.changeText(txtR, r);
		GuiUtils.changeText(txtG, g);
		GuiUtils.changeText(txtB, b);
	}

	/**
	 * Ecoute la selection d'une couleur dans le dialog des couleurs
	 * personnalisées.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CustomColorAL implements ColorEventListener {

		@Override
		public void colorChanged(final ColorEvent c) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					// activer la couleur
					setActiveColor(c.getColor());

					// mettre à jour les champs de texte
					updateTextFieldsWithoutFire();

					// ajouter aux couleurs récentes
					addToRecentColors(c.getColor());

					// mettre à jour les boutons
					updateColorButtons();

				}
			});

		}

	}

	@Override
	public ListenerHandler<ColorEventListener> getListenerHandler() {
		return listenerHandler;
	}

	/**
	 * Retourne la couleur active de premier plan
	 * 
	 * @return
	 */
	public Color getSelectedFgColor() {
		return fgColor;
	}

	/**
	 * Retourne la couleur active de second plan
	 * 
	 * @return
	 */
	public Color getSelectedBgColor() {
		return bgColor;
	}

}
