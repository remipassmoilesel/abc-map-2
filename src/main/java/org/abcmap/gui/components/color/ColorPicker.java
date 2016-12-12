package org.abcmap.gui.components.color;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.textfields.DecimalTextField;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Panneau de selection de couleur. Lorsque qu'une couleur est sélectionnée,
 *
 * @author remipassmoilesel
 */
public class ColorPicker extends JPanel implements HasListenerHandler<ColorEventListener> {

    private static final CustomLogger logger = LogManager.getLogger(ColorPicker.class);

    /**
     * green values
     */
    private DecimalTextField txtG;

    /**
     * red values
     */
    private DecimalTextField txtR;

    /**
     * blue values
     */
    private DecimalTextField txtB;

    /**
     * foreground active color
     */
    private Color fgColor;

    /**
     * background active color
     */
    private Color bgColor;

    /**
     * active mode selection (bg / fg)
     */
    private ToggleColorButton fgButton;

    /**
     * active mode selection (bg / fg)
     */
    private ToggleColorButton bgButton;

    /**
     * maximum nmber of recent colors
     */
    private int maxRecentColors;

    /**
     * Size of recent color line - begin 0
     */
    private int colorsPerLine;

    /**
     * Recent colors
     */
    private ArrayList<ColorButton> recentsColorsBtn;
    private ArrayList<Color> recentColors;

    private ListenerHandler<ColorEventListener> listenerHandler;

    public ColorPicker() {
        super(new MigLayout("insets 0, gap 0"));

        this.listenerHandler = new ListenerHandler<ColorEventListener>();

        this.maxRecentColors = 5;
        this.colorsPerLine = 5;
        this.recentColors = new ArrayList<>(maxRecentColors);

        this.fgColor = Color.blue;
        this.bgColor = Color.white;

        // colored palette
        ColorPalette cp = new ColorPalette();
        cp.setActiveColor(bgColor);
        cp.addActionListener(new ColorPaletteActionListener());
        add(cp, "gapright 7px");

        // color text fields
        JPanel p4 = new JPanel(new MigLayout("insets 0, gap 3"));
        txtR = (DecimalTextField) GuiStyle.applyStyleTo(GuiStyle.RGB_RED_TXTFIELD_STYLE, new DecimalTextField(3));
        txtG = (DecimalTextField) GuiStyle.applyStyleTo(GuiStyle.RGB_GREEN_TXTFIELD_STYLE, new DecimalTextField(3));
        txtB = (DecimalTextField) GuiStyle.applyStyleTo(GuiStyle.RGB_BLUE_TXTFIELD_STYLE, new DecimalTextField(3));

        KeyAdapter.addListener(txtR, new ColorTextFieldListener());
        KeyAdapter.addListener(txtG, new ColorTextFieldListener());
        KeyAdapter.addListener(txtB, new ColorTextFieldListener());

        // component width
        String compWidth = "width 35px!,";

        p4.add(txtR, compWidth + "wrap");
        p4.add(txtG, compWidth + "wrap");
        p4.add(txtB, compWidth + "wrap");
        p4.add(new JPanel(), "wrap");

        // black white null buttons
        ColorButtonActionListener cbal = new ColorButtonActionListener();
        ColorButton btB = new ColorButton(Color.black);
        ColorButton btW = new ColorButton(Color.white);
        ColorButton btN = new ColorButton(null);
        btB.addActionListener(cbal);
        btW.addActionListener(cbal);
        btN.addActionListener(cbal);

        // custom color button
        ColorDialogButton ccb = new ColorDialogButton();
        ccb.getListenerHandler().add(new CustomColorAL());

        p4.add(btW, compWidth + "wrap");
        p4.add(btB, compWidth + "wrap");
        p4.add(btN, compWidth + "wrap");
        p4.add(ccb, compWidth + "wrap");
        add(p4, "wrap");

        // show active colors and bg/fg selection
        JPanel plansp = new JPanel(new MigLayout("insets 3, gap 3"));
        add(plansp, "span, grow, wrap");

        fgButton = new ToggleColorButton(fgColor);
        fgButton.setOpaque(true);

        bgButton = new ToggleColorButton(bgColor);
        bgButton.setOpaque(true);

        ButtonGroup bg = new ButtonGroup();
        bg.add(fgButton);
        bg.add(bgButton);
        fgButton.setSelected(true);

        GuiUtils.addLabel("1er plan:", plansp, "");
        plansp.add(fgButton, compWidth);

        GuiUtils.addLabel("2eme plan:", plansp, "");
        plansp.add(bgButton, compWidth);

        // keep recent colors
        JPanel rcp = new JPanel(new MigLayout("insets 0, gap 3"));
        add(rcp, "span, grow");

        GuiUtils.addLabel("Couleurs récentes:", rcp, "span");

        recentsColorsBtn = new ArrayList<>(maxRecentColors);
        for (int i = 1; i <= maxRecentColors; i++) {

            ColorButton bt = new ColorButton(Color.white);
            bt.addActionListener(cbal);
            recentsColorsBtn.add(bt);

            String cs = i % colorsPerLine == 0 ? compWidth + "wrap" : compWidth;
            rcp.add(bt, cs);

        }

    }

