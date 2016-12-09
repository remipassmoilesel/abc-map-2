package org.abcmap.gui.windows.crop;

import org.abcmap.gui.shapes.Handle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CropSelectionTool extends MouseAdapter {

    private CropConfigurationWindow cropWindow;
    private boolean modifying;
    private boolean drawing;
    private Handle activeHandle;
    private Point selectionOrigin;
    private int minimalWidth;
    private CropSelectionRectangle selection;

    public CropSelectionTool(CropConfigurationWindow csw) {

        this.cropWindow = csw;

        this.minimalWidth = 50;
        this.selection = cropWindow.getSelection();

    }

    @Override
    public void mousePressed(MouseEvent e) {

        // check mouse button
        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        Point m = e.getPoint();

        drawing = false;
        modifying = false;

        // check if click is on handle, if user want to change dimensions of current selection
        for (Handle h : selection.getHandles()) {
            if (h.getInteractionArea().contains(m)) {
                activeHandle = h;
                modifying = true;
            }
        }

        // if user is not changing size of present selection, draw a new one
        if (modifying == false) {
            drawing = true;

            selection.setPosition(m);
            selection.setDimensions(new Dimension(0, 0));
            selection.refreshShape();

            selectionOrigin = new Point(m.x, m.y);
        }

        cropWindow.refreshImagePane();
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {

        if (SwingUtilities.isLeftMouseButton(arg0) == false) {
            return;
        }

        Point m = arg0.getPoint();

        Rectangle selectionRect = selection.getBounds();

        ArrayList<Handle> handles = selection.getHandles();

        Point newPos = new Point(selection.getPosition());
        Dimension newDim = new Dimension(selection.getDimensions());

        int ht = selection.getHalfThickness();

        // bottom right corner of selection
        Point brc = new Point();
        brc.x = selectionRect.x + selectionRect.width;
        brc.y = selectionRect.y + selectionRect.height;

        /**
         * Change dimensions of rectangle
         */
        if (modifying == true) {

            // upper left handle
            if (CropSelectionRectangle.ULC_HANDLE_INDEX == handles.indexOf(activeHandle)) {

                newPos.setLocation(m);

                newDim.width = brc.x - m.x - ht;
                newDim.height = brc.y - m.y - ht;

            }

            // bottom right handle
            else if (CropSelectionRectangle.BRC_HANDLE_INDEX == handles.indexOf(activeHandle)) {
                newDim.width = m.x - selectionRect.x;
                newDim.height = m.y - selectionRect.y;
            }

            // middle handle
            else if (CropSelectionRectangle.MIDDLE_HANDLE_INDEX == handles.indexOf(activeHandle)) {

                newPos = new Point(m);
                newPos.x -= selectionRect.width / 2;
                newPos.y -= selectionRect.height / 2;

            }

            // check minimum dimensions
            if (newDim.width < minimalWidth) {
                newDim.width = minimalWidth;
                newPos.x = brc.x - minimalWidth - ht;
            }

            if (newDim.height < minimalWidth) {
                newDim.height = minimalWidth;
                newPos.y = brc.y - minimalWidth - ht;
            }

            // refresh shape
            selection.setPosition(newPos);
            selection.setDimensions(newDim);
            selection.refreshShape();

        }

        // draw a new rectangle
        else if (drawing == true) {

            Point originCopy = new Point(selectionOrigin);

            // compute dimensions of selection
            int w = m.x - originCopy.x;
            int h = m.y - originCopy.y;
            Dimension dim = new Dimension(w, h);

            // always have positive dimensions
            if (dim.width < 0) {
                int x = originCopy.x + dim.width;
                originCopy.setLocation(x, originCopy.y);
                dim.width = -dim.width;
            }

            // always have positive dimensions
            if (dim.height < 0) {
                int y = originCopy.y + dim.height;
                originCopy.setLocation(originCopy.x, y);
                dim.height = -dim.height;
            }

            // check minimum position
            if (selectionOrigin.x < 0) {
                selectionOrigin.setLocation(0, originCopy.y);
            }

            if (selectionOrigin.y < 0) {
                selectionOrigin.setLocation(originCopy.x, 0);
            }


            // check minimal dimensions
            if (dim.width < minimalWidth) {
                dim.width = minimalWidth;
            }

            if (dim.height < minimalWidth) {
                dim.height = minimalWidth;
            }

            // refresh shape
            selection.setPosition(originCopy);
            selection.setDimensions(dim);
            selection.refreshShape();

        }

        // valid selection
        cropWindow.validVisualSelection();

        // refresh parent
        cropWindow.refreshImagePane();

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

        if (SwingUtilities.isLeftMouseButton(arg0) == false) {
            return;
        }

        if (drawing = true) {
            drawing = false;

            // if selection is too tiny, hide hide
            Dimension selDim = cropWindow.getSelection().getBounds().getSize();
            if (selDim.width < minimalWidth && selDim.height < minimalWidth) {
                cropWindow.hideSelection();
                cropWindow.refreshImagePane();
            }

        }

        // NO VALIDATION HERE or bad offset will appear
        // cropWindow.validVisualSelection();

        modifying = false;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public boolean isResizing() {
        return modifying;
    }
}
