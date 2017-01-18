package org.abcmap.ielements.undoredo;

import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

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
            this.accelerator = shortcutm().UNDO;
        }

        //
        else {
            this.label = "Rétablir";
            this.help = "Cliquez ici pour rétablir la dernière action annulée.";
            this.accelerator = shortcutm().REDO;
        }
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        // cancel operation
        if (Mode.UNDO.equals(mode)) {
            undom().undoOrShowMessage();
        }

        // redo operation
        else {
            undom().redoOrShowMessage();
        }

    }
}
