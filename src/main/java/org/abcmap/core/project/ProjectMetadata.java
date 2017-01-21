package org.abcmap.core.project;

import org.abcmap.core.utils.Utils;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * Project metadata container. All metadata names must be constants stored in PMContants enum.
 */
public class ProjectMetadata {

    private HashMap<String, String> metadataList;

    public ProjectMetadata() {

        metadataList = new HashMap<>();

        // default metadataList
        metadataList.put(PMNames.TITLE.toString(), "Project title");
        metadataList.put(PMNames.COMMENT.toString(), "Project comment");
        metadataList.put(PMNames.CREATED.toString(), (new Date()).toString());
        metadataList.put(PMNames.BG_COLOR.toString(), Utils.colorToString(Color.white));
        metadataList.put(PMNames.LAYOUT_FRAME_OPACITY.toString(), String.valueOf(0.6f));
        metadataList.put(PMNames.LAYOUT_FRAME_THICKNESS.toString(), String.valueOf(20));
        metadataList.put(PMNames.LAYOUT_FRAME_COLOR_1.toString(), Utils.colorToString(Color.blue));
        metadataList.put(PMNames.LAYOUT_FRAME_COLOR_2.toString(), Utils.colorToString(Color.red));

    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(PMNames name, String value) {
        metadataList.put(name.toString(), value);
    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(String name, String value) {
        metadataList.put(name, value);
    }

    /**
     * Add a custom value
     * <p>
     * If key exist, a runtime error is thrown
     *
     * @param name
     * @param value
     */
    public void addValue(String name, String value) {

        if (metadataList.get(name) != null) {
            throw new IllegalArgumentException("Name already exist: " + name);
        }
        metadataList.put(name.toString(), value);
    }

    /**
     * Get the corresponding metadata
     *
     * @param name
     * @return
     */
    public String getValue(PMNames name) {
        return metadataList.get(name.toString());
    }

    /**
     * Return all values
     * <p>
     * Values are returned in a shallow copy of map
     *
     * @return
     */
    public HashMap<String, String> getAllValues() {
        return new HashMap<>(metadataList);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMetadata that = (ProjectMetadata) o;
        return Objects.equals(metadataList, that.metadataList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadataList);
    }
}
