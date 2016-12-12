package org.abcmap.gui.ie.importation.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class CreateDataFile extends InteractionElement {

    private JRadioButton rdCreateNew;
    private JRadioButton rdConvertFile;

    public CreateDataFile() {
        label = "Créer un fichier de données";
        help = "Vous pouvez dans cet élément créer un fichier de donnée exemple, ou "
                + "ouvrir un fichier existant et le convertir en fichier de données.";

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        rdCreateNew = new JRadioButton("Créer un nouveau fichier");
        rdCreateNew.setSelected(true);
        panel.add(rdCreateNew, "wrap");

        // bouton convertir un fichier
        rdConvertFile = new JRadioButton("Convertir un fichier en liste");
        panel.add(rdConvertFile, "wrap");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdConvertFile);
        bg.add(rdCreateNew);

        // bouton d'action
        JButton button = new JButton("O.K.");
        button.addActionListener(this);

        panel.add(button, "align right");

        // creer et retourner l'element
        return panel;
    }

    /*

    @Override
    public void run() {


		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// creer le fichier
		File file = null;
		try {

			// a partir d'un autre fichier
			if (rdConvertFile.isSelected()) {
				file = convertFile();
			}

			// en temps qu'exemple
			else {
				file = createExampleFile();
			}

			// l'utilisateur à annulé
			if (file == null) {
				threadAccess.releaseAccess();
				return;
			}

		} catch (Exception e) {
			Log.error(e);

			// message d'erreur
			String message = "Erreur lors de la création du fichier.";
			if (e instanceof DataImportException) {

				if (DataImportException.UNKNOWN_FORMAT.equals(e.getMessage())) {
					message = "Impossible de convertir ce type de fichier.";
				}

				else if (DataImportException.DATAS_TOO_HEAVY.equals(e
						.getMessage())) {
					message = "Le fichier source est trop volumineux.";
				}

			}
			guim.showErrorInBox(message);

			// arret
			threadAccess.releaseAccess();
			return;
		}

		// ouvrir le fichier dans l'editeur du systeme
		try {
			Desktop.getDesktop().open(file);
		}

		catch (IOException e) {
			guim.showInformationTextFieldDialog(guim.getMainWindow(),
					"Impossible d'ouvrir le fichier d'exemple. Vous pourrez néanmoins le trouver "
							+ "à l'emplacement: ", file.getAbsolutePath());
		}

		// mettre à jour le chemin d'import
		configm.setDataImportPath(file.getAbsolutePath());



	}

	private File createExampleFile() throws IOException {

		// trouver un nom pour le fichier d'exemple
		String home = configm.getConfiguration().HOME;
		File file = null;
		do {
			file = Paths.get(home,
					"list_" + Utils.getDate("yyyy-mm-dd-S") + ".csv").toFile();
		}

		while (file.isFile());

		// ecrire le fichier
		CsvDataWriter.saveExampleAt(file);

		return file;
	}

	private File convertFile() throws IOException, DataImportException {

		// parcourir le fichier à ouvrir
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog.browseFileToOpenAndWait(
				parent, null);

		// l'utilisateur à annulé, fin
		if (result.getReturnVal().equals(BrowseDialogResult.CANCEL)) {
			return null;
		}

		// le fichier à ouvrir
		File source = result.getFile();

		// chercher un parser pour le fichier
		String ext = Utils.getExtension(source.getName());
		AbstractDataParser parser = AbstractDataParser.getParserFor(ext);

		// pas de parser disponible, message puis arret
		if (parser == null) {
			throw new DataImportException(DataImportException.UNKNOWN_FORMAT);
		}

		// trouver un nom pour le fichier destination
		String home = configm.getConfiguration().HOME;
		File destination = null;
		do {
			destination = Paths.get(home,
					"list_" + Utils.getDate("yyyy-mm-dd-S") + ".csv").toFile();
		}

		while (destination.isFile());

		// ecrire le fichier
		DataEntryList datas = parser.parseFile(source);
		CsvDataWriter writer = new CsvDataWriter();
		writer.write(datas, destination);

		return destination;

	}

	*/

}
