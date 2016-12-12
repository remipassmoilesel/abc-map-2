package org.abcmap.core.robot;

public enum RobotCaptureMode {

    START_FROM_ULC, START_FROM_MIDDLE;

    public static RobotCaptureMode safeValueOf(String s) {
        try {
            return RobotCaptureMode.valueOf(s);
        } catch (Exception e) {
            return START_FROM_ULC;
        }
    }
}
