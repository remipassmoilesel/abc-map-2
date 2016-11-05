package org.abcmap.core.notifications.monitoringtool;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Small tool allow to see the lasts transmitted events with several informations
 */
public class NotificationManagerTool {

    public static void showLastEventsTransmitted() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // build window
                JFrame frame = new JFrame("Last transmitted events");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);

                // get last events
                final LastNotificationsPanel events = new LastNotificationsPanel();
                events.refresh();

                JScrollPane sp = new JScrollPane(events);
                sp.getVerticalScrollBar().setUnitIncrement(100);

                // refresh button
                JButton refresh = new JButton("Refresh");
                refresh.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        events.refresh();
                    }
                });

                // main panel
                JPanel content = new JPanel(new MigLayout());

                content.add(refresh, "wrap");
                content.add(sp, "width 98%!, height 500");

                frame.setContentPane(content);
                frame.setVisible(true);
            }
        });
    }

}
