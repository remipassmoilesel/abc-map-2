package abcmap;

import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.configuration.ConfigurationConstants;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class LaunchError {

	public static final int ERRMSG_BASE = 0;
	public static final int ERRMSG_UPDATE_JAVA_VERSION = 1;
	public static final int ERRMSG_HEADLESS_ENV = 2;
	public static final int NO_MESSAGE_ID = -1;

	private static final String[] ERRMSGS_FR = new String[] {

			// message de base, apparait dans toutes les erreurs
			"Une erreur s'est produite lors du lancement du programme ... à compléter.",

			// erreur d'install java
			"Veuillez mettre à jour votre installation Java.",

			// envrronnement incorrect
			"Votre environnement n'est pas adapté au logiciel."

	};

	private static final String[] ERRMSGS_GB = new String[] {

			// message de base, apparait dans toutes les erreurs
			"Error happened while launching software ... to complete.",

			// erreur d'install java
			"Please update your Java installation.",

			// envrronnement incorrect
			"Incorect environnement."

	};

	public static void showConsoleError(int msgid) {

		String messageFR = getMessageFr(msgid);
		String messageGB = getMessageGb(msgid);

		System.out.println("Abc-Map: Erreur de lancement.");
		System.out.println("-----------------------------");
		System.out.println(messageFR);
		System.out.println();
		System.out.println("Abc-Map: Error while launching.");
		System.out.println("-----------------------------");
		System.out.println(messageGB);

	}

	public static String getMessageFr(int msgid) {

		String message = ERRMSGS_FR[ERRMSG_BASE];

		if (msgid != -1) {
			message += " " + ERRMSGS_FR[msgid];
		}

		return message;
	}

	public static String getMessageGb(int msgid) {

		String message = ERRMSGS_GB[ERRMSG_BASE];

		if (msgid != -1) {
			message += " " + ERRMSGS_GB[msgid];
		}

		return message;
	}

	/**
	 * Affiche une erreur et interromp le programme
	 */
	public static void showErrorAndDie() {
		showAndDie(NO_MESSAGE_ID);
	}

	/**
	 * Affiche une erreur et interromp le programme
	 * 
	 * @param msgid
	 */
	public static void showAndDie(final int msgid) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					String messageFR = getMessageFr(msgid);
					String messageGB = getMessageGb(msgid);

					// boite de dialogue
					final JDialog dial = new JDialog();
					JPanel content = new JPanel(new MigLayout("insets 20"));

					ActionListener leaveAL = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dial.dispose();
						}
					};

					ActionListener websiteAL = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Desktop.getDesktop()
										.browse(new URI(ConfigurationConstants.WEBSITE_FAQ_URL + "?msgid=" + msgid));
							} catch (IOException | URISyntaxException e1) {
								if (MainManager.isDebugMode())
									e1.printStackTrace();
							}
						}
					};

					// message principal FR
					GuiUtils.addLabel("<h3>Erreur de lancement</h3>", content, "gapbottom 20, wrap");
					GuiUtils.addLabel(messageFR, content, "gapbottom 20, wrap");

					// boutons FR
					JPanel buttons1 = new JPanel(new MigLayout("insets 10"));
					JButton b1b1 = new JButton("Site internet");
					b1b1.addActionListener(websiteAL);
					JButton b1b2 = new JButton("Quitter");
					b1b2.addActionListener(leaveAL);
					buttons1.add(b1b1);
					buttons1.add(b1b2);
					content.add(buttons1, "align right, wrap");

					// message principal EN
					GuiUtils.addLabel("<h3>Error while launching</h3>", content, "gapbottom 20, wrap");
					GuiUtils.addLabel(messageGB, content, "gapbottom 20, wrap");

					// boutons FR
					JPanel buttons2 = new JPanel(new MigLayout("insets 10"));
					JButton b2b1 = new JButton("Site internet");
					b2b1.addActionListener(websiteAL);
					JButton b2b2 = new JButton("Quitter");
					b2b2.addActionListener(leaveAL);
					buttons2.add(b2b1);
					buttons2.add(b2b2);
					content.add(buttons2, "align right, wrap");

					dial.setContentPane(content);
					dial.pack();
					dial.setResizable(false);
					dial.setLocationRelativeTo(null);
					dial.setModal(true);
					dial.setModalityType(ModalityType.APPLICATION_MODAL);

					dial.setVisible(true);

				}
			});
		}

		catch (InvocationTargetException | InterruptedException e) {
			// TODO: enlever ?
			e.printStackTrace();
		}

		// erreur sur la console
		showConsoleError(msgid);

		// sortie
		System.exit(1);

	}

}
