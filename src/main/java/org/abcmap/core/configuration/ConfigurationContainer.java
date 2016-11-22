package org.abcmap.core.configuration;

import com.labun.surf.Params;

import java.io.Serializable;

/**
 * Main configuration container.
 * <p>
 * Only use serializable objects.
 */
public class ConfigurationContainer implements Serializable {


	/*
     * General settings
	 */

    public String DEFAULT_LANGUAGE = ConfigurationConstants.DEFAULT_LANGUAGE;

    public String HOME = ConfigurationConstants.SYSTEM_HOME_PATH;

    /**
     * Profile settings
     */

    public String PROFILE_TITLE = "New configuration profile";

    public String PROFILE_COMMENT = "Comments";

    public String PROFILE_PATH = ConfigurationConstants.DEFAULT_PROFILE_PATH.toString();

    public Boolean SAVE_PROFILE_WHEN_LEAVE = true;


    /*
     * Import settings
     */
    
    public Boolean IMPORT_ENABLE_CROPPING = true;

    public Integer IMPORT_CROP_AREA_SELECTION_X = 50;

    public Integer IMPORT_CROP_AREA_SELECTION_Y = 50;

    public Integer IMPORT_CROP_AREA_SELECTION_W = 400;

    public Integer IMPORT_CROP_AREA_SELECTION_H = 400;

    /**
     * Delay in milliseconds we have to wait before a window is considered as hidden
     */
    public Integer WINDOW_HIDDING_DELAY = 700;

    /**
     * Number of shapePoints necessary for assembling images,
     */
    public Integer IMPORT_MATCHING_POINTS_THRESHOLD = 20;

    public Integer IMPORT_SURF_MODE = 2;

    public Boolean ALERT_NOW_IF_REFUSED_TILES = false;

    public String DIRECTORY_IMPORT_PATH = ConfigurationConstants.SYSTEM_HOME_PATH;

    public String DOCUMENT_IMPORT_PATH = ConfigurationConstants.SYSTEM_HOME_PATH;

    public Float IMPORT_DOCUMENT_SCALE_FACTOR = 1f;

    public Float ROBOT_IMPORT_COVERING = 0.1f;

    public Integer ROBOT_IMPORT_WIDTH = 5;

    public Integer ROBOT_IMPORT_HEIGHT = 5;

    public Integer ROBOT_IMPORT_MOVING_DELAY = 1000;

    public Integer ROBOT_IMPORT_CAPTURE_DELAY = 2000;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationContainer that = (ConfigurationContainer) o;

