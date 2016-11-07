package abcmap.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import abcmap.gui.comps.buttons.HtmlCheckbox;

public class GuiStyle {

	/** Nom de la police par défaut */
	public static final Color DEFAULT_FONT_COLOR = new Color(30, 30, 30);

	/** Nom de la police par défaut */
	public static final String DEFAULT_FONT_NAME = Font.DIALOG;

	/** Taille par defaut de la police du logiciel */
	public static final int DEFAULT_FONT_SIZE = 11;

	/** Police par défaut du logiciel */
	public static final Font DEFAULT_SOFTWARE_FONT = new Font(
			DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);

	/** Style des champs de texte de couleurs RGB */
	public static GuiStyle RGB_RED_TXTFIELD_STYLE;

	/** Style des champs de texte de couleurs RGB */
	public static GuiStyle RGB_GREEN_TXTFIELD_STYLE;

	/** Style des champs de texte de couleurs RGB */
	public static GuiStyle RGB_BLUE_TXTFIELD_STYLE;

	/** Style pour boite de message centrale */
	public static GuiStyle MESSAGE_BOX_FONT;

	/** Style pour menu de dock de niveau 1 */
	public static GuiStyle DOCK_MENU_TITLE_1;

	/** Style pour menu de dock de niveau 2 */
	public static GuiStyle DOCK_MENU_TITLE_2;

	/** Style pour nom d'element de GUI en bloc */
	public static GuiStyle SIMPLE_BLOCK_ITEM_LABEL;

	/** Style pour aide d'element de GUI en bloc */
	public static GuiStyle SIMPLE_BLOCK_ITEM_HELP;

	/** Style pour nom de widget de dock */
	public static GuiStyle WIDGET_BUTTON_LABEL;

	/** Style pour titre d'étape d'assistant dynamique */
	public static GuiStyle WIZARD_STEP_NAME;

	/** Style pour description de menu de dock */
	public static GuiStyle DOCK_MENU_DESCRIPTION;

	/** Style pour titre d'aide d'outils de dessin */
	public static GuiStyle TOOL_HELP_TITLE;

	/** Style pour titre de boite de dialogue, niv 1 */
	public static GuiStyle DIALOG_TITLE_1;

	/** Style pour titre de boite de dialogue, niv 2 */
	public static GuiStyle DIALOG_TITLE_2;

	/** Style pour introduction de boite de dialogue */
	public static GuiStyle DIALOG_INTRO;

	/** Style pour texte de boite de dialogue */
	public static GuiStyle DIALOG_TEXT;

	/** Style pour nom de résultat de recherche */
	public static GuiStyle SEARCH_RESULT_LABEL;

	/** Style pour texte de résultat de recherche */
	public static GuiStyle SEARCH_RESULT_TEXT;

	/**
	 * Style pour message d'indication d'indisponibilité de l'element à la
	 * recherche
	 */
	public static GuiStyle SEARCH_RESULT_NO_SEARCH;

