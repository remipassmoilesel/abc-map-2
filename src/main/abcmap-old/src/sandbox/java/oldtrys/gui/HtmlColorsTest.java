package oldtrys.gui;

import abcmap.utils.gui.HtmlColors;

public class HtmlColorsTest {
	public static void main(String[] args) {
		System.out.println(HtmlColors.parse("str"));
		System.out.println(HtmlColors.parse("navy"));
		System.out.println(HtmlColors.parse("255.22.222"));
		System.out.println(HtmlColors.parse("255,22,222"));
		System.out.println(HtmlColors.parse("#123456"));
	}
}
