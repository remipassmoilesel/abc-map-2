package org.abcmap.gui.utils;

import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JPanel {

    public DebugPanel(LayoutManager manager) {
        super(manager);
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        new Exception().printStackTrace();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("paintCompo " + this);
    }
}