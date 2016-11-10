package org.abcmap.core.project;

import org.abcmap.core.utils.Utils;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;


/**
 * Project metadata container. All metadata names must be constants stored in PMContants enum.
 */
public class ProjectMetadata {

    private HashMap<PMConstants, String> metadatas;

    public ProjectMetadata() {
        metadatas = new HashMap<>();

        // default metadatas
        metadatas.put(PMConstants.TITLE, "Project title");
        metadatas.put(PMConstants.COMMENT, "Project comment");
        metadatas.put(PMConstants.CREATED, (new Date()).toString());
        metadatas.put(PMConstants.BG_COLOR, Utils.colorToString(Color.white));
        metadatas.put(PMConstants.LAYOUT_FRAME_OPACITY, String.valueOf(0.6f));
        metadatas.put(PMConstants.LAYOUT_FRAME_THICKNESS, String.valueOf(20));
        metadatas.put(PMConstants.LAYOUT_FRAME_COLOR_1, Utils.colorToString(Color.blue));
        metadatas.put(PMConstants.LAYOUT_FRAME_COLOR_2, Utils.colorToString(Color.red));

    }

    public HashMap<PMConstants, String> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(HashMap<PMConstants, String> metadatas) {
        this.metadatas = metadatas;
    }

    public void updateValue(PMConstants name, String value) {
        metadatas.put(name, value);
    }

    public String getValue(PMConstants name) {
        return metadatas.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectMetadata that = (ProjectMetadata) o;

        return metadatas != null ? metadatas.equals(that.metadatas) : that.metadatas == null;
    }

    @Override
    public int hashCode() {
        return metadatas != null ? metadatas.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProjectMetadata{" +
                "metadatas=" + metadatas +
                '}';
    }
}