    /**
     * Listen user actions on palette
     */
    private class ColorPaletteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ColorPalette src = (ColorPalette) e.getSource();
            Color c = src.getActiveColor();
            setActiveColor(c);
            updateTextFieldsWithoutFire();
            addToRecentColors(c);
            updateColorButtons();
        }
    }

    /**
     * Listen user actions on color buttons
     *
     * @author remipassmoilesel
     */
    private class ColorButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Color c = ((ColorButton) e.getSource()).getColor();
            setActiveColor(c);
            updateTextFieldsWithoutFire();
            addToRecentColors(c);
            updateColorButtons();
        }
    }

    /**
     * Listen user actions on text fields
     */
    private class ColorTextFieldListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            DecimalTextField[] txtF = new DecimalTextField[]{txtR, txtG, txtB};

            Integer[] val = new Integer[3];
            for (int i = 0; i < txtF.length; i++) {
                try {
                    val[i] = txtF[i].getIntegerValue();

                    // check value
                    if (val[i] == null || val[i] > 255 || val[i] < 0) {
                        throw new Exception("Invalid value: " + val[i]);
                    }

                } catch (Exception e2) {
                    logger.error(e2);
                    return;
                }
            }

            // activate color and update buttons
            setActiveColor(new Color(val[0], val[1], val[2]));
            updateColorButtons();
        }

    }

    /**
     * Change active color
     * <p>
     * Fire an event
     *
     * @param color
     */
    public void setActiveColor(Color color) {

        if (fgButton.isSelected()) {
            setFgColor(color);
        } else {
            setBgColor(color);
        }

    }

    /**
     * Return true if foreground mode activated
     *
     * @return
     */
    public boolean isForegroundActive() {
        return fgButton.isSelected();
    }

    /**
     * Return color value
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
     * Change background color
     *
     * @param bgColor
     */
    public void setBgColor(Color bgColor) {
        setBgColor(bgColor, true);
    }

    /**
     * Change background color and fire an event if notify = true
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
     * Change foreground color
     *
     * @param fgColor
     */
    public void setFgColor(Color fgColor) {
        setFgColor(fgColor, true);
    }

    /**
     * Change foreground color and fire an event if notify = true
     *
     * @param fgColor
     * @param notify
     */
    public void setFgColor(Color fgColor, boolean notify) {

        this.fgColor = fgColor;

        if (notify) {
            listenerHandler.fireEvent(new ColorEvent(fgColor, this));
        }
    }

    /**
     * Update color buttons with active colors
     */
    public void updateColorButtons() {

        // bg / fg
        fgButton.setColor(fgColor);
        fgButton.repaint();

        bgButton.setColor(bgColor);
        bgButton.repaint();

        // recent colors
        for (int i = 0; i < recentsColorsBtn.size(); i++) {

            Color color = recentColors.size() > i ? recentColors.get(i) : null;
            ColorButton btn = recentsColorsBtn.get(i);

            btn.setColor(color);
            btn.repaint();

        }

    }

    /**
     * Add a color to recent color list
     *
     * @param clr
     */
    public void addToRecentColors(Color clr) {

        // remove same colors
        while (recentColors.contains(clr)) {
            recentColors.remove(recentColors.indexOf(clr));
        }

        recentColors.add(0, clr);

        // limit list size
        while (recentColors.size() > maxRecentColors) {
            recentColors.remove(recentColors.size() - 1);
        }

    }

    /**
     * Update text fields with active colors
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
     * Listen custom color selection
     */
    private class CustomColorAL implements ColorEventListener {

        @Override
        public void colorChanged(final ColorEvent c) {
            SwingUtilities.invokeLater(() -> {
                setActiveColor(c.getColor());
                updateTextFieldsWithoutFire();
                addToRecentColors(c.getColor());
                updateColorButtons();
            });
        }

    }

    @Override
    public ListenerHandler<ColorEventListener> getListenerHandler() {
        return listenerHandler;
    }

    /**
     * Get active foreground color
     *
     * @return
     */
    public Color getSelectedFgColor() {
        return fgColor;
    }

    /**
     * Get active background color
     *
     * @return
     */
    public Color getSelectedBgColor() {
        return bgColor;
    }

}
