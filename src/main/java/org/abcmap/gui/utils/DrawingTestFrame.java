package org.abcmap.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Utility to work on Java Graphics objects.
 * <p>
 * Create a class extending DrawingProcedure, and call DrawingTestFrame.show(dp)
 */
public class DrawingTestFrame extends JFrame {

    /**
     * Represent a drawing procedure, allow to draw with Graphics objects.
     */
    public interface DrawingProcedure {
        public void draw(Graphics2D g2d);
    }

    private ArrayList<DrawingProcedure> drawingProcedures;

    public DrawingTestFrame() {

        this.setContentPane(new DrawingPane());
        this.pack();

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        this.drawingProcedures = new ArrayList<DrawingProcedure>();
    }

    /**
     * Add a drawing procedure
     * @param p
     */
    public void addDrawingProcedure(DrawingProcedure p) {
        drawingProcedures.add(p);
    }

    /**
     * Set drawing procedures
     * @param drawingProcedures
     */
    public void setDrawingProcedures(ArrayList<DrawingProcedure> drawingProcedures) {
        this.drawingProcedures = drawingProcedures;
    }

    /**
     * Custom JPanel allowing to draw processes
     */
    private class DrawingPane extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {

            // draw drawing processes
            for (DrawingProcedure dp : drawingProcedures) {
                dp.draw((Graphics2D) g);
            }
        }
    }

    /**
     * Show a drawing process in a window
     * @param dp
     */
    public static void show(DrawingProcedure dp) {
        ArrayList<DrawingProcedure> list = new ArrayList<DrawingProcedure>();
        list.add(dp);
        show(list);
    }

    /**
     * Show several drawing process in a window
     * @param dps
     */
    public static void show(final ArrayList<DrawingProcedure> dps) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DrawingTestFrame frame = new DrawingTestFrame();
                frame.setDrawingProcedures(dps);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}
