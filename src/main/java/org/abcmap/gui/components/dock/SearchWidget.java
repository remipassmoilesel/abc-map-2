package org.abcmap.gui.components.dock;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.Main;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.dock.blockitems.DockMenuPanel;
import org.abcmap.gui.components.search.IESearchLibrary;
import org.abcmap.gui.components.search.IESearchResultPanel;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Dock widget that show current tool and active colors
 */
public class SearchWidget extends DockMenuWidget {

    /**
     * Text field used to search
     */
    private final JTextField searchTextField;

    /**
     * Panel where results are displayed
     */
    private final JPanel resultsPanel;

    /**
     * Library of interaction elements where perform search
     */
    private final IESearchLibrary library;

    /**
     * Lock used to prevent thread issues
     */
    private final ReentrantLock searchLock;

    /**
     * Copy of list of all availables elements
     */
    private final ArrayList<InteractionElement> ielements;

    /**
     * List of available GUI elements
     */
    private final ArrayList<Component> ieGUIs;

    /**
     * Maximum width of components
     */
    private final int maxResultWidth;

    /**
     * Maximum number of results displayed
     */
    private final int maxResultDisplayed;

    /**
     * Last search used to prevent too much process
     */
    private String lastSearch;

    public SearchWidget() {

        // set general properties
        setIcon(GuiIcons.SEARCH_WIDGET);
        setToolTipText("Rechercher...");

        // create a library of elements where search elements
        library = new IESearchLibrary();

        // create special menu panel
        menuPanel = new DockMenuPanel();
        menuPanel.setMenuTitle("Recherche");

        // create search components and add it to menu panel
        searchTextField = new JTextField();
        resultsPanel = new JPanel(new MigLayout("insets 0, gap 0"));
        menuPanel.addMenuElement(searchTextField);
        menuPanel.addMenuElement(resultsPanel);

        menuPanel.reconstruct();

        // search when user type
        searchTextField.addKeyListener(new SearchInteractionElements());

        // remove normal listener and add special listener
        removeActionListener(openMenuListener);
        addActionListener(new WidgetActionListener());

        // lock to prevent thread issues
        searchLock = new ReentrantLock();

        // get a copy of interaction elements stored
        ielements = library.getInteractionElements();

        // prepare graphics components and store them in order to speed up search
        // these components have same index as interaction elements
        this.ieGUIs = new ArrayList<>();
        for (InteractionElement ie : ielements) {
            ieGUIs.add(new IESearchResultPanel(ie));
        }

        this.maxResultWidth = 250;
        this.maxResultDisplayed = 30;

    }

    private class SearchInteractionElements extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {

            if (searchLock.tryLock() == false) {
                return;
            }

            try {

                // get search terms
                String terms = searchTextField.getText();
                if (terms == null) {
                    return;
                }

                // search terms does not changed
                if (Utils.safeEquals(lastSearch, terms)) {
                    return;
                } else {
                    lastSearch = terms;
                }

                ArrayList<InteractionElement> results = library.searchInInteractionElementsAndPlugins(terms);

                // get result container and remove old results
                resultsPanel.removeAll();

                // display results
                int displayedResults = 0;
                int height = 0;
                for (InteractionElement element : results) {

                    // get gui associated with element
                    int index = ielements.indexOf(element);
                    Component comp = ieGUIs.get(index);

                    // add gui
                    resultsPanel.add(comp, "width 97%!, wrap 5px");

                    // compute total height
                    height += comp.getPreferredSize().height;

                    // break if too much results displayed
                    if (displayedResults > maxResultDisplayed) {
                        break;
                    }

                    displayedResults++;
                }

                // no results founds
                if (displayedResults == 0) {
                    GuiUtils.addLabel("Aucune commande ne correspond.", resultsPanel, "wrap");

                    // display package where elements are searched for debug purposes
                    if (Main.isDebugMode()) {
                        GuiUtils.addLabel("Nom du package parent: " + ConfigurationConstants.IE_PACKAGE_ROOT, resultsPanel, "wrap");
                    }
                }

                resultsPanel.revalidate();
                resultsPanel.repaint();

            } finally {
                searchLock.unlock();
            }

        }
    }

    /**
     * Open menu on user click
     */
    private class WidgetActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            getDockParent().showWidgetspace(menuPanel);

            getDockParent().setMenuSelected(SearchWidget.this);

        }
    }

}
