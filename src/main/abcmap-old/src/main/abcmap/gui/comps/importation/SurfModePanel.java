package abcmap.gui.comps.importation;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.comps.buttons.HtmlLabel;
import net.miginfocom.swing.MigLayout;

public class SurfModePanel extends JPanel {

	private JSlider slider;

	public SurfModePanel() {

		super(new MigLayout("insets 0"));

		// valuer max du slider
		int maxValue = ConfigurationConstants.SURF_PARAMS.length - 1;

		// slider
		slider = new JSlider(SwingConstants.HORIZONTAL);
		slider.setMinimum(0);
		slider.setMaximum(maxValue);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);

		// labels du slider
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
