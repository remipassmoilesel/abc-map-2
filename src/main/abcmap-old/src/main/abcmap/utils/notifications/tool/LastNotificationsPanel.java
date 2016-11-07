package abcmap.utils.notifications.tool;

import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau listant les derniers evenements transmis
 * 
 * @author remipassmoilesel
 *
 */
public class LastNotificationsPanel extends JPanel {

	public LastNotificationsPanel() {
		super(new MigLayout());
	}

	public void refresh() {

		GuiUtils.throwIfNotOnEDT();

		// enlever tous les elements
		removeAll();

		// iterer les derniers elements transmis
		List<NotificationHistoryElement> lastEvents = NotificationManager.getLastTransmittedEvents();
		Collections.reverse(lastEvents);

		for (NotificationHistoryElement cehe : lastEvents) {
			add(cehe.getPanel(), "width 98%!, wrap");
		}

		// rafraichissement
		revalidate();
		repaint();
	}
	
	
}
