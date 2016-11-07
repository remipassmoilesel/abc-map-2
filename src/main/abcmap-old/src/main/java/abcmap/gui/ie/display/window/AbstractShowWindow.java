package abcmap.gui.ie.display.window;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.InteractionElement;

public abstract class AbstractShowWindow extends InteractionElement {

	protected enum Mode {
		MAIN, MANUAL_IMPORT, AUTO_IMPORT
	}

	private Mode action;

	public AbstractShowWindow(Mode action) {

		hiddenInDetachedWIndows = true;

		if (Mode.MAIN.equals(action)) {
			label = "Afficher la fenêtre principale";
			help = "Cliquez ici pour afficher seulement la fenêtre principale du programme.";
			menuIcon = GuiIcons.WINDOW_ICON;
		}

		else if (Mode.MANUAL_IMPORT.equals(action)) {
			label = "Afficher la fenêtre d'import manuel";
			help = "Cliquez ici pour afficher seulement la fenêtre d'import manuel du programme.";
		}

		else if (Mode.AUTO_IMPORT.equals(action)) {
			label = "Afficher la fenêtre d'import automatique";
			help = "Cliquez ici pour afficher seulement la fenêtre d'import automatique du programme.";
		}

		this.action = action;

	}

	@Override
	public Component createPrimaryGUI() {

		JButton btn = new JButton("Afficher");
		btn.addActionListener(this);

		JPanel panel = new JPanel();
		panel.add(btn);

		return panel;

	}

	@Override
	public void run() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				if (Mode.MAIN.equals(action)) {
					guim.showOnlyWindow(guim.getMainWindow());
				}

				else if (Mode.MANUAL_IMPORT.equals(action)) {
					guim.showOnlyWindow(guim.getManualImportWindow());
				}

				else if (Mode.AUTO_IMPORT.equals(action)) {
					guim.showOnlyWindow(guim.getAutoImportWindow());
				}
			}
		});

	}

}
