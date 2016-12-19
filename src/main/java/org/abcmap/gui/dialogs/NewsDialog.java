package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.ielements.ressources.GoToWebSite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewsDialog extends JDialog {

    private JEditorPane editorPane;

    public NewsDialog(Window parent) {
        super(parent);

        setTitle("Abc-Map version " + ConfigurationConstants.SOFTWARE_VERSION);
        this.setSize(new Dimension(500, 400));
        this.setLocationRelativeTo(parent);
        this.setModal(true);

        JPanel mainPanel = new JPanel(new MigLayout());

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        mainPanel.add(editorPane);

        JButton btnGoTo = new JButton("Visiter le site");
        btnGoTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                GoToWebSite gtw = new GoToWebSite();
                gtw.actionPerformed(e);
            }
        });

        JButton btnCancel = new JButton("Fermer cette fenêtre");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        mainPanel.add(btnGoTo);
        mainPanel.add(btnCancel);

        setContentPane(mainPanel);
        pack();
    }

    public void setContent(String t) {
        editorPane.setText(t);
    }

}
