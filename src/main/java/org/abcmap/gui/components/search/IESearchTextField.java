package org.abcmap.gui.components.search;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Special text field which allow user to search interaction elements
 */
public class IESearchTextField extends PopupTextField {

    public static final Integer POPUP_WIDTH_PX = 350;

    /**
     * Library of interaction elements
     */
    private final IESearchLibrary library;

    /**
     * Copy of interaction elements list
     */
    private final ArrayList<InteractionElement> ielements;

    /**
     * List og GUI elements wrapped in
     */
    private ArrayList<Component> ieGUIs;

    /**
     * Last search string used to prevent too much search
     */
    private String lastSearch = "";

    /**
     * Maximum number of results displayede
     */
    private int maxResultDisplayed;

    /**
     * Search lock used to prevent
     */
    private ReentrantLock searchLock = new ReentrantLock();

    public IESearchTextField() {
        super();

        this.maxResultDisplayed = 30;

        // create a search library
        library = new IESearchLibrary();

        // get a copy of interaction elements stored
        ielements = library.getInteractionElements();

        // prepare graphics components and store them in order to speed up search
        // these components have same index as interaction elements
        this.ieGUIs = new ArrayList<>();
        for (InteractionElement ie : ielements) {
            ieGUIs.add(new IESearchResultPanel(ie, getPopup()));
        }

    }

    @Override
    protected void userHaveTypedThis(String text) {

        if (searchLock.tryLock() == false) {
            return;
        }

        try {

            // search terms does not changed
            if (lastSearch.equals(text)) {
                showPopup(true);
                return;
            } else {
                lastSearch = new String(text);
            }

            ArrayList<InteractionElement> results = library.searchInInteractionElementsAndPlugins(text);

            // get result container
            JPanel ctr = getPopupContentPane();
            ctr.removeAll();

            // display results
            int displayedResults = 0;
            int height = 0;
            for (InteractionElement element : results) {

                // get gui associated with element
                int index = ielements.indexOf(element);
                Component comp = ieGUIs.get(index);

                // add gui
                ctr.add(comp, "width " + (POPUP_WIDTH_PX - 10) + "px!, wrap");

                // compute total height
                height += comp.getPreferredSize().height;

                // break if too much results displayed
                if (displayedResults > maxResultDisplayed) {
                    break;
                }

                displayedResults++;
            }

            // adapt menu height
            getPopup().proposePopupHeight(height);

            // no results founds
            if (displayedResults == 0) {
                GuiUtils.addLabel("Aucune commande ne correspond.", ctr, "wrap");

                // display package where elements are searched for debug purposes
                if (Main.isDebugMode()) {
                    GuiUtils.addLabel("Nom du package parent: " + ConfigurationConstants.IE_PACKAGE_ROOT, ctr, "wrap");
                }
            }

            showPopup(true);

        } finally {
            searchLock.unlock();
        }

    }

}
