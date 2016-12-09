package org.abcmap.gui.windows;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.components.progressbar.HasProgressbarManager;
import org.abcmap.gui.components.progressbar.ProgressbarManager;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.toProcess.gui.ie.display.window.ShowMainWindow;
import org.abcmap.gui.toProcess.gui.iegroup.InteractionElementGroup;
import org.abcmap.gui.utils.HasDisplayableSpace;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Smaller window that can be hold components "detached" from main window
 */
public class DetachedWindow extends AbstractCustomWindow implements HasNotificationManager, HasDisplayableSpace, HasProgressbarManager {

    private static final Dimension WINDOW_PREF_SIZE = new Dimension(300, 500);

    /**
     * Object to display in window
     */
    private Object displayable;
    /**
     * Panel on top off window with return command, etc...
     */
    private JPanel headerPane;

    /**
     * Main content of window
     */
    private JPanel contentPane;

    /**
     * View inside main scrollpane
     */
    private JPanel viewportView;

    /**
     * If set to true, "return to main window" button will be visible
     */
    private boolean buttonToMainWindowEnabled;

    private GuiManager guim;
    private ProgressbarManager progressbarManager;
    private NotificationManager notifm;

    public DetachedWindow() {
        super();

        this.guim = MainManager.getGuiManager();
        this.notifm = new NotificationManager(this);

        this.setSize(WINDOW_PREF_SIZE);
        this.setResizable(false);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new CustomWindowListener());

        contentPane = new JPanel(new MigLayout("insets 0, gap 0, fill"));
        this.setContentPane(contentPane);

        buttonToMainWindowEnabled = true;

        setAlwaysOnTop(true);

        reconstruct();
    }

    @Override
    public void displayComponent(JComponent displayable) {
        this.displayable = displayable;
        reconstruct();
        refresh();
    }

    public void displayComponent(InteractionElementGroup group) {
        this.displayable = group;
    }

    public void reconstruct() {

        contentPane.removeAll();

        // header panel
        headerPane = new JPanel(new MigLayout("insets 5, gap 5"));
        headerPane.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        contentPane.add(headerPane, "north");

        // "always on top" button
        HtmlCheckbox chkAlwaysOnTop = new HtmlCheckbox("Afficher cette fenêtre toujours au dessus des autres.");
        chkAlwaysOnTop.setSelected(true);
        chkAlwaysOnTop.addActionListener((ActionEvent arg0) -> {
            DetachedWindow.this.setAlwaysOnTop(((AbstractButton) arg0
                    .getSource()).isSelected());

        });
        headerPane.add(chkAlwaysOnTop, "wrap");

        // "return to main" button
        if (buttonToMainWindowEnabled) {
            HtmlButton btnMainWindow = new HtmlButton("Retour à la fenêtre principale");
            btnMainWindow.addActionListener(new ShowMainWindow());
            headerPane.add(btnMainWindow, "align right, wrap");
        }

        // progress bar
        progressbarManager = new ProgressbarManager();
        progressbarManager.setComponentsVisible(false);

        // scroll pane
        viewportView = new JPanel(new MigLayout("insets 5, gap 5"));

        JScrollPane scrollPane = new JScrollPane(viewportView);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        contentPane.add(scrollPane, "center, grow, push");

        // if nothing to display, return
        if (displayable == null) {
            return;
        }

        // object to display is a group
        if (displayable instanceof InteractionElementGroup) {

            for (InteractionElement e : ((InteractionElementGroup) displayable).getElements()) {

                if (e.isHiddenInDetachedWindow()) {
                    continue;
                }

                addViewportItem(e.getBlockGUI());
            }

        }

        // component is a Swing element
        else if (displayable instanceof Component) {
            viewportView.add((Component) displayable);
        }

        // unknown component
        else {
            throw new IllegalArgumentException("Unknown displayable object: " + displayable);
        }


        refresh();
    }

    /**
     * Repaint window and children
     */
    public void refresh() {

        headerPane.revalidate();
        headerPane.repaint();

        contentPane.revalidate();
        contentPane.repaint();

        revalidate();
        repaint();
    }

    /**
     * Add component into scrollpane viewport
     *
     * @param c
     */
    private void addViewportItem(Component c) {
        viewportView.add(c, "width " + (DetachedWindow.WINDOW_PREF_SIZE.width - 40) + "!, wrap");
    }

    /**
     * Hide this window when closing and show main window
     */
    public class CustomWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            setVisible(false);
            new ShowMainWindow().run();
        }
    }

    public void moveToDefaultPosition() {
        setLocation(new Point(20, 20));
    }

    @Override
    public NotificationManager getNotificationManager() {
        return notifm;
    }

    @Deprecated
    @Override
    public void dispose() {
        super.dispose();
    }

    public ProgressbarManager getProgressbarManager() {
        return progressbarManager;
    }

    public void setMainWindowButtonVisible(boolean b) {
        this.buttonToMainWindowEnabled = b;
    }

}
