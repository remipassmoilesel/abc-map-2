package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class WMSSelectionDialog extends JDialog {

    private final JPanel contentPane;
    private final JList<String> mainList;
    private String selectedValue;

    public WMSSelectionDialog(ArrayList<String> choices) {

        setModal(true);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setTitle("Sélection de la couche");

        // create a content pane
        contentPane = new JPanel(new MigLayout("fill"));
        setContentPane(contentPane);

        // add an explication
        GuiUtils.addLabel("Sélectionnez le nom de la couche à ajouter ci-dessous.", contentPane, "wrap 15");

        // add selection list
        mainList = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : choices) {
            model.addElement(s);
        }
        mainList.setModel(model);
        JScrollPane scroll = new JScrollPane(mainList);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        contentPane.add(scroll, "width 95%!, grow, align center, wrap 15px");

        // add buttons
        JButton cancel = new JButton("Annuler");
        JButton valid = new JButton("Valider");
        JPanel support = new JPanel(new MigLayout());
        support.add(valid);
        support.add(cancel);
        contentPane.add(support);

        // button actions
        cancel.addActionListener((event) -> {
            selectedValue = null;
            dispose();
        });

        valid.addActionListener((event) -> {
            selectedValue = mainList.getSelectedValue();
            dispose();
        });

    }

    /**
     * Get current or last selection made by user
     *
     * @return
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Set current or made by user
     *
     * @return
     */
    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
        mainList.setSelectedValue(selectedValue, true);
        mainList.repaint();
    }
}
