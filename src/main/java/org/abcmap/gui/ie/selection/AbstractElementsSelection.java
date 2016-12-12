package org.abcmap.gui.ie.selection;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public abstract class AbstractElementsSelection extends InteractionElement {

    public enum Mode {
        SELECT_ALL, UNSELECT_ALL,
    }

    private Mode mode;

    public AbstractElementsSelection(Mode mode) {

        this.mode = mode;


        if (Mode.SELECT_ALL.equals(mode)) {
            this.label = "Sélectionner tout";
            this.help = "Cliquez ici pour sélectionner tous les objets du calque actif.";
            this.accelerator = shortcuts.SELECT_ALL;
            this.menuIcon = GuiIcons.SMALLICON_SELECTALL;
        }

        //
        else {
            this.label = "Dé-sélectionner tout";
            this.help = "Cliquez ici pour dé-sélectionner tous les objets du calque actif.";
            this.accelerator = shortcuts.UNSELECT_ALL;
            this.menuIcon = GuiIcons.SMALLICON_UNSELECTALL;
        }

    }

    @Override
    public void run() {

        /*
        // pas de lancement dans l'EDT
        GuiUtils.throwIfOnEDT();

        // Verifier le projet et obtenir le calque actif, ou afficher un
        // message d'erreur
        MapLayer layer = checkProjectAndGetActiveLayer();
        if (layer == null) {
            return;
        }

        // valeur de la selection
        boolean value = mode.equals(Mode.SELECT_ALL);

        // appliquer les changements
        for (LayerElement elmt : layer.getAllElementsReversed()) {
            elmt.setSelected(value);
        }

        // notification des changements
        projectm.fireElementsChanged();

        */
    }

}
