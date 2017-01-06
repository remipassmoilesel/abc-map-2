package org.abcmap.core.managers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyboardManager extends ManagerTreeAccessUtil {

    public final int ESC_KEYCODE = 27;

    public final KeyStroke STOP_TASKS = KeyStroke.getKeyStroke("ESCAPE");

    public final KeyStroke SHOW_HELP = KeyStroke.getKeyStroke("F1");
    public final KeyStroke DIR_IMPORT_MODE = KeyStroke.getKeyStroke("F2");
    public final KeyStroke MANUAL_IMPORT_MODE = KeyStroke.getKeyStroke("F3");
    public final KeyStroke ROBOT_IMPORT_MODE = KeyStroke.getKeyStroke("F4");

    public final KeyStroke INSERT_TILE_FROM_FILE = KeyStroke.getKeyStroke("F5");
    public final KeyStroke INSERT_IMAGE_FROM_FILE = KeyStroke.getKeyStroke("F6");

    public final KeyStroke DEFAULT_DISPLAY = KeyStroke.getKeyStroke("F12");

    public final KeyStroke ZOOM_IN = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
            InputEvent.CTRL_DOWN_MASK);
    public final KeyStroke ZOOM_OUT = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
            InputEvent.CTRL_DOWN_MASK);

    public final KeyStroke NEW_PROJECT = KeyStroke.getKeyStroke("control N");
    public final KeyStroke OPEN_PROJECT = KeyStroke.getKeyStroke("control O");
    public final KeyStroke SAVE_PROJECT = KeyStroke.getKeyStroke("control S");
    public final KeyStroke SAVE_PROJECT_AS = KeyStroke.getKeyStroke("control shift S");
    public final KeyStroke CLOSE_PROJECT = KeyStroke.getKeyStroke("control shift C");

    public final KeyStroke PRINT_PROJECT = KeyStroke.getKeyStroke("control P");
    public final KeyStroke EXPORT_PNG = KeyStroke.getKeyStroke("control shift P");

    public final KeyStroke LAYOUT_CONFIGURATION = KeyStroke.getKeyStroke("control L");

    public final KeyStroke QUIT_PROGRAM = KeyStroke.getKeyStroke("control Q");
    public final KeyStroke WIZARD_ON_EAST = KeyStroke.getKeyStroke("control W");

    public final KeyStroke SHOW_MAP = KeyStroke.getKeyStroke("control M");
    public final KeyStroke SHOW_MAP_ONLY = KeyStroke.getKeyStroke("control shift M");

    // edition
    public final KeyStroke UNDO = KeyStroke.getKeyStroke("control Z");
    public final KeyStroke REDO = KeyStroke.getKeyStroke("control Y");

    public final KeyStroke DUPLICATE = KeyStroke.getKeyStroke("control D");

    public final KeyStroke COPY = KeyStroke.getKeyStroke("control C");
    public final KeyStroke CUT = KeyStroke.getKeyStroke("control X");
    public final KeyStroke PASTE = KeyStroke.getKeyStroke("control V");
    public final KeyStroke PASTE_AS_TILE = KeyStroke.getKeyStroke("control shift V");

    public final KeyStroke SELECT_ALL = KeyStroke.getKeyStroke("control A");
    public final KeyStroke UNSELECT_ALL = KeyStroke.getKeyStroke("control shift A");
    public final KeyStroke DELETE_SELECTED_ELEMENTS = KeyStroke.getKeyStroke("DELETE");

    public final KeyStroke MOVE_UP = KeyStroke.getKeyStroke("alt UP");
    public final KeyStroke MOVE_DOWN = KeyStroke.getKeyStroke("alt DOWN");
    public final KeyStroke MOVE_TOP = KeyStroke.getKeyStroke("alt PAGE_UP");
    public final KeyStroke MOVE_BOTTOM = KeyStroke.getKeyStroke("alt PAGE_DOWN");

    public final KeyStroke GEOREF_TOOL_IF = KeyStroke.getKeyStroke("ctrl alt G");

    /*
     * Outils de dessin
     */
    public final KeyStroke IMAGE_TOOL = KeyStroke.getKeyStroke("alt I");
    public final KeyStroke SELECTION_TOOL = KeyStroke.getKeyStroke("alt S");
    public final KeyStroke RECTANGLE_TOOL = KeyStroke.getKeyStroke("alt R");
    public final KeyStroke GEOREF_TOOL = KeyStroke.getKeyStroke("alt G");
    public final KeyStroke TILE_TOOL = KeyStroke.getKeyStroke("alt T");
    public final KeyStroke ELLIPSE_TOOL = KeyStroke.getKeyStroke("alt E");
    public final KeyStroke LINE_TOOL = KeyStroke.getKeyStroke("alt P");
    public final KeyStroke POLYGON_TOOL = KeyStroke.getKeyStroke("alt shift P");
    public final KeyStroke LABEL_TOOL = KeyStroke.getKeyStroke("alt L");
    public final KeyStroke SYMBOL_TOOL = KeyStroke.getKeyStroke("alt shift S");
    public final KeyStroke LEGEND_TOOL = KeyStroke.getKeyStroke("alt shift L");
    public final KeyStroke LINK_TOOL = KeyStroke.getKeyStroke("alt h");

    /*
     * Tool box
     */
    public final KeyStroke LAYER_SELECTOR_TB = KeyStroke.getKeyStroke("control shift L");
    public final KeyStroke DRAW_TOOL_TB = KeyStroke.getKeyStroke("control shift T");
    public final KeyStroke ELEMENT_POSITION_TB = KeyStroke.getKeyStroke("control alt P");
    public final KeyStroke STOKE_PROPERTIES_TB = KeyStroke.getKeyStroke("control shift F");
    public final KeyStroke MULTI_SELECTION_TB = KeyStroke.getKeyStroke("control shift I");

    /*
     * Special frames
     */
    public final KeyStroke MAP_SIF = KeyStroke.getKeyStroke("control shift M");
    public final KeyStroke WIZARD_SIF = KeyStroke.getKeyStroke("control shift W");
    public final KeyStroke LAYOUT_SIF = KeyStroke.getKeyStroke("control shift N");
    public final KeyStroke REFUSED_TILES_SIF = KeyStroke.getKeyStroke("control shift R");

	/*
     * Parameter Internal Frames
	 */

    public final KeyStroke PROJECT_PROPERTIES_PIF = KeyStroke.getKeyStroke("control shift P");

    /**
     * Store states of keyboard
     */
    private HashMap<Integer, KeyState> keyStates;

    private enum KeyState {
        NULL,
        PRESSED,
        RELEASED,
    }

    public KeyboardManager() {

        // Add key codes here to monitor it
        this.keyStates = new HashMap<>();
        keyStates.put(KeyEvent.VK_SPACE, KeyState.NULL);

        /**
         * Monitor states of specified keys
         */
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {

                        // System.out.println();
                        // System.out.println("Event received");
                        // System.out.println(e.getKeyChar());


                        int code = e.getKeyCode();

                        // check iff we monitor this key code
                        if (keyStates.get(code) != null) {

                            int id = e.getID();

                            // check if space bar pressed
                            if (KeyEvent.KEY_PRESSED == id) {
                                keyStates.put(code, KeyState.PRESSED);
                            }

                            // check if space bar released
                            if (KeyEvent.KEY_RELEASED == id) {
                                keyStates.put(code, KeyState.RELEASED);
                            }

                        }

                        // always return false to continue propagation of event
                        return false;
                    }
                });

    }

    /**
     * Return true if one window of software is focused and space bar is down
     *
     * @return
     */
    public boolean isSpaceBarDown() {
        return keyStates.get(KeyEvent.VK_SPACE) == KeyState.PRESSED;
    }

}
