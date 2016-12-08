package org.abcmap.gui.components.fileselection;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.utils.Refreshable;

/**
 * Panel which show a file list and allow user to select them
 *
 * @author remipassmoilesel
 */
public class FileSelectionPanel extends JPanel implements Refreshable {

    private DefaultListModel<File> filesModel;
    private JList<File> jlist;
    private JButton resetButton;
    private JButton actionButton;
    private File activeFile;

    public FileSelectionPanel() {

        super(new MigLayout("insets 5"));

        this.activeFile = null;

        this.filesModel = new DefaultListModel<File>();

        jlist = new JList<File>(filesModel);
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
        add(sp, "span, width 90%, height 130px!, wrap 8px");

        actionButton = new JButton("Ouvrir");
        add(actionButton);

        resetButton = new JButton("Effacer l'historique");
        add(resetButton, "wrap");

    }

    public FileSelectionPanel(Collection<File> files) {
        this();
        addFiles(files);
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


    @Override
    public void refresh() {
        revalidate();
        repaint();
    }

    @Override
    public void reconstruct() {
        revalidate();
        repaint();
    }
}
