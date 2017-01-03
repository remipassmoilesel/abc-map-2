package org.abcmap.ielements.copy;

import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;

import java.util.Arrays;

public abstract class AbstractCopyToClipboard extends InteractionElement {

    public static final String COPY_MODE = "COPY_MODE";
    public static final String CUT_MODE = "CUT_MODE";
    private static final String[] availableModes = new String[]{COPY_MODE,
            CUT_MODE};

    private String mode;

    public AbstractCopyToClipboard(String mode) {

        if (Arrays.asList(availableModes).contains(mode) == false)
            throw new IllegalArgumentException("Unknown mode: " + mode);

        this.mode = mode;

        if (COPY_MODE.equals(mode)) {
            this.label = "Copier";
            this.help = "Cliquez ici pour copier les objets sélectionnés dans le presse-papier.";
            this.menuIcon = GuiIcons.SMALLICON_COPY;
            this.accelerator = Main.getShortcutManager().COPY;
        } else if (CUT_MODE.equals(mode)) {
            this.label = "Couper";
            this.help = "Cliquez ici pour couper les objets sélectionnés et les déplacer dans le presse-papier.";
            // this.menuIcon = GuiIcons.cut;
            this.accelerator = Main.getShortcutManager().CUT;
        }

    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// Verifier le projet et obtenir le calque actif, ou afficher un message
		// d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// reclamer le focus sur la carte
		mapm.requestFocusOnMap();

		// liste des elements à copier ou a coller
		ArrayList<LayerElement> toCopy = new ArrayList<LayerElement>();
		ArrayList<LayerElement> toRemove = new ArrayList<LayerElement>();

		// iteration des elements
		for (LayerElement e : layer.getAllElements()) {
			if (e.isSelected()) {
				toCopy.add(e.duplicate());
				toRemove.add(e);
			}
		}

		// AnalyseMode couper: retirer les elements de leur calque d'origine
		if (CUT_MODE.equals(mode)) {
			for (LayerElement e : toRemove) {
				layer.removeElement(e);
			}
		}

		// envoi
		if (toCopy.size() > 0) {

			if (CUT_MODE.equals(mode)) {
				// enregistrement pour annulation
				ElementsCancelOp op = cancelm.addDrawOperation(layer, toCopy);
				op.elementsHaveBeenDeleted(true);
			}

			clipboardm.copyElementsToClipboard(toCopy);
			projectm.fireElementsChanged();
		}

*/
    }

}
