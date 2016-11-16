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

    private HashMap<PMConstants, String> metadataList;

    public ProjectMetadata() {

        metadataList = new HashMap<>();

        // default metadataList
        metadataList.put(PMConstants.TITLE, "Project title");
        metadataList.put(PMConstants.COMMENT, "Project comment");
        metadataList.put(PMConstants.CREATED, (new Date()).toString());
        metadataList.put(PMConstants.BG_COLOR, Utils.colorToString(Color.white));
        metadataList.put(PMConstants.LAYOUT_FRAME_OPACITY, String.valueOf(0.6f));
        metadataList.put(PMConstants.LAYOUT_FRAME_THICKNESS, String.valueOf(20));
        metadataList.put(PMConstants.LAYOUT_FRAME_COLOR_1, Utils.colorToString(Color.blue));
        metadataList.put(PMConstants.LAYOUT_FRAME_COLOR_2, Utils.colorToString(Color.red));

    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(PMConstants name, String value) {
        metadataList.put(name, value);
    }

    /**
     * Get the corresponding metadata
     *
     * @param name
     * @return
     */
    public String getValue(PMConstants name) {
        return metadataList.get(name);
    }

    public HashMap<PMConstants, String> getAllValues() {
        return metadataList;
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
