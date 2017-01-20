package org.abcmap.gui.components.fileselection;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

/**
 * Panel which show a file list and allow user to select them
 *
 * @author remipassmoilesel
 */
public class FileSelectionPanel extends JPanel {

    private DefaultListModel<File> filesModel;
    private JList<File> jlist;
    private JButton resetButton;
    private JButton actionButton;
    private File activeFile;

    public FileSelectionPanel() {

        super(new MigLayout("insets 0, fillx"));

        this.activeFile = null;

        this.filesModel = new DefaultListModel<>();

        jlist = new JList<>(filesModel);
        jlist.setAlignmentY(Component.TOP_ALIGNMENT);
        jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
        jlist.setBorder(BorderFactory.createLineBorder(Color.gray));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setVisibleRowCount(5);
        jlist.setCellRenderer(new FileSelectionRenderer());
        jlist.addListSelectionListener(new SelectionListener());

        JScrollPane sp = new JScrollPane(jlist);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(sp, "span, width 95%!, height 130px!, wrap 8px");

        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0, gap 5"));

        actionButton = new JButton("Ouvrir");
        buttonsPanel.add(actionButton);

        resetButton = new JButton("Effacer tout");
        buttonsPanel.add(resetButton);

        // set active to null on reset
        resetButton.addActionListener((ev) -> {
            activeFile = null;
            jlist.setSelectedValue(null, false);
        });

        add(buttonsPanel, "width 95%!");

    }

    /**
     * Listen user actions and change active file
     */
    private class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {

            File selection = jlist.getSelectedValue();
            if (selection != null && selection.equals(activeFile) == false) {
                activeFile = selection;
            }
        }
    }

    /**
     * Add a listener to action button
     *
     * @param al
     */
    public void addActionButtonListener(ActionListener al) {
        actionButton.addActionListener(al);
    }

    /**
     * Add a listener to reset button
     *
     * @param al
     */
    public void addResetButtonListener(ActionListener al) {
        resetButton.addActionListener(al);
    }

    /**
     * Remove all file names from the panel
     */
    public void clearFileList() {
        filesModel.clear();
        jlist.revalidate();
        jlist.repaint();
    }

    /**
     * Add files to list and display them
     *
     * @param paths
     */
    public void addPaths(Collection<String> paths) {

        for (String p : paths) {
            filesModel.addElement(new File(p));
        }

        jlist.revalidate();
        jlist.repaint();

    }

    /**
     * Add files to list and display them
     *
     * @param files
     */
    public void addFiles(Collection<File> files) {

        for (File f : files) {
            filesModel.addElement(f);
        }

        jlist.revalidate();
        jlist.repaint();

    }

    /**
     * Return selected file
     *
     * @return
     */
    public File getActiveFile() {
        return activeFile;
    }

    /**
     * Revalidate and repaint component
     */
    public void refresh() {
        revalidate();
        repaint();
    }

}
