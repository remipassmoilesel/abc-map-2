package org.abcmap.ielements.importation.directory;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.textfields.TextFieldDelayedAction;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SelectDirectoryToImport extends InteractionElement {

    private JTextField txtPath;
    private ImportManagerUpdater textFieldListener;
    private TextFieldUpdater textfieldUpdater;
    private Runnable memoryPanelUpdater;

    public SelectDirectoryToImport() {
        this.label = "Chemin du dossier d'images";
        this.help = "Entrez le chemin d'un dossier où importer des images.";
    }

    @Override
    protected Component createPrimaryGUI() {

        // create GUI with a text field and a "Browse button"
        this.txtPath = new JTextField();
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.add(txtPath, "width 80%!, wrap");

        JButton btn = new JButton("Parcourir");
        panel.add(btn, "align right, " + wrap15());

        // listen user input on text field
        this.textFieldListener = new ImportManagerUpdater();
        TextFieldDelayedAction.delayedActionFor(txtPath, textFieldListener, false);

        /*
        // listen user actions on button
        btn.addActionListener(new BrowsePathActionListener(txtPath, false, true));

        // listen profile changes
        textfieldUpdater = new TextFieldUpdater();
        notifm.addEventListener(textfieldUpdater);
        configm().getNotificationManager().addObserver(this);
        textfieldUpdater.run();

        return panel;
        */

        return null;
    }

    /**
     * Update text field from configuration
     */
    private class TextFieldUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();
/*
            configPath = configm().getConfiguration().DIRECTORY_IMPORT_PATH

            GuiUtils.changeText(txtPath, configm.getDirectoryImportPath());
            */
        }

    }

    /**
     * Update import manager from text field
     */
    private class ImportManagerUpdater implements Runnable {

        @Override
        public void run() {

            /*
            String path = txtPath.getText();

            if (Utils.safeEquals(configm.getDirectoryImportPath(), path) == false) {
                configm.setDirectoryImportPath(path);
            }

            */
        }

    }

}
