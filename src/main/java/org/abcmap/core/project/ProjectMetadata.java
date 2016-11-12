package org.abcmap.core.project;

import org.abcmap.core.utils.Utils;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;


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

    public HashMap<PMConstants, String> getMetadata() {
        return metadataList;
    }

    public void setMetadataList(HashMap<PMConstants, String> metadataList) {
        this.metadataList = metadataList;
    }

    public void updateValue(PMConstants name, String value) {
        metadataList.put(name, value);
    }

    public String getValue(PMConstants name) {
        return metadataList.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectMetadata that = (ProjectMetadata) o;

        return metadataList != null ? metadataList.equals(that.metadataList) : that.metadataList == null;
    }

    @Override
    public int hashCode() {
        return metadataList != null ? metadataList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProjectMetadata{" +
                "metadataList=" + metadataList +
                '}';
    }
}
