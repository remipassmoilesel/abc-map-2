package org.abcmap.ielements.importation.directory;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.CFNames;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.textfields.TextFieldDelayedAction;
import org.abcmap.gui.utils.BrowseActionListener;
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

        // listen user actions on button
        btn.addActionListener(new BrowseActionListener(txtPath, BrowseActionListener.Type.DIRECTORY_ONLY, ()->{
            textFieldListener.run();
        }));

        // listen profile changes
        textfieldUpdater = new TextFieldUpdater();
        notifm.addEventListener(textfieldUpdater);
        configm().getNotificationManager().addObserver(this);
        textfieldUpdater.run();

        return panel;

    }

    /**
     * Update text field from configuration
     */
    private class TextFieldUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();

            String configPath = configm().getConfiguration().getValue(CFNames.DIRECTORY_IMPORT_PATH);

            // update component if needed
            updateComponentWithoutFire(txtPath, configPath);

        }

    }

    /**
     * Update import manager text field input
     */
    private class ImportManagerUpdater implements Runnable {

        @Override
        public void run() {

            String path = txtPath.getText();

            if (Utils.safeEquals(configm().getConfiguration().getValue(CFNames.DIRECTORY_IMPORT_PATH), path) == false) {
                configm().getConfiguration().updateValue(CFNames.DIRECTORY_IMPORT_PATH, path);
            }

        }

    }

}
