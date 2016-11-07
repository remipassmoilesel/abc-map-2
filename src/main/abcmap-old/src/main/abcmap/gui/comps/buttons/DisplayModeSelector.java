package abcmap.gui.comps.buttons;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.windows.MainWindowMode;
import abcmap.utils.gui.GuiUtils;

public class DisplayModeSelector extends JPanel {

	private JComboBox<MainWindowMode> combo;

	public DisplayModeSelector() {
		super(new MigLayout("insets 0, gap 3"));

		// etiquette "Affichage:"
		JLabel lbl = GuiUtils.addLabel("Affichage: ", this, "");
		lbl.setVerticalAlignment(SwingConstants.CENTER);

		// liste de sélection
		combo = new JComboBox<MainWindowMode>(MainWindowMode.values());
		combo.setRenderer(new ListItemRenderer());
		combo.setBorder(null);

		add(combo);

	}

	private class ListItemRenderer extends JLabel implements
			ListCellRenderer<MainWindowMode> {

		@Override
		public Component getListCellRendererComponent(
				JList<? extends MainWindowMode> list, MainWindowMode mode,
				int index, boolean isSelected, boolean cellHasFocus) {

			// affichage du texte
			setText(mode.getLabel());

			// changement des couleurs
			if (isSelected || cellHasFocus) {
				setBackground(list.getSelectionBackground());
				setForeground(mode.getFgColor());
			} else {
				setBackground(list.getBackground());
				setForeground(mode.getFgColor());
			}

			revalidate();
			repaint();

			return this;
		}
	}

	public void addActionListener(ActionListener al) {
		combo.addActionListener(al);
	}

	public abcmap.gui.windows.MainWindowMode getSelectedItem() {
		return (MainWindowMode) combo.getSelectedItem();
	}

	public void setSelectedItem(MainWindowMode mode) {
		combo.setSelectedItem(mode);

		combo.revalidate();
		combo.repaint();
		revalidate();
		repaint();
	}

}
