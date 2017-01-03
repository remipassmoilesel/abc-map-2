package org.abcmap.gui.components.search;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.Main;
import org.abcmap.core.utils.CoeffComparator;
import org.abcmap.core.utils.Utils;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Special text field which allow user to search interaction elements
 */
public class CommandSearchTextField extends InteractiveTextField {

    public static final Integer POPUP_WIDTH_PX = 350;

    private ArrayList<InteractionElement> ielements;
    private ArrayList<Component> ieGUIs;

    private String lastSearch = "";
    private int maxResultDisplayed;

    private ReentrantLock searchLock = new ReentrantLock();

    public CommandSearchTextField() {
        super();

        this.maxResultDisplayed = 30;

        // list all search possibilities
        ielements = InteractionElement.getAllAvailablesInteractionElements();
        ielements.addAll(InteractionElement.getAllAvailablesPlugins());

        // create all graphics components and store them
        this.ieGUIs = new ArrayList<>();
        for (InteractionElement ie : ielements) {
            ieGUIs.add(new IESearchResultPanel(getPopup(), ie));
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

            // create regex from terms
            String[] words = Pattern.compile("\\s+").split(text);
            Pattern[] patterns = new Pattern[words.length];

            for (int i = 0; i < words.length; i++) {

                String w = words[i];

                // replace all special chars
                String regex = w.replaceAll("\\W", ".").toLowerCase();
                regex = regex.replaceAll("[eao]", ".").toLowerCase();

                patterns[i] = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }

            ArrayList<CoeffComparator.ComparableObject> results = new ArrayList<>();

            // Coefficient of importance of fields
            // here a match in label count 2 points, match in help only one
            Integer[] points = new Integer[]{2, 1};

            for (InteractionElement ie : ielements) {

                String label = ie.getLabel();
                String help = ie.getHelp();

                String[] searchIn = new String[]{label, help};

                CoeffComparator.ComparableObject sr = CoeffComparator.getComparableFor(ie);
                for (int i = 0; i < searchIn.length; i++) {

                    String currentSearch = searchIn[i];

                    if (currentSearch == null) {
                        continue;
                    }

                    for (int j = 0; j < patterns.length; j++) {

                        Matcher m = patterns[j].matcher(currentSearch);

                        while (m.find()) {
                            sr.addToCoeff(points[i]);
                        }
                    }

                }

                results.add(sr);

            }

            // sort by points
            CoeffComparator.sort(results, CoeffComparator.Order.DESCENDING);

            // get result container
            JPanel ctr = getPopupContentPane();
            ctr.removeAll();

            // display results
            int displayedResults = 0;
            int height = 0;
            for (CoeffComparator.ComparableObject obj : results) {

                if (obj.getCoeff() == 0) {
                    break;
                }

                int index = ielements.indexOf(obj.getObject());
                Component comp = ieGUIs.get(index);

                ctr.add(comp, "width " + (POPUP_WIDTH_PX - 10) + "px!, wrap");

                height += comp.getPreferredSize().height;

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

                // affichage du packet parent pour debogage
                if (Main.isDebugMode()) {
                    GuiUtils.addLabel("Nom du package parent: " + ConfigurationConstants.IE_PACKAGE_ROOT, ctr, "wrap");
                    GuiUtils.addLabel("Pattern: " + Utils.join(", ", Arrays.asList(patterns)), ctr, "wrap");
                }
            }

            showPopup(true);

        } finally {
            searchLock.unlock();
        }

    }

}
