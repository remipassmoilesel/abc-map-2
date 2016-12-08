package org.abcmap.gui.components.help;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

/**
 * Provide a simple help on tool
 */
public class ToolHelpPanel extends JPanel {

    private NotificationManager om;
    private String messageNoHelp;

    public ToolHelpPanel() {
        super(new MigLayout("insets 5"));

        this.messageNoHelp = "<html><i>Aide indisponible.</i></html>";
    }

    public void setMessageNoHelp(String messageNoHelp) {
        this.messageNoHelp = messageNoHelp;
    }

    public void constructWith(String toolid) {

        GuiUtils.addLabel("<i>" + messageNoHelp + "</i>", this);
        
        /*

        // Pas d'action dans l'EDT
        GuiUtils.throwIfNotOnEDT();

        // tout enlever
        removeAll();

        // outil non déterminé
        if (tc == null) {
            // ajout de la description
            GuiUtils.addLabel("<i>Veuillez sélectionner un outil.</i>", this);
            refresh();
            return;
        }

        // recuperer les interactions
        InteractionSequence[] inters = tc.getInteractions();

        // interactions absentes
        if (inters == null) {

            refresh();
            return;
        }

        // construire le panneau d'aide
        InteractionHelpPanel interPanel = new InteractionHelpPanel(
                tc.getReadableName(), inters);
        add(interPanel, "width max");

        */

        refresh();

    }

    public void refresh() {
        revalidate();
        repaint();
    }

}
