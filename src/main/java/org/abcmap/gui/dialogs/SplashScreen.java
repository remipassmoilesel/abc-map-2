package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.events.manager.Event;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.HtmlLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SplashScreen extends JDialog implements HasEventNotificationManager {

    private EventNotificationManager om;

    public SplashScreen() {
        super();


        setUndecorated(true);
        setModal(true);

        setSize(new Dimension(400, 400));
        setLocationRelativeTo(null);

        om = new EventNotificationManager(this);
        om.setDefaultListener(new EventListener() {
            @Override
            public void notificationReceived(Event arg) {
                SplashScreen.this.dispose();
            }
        });

        JPanel content = new JPanel(new MigLayout("insets 20, fillx"));
        content.setBorder(BorderFactory.createLineBorder(Color.lightGray, 7));

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
    public EventNotificationManager getNotificationManager() {
        return om;
    }

}
