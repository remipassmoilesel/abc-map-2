package org.abcmap.ielements.importation.directory;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.textfields.TextFieldDelayedAction;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SelectDirectoryToImport extends InteractionElement {

    private JTextField txtPath;
    private ImportManagerUpdater textFieldListener;
    //private TextFieldUpdater textfieldUpdater;
    private Runnable memoryPanelUpdater;

    public SelectDirectoryToImport() {
        this.label = "Chemin du dossier d'images";
        this.help = "Entrez le chemin d'un dossier où importer des images.";
    }

    @Override
    protected Component createPrimaryGUI() {

        // create GUI
        this.txtPath = new JTextField();
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.add(txtPath, "width 80%!, wrap");

        JButton btn = new JButton("Parcourir");
        panel.add(btn, "align right, " + wrap15());

        this.textFieldListener = new ImportManagerUpdater();
        /*


        this.memoryPanelUpdater = new MemoryPanelUpdater();

        TextFieldDelayedAction.delayedActionFor(txtPath, memoryPanelUpdater, false);

        TextFieldDelayedAction.delayedActionFor(txtPath, textFieldListener, false);


        btn.addActionListener(new BrowsePathActionListener(txtPath, false, true));


        textfieldUpdater = new TextFieldUpdater();
        notifm.setDefaultUpdatableObject(textfieldUpdater);
		configm.getNotificationManager().addObserver(this);
		importm.getNotificationManager().addObserver(this);
		textfieldUpdater.run();

        ThreadManager.runLater(new Runnable() {
            @Override
            public void run() {
                //memoryPanelUpdater.run();
            }
        });
        */
        return panel;
    }

    /**
     * Mettre à jour le chmaps texte en fonction du gestionnaire d'import
     *
     * @author remipassmoilesel
     *
     *
    private class TextFieldUpdater implements NotificationListener, Runnable {


    @Override public void notificationReceived(Event arg) {
    SwingUtilities.invokeLater(this);
    }

    @Override public void run() {

    // pas d'action hors de l'EDT
    GuiUtils.throwIfNotOnEDT();

    GuiUtils.changeText(txtPath, configm.getDirectoryImportPath());
    }

    }

    /**
     * Mettre à jour le panneau d'indicateur de charge de mémoire
     *
     * @author remipassmoilesel
     *
     *
    private class MemoryPanelUpdater implements Runnable {

    private static final int FILES_TO_ANALYSE_AS_SAMPLE = 3;

    private static final float COVERING_PERCENT = 0.1f;

     @Override public void run() {

     // pas d'action hors de l'EDT
     GuiUtils.throwIfOnEDT();

     // fichier du repertoire
     File directory = new File(txtPath.getText());

     // le fichier est invalide, retour
     if (directory.isDirectory() == false) {
     setMemoryValues(0);
     return;
     }

     // lister les fichiers disponibles
     ArrayList<File> files;
     try {
     files = importm.getAllValidPicturesFrom(directory);
     } catch (IOException e) {
     setMemoryValues(0);
     return;
     }

     // compter les fichiers disponibles
     int fileNbr = files.size();

     // dimensions pour une seule image
     double estimatedWidth = 0d;
     double estimatedHeight = 0d;

     // le recadrage est activé, prendre en compte la taille de recadrage
     if (configm.isCroppingEnabled()) {

     Rectangle rect = configm.getCropRectangle();
     estimatedWidth = (int) (rect.width - rect.width
      * COVERING_PERCENT);
     estimatedHeight = (int) (rect.height - rect.height
      * COVERING_PERCENT);
     }

     // le recadrage est desactivé, prendre en compte les n premieres
     // images
     else {

     int i = 0;
     int sumW = 0;
     int sumH = 0;
     for (; i < FILES_TO_ANALYSE_AS_SAMPLE && i < files.size(); i++) {
     try {
     Dimension dim = Utils.getImageDimensions(files.get(i));
     sumW += dim.width;
     sumH += dim.height;
     } catch (IOException e) {
     Log.error(e);
     }
     }

     int avgW = sumW / i;
     int avgH = sumH / i;

     estimatedWidth = (int) (avgW - avgW * COVERING_PERCENT);
     estimatedHeight = (int) (avgH - avgH * COVERING_PERCENT);
     }

     // calculer la taille totale
     double valueMp = estimatedWidth * estimatedHeight * fileNbr
     / 1000000d;

     setMemoryValues(valueMp);
     }

     private void setMemoryValues(final double val) {
     SwingUtilities.invokeLater(new Runnable() {
     public void run() {
     memPanel.setIndicationFor(val);
     memPanel.reconstruct();
     }
     });
     }
     }

     */
    /**
     * Mettre à jour le gestionnaire d'import en fonction de la saisie
     *
     * @author remipassmoilesel
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
