package org.abcmap.core.managers;

import org.abcmap.core.cancel.LayerListOperation;
import org.abcmap.core.cancel.UndoableOperationWrapper;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class UndoManager extends ManagerTreeAccessUtil {

    private static final CustomLogger logger = LogManager.getLogger(UndoManager.class);
    private final javax.swing.undo.UndoManager internalManager;

    public UndoManager() {
        internalManager = new javax.swing.undo.UndoManager();
    }

    public void addLayerListOperation(LayerListOperation.LayerListOperationType type, Project p, AbmAbstractLayer lay) {
        LayerListOperation op = new LayerListOperation(type, p, lay);
        internalManager.addEdit(new UndoableOperationWrapper(op));
    }

    /**
     * Undo last action if possible or show a message
     */
    public void undoOrShowMessage() {
        try {
            internalManager.undo();
        } catch (Exception e) {
            logger.debug(e);
            dialm().showErrorInBox("Impossible d'annuler l'action");
        }
    }

    /**
     * Redo last action if possible or show a message
     */
    public void redoOrShowMessage() {
        try {
            internalManager.redo();
        } catch (Exception e) {
            logger.debug(e);
            dialm().showErrorInBox("Impossible de refaire l'action");
        }
    }

}
