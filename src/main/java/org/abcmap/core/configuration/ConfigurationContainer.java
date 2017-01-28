package org.abcmap.core.configuration;

import org.abcmap.core.robot.RobotCaptureMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class ConfigurationContainer implements Serializable {

    private HashMap<String, String> metadataList;
    private CFNames anInt;

    public ConfigurationContainer() {

        metadataList = new HashMap<>();

        // default metadataList
        metadataList.put(CFNames.LANGUAGE.toString(), "fr");
        metadataList.put(CFNames.DEFAULT_LANGUAGE.toString(), ConfigurationConstants.DEFAULT_LANGUAGE);
        metadataList.put(CFNames.HOME.toString(), ConfigurationConstants.SYSTEM_HOME_PATH);

        metadataList.put(CFNames.PROFILE_TITLE.toString(), "New configuration profile");
        metadataList.put(CFNames.PROFILE_COMMENT.toString(), "Profile comments");
        metadataList.put(CFNames.PROFILE_PATH.toString(), ConfigurationConstants.DEFAULT_PROFILE_PATH.toString());
        metadataList.put(CFNames.SAVE_PROFILE_WHEN_LEAVE.toString(), String.valueOf(true));

        metadataList.put(CFNames.IMPORT_ENABLE_CROPPING.toString(), String.valueOf(true));
        metadataList.put(CFNames.IMPORT_CROP_AREA_SELECTION_X.toString(), String.valueOf(50));
        metadataList.put(CFNames.IMPORT_CROP_AREA_SELECTION_Y.toString(), String.valueOf(50));
        metadataList.put(CFNames.IMPORT_CROP_AREA_SELECTION_W.toString(), String.valueOf(400));
        metadataList.put(CFNames.IMPORT_CROP_AREA_SELECTION_H.toString(), String.valueOf(400));

        metadataList.put(CFNames.WINDOW_HIDDING_DELAY_MS.toString(), String.valueOf(700));
        metadataList.put(CFNames.IMPORT_MATCHING_POINTS_THRESHOLD.toString(), String.valueOf(20));
        metadataList.put(CFNames.IMPORT_SURF_MODE.toString(), String.valueOf(2));

        metadataList.put(CFNames.ALERT_NOW_IF_REFUSED_TILES.toString(), String.valueOf(true));

        metadataList.put(CFNames.DIRECTORY_IMPORT_PATH.toString(), ConfigurationConstants.SYSTEM_HOME_PATH);
        metadataList.put(CFNames.DOCUMENT_IMPORT_PATH.toString(), ConfigurationConstants.SYSTEM_HOME_PATH);
        metadataList.put(CFNames.IMPORT_DOCUMENT_SCALE_FACTOR.toString(), String.valueOf(1f));

        metadataList.put(CFNames.ROBOT_IMPORT_COVERING.toString(), String.valueOf(0.1f));
        metadataList.put(CFNames.ROBOT_IMPORT_WIDTH.toString(), String.valueOf(5));
        metadataList.put(CFNames.ROBOT_IMPORT_HEIGHT.toString(), String.valueOf(5));
        metadataList.put(CFNames.ROBOT_IMPORT_MOVING_DELAY_MS.toString(), String.valueOf(1000));
        metadataList.put(CFNames.ROBOT_IMPORT_CAPTURE_DELAY_MS.toString(), String.valueOf(2000));

        metadataList.put(CFNames.ROBOT_IMPORT_MODE.toString(), RobotCaptureMode.START_FROM_MIDDLE.toString());


    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(CFNames name, String value) {
        updateValue(name.toString(), value);
    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(CFNames name, Integer value) {
        updateValue(name.toString(), String.valueOf(value));
    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(String name, Integer value) {
        updateValue(name, String.valueOf(value));
    }

    /**
     * Update a single value
     *
     * @param name
     * @param value
     */
    public void updateValue(String name, String value) {

        if (metadataList.get(name) == null) {
            throw new IllegalArgumentException("Specified name does not exist: " + name);
        }

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

        if (metadataList.get(name) == null) {
            throw new IllegalArgumentException("Specified name already exist: " + name);
        }

        metadataList.put(name.toString(), value);
    }

    /**
     * Get the corresponding metadata
     *
     * @param name
     * @return
     */
    public String getValue(CFNames name) {
        return metadataList.get(name.toString());
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Float getFloat(CFNames name) {
        return getFloat(name.toString());
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Float getFloat(String name) {
        try {
            return Float.valueOf(metadataList.get(name));
        } catch (Exception e) {
            throw new IllegalStateException("Error while transforming value", e);
        }
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Integer getInt(CFNames name) {
        return getInt(name.toString());
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Integer getInt(String name) {
        try {
            return Integer.valueOf(metadataList.get(name));
        } catch (Exception e) {
            throw new IllegalStateException("Error while transforming value", e);
        }
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Boolean getBoolean(CFNames name) {
        return getBoolean(name.toString());
    }

    /**
     * Get corresponding value
     *
     * @param name
     * @return
     */
    public Boolean getBoolean(String name) {
        try {
            return Boolean.valueOf(metadataList.get(name));
        } catch (Exception e) {
            throw new IllegalStateException("Error while transforming value", e);
        }
    }

    /**
     * Get the corresponding metadata
     *
     * @param name
     * @return
     */
    public String getValue(String name) {
        return metadataList.get(name);
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
        ConfigurationContainer that = (ConfigurationContainer) o;
        return Objects.equals(metadataList, that.metadataList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadataList);
    }


}
