package org.abcmap.core.notifications.monitoringtool;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.core.notifications.NotificationManager;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * Panel where are displayed last transmitted events
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
