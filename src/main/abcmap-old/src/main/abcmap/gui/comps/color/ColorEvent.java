package abcmap.gui.comps.color;

import java.awt.Color;

public class ColorEvent {
	private Color color;
	private long when;
	private Object source;

	public ColorEvent(Color color, Object source) {
		
		this.color = color;
		this.when = System.currentTimeMillis();
		this.source = source;
	}

	public Color getColor() {
		return color;
	}

	public long getWhen() {
		return when;
	}

	public Object getSource() {
		return source;
	}
}
