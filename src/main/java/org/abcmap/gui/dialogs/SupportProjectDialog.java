package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.share.ShareButtonsPanel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupportProjectDialog extends JDialog {

    public SupportProjectDialog(Window parent) {
        super(parent);
        setSize(420, 550);
        setModal(true);
        setLocationRelativeTo(null);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setIconImage(GuiIcons.WINDOW_ICON.getImage());

        JPanel content = new JPanel(new MigLayout("insets 20"));
        setContentPane(content);

        String title = "Avez-vous réussi votre carte ?";
        setTitle(title);
        GuiUtils.addLabel(title, content, "wrap", GuiStyle.DIALOG_TITLE_1);

        // intro
        String intro = "Abc-Map est un logiciel libre créé dans un esprit de partage. Pour soutenir ce projet, "
                + "notez votre expérience et partagez la autour de vous !";

        GuiUtils.addLabel(intro, content, "wrap", GuiStyle.DIALOG_INTRO);

        // vote
        GuiUtils.addLabel("J'ai réussi ma carte:", content, "wrap",
                GuiStyle.DIALOG_TITLE_2);

        JPanel votePanel = new JPanel(new MigLayout("insets 10, gap 10"));
        ImageIcon[] voteIcons = new ImageIcon[]{GuiIcons.VOTE_0,
                GuiIcons.VOTE_1, GuiIcons.VOTE_2};

        for (ImageIcon icon : voteIcons) {
            votePanel.add(new JButton(icon));
        }

        content.add(votePanel, "align center, wrap");

        // réseaux sociaux
        GuiUtils.addLabel("Partager mon expérience: ", content,
                "align center, gaptop 15px, wrap", GuiStyle.DIALOG_TITLE_2);

        ShareButtonsPanel sharePanel = new ShareButtonsPanel();
        content.add(sharePanel, "align center, wrap");

        JButton closeButton = (JButton) GuiStyle.applyStyleTo(
                GuiStyle.DIALOG_TITLE_2, new JButton("Fermer"));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SupportProjectDialog.this.dispose();
            }
        });
        add(closeButton, "align center");

        content.revalidate();
        content.repaint();

    }
}
