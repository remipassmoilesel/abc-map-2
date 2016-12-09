package org.abcmap.gui.windows.crop;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.ImportManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.color.ColorDialogButton;
import org.abcmap.gui.components.color.ColorEvent;
import org.abcmap.gui.components.color.ColorEventListener;
import org.abcmap.gui.components.importation.CropDimensionsPanel;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Dialog used with crop selection window to show dimensions of crop
 */
public class CropDimensionsDialog extends JDialog {

    private static final int MARGIN = 20;
    private CropDimensionsPanel cropPanel;
    private CropConfigurationWindow cropWindow;
    private GuiManager guim;
    private ConfigurationManager configm;
    private ImportManager importm;

    public CropDimensionsDialog(CropConfigurationWindow cropwin) {
        super();

        this.guim = MainManager.getGuiManager();
        this.configm = MainManager.getConfigurationManager();
        this.importm = MainManager.getImportManager();

        guim.setWindowIconFor(this);

        this.cropWindow = cropwin;

        this.setModal(false);
        this.setTitle("Recadrage");
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // uneeded here because added in main crop window
        // this.addWindowListener(closeWL);

        // main panel
        JPanel contentPane = new JPanel(new MigLayout("insets 15px, gap 8px"));
        this.setContentPane(contentPane);

        // change selection color
        GuiUtils.addLabel("Couleur du tracé: ", this, "wrap", GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

        ColorDialogButton colorButton = new ColorDialogButton();
        colorButton.getListenerHandler().add(new ColorbuttonListener());

        contentPane.add(colorButton, "gapleft 10px, wrap 15px");

        // Panel with crop dimensions
        cropPanel = new CropDimensionsPanel(CropDimensionsPanel.Mode.WITH_CLOSE_WINDOW_BUTTON);

        // close and validate actions
        cropPanel.activateCroppingListener(true);
        cropPanel.getBtnCloseWindow().addActionListener(new CloseListener());

        GuiUtils.addLabel("Valeurs de recadrage: ", this, "wrap", GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

        contentPane.add(cropPanel, "gapleft 10px, wrap 10px");

        // listen user input
        cropPanel.addListener(new TextfieldListener());

        this.pack();
    }

    public void moveToDefaultPosition() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dial = this.getSize();
        Point loc = new Point(screen.width - dial.width - MARGIN, MARGIN);
        this.setLocation(loc);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        refresh();
    }

    public void refresh() {

        cropPanel.updateValuesWithoutFire(configm.getCropRectangle());

        cropPanel.updateChkCroppingWithoutFire(configm.isCroppingEnabled());

        cropPanel.repaint();
    }

    public CropDimensionsPanel getCropPanel() {
        return cropPanel;
    }

    /**
     * User want to close crop configuration
     */
    private class CloseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            cropWindow.setVisible(false);
        }
    }

    /**
     * Change selection color
     */
    private class ColorbuttonListener implements ColorEventListener {

        @Override
        public void colorChanged(ColorEvent c) {

            CropSelectionRectangle selection = cropWindow.getSelection();
            selection.setColor(c.getColor());

            selection.refreshShape();
            cropWindow.refreshImagePane();

        }

    }

    /**
     * Listen user input
     */
    private class TextfieldListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            Rectangle rect = null;
            try {
                rect = cropPanel.getRectangle();
            } catch (InvalidInputException e1) {
                return;
            }

            // change only if needed
            if (rect.equals(configm.getCropRectangle()) == false) {
                configm.setCropRectangle(rect);
            }

        }

    }

}
