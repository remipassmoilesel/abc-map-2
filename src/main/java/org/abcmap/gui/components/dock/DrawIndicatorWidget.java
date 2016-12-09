package org.abcmap.gui.components.dock;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.tools.containers.ToolContainer;
import abcmap.events.DrawManagerEvent;
import abcmap.gui.comps.color.ColorButton;
import abcmap.gui.iegroup.docks.GroupDrawingPalette;
import abcmap.gui.iegroup.docks.GroupDrawingTools;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Refreshable;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.DrawManagerEvent;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.notifications.UpdatableByNotificationManager;
import org.abcmap.gui.components.color.ColorButton;

/**
 * Dock widget that show current tool and active colors
 */
public class DrawIndicatorWidget extends JPanel implements HasNotificationManager {

    private NotificationManager om;
    private DrawManager drawm;
    private GuiManager guim;

    private JLabel toolLabel;
    private ColorButton fgColor;
    private ColorButton bgColor;

    public DrawIndicatorWidget() {
        super(new MigLayout("insets 3, fillx"));

        drawm = MainManager.getDrawManager();
        guim = MainManager.getGuiManager();

        setBorder(BorderFactory.createEtchedBorder());

        // listen draw manager
        om = new NotificationManager(this);
        om.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
            @Override
            public void notificationReceived(final Notification arg) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (arg instanceof DrawManagerEvent) {
                            DrawIndicatorWidget.this.refresh();
                        }
                    }
                });
            }
        });
        drawm.getNotificationManager().addObserver(this);

        // if user click on element, the cresponding panel will be opened
        CustomML colorsAL = new CustomML(CustomML.SHOW_COLORS);
        CustomML toolsAL = new CustomML(CustomML.SHOW_TOOLS);

        toolLabel = new JLabel();
        toolLabel.addMouseListener(toolsAL);
        toolLabel.setCursor(MainManager.getGuiManager().getClickableCursor());

        fgColor = new ColorButton(null);
        fgColor.addMouseListener(colorsAL);

        bgColor = new ColorButton(null);
        bgColor.addMouseListener(colorsAL);


        refresh();

    }

    public void refresh() {

        removeAll();

        ToolContainer currentTC = drawm.getCurrentToolContainer();

        if (currentTC == null) {
            toolLabel.setIcon(new ImageIcon());
            toolLabel.setToolTipText("Veuillez sélectionner un outil.");
        } else {
            toolLabel.setIcon(currentTC.getIcon());
            toolLabel.setToolTipText("Outil courant: " + currentTC.getReadableName());
        }

        add(toolLabel, "alignx center, wrap");

        DrawProperties st = drawm.getNewStroke();

        fgColor.setColor(st.getFgColor());
        fgColor.setToolTipText("Couleur de premier plan: " + fgColor.getStringRGB() + " (RGB)");

        bgColor.setColor(st.getBgColor());
        bgColor.setToolTipText("Couleur de second plan: " + bgColor.getStringRGB() + " (RGB)");

        add(fgColor, "width 80%!, alignx center, wrap");
        add(bgColor, "width 80%!, alignx center, wrap");


        fgColor.revalidate();
        fgColor.repaint();
        bgColor.revalidate();
        bgColor.repaint();
        toolLabel.revalidate();
        toolLabel.repaint();

        revalidate();
        repaint();

    }

    @Override
    public NotificationManager getNotificationManager() {
        return om;
    }


    private class CustomML extends MouseAdapter {

        public static final String SHOW_COLORS = "SHOW_COLORS";
        public static final String SHOW_TOOLS = "SHOW_TOOLS";
        private String mode;

        public CustomML(String mode) {
            this.mode = mode;
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            Dock parent = Dock.getDockParentForComponent(DrawIndicatorWidget.this);
            if (parent == null){
                return;
            }

            Class clss = SHOW_COLORS.equals(mode) ? GroupDrawingPalette.class : GroupDrawingTools.class;
            guim.showGroupInDock(clss);

        }
    }

}
