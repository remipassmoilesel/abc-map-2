package org.abcmap.gui.tools.containers;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ShortcutManager;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.help.InteractionSequence;
import org.abcmap.gui.tools.MapTool;
import org.abcmap.gui.tools.options.ToolOptionPanel;

import javax.swing.*;


/**
 * Tool container wrap metadata of tools
 * <p>
 * Each container should be able to create tool instances, option panel instances,
 * <p>
 * and to keep metadata like name, icon, keyboard, shortcut...
 */
public class ToolContainer {

    private static final CustomLogger logger = LogManager.getLogger(ProjectWriter.class);

    /**
     * Current instance of tool
     */
    protected MapTool currentInstance;

    /**
     * Unique ID of tool
     */
    protected String id;

    /**
     * Class of tool to instantiate
     */
    protected Class<? extends MapTool> toolClass;

    /**
     * Human readable name of tool
     */
    protected String readableName;

    /**
     * Icon of tool
     */
    protected ImageIcon icon;

    /**
     * Keystroke of tool
     */
    protected KeyStroke accelerator;

    /**
     * Class of option panel for instantiate it
     */
    protected Class<? extends ToolOptionPanel> optionPanelClass;

    /**
     * Current instance of option panel
     */
    protected ToolOptionPanel optionPanel;

    /**
     * Interaction sequences to provide a rapid help
     */
    protected InteractionSequence[] interactionsSequences;

    protected ShortcutManager shortcuts;

    public ToolContainer() {
        this.shortcuts = Main.getShortcutManager();
    }

    /**
     * Get current tool option panel instance
     *
     * @return
     */
    public ToolOptionPanel getOptionPanel() {
        if (optionPanel == null) {
            return createToolOptionPanel();
        } else {
            return optionPanel;
        }
    }

    /**
     * Create a tool option panel
     *
     * @return
     */
    protected ToolOptionPanel createToolOptionPanel() {

        if (optionPanelClass != null) {
            try {
                optionPanel = optionPanelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e);
                return null;
            }
        }

        return optionPanel;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ToolContainer == false) {
            return false;
        }

        ToolContainer t = (ToolContainer) obj;
        return Utils.safeEqualsIgnoreCase(t.getId(), getId());

    }

    /**
     * Create a new instance of tool
     *
     * @return
     */
    public MapTool getNewInstance() {

        if (toolClass == null) {
            throw new IllegalStateException("No tool affected");
        } else {

            try {
                currentInstance = toolClass.newInstance();
                return currentInstance;
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e);
                throw new IllegalStateException("Cannot instantiate tool");
            }

        }

    }

    /**
     * Return current instance of map tool. Each time a new tool is enabled, a new instance is created.
     *
     * @return
     */
    public MapTool getCurrentInstance() {
        return currentInstance;
    }

    /**
     * Get keyboard accelerator of tool
     *
     * @return
     */
    public KeyStroke getAccelerator() {
        return accelerator;
    }

    /**
     * Get interactions possible with tool
     *
     * @return
     */
    public InteractionSequence[] getInteractions() {
        return interactionsSequences;
    }

    /**
     * Get name displayable on interface
     *
     * @return
     */
    public String getReadableName() {
        return readableName;
    }

    /**
     * Get icon associated with tool
     *
     * @return
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Get ID of tool
     *
     * @return
     */
    public String getId() {

        if (id == null) {
            throw new IllegalStateException("Id should be set in controller: " + id);
        }

        return id;
    }

}
