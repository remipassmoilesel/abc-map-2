package abcmap.gui.comps.draw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import abcmap.draw.styles.LineStyle;

public class LineStyleRenderer extends JLabel implements ListCellRenderer<LineStyle> {

	private Stroke stroke;
	private boolean selected;
	private int margin;
	private int thickness;
	private Color color;
	private Dimension preferredSize;

	public LineStyleRenderer() {
		setOpaque(true);

		this.thickness = 3;
		this.margin = 5;
		this.color = Color.blue;

		setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

		this.preferredSize = new Dimension(80, 20);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends LineStyle> list, LineStyle value,
			int index, boolean isSelected, boolean cellHasFocus) {

		this.stroke = value.getSwingStroke(thickness);
		this.selected = isSelected;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// ligne nulle, retour
		if (stroke == null)
			return;

		// calculer les dimensions
		int x1 = margin;
		int x2 = this.getWidth() - margin * 2;
		int y = (this.getHeight()) / 2;

		// dessiner
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.setStroke(stroke);
		g2.drawLine(x1, y, x2, y);

	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

}
