package abcmap.gui.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiIcons;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

public class SplashScreen extends JDialog implements HasNotificationManager {

	private NotificationManager om;

	public SplashScreen() {

		super();

		// style
		setUndecorated(true);
		setModal(true);

		setSize(new Dimension(400, 400));
		setLocationRelativeTo(null);

		// fermer au lancement de l'application
		om = new NotificationManager(this);
		om.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {
				SplashScreen.this.dispose();
			}
		});

		// contenu
		JPanel content = new JPanel(new MigLayout("insets 20, fillx"));
		content.setBorder(BorderFactory.createLineBorder(Color.lightGray, 7));

		// bouton fermer
		HtmlLabel close = new HtmlLabel("X");
		close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				SplashScreen.this.dispose();
			}
		});
		content.add(close, "align right, wrap");

		// splash
		content.add(new JLabel(GuiIcons.SPLASH_SCREEN), "align center");

		setContentPane(content);

	}

	@Override
	public NotificationManager getNotificationManager() {
		return om;
	}

}