	static {
		DOCK_MENU_TITLE_1 = new GuiStyle();
		DOCK_MENU_TITLE_1.setFont(DEFAULT_FONT_NAME, Font.BOLD, 15);
		DOCK_MENU_TITLE_1.setForeground(GuiColors.MENU_TITLE_1);

		DOCK_MENU_TITLE_2 = new GuiStyle();
		DOCK_MENU_TITLE_2.setFont(DEFAULT_FONT_NAME, Font.BOLD, 14);
		DOCK_MENU_TITLE_2.setForeground(GuiColors.MENU_TITLE_2);

		DOCK_MENU_DESCRIPTION = new GuiStyle();
		DOCK_MENU_DESCRIPTION.setFont(DEFAULT_FONT_NAME, Font.ITALIC,
				DEFAULT_FONT_SIZE);
		DOCK_MENU_DESCRIPTION.setForeground(new Color(13, 0, 178));

		SIMPLE_BLOCK_ITEM_LABEL = new GuiStyle();
		SIMPLE_BLOCK_ITEM_LABEL.setFont(DEFAULT_FONT_NAME, Font.BOLD,
				DEFAULT_FONT_SIZE);
		SIMPLE_BLOCK_ITEM_LABEL.setForeground(Color.darkGray);

		MESSAGE_BOX_FONT = new GuiStyle();
		MESSAGE_BOX_FONT.setFont(DEFAULT_FONT_NAME, Font.BOLD, 20);
		MESSAGE_BOX_FONT.setForeground(new Color(13, 0, 178));

		WIDGET_BUTTON_LABEL = new GuiStyle();
		WIDGET_BUTTON_LABEL.setFont(DEFAULT_FONT_NAME, Font.BOLD,
				DEFAULT_FONT_SIZE);
		WIDGET_BUTTON_LABEL.setForeground(Color.DARK_GRAY);
		WIDGET_BUTTON_LABEL.setHorizontalAlignement(Align.CENTER);

		SIMPLE_BLOCK_ITEM_HELP = new GuiStyle();
		SIMPLE_BLOCK_ITEM_HELP.setFont(DEFAULT_FONT_NAME, Font.ITALIC,
				DEFAULT_FONT_SIZE);
		SIMPLE_BLOCK_ITEM_HELP.setForeground(Color.white);
		SIMPLE_BLOCK_ITEM_HELP.setBackground(Color.DARK_GRAY);
		SIMPLE_BLOCK_ITEM_HELP.setPadding(5);

		WIZARD_STEP_NAME = new GuiStyle();
		WIZARD_STEP_NAME.setFont(DEFAULT_FONT_NAME, Font.BOLD,
				DEFAULT_FONT_SIZE);
		WIZARD_STEP_NAME.setForeground(Color.DARK_GRAY);

		TOOL_HELP_TITLE = new GuiStyle();
		TOOL_HELP_TITLE
				.setFont(DEFAULT_FONT_NAME, Font.BOLD, DEFAULT_FONT_SIZE);
		TOOL_HELP_TITLE.setForeground(Color.DARK_GRAY);

		DIALOG_TITLE_1 = new GuiStyle();
		DIALOG_TITLE_1.setFont(DEFAULT_FONT_NAME, Font.BOLD, 17);
		DIALOG_TITLE_1.setForeground(new Color(13, 0, 178));

		DIALOG_INTRO = new GuiStyle();
		DIALOG_INTRO.setFont(DEFAULT_FONT_NAME, Font.ITALIC, DEFAULT_FONT_SIZE);
		DIALOG_INTRO.setForeground(new Color(55, 55, 55));

		DIALOG_TEXT = new GuiStyle();
		DIALOG_TEXT.setFont(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);
		DIALOG_TEXT.setForeground(new Color(55, 55, 55));

		DIALOG_TITLE_2 = new GuiStyle();
		DIALOG_TITLE_2.setFont(DEFAULT_FONT_NAME, Font.BOLD, DEFAULT_FONT_SIZE);
		DIALOG_TITLE_2.setForeground(new Color(55, 55, 55));

		SEARCH_RESULT_LABEL = new GuiStyle();
		SEARCH_RESULT_LABEL.setFont(DEFAULT_FONT_NAME, Font.BOLD,
				DEFAULT_FONT_SIZE);
		SEARCH_RESULT_LABEL.setForeground(new Color(55, 55, 55));

		SEARCH_RESULT_TEXT = new GuiStyle();
		SEARCH_RESULT_TEXT.setFont(DEFAULT_FONT_NAME, Font.ITALIC,
				DEFAULT_FONT_SIZE);
		SEARCH_RESULT_TEXT.setForeground(new Color(55, 55, 55));

		SEARCH_RESULT_NO_SEARCH = new GuiStyle();
		SEARCH_RESULT_NO_SEARCH.setFont(DEFAULT_FONT_NAME, Font.BOLD,
				DEFAULT_FONT_SIZE);
		SEARCH_RESULT_NO_SEARCH.setForeground(new Color(55, 55, 55));

		RGB_RED_TXTFIELD_STYLE = new GuiStyle();
		RGB_RED_TXTFIELD_STYLE.setForeground(Color.red);
		RGB_RED_TXTFIELD_STYLE.setFont(Font.SANS_SERIF, Font.BOLD,
				DEFAULT_FONT_SIZE);
		RGB_RED_TXTFIELD_STYLE.setHorizontalAlignement(GuiStyle.Align.CENTER);

		RGB_GREEN_TXTFIELD_STYLE = new GuiStyle();
		RGB_GREEN_TXTFIELD_STYLE.setForeground(Color.green);
		RGB_GREEN_TXTFIELD_STYLE.setFont(Font.SANS_SERIF, Font.BOLD,
				DEFAULT_FONT_SIZE);
		RGB_GREEN_TXTFIELD_STYLE.setHorizontalAlignement(GuiStyle.Align.CENTER);

		RGB_BLUE_TXTFIELD_STYLE = new GuiStyle();
		RGB_BLUE_TXTFIELD_STYLE.setForeground(Color.blue);
		RGB_BLUE_TXTFIELD_STYLE.setFont(Font.SANS_SERIF, Font.BOLD,
				DEFAULT_FONT_SIZE);
		RGB_BLUE_TXTFIELD_STYLE.setHorizontalAlignement(GuiStyle.Align.CENTER);

	}

	public enum Align {

		RIGHT, LEFT, CENTER;

		public int getSwingConstant() {
			if (RIGHT.equals(Align.this)) {
				return SwingConstants.RIGHT;
			} else if (LEFT.equals(Align.this)) {
				return SwingConstants.LEFT;
			} else if (CENTER.equals(Align.this)) {
				return SwingConstants.CENTER;
			}

			else {
				throw new IllegalArgumentException();
			}
		}
	}

