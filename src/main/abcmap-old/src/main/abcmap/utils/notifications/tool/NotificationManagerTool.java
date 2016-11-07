package abcmap.utils.notifications.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class NotificationManagerTool {

	public static void showLastEventsTransmitted() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				// construction de la fenêtre
				JFrame frame = new JFrame("Derniers événements transmis");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setSize(800, 600);
				frame.setLocationRelativeTo(null);

				// panneau d'evenements
				final LastNotificationsPanel events = new LastNotificationsPanel();
				events.refresh();

				// dans un panneau scroll
				JScrollPane sp = new JScrollPane(events);
				sp.getVerticalScrollBar().setUnitIncrement(100);

				// bouton rafraichir
				JButton refresh = new JButton("Rafraichir");
				refresh.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						events.refresh();
					}
				});

				// panneau de contenu principal
				JPanel content = new JPanel(new MigLayout());

				// assembler puis montrer
				content.add(refresh, "wrap");
				content.add(sp, "width 98%!, height 500");

				frame.setContentPane(content);
				frame.setVisible(true);
			}
		});
	}

}
