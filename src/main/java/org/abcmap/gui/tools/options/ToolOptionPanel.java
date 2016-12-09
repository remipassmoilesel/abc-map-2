package org.abcmap.gui.tools.options;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.*;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;

import javax.swing.*;

public class ToolOptionPanel extends JPanel implements HasNotificationManager {

    protected String title;
    protected NotificationManager observer;
    protected MapManager mapm;
    protected ProjectManager projectm;
    protected DrawManager drawm;
    protected String gapLeft;
    protected String largeWrap;
    protected GuiManager guim;
    protected CancelManager cancelm;

    public ToolOptionPanel() {
        super(new MigLayout("insets 5"));
        // super(new MigLayout("debug, insets 5"));

        this.guim = MainManager.getGuiManager();
        this.mapm = MainManager.getMapManager();
        this.projectm = MainManager.getProjectManager();
        this.drawm = MainManager.getDrawManager();
        this.cancelm = MainManager.getCancelManager();

        this.observer = new NotificationManager(this);

        // default constraints
        gapLeft = "gapleft 15px,";
        largeWrap = "wrap 10px,";
    }

    @Override
    public NotificationManager getNotificationManager() {
        return observer;
    }

}