	private Font font;
	private Color background;
	private Color foreground;
	private Align horizontalAlignement;
	private int padding;

	public GuiStyle() {
		this(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE, Color.black,
				null);
	}

	public GuiStyle(String font, int style, int size, Color foreground,
			Color background) {
		this.font = new Font(font, style, size);
		this.foreground = foreground;
		this.background = background;
		this.horizontalAlignement = null;
	}

	public static JTextField applyStyleTo(GuiStyle style, JTextField txt) {

		txt.setFont(style.getFont());

		if (style.getForeground() != null)
			txt.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			txt.setBackground(style.getBackground());
			txt.setOpaque(true);
		}

		if (style.horizontalAlignement != null) {
			txt.setHorizontalAlignment(style.horizontalAlignement
					.getSwingConstant());
		}

		int p = style.getPadding();
		txt.setMargin(new Insets(p, p, p, p));

		txt.revalidate();
		txt.repaint();

		return txt;
	}

	public static JTextArea applyStyleTo(GuiStyle style, JTextArea tarea) {
		tarea.setFont(style.getFont());

		if (style.getForeground() != null)
			tarea.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			tarea.setBackground(style.getBackground());
			tarea.setOpaque(true);
		}

		int p = style.getPadding();
		tarea.setMargin(new Insets(p, p, p, p));

		tarea.revalidate();
		tarea.repaint();

		return tarea;
	}

	public static JEditorPane applyStyleTo(GuiStyle style, JEditorPane comp) {

		comp.setFont(style.getFont());

		if (style.getForeground() != null)
			comp.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			comp.setBackground(style.getBackground());
			comp.setOpaque(true);
		}

		int p = style.getPadding();
		comp.setMargin(new Insets(p, p, p, p));

		comp.revalidate();
		comp.repaint();

		return comp;
	}

	public static JLabel applyStyleTo(GuiStyle style, JLabel label) {
		label.setFont(style.getFont());

		if (style.getForeground() != null)
			label.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			label.setBackground(style.getBackground());
			label.setOpaque(true);
		}

		if (style.horizontalAlignement != null) {
			label.setHorizontalAlignment(style.horizontalAlignement
					.getSwingConstant());
		}

		label.revalidate();
		label.repaint();

		return label;
	}

	public static JCheckBox applyStyleTo(GuiStyle style, JCheckBox chkbox) {

		chkbox.setFont(style.getFont());

		if (style.getForeground() != null)
			chkbox.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			chkbox.setBackground(style.getBackground());
			chkbox.setOpaque(true);
		}

		if (style.horizontalAlignement != null) {
			chkbox.setHorizontalAlignment(style.horizontalAlignement
					.getSwingConstant());
		}

		chkbox.revalidate();
		chkbox.repaint();

		return chkbox;
	}

	public static AbstractButton applyStyleTo(GuiStyle style,
			AbstractButton jButton) {

		jButton.setFont(style.getFont());

		if (style.getForeground() != null)
			jButton.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			jButton.setBackground(style.getBackground());
			jButton.setOpaque(true);
		}

		if (style.horizontalAlignement != null) {
			jButton.setHorizontalAlignment(style.horizontalAlignement
					.getSwingConstant());
		}

		jButton.revalidate();
		jButton.repaint();

		return jButton;
	}

	public static JList applyStyleTo(GuiStyle style, JList list) {

		list.setFont(style.getFont());

		if (style.getForeground() != null)
			list.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			list.setBackground(style.getBackground());
			list.setOpaque(true);
		}

		list.revalidate();
		list.repaint();

		return list;
	}

	public static JComboBox applyStyleTo(GuiStyle style, JComboBox list) {

		list.setFont(style.getFont());

		if (style.getForeground() != null)
			list.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			list.setBackground(style.getBackground());
			list.setOpaque(true);
		}

		list.revalidate();
		list.repaint();

		return list;
	}

	public static void applyStyleTo(GuiStyle style, JPanel panel) {

		if (style.getForeground() != null)
			panel.setForeground(style.getForeground());

		if (style.getBackground() != null) {
			panel.setBackground(style.getBackground());
			panel.setOpaque(true);
		}

		panel.revalidate();
		panel.repaint();

	}

	public Color getBackground() {
		return background;
	}

	public Color getForeground() {
		return foreground;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setFont(String font, int style, int size) {
		this.font = new Font(font, style, size);
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getPadding() {
		return padding;
	}

	public Align getHorizontalAlignement() {
		return horizontalAlignement;
	}

	public void setHorizontalAlignement(Align horizontalAlignement) {
		this.horizontalAlignement = horizontalAlignement;
	}

}
