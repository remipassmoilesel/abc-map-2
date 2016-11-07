package abcmap.draw.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class LegendTool extends MapTool {

	private Tracer tracer;

	public LegendTool() {
		this.tracer = new Tracer();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		tracer.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		tracer.mouseDragged(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tracer.mouseClicked(e);
	}

	@Override
	public void drawOnCanvas(Graphics2D g2d) {
		if (tracer != null)
			tracer.draw(g2d);
	}

	private class Tracer extends SimpleRectangleTracer {

		public Tracer() {

			// style du rectangle
			BasicStroke dashStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, new float[] { 10f, 5f }, 0.0f);

			setRectangleColor(new Color(114, 14, 5));
			setRectangleStroke(dashStroke);

			setDeleteRectangleOnMouseReleased(false);

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			resetRectangle();
			mapm.refreshMapComponent();
		}
	}

	public Rectangle getDesiredBounds() {
		if (tracer == null)
			return null;

		return tracer.getRectangle();
	}

}
