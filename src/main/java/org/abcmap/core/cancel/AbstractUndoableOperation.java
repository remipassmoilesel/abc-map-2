package org.abcmap.core.cancel;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.project.Project;


/**
 * This class represent an undoable action. To create a specific undo or redo action, override this class and add it to
 * the redo manager when needed.
 * <p>
 * This class will be wrapped then to be added in Swing undo/redo system.
 */
public abstract class AbstractUndoableOperation extends ManagerTreeAccessUtil {

    protected static final CustomLogger logger = LogManager.getLogger(AbstractUndoableOperation.class);

    /**
     * Readable name of this action
     */
    private String presentationName;

    AbstractUndoableOperation() {
        this.presentationName = "no name";
    }

    /**
     * This method should produces needed actions to undo effects of this action.
     *
     * @throws UndoRedoException
     */
    public abstract void undo() throws UndoRedoException;

    /**
     * This method should produces needed actions to redo effects of this action.
     *
     * @throws UndoRedoException
     */
    public abstract void redo() throws UndoRedoException;

    /**
     * Return the readable name of this action
     *
     * @return
     */
    public String getPresentationName() {
        return presentationName;
    }

    /**
     * Set the readable name of this action
     *
     * @param presentationName
     */
    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    /**
     * Throw an exception if project is not initialized or if project is not the same as provided
     *
     * @param p
     * @throws UndoRedoException
     */
    protected void checkProject(Project p) throws UndoRedoException {
        if (Main.projectm().isInitialized() == false) {
            throw new UndoRedoException("Project not initialized !");
        }
        if (Main.projectm().getProject().equals(p) == false) {
            throw new UndoRedoException("Project is not the same !" + p + " / " + Main.projectm().getProject());
        }
    }
}
