package org.abcmap.gui;

import javax.swing.*;
import java.awt.*;

public class GuiColors {

	public static final Color MENU_TITLE_1 = new Color(13, 0, 178);

	public static final Color MENU_TITLE_2 = new Color(80, 80, 80);

	public static Color FOCUS_COLOR_BACKGROUND;

	public static int FOCUS_STROKE_THICKNESS;

	public static Stroke FOCUS_STROKE;

	public static Color MENU_ITEM_BACKGROUND;

	public static Color PANEL_BACKGROUND;

	public static Color INFO_BOX_BACKGROUND;

	public static Color ERROR_BOX_BACKGROUND;

	public static void init() {

		FOCUS_COLOR_BACKGROUND = UIManager.getColor("MenuItem.selectionBackground");

		FOCUS_STROKE_THICKNESS = 1;

		FOCUS_STROKE = new BasicStroke(FOCUS_STROKE_THICKNESS);

		MENU_ITEM_BACKGROUND = UIManager.getColor("MenuItem.background");

		PANEL_BACKGROUND = UIManager.getColor("Panel.background");

		INFO_BOX_BACKGROUND = new Color(0, 50, 10);

		ERROR_BOX_BACKGROUND = new Color(50, 10, 0);

	}

}
