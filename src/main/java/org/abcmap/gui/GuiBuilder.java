package org.abcmap.gui;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.gui.components.dock.DockBuilder;
import org.abcmap.gui.components.dock.DockOrientation;
import org.abcmap.gui.components.dock.DrawIndicatorWidget;
import org.abcmap.gui.ie.importation.manual.MenuImportManualCapture;
import org.abcmap.gui.ie.importation.robot.MenuRobotImport;
import org.abcmap.gui.ie.toolbar.DisplayToolbar;
import org.abcmap.gui.ie.toolbar.EditionToolbar;
import org.abcmap.gui.ie.toolbar.FileToolbar;
import org.abcmap.gui.iegroup.docks.*;
import org.abcmap.gui.iegroup.docks.GroupLayout;
import org.abcmap.gui.iegroup.menubar.GuiMenuBar;
import org.abcmap.gui.toolbars.SearchToolbar;
import org.abcmap.gui.toolbars.ToolbarSupport;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.windows.DetachedWindow;
import org.abcmap.gui.windows.MainWindow;
import org.abcmap.gui.windows.crop.CropConfigurationWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GuiBuilder {

    private MainWindow mainWindow;


    private DetachedWindow robotWindow;

    private DetachedWindow manualImportWindow;

    private CropConfigurationWindow cropWindow;

    private DetachedWindow detachedWizard;

    private GuiManager guim;

    public GuiBuilder() {
        guim = MainManager.getGuiManager();
    }

    public void constructGui() {

        GuiUtils.throwIfNotOnEDT();

        constructWindows();

        constructToolbars();

        constructWindowPanels();

        constructDocks();

        constructMenuBar();

    }

    private void constructWindows() {

        mainWindow = new MainWindow();

        cropWindow = new CropConfigurationWindow();

        robotWindow = new DetachedWindow();
        robotWindow.displayComponent(new MenuRobotImport());
        robotWindow.reconstruct();
        robotWindow.setTitle("Capture automatique d'écran");

        manualImportWindow = new DetachedWindow();
        manualImportWindow.displayComponent(new MenuImportManualCapture());
        manualImportWindow.reconstruct();
        manualImportWindow.setTitle("Capture manuelle d'écran");

        detachedWizard = new DetachedWindow();
        detachedWizard.setMainWindowButtonVisible(false);
        detachedWizard.reconstruct();
        detachedWizard.setTitle("Assistants de création");

    }

    private void constructMenuBar() {
        JMenuBar menubar = new GuiMenuBar();
        menubar.setPreferredSize(new Dimension(800, 25));
        mainWindow.setJMenuBar(menubar);
    }

    private void constructToolbars() {

        ToolbarSupport tbs = new ToolbarSupport();
        tbs.addToolbar(new FileToolbar());
        tbs.addToolbar(new EditionToolbar());
        tbs.addToolbar(new DisplayToolbar());
        tbs.addToolbar(new SearchToolbar());

        mainWindow.getContentPane().add(tbs, BorderLayout.NORTH);
    }

    private void constructDocks() {

        DockBuilder df = new DockBuilder();

        // west dock
        ArrayList<Object> list = new ArrayList<>();
        list.add(new GroupWizard());
        list.add(new GroupProject());
        list.add(new GroupConfigProfile());
        list.add(new GroupImportation());
        list.add(new GroupGeoreferencement());
        list.add(new GroupLayout());
        list.add(new GroupExport());

        df.setWidgets(list);
        df.setOrientation(DockOrientation.WEST);
        Dock dockW = df.make();

        // east dock
        ArrayList<Object> list2 = new ArrayList<Object>();
        list2.add(new DrawIndicatorWidget());
        list2.add(new GroupLayersAndDimensions());
        list2.add(new GroupDrawingTools());
        list2.add(new GroupDrawingPalette());
        list2.add(new GroupObjectPosition());
        list2.add(new GroupPlugins());
        list2.add(new GroupSettings());

        df.setWidgets(list2);
        df.setOrientation(DockOrientation.EST);
        Dock dockE = df.make();

        mainWindow.setEastDock(dockE);
        mainWindow.setWestDock(dockW);

    }

    private void constructWindowPanels() {

        JPanel refusedTilesPanel = new JPanel();
        refusedTilesPanel.setBackground(Color.RED);
        mainWindow.setRefusedTilesPanel(refusedTilesPanel);

    }

    public Component getMap() {
        return new JPanel();
    }

    public void registerWindows() {

        guim.registerWindow(Windows.MAIN, mainWindow);
        guim.registerWindow(Windows.CROP_CONFIG, cropWindow);
        guim.registerWindow(Windows.ROBOT_IMPORT, robotWindow);
        guim.registerWindow(Windows.MANUAL_IMPORT, manualImportWindow);
        guim.registerWindow(Windows.DETACHED_WIZARD, detachedWizard);

    }

}
