package org.abcmap.ielements.importation.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SelectDataToImport extends InteractionElement {

    private JTextField txtPath;
//    private ImportManagerUpdater textFieldListener;

    public SelectDataToImport() {
        label = "Chemin du fichier à importer";
        help = "Sélectionnez ci-dessous le fichier de données à importer.";
    }

    @Override
    protected Component createPrimaryGUI() {

        this.txtPath = new JTextField();

//        this.textFieldListener = new ImportManagerUpdater();

//        TextFieldDelayedAction.delayedActionFor(txtPath, textFieldListener, false);

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        panel.add(txtPath, "width 200px!, wrap");

        JButton btn = new JButton("Parcourir");
//        btn.addActionListener(new BrowsePathActionListener(txtPath, true, true));
        panel.add(btn, "align right, ");

        //TextFieldUpdater txtUpdater = new TextFieldUpdater();
        //notifm.setDefaultUpdatableObject(txtUpdater);
        configm().getNotificationManager().addObserver(this);

        // premiere mise à jour
        //txtUpdater.updateFields();

        return panel;

    }

    /*
    private class ImportManagerUpdater implements Runnable {

        @Override
        public void run() {

            String path = txtPath.getText();

            if (Utils.safeEquals(configm.getDataImportPath(), path) == false) {
                configm.setDataImportPath(path);
            }
        }

    }


    private class TextFieldUpdater extends FormUpdater {

        @Override
        protected void updateFields() {
            super.updateFields();

            // pas d'action hors de l'EDT
            GuiUtils.throwIfNotOnEDT();

            GuiUtils.changeText(txtPath, configm.getDataImportPath());
        }

    }

    */
}
