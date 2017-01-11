package org.abcmap.gui.toolbars;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.utils.GraphicsConsumer;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.windows.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TooManyListenersException;

public class DropFilesToolbar extends Toolbar {

    private static final CustomLogger logger = LogManager.getLogger(DropFilesToolbar.class);

    private final MainWindowPainter mainWindowPainter;
    private final GuiManager guim;
    private final DropTarget dropTarget;
    private Rectangle rectangleToHighlight;

    public DropFilesToolbar() {

        this.guim = Main.getGuiManager();

        setLayout(new MigLayout("insets 6, gap 6"));

        this.mainWindowPainter = new MainWindowPainter();

        JLabel dropLabel = new JLabel(GuiIcons.DROP_FILES_ICON_BLACK);
        this.addMouseListener(new MouseListener());

        add(dropLabel, "width 70px!, height 30px!");

        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
        try {
            dropTarget.addDropTargetListener(new DropFileHandler());
        } catch (TooManyListenersException e) {
            logger.error(e);
        }
    }

    /**
     * Paint a gray veil on window when mouse is over drop file icon
     */
    private class MainWindowPainter implements GraphicsConsumer {
        @Override
        public void paint(Graphics2D g2d) {

            GuiUtils.applyQualityRenderingHints(g2d);

            Rectangle bounds = g2d.getClipBounds();

            // draw image
            Image img = GuiIcons.DROP_FILES_ICON_WHITE.getImage();
            int x = (int) ((bounds.getWidth() - img.getWidth(null)) / 2);
            int y = 200;

            g2d.drawImage(img, x, y, null);

            // draw help below
            int h = img.getHeight(null);
            y = y + h + 30;
            g2d.setColor(Color.white);
            g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

            String str = "Déposez des fichiers ici pour les importer";
            x = (int) ((bounds.getWidth() - g2d.getFontMetrics()
                    .getStringBounds(str, g2d).getWidth()) / 2);

            g2d.drawString(str, x, y);
        }
    }

    /**
     * Paint a veil around toolbar to highlight it
     *
     * @param paint
     */
    private void paintVeil(boolean paint) {

        // paint veil
        if (paint == true) {

            Point loc = this.getLocationOnScreen();
            rectangleToHighlight = new Rectangle(loc.x, loc.y, getWidth(), getHeight());

            MainWindow main = guim.getMainWindow();
            main.highlightRectangle(rectangleToHighlight);
            main.addPainter(mainWindowPainter);
            main.repaint();
        }

        // clean veil
        else {
            MainWindow main = guim.getMainWindow();
            main.unhighlightRectangle(rectangleToHighlight);
            guim.getMainWindow().removePainter(mainWindowPainter);
            guim.getMainWindow().repaint();
        }
    }


    /**
     * Listen file drop actions
     */
    private class DropFileHandler extends DropTargetAdapter {

        @Override
        public void drop(DropTargetDropEvent dtde) {

            // remove veil from app
            paintVeil(false);

            Transferable transferable = dtde.getTransferable();

            // resource dropped is a file list
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(dtde.getDropAction());
                try {

                    java.util.List transferData = (java.util.List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (transferData != null && transferData.size() > 0) {
                        System.out.println();
                        System.out.println(transferData);
                        System.out.println(transferData);
                        System.out.println(transferData.getClass());
                        dtde.dropComplete(true);
                    }

                } catch (Exception ex) {
                    logger.error(ex);
                }
            }

            // resource dropped is a string, maybe an URL
            else if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(dtde.getDropAction());
                try {

                    java.util.List transferData = (java.util.List) transferable.getTransferData(DataFlavor.stringFlavor);
                    if (transferData != null && transferData.size() > 0) {
                        System.out.println();
                        System.out.println(transferData);
                        System.out.println(transferData);
                        System.out.println(transferData.getClass());

                        dtde.dropComplete(true);
                    }

                } catch (Exception ex) {
                    logger.error(ex);
                }
            }

            // resource dropped is an image
            else if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                dtde.acceptDrop(dtde.getDropAction());
                try {

                    java.util.List transferData = (java.util.List) transferable.getTransferData(DataFlavor.imageFlavor);
                    if (transferData != null && transferData.size() > 0) {
                        System.out.println();
                        System.out.println(transferData);
                        System.out.println(transferData);
                        System.out.println(transferData.getClass());

                        dtde.dropComplete(true);
                    }

                } catch (Exception ex) {
                    logger.error(ex);
                }
            }

            // unrecognized resource
            else {
                logger.error("Unrecognized resource: " + dtde);
                dtde.rejectDrop();
            }
        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            super.dragEnter(dtde);
            paintVeil(true);
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            super.dragExit(dte);
            paintVeil(false);
        }
    }

    /**
     * Paint a gray veil on window when mouse is over drop file icon
     */
    private class MouseListener extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            paintVeil(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            paintVeil(false);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            paintVeil(true);
        }

    }

}
