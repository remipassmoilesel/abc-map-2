package org.abcmap.gui.ie.layouts;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class ModifyLayouts extends InteractionElement {

    private static final String NEW_PAPER = "NEW_PAPER";
    private static final String MOVE_PAPERS_UP = "MOVE_PAPERS_UP";
    private static final String MOVE_PAPERS_DOWN = "MOVE_PAPERS_DOWN";
    private static final String REMOVE_PAPERS = "REMOVE_PAPERS";
    private static final String CENTER_PAPERS = "CENTER_PAPERS";

    public ModifyLayouts() {
        this.label = "Modifier les feuilles de mise en page";
        this.help = "Modifiez ci-dessous les feuilles de mise en pages: ajoutez, "
                + "supprimez, changez l'ordre ...";

        this.displaySimplyInSearch = false;
        this.displayInHideableElement = false;
    }

    @Override
    public Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout());

        HtmlButton buttonAdd = new HtmlButton("Ajouter une feuille");

        HtmlButton buttonRemove = new HtmlButton(
                "Supprimer les feuilles sélectionnées");

        HtmlButton buttonMoveUp = new HtmlButton(
                "Feuilles sélectionnées vers le haut");

        HtmlButton buttonMoveBottom = new HtmlButton(
                "Feuilles sélectionnées vers le bas");

        HtmlButton buttonCenter = new HtmlButton(
                "Centrer les feuilles sélectionnées");

        HtmlButton[] btns = new HtmlButton[]{buttonAdd, buttonRemove,
                buttonMoveUp, buttonMoveBottom, buttonCenter};
        String[] cmds = new String[]{NEW_PAPER, REMOVE_PAPERS,
                MOVE_PAPERS_UP, MOVE_PAPERS_DOWN, CENTER_PAPERS};

        for (int i = 0; i < btns.length; i++) {

            HtmlButton btn = btns[i];
            String cmd = cmds[i];

            btn.addActionListener(this);
            btn.setActionCommand(cmd);
            panel.add(btn, "width 200px, wrap");
        }

        return panel;
    }

    @Override
    public void run() {

		/*
        GuiUtils.throwIfOnEDT();

		String action = getLastActionCommand();
		if (action == null) {
			return;
		}

		// nouvelle feuille
		if (NEW_PAPER.equals(action)) {

			// désactiver toutes les layouts
			projectm.setAllLayoutsActive(false);

			// Créer la feuille
			LayoutPaper lay = projectm.addNewLayout();

			// activer la nouvelle
			lay.setActive(true);

			// notification
			projectm.fireLayoutListChanged();

		}

		// supprimer les feuilles actives
		else if (REMOVE_PAPERS.equals(action)) {

			for (LayoutPaper p : projectm.getLayouts()) {
				if (p.isActive()) {
					projectm.removeLayout(p);
				}
			}

			// pas besoin de notification supplémentaire ici
			// projectm.fireLayoutListChanged();

		}

		// supprimer les feuilles actives
		else if (MOVE_PAPERS_UP.equals(action)
				|| MOVE_PAPERS_DOWN.equals(action)) {

			// desactiver les notifications
			projectm.getProject().setNotificationsEnabled(false);

			ArrayList<LayoutPaper> layouts = projectm.getLayouts();
			for (int i = 0; i < layouts.size(); i++) {
				LayoutPaper p = layouts.get(i);
				if (p.isActive()) {
					projectm.removeLayout(p);
					try {
						int newIndex = i;
						if (MOVE_PAPERS_UP.equals(action)) {
							newIndex--;
						} else {
							newIndex++;
						}

						if (newIndex < 0)
							newIndex = 0;
						if (newIndex > layouts.size() - 1)
							newIndex = layouts.size() - 1;

						projectm.addLayout(p, newIndex);
					} catch (LayoutPaperException e) {
						Log.debug(e);
					}
				}
			}

			// activer à nouveau les notifs
			projectm.getProject().setNotificationsEnabled(true);

			projectm.fireLayoutListChanged();

		}

		// centrer les feuilles
		else if (CENTER_PAPERS.equals(action)) {

			for (LayoutPaper p : projectm.getLayouts()) {
				if (p.isActive()) {
					p.setPositionOnMapCenter();
				}
			}

			// notification
			projectm.fireLayoutListChanged();

		}

		*/

    }
}
