package org.abcmap.gui.tools;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ShortcutManager;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.help.InteractionSequence;
import org.abcmap.gui.tools.options.ToolOptionPanel;

import javax.swing.*;

public class ToolContainer {

    private static final CustomLogger logger = LogManager.getLogger(ProjectWriter.class);

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
        this.shortcuts = MainManager.getShortcutManager();
    }

    public ToolOptionPanel getOptionPanel() {
        if (optionPanel == null) {
            return createToolOptionPanel();
        } else {
            return optionPanel;
        }
    }

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

    public String getReadableName() {
        return readableName;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ToolContainer == false) {
            return false;
        }

        ToolContainer t = (ToolContainer) obj;
        return Utils.safeEqualsIgnoreCase(t.getId(), getId());

    }

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

    public MapTool getCurrentInstance() {
        return currentInstance;
    }

    public KeyStroke getAccelerator() {
        return accelerator;
    }

    public InteractionSequence[] getInteractions() {
        return interactionsSequences;
    }
}
