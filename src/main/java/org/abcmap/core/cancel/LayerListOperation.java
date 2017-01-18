package org.abcmap.core.cancel;

import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;

/**
 * Created by remipassmoilesel on 17/01/17.
 */
public class LayerListOperation extends AbstractUndoableOperation {

    private final LayerListOperationType type;
    private final Project project;
    private final AbmAbstractLayer layer;

    public enum LayerListOperationType {
        LAYER_ADDED,
        LAYER_REMOVED,
    }

    public LayerListOperation(LayerListOperationType type, Project p, AbmAbstractLayer lay) {
        this.type = type;
        this.project = p;
        this.layer = lay;

        // add a layer
        if (LayerListOperationType.LAYER_ADDED.equals(type)) {
            setPresentationName("Ajouter une couche");
        }

        // remove a layer
        else if (LayerListOperationType.LAYER_ADDED.equals(type)) {
            setPresentationName("Supprimer une couche");
        }

    }

    @Override
    public void undo() throws UndoRedoException {

        // check if project is the same
        checkProject(project);

        // layer was added, remove it
        if (LayerListOperationType.LAYER_ADDED.equals(type)) {
            project.removeLayer(layer);
            project.setFirstLayerActive();
        }

        // layer was removed, add it
        else if (LayerListOperationType.LAYER_REMOVED.equals(type)) {
            project.addLayer(layer);
            project.setActiveLayer(layer);
        }

        projectm().fireLayerListChanged();

    }

    @Override
    public void redo() throws UndoRedoException {

        // check if project is the same
        checkProject(project);

        // layer was added, re-add it
        if (LayerListOperationType.LAYER_ADDED.equals(type)) {
            project.addLayer(layer);
            project.setActiveLayer(layer);
        }

        // layer was removed, re-remove it
        else if (LayerListOperationType.LAYER_ADDED.equals(type)) {
            project.removeLayer(layer);
            project.setFirstLayerActive();
        }

        projectm().fireLayerListChanged();
    }
}
