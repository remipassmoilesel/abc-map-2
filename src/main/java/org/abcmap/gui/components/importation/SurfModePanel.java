package org.abcmap.gui.components.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.gui.HtmlLabel;

import javax.swing.*;
import java.util.Hashtable;

public class SurfModePanel extends JPanel {

    private JSlider slider;

    public SurfModePanel() {

        super(new MigLayout("insets 0"));

        int maxValue = ConfigurationConstants.SURF_PARAMS.length - 1;

        slider = new JSlider(SwingConstants.HORIZONTAL);
        slider.setMinimum(0);
        slider.setMaximum(maxValue);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);

        Hashtable<Integer, JLabel> lbls = new Hashtable<Integer, JLabel>();
        lbls.put(new Integer(0), new HtmlLabel("Rapide"));
        lbls.put(new Integer(maxValue), new HtmlLabel("Complète"));
        slider.setLabelTable(lbls);
        slider.setPaintLabels(true);

        add(slider, "width 99%!");

        revalidate();
        repaint();
    }

    public JSlider getSlider() {
        return slider;
    }

}
