package org.abcmap.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

import abcmap.gui.comps.geo.MapPanel;
import abcmap.gui.comps.layout.LayoutScrollPanel;
import abcmap.gui.dock.DockBuilder;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.dock.comps.Dock.DockOrientation;
import abcmap.gui.dock.comps.DrawIndicatorWidget;
import abcmap.gui.ie.importation.manual.MenuImportManualCapture;
import abcmap.gui.ie.importation.robot.MenuRobotImport;
import abcmap.gui.iegroup.docks.GroupConfigProfile;
import abcmap.gui.iegroup.docks.GroupDrawingPalette;
import abcmap.gui.iegroup.docks.GroupDrawingTools;
import abcmap.gui.iegroup.docks.GroupExport;
import abcmap.gui.iegroup.docks.GroupGeoreferencement;
import abcmap.gui.iegroup.docks.GroupImportation;
import abcmap.gui.iegroup.docks.GroupLayersAndDimensions;
import abcmap.gui.iegroup.docks.GroupLayout;
import abcmap.gui.iegroup.docks.GroupObjectPosition;
import abcmap.gui.iegroup.docks.GroupPlugins;
import abcmap.gui.iegroup.docks.GroupProject;
import abcmap.gui.iegroup.docks.GroupSettings;
import abcmap.gui.iegroup.docks.GroupWizard;
import abcmap.gui.iegroup.menubar.GuiMenuBar;
import abcmap.gui.iegroup.toolbar.DisplayToolbar;
import abcmap.gui.iegroup.toolbar.EditionToolbar;
import abcmap.gui.iegroup.toolbar.FileToolbar;
import abcmap.gui.toolbar.SearchToolbar;
import abcmap.gui.toolbar.ToolbarSupport;
import abcmap.gui.windows.DetachedWindow;
import abcmap.gui.windows.MainWindow;
import abcmap.gui.windows.crop.CropConfigurationWindow;
import abcmap.managers.GuiManager;
import abcmap.managers.GuiManager.Windows;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

public class GuiBuilder {

    private MainWindow mainWindow;

    private MapPanel mapPanel;

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

        LayoutScrollPanel layoutPanel = new LayoutScrollPanel();
        mainWindow.setLayoutPanel(layoutPanel);

        mapPanel = new MapPanel();

        mapPanel = new MapPanel();
        mainWindow.setMapPanel(mapPanel);

    }

    public Component getMap() {
        return mapPanel;
    }

    public void registerWindows() {

        guim.registerWindow(Windows.MAIN, mainWindow);
        guim.registerWindow(Windows.CROP_CONFIG, cropWindow);
        guim.registerWindow(Windows.ROBOT_IMPORT, robotWindow);
        guim.registerWindow(Windows.MANUAL_IMPORT, manualImportWindow);
        guim.registerWindow(Windows.DETACHED_WIZARD, detachedWizard);

    }

}
