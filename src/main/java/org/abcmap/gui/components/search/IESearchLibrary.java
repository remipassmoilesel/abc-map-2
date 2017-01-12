package org.abcmap.gui.components.search;

import org.abcmap.core.utils.CoeffComparator;
import org.abcmap.ielements.InteractionElement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This object store a list of all available interaction elements and plugin of software, and allow to search in.
 */
public class IESearchLibrary {

    /**
     * List of all interaction elements
     */
    private final ArrayList<InteractionElement> ielements;

    public IESearchLibrary() {

        // list all search possibilities
        ielements = InteractionElement.getAllAvailablesInteractionElements();
        ielements.addAll(InteractionElement.getAllAvailablesPlugins());

    }

    /**
     * Search in stored interaction elements and return
     *
     * @return
     */
    public ArrayList<InteractionElement> searchInInteractionElementsAndPlugins(String keywords) {

        // create regex from terms
        String[] words = Pattern.compile("\\s+").split(keywords);
        Pattern[] patterns = new Pattern[words.length];

        for (int i = 0; i < words.length; i++) {

            String w = words[i];

            // replace all special chars
            String regex = w.replaceAll("\\W", ".").toLowerCase();
            regex = regex.replaceAll("[eao]", ".").toLowerCase();

            patterns[i] = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }

        ArrayList<CoeffComparator.ComparableObject> sortedResults = new ArrayList<>();

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

            sortedResults.add(sr);

        }

        // sort by points
        CoeffComparator.sort(sortedResults, CoeffComparator.Order.DESCENDING);

        // return list
        ArrayList<InteractionElement> ies = new ArrayList<>();
        for (CoeffComparator.ComparableObject obj : sortedResults) {

            // break when coeff is empty
            if (obj.getCoeff() == 0) {
                break;
            }

            ies.add((InteractionElement) obj.getObject());
        }

        return ies;

    }

    /**
     * Return a shallow copy of interaction elements list
     *
     * @return
     */
    public ArrayList<InteractionElement> getInteractionElements() {
        return new ArrayList<>(ielements);
    }
}
