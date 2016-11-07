package abcmap.draw.basicshapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface Drawable {

	public static final String RENDER_FOR_DISPLAYING = "RENDER_FOR_DISPLAYING";
	public static final String RENDER_FOR_PRINTING = "RENDER_FOR_PRINTING";

	public void draw(Graphics2D g, String mode);

	public Rectangle getMaximumBounds();

	public void refreshShape();

}
