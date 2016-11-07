package abcmap.gui.toolOptionPanels;

import javax.swing.JPanel;

import abcmap.managers.CancelManager;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

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

		// contrainte par defaut pour les elements
		gapLeft = "gapleft 15px,";
		largeWrap = "wrap 10px,";
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

}
