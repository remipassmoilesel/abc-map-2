package org.abcmap.core.draw;

import abcmap.draw.basicshapes.DrawProperties;

import java.awt.*;
import java.util.ArrayList;

public class BackgroundRenderer {

	private static ArrayList<BackgroundTexture> textureLibrary;

	public static void init() {
		if (textureLibrary != null)
			throw new IllegalStateException();

		textureLibrary = new ArrayList<>();
	}

	public static void fill(Graphics g, Shape shape, Texture type, Color color) {

		Graphics2D g2d = (Graphics2D) g;

		// cas du plain
		if (Texture.PLAIN.equals(type)) {
			g2d.setPaint(color);
			g2d.fill(shape);
		}

		// cas des textures
		else {
			// rechercher la texture
			BackgroundTexture bgText = searchTexture(type, color);

			// rendu
			g2d.setPaint(bgText.getPaint());
			g2d.fill(shape);
		}

	}

	public static void fill(Graphics g, Shape shape, DrawProperties stroke) {
		fill(g, shape, stroke.getTexture(), stroke.getBgColor());
	}

	public static void fillOval(Graphics g, Texture type, Color color, int x, int y, int w, int h) {

		Graphics2D g2d = (Graphics2D) g;

		// cas du plain
		if (Texture.PLAIN.equals(type)) {
			g2d.setPaint(color);
			g2d.fillOval(x, y, w, h);
		}

		// cas des textures
		else {
			// rechercher la texture
			BackgroundTexture bgText = searchTexture(type, color);

			// rendu
			g2d.setPaint(bgText.getPaint());
			g2d.fillOval(x, y, w, h);
		}

	}

	private static BackgroundTexture searchTexture(Texture type, Color color) {

		// creer une texture pour recherche
		BackgroundTexture texture = new BackgroundTexture(type, color);

		int index = textureLibrary.indexOf(texture);

		// la texture existe déjà, retour
		if (index != -1) {
			return textureLibrary.get(index);
		}

		// la teture n'esite pas, ajout
		else {
			textureLibrary.add(texture);
			return texture;
		}

	}

}
