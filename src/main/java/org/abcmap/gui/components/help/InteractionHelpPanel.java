package org.abcmap.gui.components.help;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

/**
 * This panel provide a simple help about possible interactions with software like click, CTRL click, ...
 */
public class InteractionHelpPanel extends JPanel {

    private InteractionSequence[] interSequences;
    private String title;

    public InteractionHelpPanel(String title, InteractionSequence[] inters) {
        super(new MigLayout("insets 5, width max"));

        this.title = title;

        this.interSequences = inters;

        reconstruct();
    }

    private void reconstruct() {

        removeAll();

        // nothing to show, stop
        if (interSequences == null) {
            throw new IllegalArgumentException("Null interactions");
        }

        GuiUtils.addLabel(title, this, "span, gapbottom 10px, wrap", GuiStyle.TOOL_HELP_TITLE);

        for (InteractionSequence inter : interSequences) {

            for (Interaction t : inter.getSequence()) {
                JLabel label = new JLabel(t.getIcon());
                label.setToolTipText(t.getToolTipText());
                add(label);
            }

            GuiUtils.addLabel(inter.getDescription(), this, "gapleft 5px, gapbottom 10px, span, wrap");

        }

        revalidate();
        repaint();

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInteractions(InteractionSequence[] interactions) {
        this.interSequences = interactions;
    }

}
