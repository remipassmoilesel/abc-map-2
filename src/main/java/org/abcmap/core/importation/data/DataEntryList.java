package org.abcmap.core.importation.data;

import org.abcmap.core.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * List of data entries
 */
public class DataEntryList extends ArrayList<DataEntry> {

    /**
     * Optionnal list of comments
     */
    private ArrayList<String> comments;

    public DataEntryList() {
        this.comments = new ArrayList<>();
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Join all comments in one string
     *
     * @return
     */
    public String getOneStringComments() {
        return Utils.join(" ", comments);
    }

    /**
     * Return all headers available, exlcuding latitude and longitude headers
     * <p>
     * /!\ Each entry can have different number of header
     *
     * @return
     */
    public Set<String> getAllHeaders() {

        HashSet<String> rslt = new HashSet<>();

        for (DataEntry entry : this) {
            rslt.addAll(entry.getFieldNames());
        }

        return rslt;
    }

}
