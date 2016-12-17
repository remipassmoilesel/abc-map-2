package org.abcmap.gui.ie.layouts;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layouts.LayoutSheet;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

public class ModifyLayouts extends InteractionElement {

    private static final String NEW_SHEET = "NEW_SHEET";
    private static final String MOVE_SHEET_UP = "MOVE_SHEET_UP";
    private static final String MOVE_SHEET_DOWN = "MOVE_SHEET_DOWN";
    private static final String REMOVE_SHEET = "REMOVE_SHEET";
    private static final String CENTER_MAP_IN_SHEET = "CENTER_MAP_IN_SHEET";
    private static final String REMOVE_ALL = "REMOVE_ALL";

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

        HtmlButton buttonRemove = new HtmlButton("Supprimer les feuilles sélectionnées");

        HtmlButton buttonMoveUp = new HtmlButton("Feuilles sélectionnées vers le haut");

        HtmlButton buttonMoveBottom = new HtmlButton("Feuilles sélectionnées vers le bas");

        HtmlButton buttonCenter = new HtmlButton("Centrer les feuilles sélectionnées");

        HtmlButton removeAll = new HtmlButton("Supprimer toutes les feuilles");

        // construct panel and buttons
        HtmlButton[] btns = new HtmlButton[]{buttonAdd, buttonRemove, buttonMoveUp, buttonMoveBottom, buttonCenter, removeAll};
        String[] cmds = new String[]{NEW_SHEET, REMOVE_SHEET, MOVE_SHEET_UP, MOVE_SHEET_DOWN, CENTER_MAP_IN_SHEET, REMOVE_ALL};
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

        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            Project project = getCurrentProjectOrShowMessage();

            String action = getLastActionCommand();
            if (action == null) {
                return;
            }

            // create a new sheet
            if (NEW_SHEET.equals(action)) {

                //projectm.setAllLayoutsActive(false);

                LayoutSheet lay = new LayoutSheet(false, 200, 300, 600, 800, 210, 290, 10, 1.5d, project.getCrs());
                project.addLayout(lay);
            }

            // remove all sheets
            else if (REMOVE_ALL.equals(action)) {
                project.removeAllLayouts();
            }

            // notification
            projectm.fireLayoutListChanged();

           /* // supprimer les feuilles actives
            else if (REMOVE_SHEET.equals(action)) {

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

            }*/
        } finally {
            releaseOperationLock();
        }

    }
}
