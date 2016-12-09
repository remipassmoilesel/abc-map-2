package org.abcmap.gui.menu;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.components.buttons.HtmlMenuItem;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Display interaction element in a dialog box
 */
public class DialogMenuItem extends HtmlMenuItem {

    private JDialog dialog;
    private GuiManager guim;

    public DialogMenuItem(InteractionElement elmt) {

        super(elmt.getLabel());

        guim = MainManager.getGuiManager();

        setIcon(elmt.getMenuIcon());
        setAccelerator(elmt.getAccelerator());
        addActionListener(new ShowDialogListener());

        dialog = new JDialog(guim.getMainWindow());
        dialog.setModal(true);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);

        Component primary = elmt.getPrimaryGUI();
        if (primary == null) {
            throw new NullPointerException("Gui is null");
        }

        JPanel contentPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contentPane.add(primary);

        dialog.pack();
    }

    private class ShowDialogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

    }

}
