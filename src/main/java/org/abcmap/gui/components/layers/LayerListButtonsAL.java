package org.abcmap.gui.components.layers;

import org.abcmap.core.managers.CancelManager;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.gui.utils.GuiUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listent user actions on layers
 */
public class LayerListButtonsAL implements ActionListener {

    public static final String NEW = "NEW";
    public static final String REMOVE = "REMOVE";

    public static final String RENAME = "RENAME";
    public static final String DUPLICATE = "DUPLICATE";
    public static final String CHANGE_VISIBILITY = "CHANGE_VISIBILITY";

    public static final String MOVE_UP = "MOVE_UP";
    public static final String MOVE_DOWN = "MOVE_DOWN";

    private String mode;
    private ProjectManager projectm;
    private CancelManager cancelm;
    private GuiManager guim;

    public LayerListButtonsAL(String mode) {
        this.mode = mode;
        this.projectm = MainManager.getProjectManager();
        this.cancelm = MainManager.getCancelManager();
        this.guim = MainManager.getGuiManager();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        GuiUtils.throwIfNotOnEDT();

        if (projectm.isInitialized() == false){
            return;
        }
/*

        MapLayer lay;
        try {
            lay = projectm.getActiveLayer();
        } catch (MapLayerException e1) {
            Log.debug(e1);
            return;
        }

        if (CHANGE_VISIBILITY.equals(mode)) {
            changeLayerVisibility(lay);
        }

        else if (NEW.equals(mode)) {
            createNewLayer();
        }

        else if (MOVE_UP.equals(mode)) {
            moveLayer(lay, +1);
        }

        else if (MOVE_DOWN.equals(mode)) {
            moveLayer(lay, -1);
        }

        else if (REMOVE.equals(mode)) {
            removeLayer(lay);
        }

        else if (DUPLICATE.equals(mode)) {
            duplicateLayer(lay);
        }

        else if (RENAME.equals(mode)) {
            renameLayer(lay);
        }
*/

    }

    private void changeLayerVisibility(AbstractLayer lay) {

/*        lay.getMementoManager().saveStateToRestore();
        lay.setVisible(!lay.isVisible());
        lay.getMementoManager().saveStateToRedo();

        cancelm.addMapLayerOperation(lay);

        projectm.fireLayerListChanged();*/

    }

    private void moveLayer(AbstractLayer lay, int step) {

       /* 
       ArrayList<MapLayer> layers = projectm.getLayers();
        int index = layers.indexOf(lay) + step;

        if (index >= 0 && index < layers.size()) {

            projectm.getMementoManager().saveStateToRestore();

            projectm.getProject().setNotificationsEnabled(false);

            try {
                projectm.removeLayer(lay);
                projectm.addLayer(lay, index);
            } catch (MapLayerException e) {
                Log.debug(e);
            }

            projectm.getProject().setNotificationsEnabled(true);

            projectm.fireLayerListChanged();

            projectm.getMementoManager().saveStateToRedo();

            cancelm.addProjectListsOperation();
        }
*/
    }

    private void removeLayer(AbstractLayer lay) {
/*

        // enregistrer l'opération
        MapLayerCancelOp op = cancelm.addMapLayerOperation(lay);
        op.layerHaveBeenDeleted(true);

        // supprimer le calque puis choisir le prochain
        projectm.removeLayer(lay);

        // trouver un nouvel index de calque actif
        int index = projectm.getLayers().size() - 1;
        if (index < 0)
            index = 0;

        // changer le calque actif
        try {
            projectm.setActiveLayer(index);
        } catch (MapLayerException e) {
            Log.debug(e);
        }
*/

    }

    private void createNewLayer() {

     /*
        MapLayer lay = projectm.addNewLayer();

        try {
            projectm.setActiveLayer(lay);
        } catch (MapLayerException e) {
            Log.debug(e);
        }

        MapLayerCancelOp op = cancelm.addMapLayerOperation(lay);
        op.layerHaveBeenAdded(true);*/

    }

    private void duplicateLayer(final AbstractLayer lay) {

       /*
        ThreadManager.runLater(new Runnable() {
            @Override
            public void run() {

                 MapLayer newLay = lay.duplicate();

                newLay.setName(lay.getName() + " (copie)");

                projectm.addLayer(newLay);

                try {
                    projectm.setActiveLayer(newLay);
                } catch (MapLayerException e) {
                    Log.debug(e);
                }

                MapLayerCancelOp op = cancelm.addMapLayerOperation(newLay);
                op.layerHaveBeenAdded(true);

            }
        });*/
    }

    private void renameLayer(AbstractLayer lay) {

       /*

        String name = lay.getName();


        RenameLayerDialog dial = new RenameLayerDialog(guim.getMainWindow(), name);
        dial.setVisible(true);


        if (RenameLayerDialog.ACTION_VALIDATED.equals(dial.getAction())) {


            lay.getMementoManager().saveStateToRestore();


            lay.setName(dial.getInput());


            lay.getMementoManager().saveStateToRedo();
            cancelm.addMapLayerOperation(lay);

        }*/

    }

}
