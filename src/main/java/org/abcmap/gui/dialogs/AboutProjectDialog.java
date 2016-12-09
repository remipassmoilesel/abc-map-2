package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.components.share.DonateButtonsPanel;
import org.abcmap.gui.components.share.ShareButtonsPanel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutProjectDialog extends JDialog {

    public AboutProjectDialog(Window parent) {
        super(parent);

        setModal(true);
        setUndecorated(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setSize(new Dimension(300, 500));
        setLocationRelativeTo(null);

        JPanel cp = new JPanel(new MigLayout());

        GuiUtils.addLabel("A propos d'Abc-Map", cp, "gaptop 10px, wrap 10px,", GuiStyle.DIALOG_TITLE_1);

        GuiUtils.addLabel("Ce programme à été conçu grâce aux technologies suivantes: "
                        + "<ul><li>ImageJ du NIH,</li>" + "<li>Geotools,</li>"
                        + "<li>Java,</li>" + "</ul>", cp,
                "gaptop 10px, wrap 10px,", GuiStyle.DIALOG_TEXT);

        GuiUtils.addLabel("Faites connaitre le projet: ", cp,
                "align center, gaptop 15px, wrap", GuiStyle.DIALOG_TITLE_2);

        ShareButtonsPanel sharePanel = new ShareButtonsPanel();
        cp.add(sharePanel, "align center, wrap");

        DonateButtonsPanel dbp = new DonateButtonsPanel();
        cp.add(dbp, "align center, wrap");

        HtmlButton closeBtn = new HtmlButton("Fermer");
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        cp.add(closeBtn, "align center, wrap");

        setContentPane(cp);

        pack();

    }

}
