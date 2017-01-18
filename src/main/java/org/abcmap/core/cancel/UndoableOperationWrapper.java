package org.abcmap.core.cancel;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

/**
 * Wrap an Abcmap undoable action in a Swing undoable action
 */
public class UndoableOperationWrapper extends AbstractUndoableEdit {

    private static final CustomLogger logger = LogManager.getLogger(UndoableOperationWrapper.class);

    private final AbstractUndoableOperation wrappedAction;

    public UndoableOperationWrapper(AbstractUndoableOperation action) {
        this.wrappedAction = action;
    }

    @Override
    public void undo() throws javax.swing.undo.CannotUndoException {
        super.undo();

        try {
            wrappedAction.undo();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        try {
            wrappedAction.redo();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public String getUndoPresentationName() {
        return "Annuler '" + wrappedAction.getPresentationName() + "'";
    }

    @Override
    public String getRedoPresentationName() {
        return "Refaire '" + wrappedAction.getPresentationName() + "'";
    }


}
