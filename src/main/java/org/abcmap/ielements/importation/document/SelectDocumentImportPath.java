package org.abcmap.ielements.importation.document;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SelectDocumentImportPath extends InteractionElement {

    private JTextField txtPath;
//    private ParameterListener paramListener;
//    private Updater formUpdater;

    public SelectDocumentImportPath() {
        this.label = "Chemin du document à importer";
        this.help = "Sélectionnez ici le chemin d'un document à importer.";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        this.txtPath = new JTextField();

//        paramListener = new ParameterListener();
//		TextFieldDelayedAction.delayedActionFor(txtPath, paramListener, false);

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        panel.add(txtPath, "width 200px!, wrap");

        JButton btn = new JButton("Parcourir");
//		btn.addActionListener(new BrowsePathActionListener(txtPath, true, true));
        panel.add(btn, "align right,");

		/*
        formUpdater = new Updater();
		notifm.setDefaultUpdatableObject(formUpdater);
		importm.getNotificationManager().addObserver(this);
		configm.getNotificationManager().addObserver(this);

		formUpdater.run();

		*/

        return panel;
    }

    /*
    private class Updater extends FormUpdater {

        @Override
        protected void updateFields() {
            super.updateFields();

            // mettre à jour le chemin du document
            GuiUtils.changeText(txtPath, configm.getDocumentImportPath());

        }

    }


    private class ParameterListener implements Runnable {

        @Override
        public void run() {

            // mettre à jour le chemin
            String path = txtPath.getText();

            if (Utils.safeEquals(configm.getDocumentImportPath(), path) == false) {
                configm.setDocumentImportPath(path);
            }

        }

    }
    */

}
