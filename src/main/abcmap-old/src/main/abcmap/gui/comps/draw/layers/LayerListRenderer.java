package abcmap.gui.comps.draw.layers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import abcmap.exceptions.MapLayerException;
import abcmap.gui.GuiIcons;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;

/**
 * Objet de rendu d'une liste de calque pour une JList
 * 
 * @author remipassmoilesel
 *
 */
public class LayerListRenderer extends JLabel implements ListCellRenderer<MapLayer> {

	private ProjectManager projectm;

	private Color activeLayerColor;

	public LayerListRenderer() {

		this.projectm = MainManager.getProjectManager();

		// couleur du claque actif
		this.activeLayerColor = new Color(43, 3, 188);

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// caracteristiques
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MapLayer> list, MapLayer layer, int index,
			boolean isSelected, boolean cellHasFocus) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// recuperer le calque actif
		MapLayer activeLayer = null;
		try {
			activeLayer = projectm.getActiveLayer();
		} catch (NullPointerException | MapLayerException e) {
			activeLayer = null;
		}

		// le projet n'est pas initialisé
		if (layer == null || activeLayer == null) {
			setText("");
			setIcon(null);
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setBorder(new LineBorder(list.getBackground(), 2, false));
		}

		// le projet est initialisé
		else {
			Color cBorder;

			if (activeLayer.equals(layer)) {
				setBackground(activeLayerColor);
				setForeground(Color.white);
				cBorder = Color.LIGHT_GRAY;
			}

			else {
				setBackground(list.getBackground());
				setForeground(Color.black);
				cBorder = list.getBackground();
			}

			// espacement entre les items
			setBorder(new LineBorder(cBorder, 2, false));

			// icone
			ImageIcon icon;
			if (layer.isVisible()) {
				icon = GuiIcons.LAYER_IS_VISIBLE;
			} else {
				icon = GuiIcons.LAYER_IS_INVISIBLE;
			}
			setIcon(icon);

			// texte
			setText(layer.getName());
			setFont(list.getFont());

		}

		// raccourcir le nom du calque au besoin
		int i = this.getText().length();
		String txt = this.getText();

		while (this.getPreferredSize().width > list.getSize().width - 15 && i > 0) {
			i -= 2;
			if (i < 1) {
				i = 1;
				this.setText(txt.substring(0, i) + "...");
				this.setSize(getPreferredSize());
				break;
			} else {
				this.setText(txt.substring(0, i) + "...");
				this.setSize(getPreferredSize());
			}
		}
		return this;

	}
}
