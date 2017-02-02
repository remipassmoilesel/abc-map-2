package org.abcmap.ielements.importation.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.dialogs.simple.SimpleFileFilter;
import org.abcmap.gui.utils.BrowseActionListener;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SelectSpreadsheetToImport extends InteractionElement {

    private JTextField txtPath;

    public SelectSpreadsheetToImport() {
        label = "Chemin du classeur à importer";
        help = "Sélectionnez ci-dessous le classeur de données à importer.";
    }

    @Override
    protected Component createPrimaryGUI() {

        // main panel
        JPanel panel = new JPanel(new MigLayout("insets 0"));

        // text fielf for path
        this.txtPath = new JTextField();
        panel.add(txtPath, "width 200px!, wrap");

        // browse button
        JButton btn = new JButton("Parcourir");
        SimpleFileFilter fileFilter = new SimpleFileFilter(importm().getValidExtensionsForSpreadsheets(), "*.csv, *.xls files");
        btn.addActionListener(new BrowseActionListener(BrowseActionListener.Type.FILES_ONLY, txtPath, fileFilter, () -> {

        }));
        panel.add(btn, "align right, ");

        //TextFieldUpdater txtUpdater = new TextFieldUpdater();
        //notifm.setDefaultUpdatableObject(txtUpdater);
        configm().getNotificationManager().addObserver(this);


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
