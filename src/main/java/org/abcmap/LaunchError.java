package org.abcmap;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Standalone class which allow to show errors if the software cannot initialize itself.
 * <p>
 * /!\ This class absolutely need to be standalone.
 */
public class LaunchError {

    private static final CustomLogger logger = LogManager.getLogger(LaunchError.class);

    /**
     * Only static methods here
     */
    private LaunchError() {
    }

    private static final String MESSAGE_FR = "Erreur lors du lancement d'Abc-Map. Le logiciel n'a pas pu s'initialiser correctement. " +
            "Veuillez consulter l'aide en ligne pour plus d'informations: " + ConfigurationConstants.WEBSITE_URL;

    private static final String MESSAGE_EN = "Error while launching Abc-Map. The software could not initialize properly. Please consult " +
            "the online help for more information: " + ConfigurationConstants.WEBSITE_URL;

    private static void showConsoleError() {

        System.err.println("");
        System.err.println(MESSAGE_EN);
        System.err.println("");
        System.err.println(MESSAGE_FR);
        System.err.println("");
        System.err.println("");
    }

    /**
     * Show an error and die
     * <p>
     * If a cause is specified, output will be shown in console
     */
    public static void showErrorAndDie() {
        showErrorAndDie(null);
    }

    /**
     * Show an error and die
     * <p>
     * If a cause is specified, output will be shown in console
     *
     * @param cause
     */
    public static void showErrorAndDie(Throwable cause) {

        showConsoleError();

        try {

            SwingUtilities.invokeAndWait(() -> {

                // show a dialog with an error message
                final JDialog dial = new JDialog();
                JPanel content = new JPanel(new MigLayout("insets 20"));

                // close the dialog
                ActionListener leaveAL = (e) -> {
                    dial.dispose();
                };

                // open web browser and close window
                ActionListener websiteAL = (e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(ConfigurationConstants.WEBSITE_URL));
                        dial.dispose();
                    } catch (Exception e1) {
                        String enError = "<p>Unable to open your web browser. Website is available at: " + ConfigurationConstants.WEBSITE_URL + ".</p>";
                        String frError = "<p>Impossible d'ouvrir votre navigateur. Le site est disponible à l'adresse: " + ConfigurationConstants.WEBSITE_URL + "</p>";
                        JOptionPane.showMessageDialog(null, "<html>" + enError + frError + "</html>");
                        logger.error(e1);
                    }
                };

                // English error
                content.add(new JLabel("<html><h3>Error</h3></html>"), "gapbottom 20, wrap");
                content.add(new JLabel("<html><p>" + MESSAGE_EN + "</p></html>"), "gapbottom 20, wrap");

                // English buttons
                JPanel buttons2 = new JPanel(new MigLayout("insets 10"));
                JButton b2b1 = new JButton("Website");
                b2b1.addActionListener(websiteAL);
                JButton b2b2 = new JButton("Leave");
                b2b2.addActionListener(leaveAL);
                buttons2.add(b2b1);
                buttons2.add(b2b2);
                content.add(buttons2, "align right, wrap");

                // French error
                content.add(new JLabel("<html><h3>Erreur</h3></html>"), "gapbottom 20, wrap");
                content.add(new JLabel("<html><p>" + MESSAGE_FR + "</p></html>"), "gapbottom 20, wrap");

                // French buttons
                JPanel buttons1 = new JPanel(new MigLayout("insets 10"));
                JButton b1b1 = new JButton("Site internet");
                b1b1.addActionListener(websiteAL);
                JButton b1b2 = new JButton("Quitter");
                b1b2.addActionListener(leaveAL);
                buttons1.add(b1b1);
                buttons1.add(b1b2);
                content.add(buttons1, "align right, wrap");

                dial.setContentPane(content);
                dial.pack();
                dial.setResizable(false);
                dial.setLocationRelativeTo(null);
                dial.setModal(true);
                dial.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                dial.setVisible(true);

            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (cause != null) {
            System.err.println("Caused by: " + cause.getClass().getName() + " " + cause.getMessage());
            cause.printStackTrace();
        }

        // exit program
        System.exit(1);

    }

}
