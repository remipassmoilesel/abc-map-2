package org.abcmap.gui.tools.options;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.*;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.events.manager.EventNotificationManager;

import javax.swing.*;

public class ToolOptionPanel extends JPanel implements HasEventNotificationManager {

    protected String title;
    protected EventNotificationManager observer;
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

        this.guim = Main.getGuiManager();
        this.mapm = Main.getMapManager();
        this.projectm = Main.getProjectManager();
        this.drawm = Main.getDrawManager();
        this.cancelm = Main.getCancelManager();

        this.observer = new EventNotificationManager(this);

        // default constraints
        gapLeft = "gapleft 15px,";
        largeWrap = "wrap 10px,";
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return observer;
    }

}
