package org.abcmap.gui.components.data;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataImportOptionsPanel extends JPanel {

    private JComboBox<String> cbMode;
    private JPanel bottomComponent;
    private Object wrap15;
    private String gapLeft;
    private JPanel[] optionPanels;

    public DataImportOptionsPanel() {
        super(new MigLayout("insets 0"));

        wrap15 = "wrap 15, ";
        gapLeft = "gapleft 15, ";

        String[] modes = new String[]{"Rendre les données tel quel", "Créer des formes",
                "Créer des étiquettes de texte", "Créer une ligne ou un polygone"};

        optionPanels = new JPanel[]{new DataImportRenderAsIs(this),
                new DataImportCreateShapes(this), new DataImportCreateLabels(this),
                new DataImportCreateLines(this),};

        cbMode = new JComboBox<>(modes);
        cbMode.setEditable(false);
        cbMode.addActionListener(new ComboChangeListener());

        add(cbMode, wrap15);

        bottomComponent = new JPanel(new MigLayout("insets 0"));
        add(bottomComponent);

        cbMode.setSelectedIndex(0);

    }

    /**
     * Chnage type of import when user select it
     */
    private class ComboChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            int index = cbMode.getSelectedIndex();


            if (index < 0 || index >= optionPanels.length) {
                index = 0;
            }


            setBottomComponent(optionPanels[index]);


            refresh();
        }

    }

    /**
     * Set bottom component of panel, in terms of combo selection e.g.
     *
     * @param comp
     */
    public void setBottomComponent(JPanel comp) {
        remove(bottomComponent);
        bottomComponent = comp;
        add(bottomComponent);
    }

    public void refresh() {
        bottomComponent.revalidate();
        bottomComponent.repaint();
        revalidate();
        repaint();
    }

}
