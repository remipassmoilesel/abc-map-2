package org.abcmap.gui.components.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

public class InteractivePopupDisplay extends JPopupMenu {

	private JPanel content;
	private JScrollPane scroll;

	private Component compParent;

	private int maxPopupHeight;
	private int minPopupHeight;
	private int preferredPopupWidth;

	public InteractivePopupDisplay(Component parent) {

		super();
		this.setLayout(new BorderLayout());

		// taille par defaut de la popup
		this.preferredPopupWidth = 400;
		this.minPopupHeight = 450;
		this.maxPopupHeight = 600;

		setPreferredSize(new Dimension(preferredPopupWidth, minPopupHeight));

		// le composant parent
		this.compParent = parent;

		// panneau principal
		this.content = new JPanel(new MigLayout("insets 5"));

		// contenu dans un scroll
		this.scroll = new JScrollPane(content);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getHorizontalScrollBar().setUnitIncrement(150);

		this.add(scroll, BorderLayout.CENTER);
	}

	public void showPopup(boolean val) {

		// afficher
		if (val) {
			int x = 0;
			int y = compParent.getHeight();

			this.show(compParent, x, y);
		}

		// masquer
		else {
			this.setVisible(false);
		}

		this.revalidate();
		this.repaint();
	}

	public JPanel getContentPane() {
		return content;
	}

	public void proposePopupHeight(Integer height) {

		// hauteur nulle: calculer la hauteur max
		if (height == null)
			height = computePreferredHeight();

		if (height < minPopupHeight)
			height = minPopupHeight;

		if (height > maxPopupHeight)
			height = maxPopupHeight;

		Dimension dim = getPreferredSize();
		dim.height = height;
		setPreferredSize(dim);
	}

	public int computePreferredHeight() {
		int height = 0;
		for (Component c : content.getComponents()) {
			height += c.getPreferredSize().height;
		}

		return height;
	}

	public void adjustHeight() {
		proposePopupHeight(null);
	}

}
