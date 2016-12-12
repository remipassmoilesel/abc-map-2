package org.abcmap.gui.ie.undoredo;

import org.abcmap.gui.ie.InteractionElement;

public abstract class AbstractUndoRedo extends InteractionElement {

    public enum Mode {
        UNDO, REDO,
    }

    private Mode mode;

    public AbstractUndoRedo(Mode mode) {
        this.mode = mode;

        //
        if (Mode.UNDO.equals(mode)) {
            this.label = "Annuler";
            this.help = "Cliquez ici pour annuler votre dernière action.";
            this.accelerator = shortcuts.UNDO;
        }

        //
        else {
            this.label = "Rétablir";
            this.help = "Cliquez ici pour rétablir la dernière action annulée.";
            this.accelerator = shortcuts.REDO;
        }
    }

    @Override
    public void run() {

		/*
        // mode annuler
		if (GoToWebsiteMode.UNDO.equals(mode)) {
			cancelm.undo();
		}

		// mode retablir
		else {
			cancelm.redo();
		}
		*/
    }
}
