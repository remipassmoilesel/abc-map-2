package abcmap.gui.comps.fileselection;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import abcmap.gui.GuiColors;
import abcmap.managers.GuiManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

/**
 * Objet de rendu d'une liste de calque pour une JList
 * 
 * @author remipassmoilesel
 *
 */
public class FileSelectionRenderer extends JLabel implements ListCellRenderer<File> {

	private ProjectManager projectm;
	private GuiManager guim;

	private Color fileExistColor;
	private Color fileNotExistColor;
	private Color selectedColor;

	public FileSelectionRenderer() {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		this.projectm = MainManager.getProjectManager();
		this.guim = MainManager.getGuiManager();

		// couleurs
		this.fileNotExistColor = new Color(55, 0, 0);
		this.fileExistColor = new Color(0, 0, 200);
		this.selectedColor = GuiColors.FOCUS_COLOR_BACKGROUND.brighter().brighter();

		// caracteristiques du label
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends File> list, File file, int index, boolean isSelected,
			boolean cellHasFocus) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// afficher le nom du fichier
		setText(file.getName());

		// le fichier existe
		if (file.isFile()) {
			setForeground(fileExistColor);
			setToolTipText(file.getAbsolutePath());
		}

		// le fichier n'existe pas
		else {
			setForeground(fileNotExistColor);
			setToolTipText("Ce fichier n'existe pas: " + file.getAbsolutePath());
		}

		// couleur de selection
		if (file.equals(list.getSelectedValue())) {
			setBackground(selectedColor);
		}

		else {
			setBackground(list.getBackground());
		}

		return this;

	}

}
