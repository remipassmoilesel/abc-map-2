package org.abcmap.gui.ie.edition;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public class Duplicate extends InteractionElement {

    /**
     * Interval en pixel entre les anciens et les nuveaux elements
     */
    private static final int INTERVAL_BETWEEN_ELEMENTS = 30;

    public Duplicate() {
        label = "Dupliquer";
        help = "Cliquez ici pour dupliquer les éléments sélectionner sur la carte.";
        menuIcon = GuiIcons.SMALLICON_DUPLICATE;
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

        // recherche des elements et duplication
        ArrayList<LayerElement> toDuplicate = new ArrayList<LayerElement>();
        ArrayList<LayerElement> newElmts = new ArrayList<LayerElement>();
        for (LayerElement elmt : layer.getAllElements()) {
            if (elmt.isSelected()) {
                toDuplicate.add(elmt);
            }
        }

        projectm.setAllElementsSelected(false);

        // duplication + ajout
        for (LayerElement e : toDuplicate) {

            LayerElement ne = e.duplicate();

            // nvelle position
            Point np = ne.getPosition();
            np.x += INTERVAL_BETWEEN_ELEMENTS;
            np.y += INTERVAL_BETWEEN_ELEMENTS;
            ne.setPosition(np);

            // selectionner
            ne.setSelected(true);

            // ajout au calque
            layer.addElement(ne, false);

            // enregistrement pour annul
            newElmts.add(ne);
        }

        // enregistrement de l'operation pour annulation
        ElementsCancelOp op = MainManager.getCancelManager().addDrawOperation(
                layer, newElmts);
        op.elementsHaveBeenAdded(true);

        // notification
        projectm.fireElementsChanged();

        */
    }

}
