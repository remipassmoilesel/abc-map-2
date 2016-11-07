package abcmap.utils.sandbox;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DrawingTestFrame extends JFrame {

	public interface DrawingProcess {
		public void draw(Graphics2D g2d);
	}

	private ArrayList<DrawingProcess> drawingProcess;

	public DrawingTestFrame() {

		this.setContentPane(new DrawingPane());
		this.pack();

		this.setSize(800, 600);
		this.setLocationRelativeTo(null);

		this.drawingProcess = new ArrayList<DrawingProcess>();
	}

	public void addDrawingProcess(DrawingProcess p) {
		drawingProcess.add(p);
	}

	public void setDrawingProcess(ArrayList<DrawingProcess> drawingProcess) {
		this.drawingProcess = drawingProcess;
	}

	private class DrawingPane extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {

			// dessiner les process
			for (DrawingProcess dp : drawingProcess) {
				dp.draw((Graphics2D) g);
			}
		}
	}

	public static void show(DrawingProcess dp) {
		ArrayList<DrawingProcess> list = new ArrayList<DrawingProcess>();
		list.add(dp);
		show(list);
	}

	public static void show(final ArrayList<DrawingProcess> dps) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DrawingTestFrame frame = new DrawingTestFrame();
				frame.setDrawingProcess(dps);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

}