        if (DEFAULT_LANGUAGE != null ? !DEFAULT_LANGUAGE.equals(that.DEFAULT_LANGUAGE) : that.DEFAULT_LANGUAGE != null)
            return false;
        if (HOME != null ? !HOME.equals(that.HOME) : that.HOME != null) return false;
        if (PROFILE_TITLE != null ? !PROFILE_TITLE.equals(that.PROFILE_TITLE) : that.PROFILE_TITLE != null)
            return false;
        if (PROFILE_COMMENT != null ? !PROFILE_COMMENT.equals(that.PROFILE_COMMENT) : that.PROFILE_COMMENT != null)
            return false;
        if (PROFILE_PATH != null ? !PROFILE_PATH.equals(that.PROFILE_PATH) : that.PROFILE_PATH != null) return false;
        if (SAVE_PROFILE_WHEN_LEAVE != null ? !SAVE_PROFILE_WHEN_LEAVE.equals(that.SAVE_PROFILE_WHEN_LEAVE) : that.SAVE_PROFILE_WHEN_LEAVE != null)
            return false;
        if (IMPORT_ENABLE_CROPPING != null ? !IMPORT_ENABLE_CROPPING.equals(that.IMPORT_ENABLE_CROPPING) : that.IMPORT_ENABLE_CROPPING != null)
            return false;
        if (IMPORT_CROP_AREA_SELECTION_X != null ? !IMPORT_CROP_AREA_SELECTION_X.equals(that.IMPORT_CROP_AREA_SELECTION_X) : that.IMPORT_CROP_AREA_SELECTION_X != null)
            return false;
        if (IMPORT_CROP_AREA_SELECTION_Y != null ? !IMPORT_CROP_AREA_SELECTION_Y.equals(that.IMPORT_CROP_AREA_SELECTION_Y) : that.IMPORT_CROP_AREA_SELECTION_Y != null)
            return false;
        if (IMPORT_CROP_AREA_SELECTION_W != null ? !IMPORT_CROP_AREA_SELECTION_W.equals(that.IMPORT_CROP_AREA_SELECTION_W) : that.IMPORT_CROP_AREA_SELECTION_W != null)
            return false;
        if (IMPORT_CROP_AREA_SELECTION_H != null ? !IMPORT_CROP_AREA_SELECTION_H.equals(that.IMPORT_CROP_AREA_SELECTION_H) : that.IMPORT_CROP_AREA_SELECTION_H != null)
            return false;
        if (WINDOW_HIDDING_DELAY != null ? !WINDOW_HIDDING_DELAY.equals(that.WINDOW_HIDDING_DELAY) : that.WINDOW_HIDDING_DELAY != null)
            return false;
        if (IMPORT_MATCHING_POINTS_THRESHOLD != null ? !IMPORT_MATCHING_POINTS_THRESHOLD.equals(that.IMPORT_MATCHING_POINTS_THRESHOLD) : that.IMPORT_MATCHING_POINTS_THRESHOLD != null)
            return false;
        if (IMPORT_SURF_MODE != null ? !IMPORT_SURF_MODE.equals(that.IMPORT_SURF_MODE) : that.IMPORT_SURF_MODE != null)
            return false;
        if (ALERT_NOW_IF_REFUSED_TILES != null ? !ALERT_NOW_IF_REFUSED_TILES.equals(that.ALERT_NOW_IF_REFUSED_TILES) : that.ALERT_NOW_IF_REFUSED_TILES != null)
            return false;
        if (DIRECTORY_IMPORT_PATH != null ? !DIRECTORY_IMPORT_PATH.equals(that.DIRECTORY_IMPORT_PATH) : that.DIRECTORY_IMPORT_PATH != null)
            return false;
        if (DOCUMENT_IMPORT_PATH != null ? !DOCUMENT_IMPORT_PATH.equals(that.DOCUMENT_IMPORT_PATH) : that.DOCUMENT_IMPORT_PATH != null)
            return false;
        if (IMPORT_DOCUMENT_SCALE_FACTOR != null ? !IMPORT_DOCUMENT_SCALE_FACTOR.equals(that.IMPORT_DOCUMENT_SCALE_FACTOR) : that.IMPORT_DOCUMENT_SCALE_FACTOR != null)
            return false;
        if (ROBOT_IMPORT_COVERING != null ? !ROBOT_IMPORT_COVERING.equals(that.ROBOT_IMPORT_COVERING) : that.ROBOT_IMPORT_COVERING != null)
            return false;
        if (ROBOT_IMPORT_WIDTH != null ? !ROBOT_IMPORT_WIDTH.equals(that.ROBOT_IMPORT_WIDTH) : that.ROBOT_IMPORT_WIDTH != null)
            return false;
        if (ROBOT_IMPORT_HEIGHT != null ? !ROBOT_IMPORT_HEIGHT.equals(that.ROBOT_IMPORT_HEIGHT) : that.ROBOT_IMPORT_HEIGHT != null)
            return false;
        if (ROBOT_IMPORT_MOVING_DELAY != null ? !ROBOT_IMPORT_MOVING_DELAY.equals(that.ROBOT_IMPORT_MOVING_DELAY) : that.ROBOT_IMPORT_MOVING_DELAY != null)
            return false;
        return ROBOT_IMPORT_CAPTURE_DELAY != null ? ROBOT_IMPORT_CAPTURE_DELAY.equals(that.ROBOT_IMPORT_CAPTURE_DELAY) : that.ROBOT_IMPORT_CAPTURE_DELAY == null;

    }

    @Override
    public int hashCode() {
        int result = DEFAULT_LANGUAGE != null ? DEFAULT_LANGUAGE.hashCode() : 0;
        result = 31 * result + (HOME != null ? HOME.hashCode() : 0);
        result = 31 * result + (PROFILE_TITLE != null ? PROFILE_TITLE.hashCode() : 0);
        result = 31 * result + (PROFILE_COMMENT != null ? PROFILE_COMMENT.hashCode() : 0);
        result = 31 * result + (PROFILE_PATH != null ? PROFILE_PATH.hashCode() : 0);
        result = 31 * result + (SAVE_PROFILE_WHEN_LEAVE != null ? SAVE_PROFILE_WHEN_LEAVE.hashCode() : 0);
        result = 31 * result + (IMPORT_ENABLE_CROPPING != null ? IMPORT_ENABLE_CROPPING.hashCode() : 0);
        result = 31 * result + (IMPORT_CROP_AREA_SELECTION_X != null ? IMPORT_CROP_AREA_SELECTION_X.hashCode() : 0);
        result = 31 * result + (IMPORT_CROP_AREA_SELECTION_Y != null ? IMPORT_CROP_AREA_SELECTION_Y.hashCode() : 0);
        result = 31 * result + (IMPORT_CROP_AREA_SELECTION_W != null ? IMPORT_CROP_AREA_SELECTION_W.hashCode() : 0);
        result = 31 * result + (IMPORT_CROP_AREA_SELECTION_H != null ? IMPORT_CROP_AREA_SELECTION_H.hashCode() : 0);
        result = 31 * result + (WINDOW_HIDDING_DELAY != null ? WINDOW_HIDDING_DELAY.hashCode() : 0);
        result = 31 * result + (IMPORT_MATCHING_POINTS_THRESHOLD != null ? IMPORT_MATCHING_POINTS_THRESHOLD.hashCode() : 0);
        result = 31 * result + (IMPORT_SURF_MODE != null ? IMPORT_SURF_MODE.hashCode() : 0);
        result = 31 * result + (ALERT_NOW_IF_REFUSED_TILES != null ? ALERT_NOW_IF_REFUSED_TILES.hashCode() : 0);
        result = 31 * result + (DIRECTORY_IMPORT_PATH != null ? DIRECTORY_IMPORT_PATH.hashCode() : 0);
        result = 31 * result + (DOCUMENT_IMPORT_PATH != null ? DOCUMENT_IMPORT_PATH.hashCode() : 0);
        result = 31 * result + (IMPORT_DOCUMENT_SCALE_FACTOR != null ? IMPORT_DOCUMENT_SCALE_FACTOR.hashCode() : 0);
        result = 31 * result + (ROBOT_IMPORT_COVERING != null ? ROBOT_IMPORT_COVERING.hashCode() : 0);
        result = 31 * result + (ROBOT_IMPORT_WIDTH != null ? ROBOT_IMPORT_WIDTH.hashCode() : 0);
        result = 31 * result + (ROBOT_IMPORT_HEIGHT != null ? ROBOT_IMPORT_HEIGHT.hashCode() : 0);
        result = 31 * result + (ROBOT_IMPORT_MOVING_DELAY != null ? ROBOT_IMPORT_MOVING_DELAY.hashCode() : 0);
        result = 31 * result + (ROBOT_IMPORT_CAPTURE_DELAY != null ? ROBOT_IMPORT_CAPTURE_DELAY.hashCode() : 0);
        return result;
    }
}
