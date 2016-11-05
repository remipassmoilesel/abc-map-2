package org.abcmap.core.notifications.monitoringtool;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class NotificationHistoryPanel extends JPanel {

    private NotificationHistoryElement elmt;
    private Notification ev;
    private NotificationManager om;
    private Object owner;

    public NotificationHistoryPanel(NotificationHistoryElement elmt) {

        GuiUtils.throwIfNotOnEDT();

        this.elmt = elmt;
        this.ev = elmt.getNotification();
        this.om = elmt.getObserverManager();
        this.owner = elmt.getOwner();

        // layout manager
        setLayout(new MigLayout("insets 10"));


        setBorder(BorderFactory.createLineBorder(Color.lightGray));

        GuiUtils.addLabel("<h4>#" + ev.getInstanceNumber() + ": "
                + ev.getClass().getSimpleName() + " - " + ev.hashCode()
                + "</h4>", this, "span, wrap");

        GuiUtils.addLabel(
                "Name - Value: <b>" + ev.getName() + " - " + ev.getValue(),
                this, "span, wrap");

        GuiUtils.addLabel("Created: " + ev.getCreationTime(), this,
                "span, wrap");

        GuiUtils.addLabel("Instance n°: " + ev.getInstanceNumber(), this,
                "wrap");

        GuiUtils.addLabel("Owner: "
                + owner.getClass(), this, "wrap");

        GuiUtils.addLabel(
                "Transmitted by: " + om.getClass() + " - " + om.hashCode(), this,
                "wrap");

        JPanel receivers = new ObserversDisplayPanel(elmt.getObservers());
        add(receivers, "span, width 98%!");

    }

    /**
     * Display notification destinations
     */
    private static class ObserversDisplayPanel extends JPanel implements
            ActionListener {

        private ArrayList<NotificationManager> observers;
        private boolean receiversAreShowed;
        private JButton buttonShowReceivers;
        private HtmlLabel labelNbrObservers;

        public ObserversDisplayPanel(ArrayList<NotificationManager> observers) {

            GuiUtils.throwIfNotOnEDT();

            this.observers = observers;
            this.setLayout(new MigLayout());

            this.labelNbrObservers = new HtmlLabel(observers.size()
                    + " observers");

            this.buttonShowReceivers = new JButton(
                    "Show / Hide observers");
            buttonShowReceivers.addActionListener(this);

            this.receiversAreShowed = false;
            showObservers(receiversAreShowed);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            receiversAreShowed = !receiversAreShowed;

            showObservers(receiversAreShowed);

        }

        public void showObservers(boolean state) {


            this.removeAll();

            this.add(labelNbrObservers);
            this.add(buttonShowReceivers, "wrap");

            if (state == true) {

                int i = 0;

                for (NotificationManager om : observers) {

                    String className = om.getClass().getName();
                    String owner = om.getOwner().getClass().getName();
                    String text = "<html>#" + i + ":";
                    text += "<br>Owner class: " + owner + " / " + className;
                    text += "</html>";

                    HtmlLabel label = new HtmlLabel(text);
                    label.setBorder(BorderFactory
                            .createLineBorder(Color.lightGray));

                    this.add(label, "span, width 98%!, wrap");

                    i++;
                }
            }

            revalidate();
            repaint();

        }

    }

}
