package org.abcmap.gui.components.geo;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.*;
import java.awt.*;

public class CrsRenderer extends JLabel implements ListCellRenderer<CoordinateReferenceSystem> {

	public CrsRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends CoordinateReferenceSystem> list,
			CoordinateReferenceSystem value, int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value == null) {
			setText("Système invalide");
		}

		else {
			setText(value.getName().getCode());
		}

		return this;
	}

}
