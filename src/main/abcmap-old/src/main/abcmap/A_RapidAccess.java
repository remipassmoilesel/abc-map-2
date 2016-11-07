package abcmap;

import abcmap.configuration.Configuration;
import abcmap.configuration.ConfigurationConstants;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Polyline;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.PolypointShapeTool;
import abcmap.draw.tools.RectangleShapeTool;
import abcmap.draw.tools.SelectionTool;
import abcmap.draw.tools.ShapeMover;
import abcmap.draw.tools.TileTool;
import abcmap.draw.tools.containers.ToolContainer;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.events.DrawManagerEvent;
import abcmap.events.MapEvent;
import abcmap.events.RecentHistoryEvent;
import abcmap.gui.GuiColors;
import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.color.ColorPalette;
import abcmap.gui.comps.draw.layers.LayerSelectorPanel;
import abcmap.gui.comps.geo.CoordinatesPanel;
import abcmap.gui.comps.geo.MapPanel;
import abcmap.gui.comps.help.AttentionPanel;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionHelpPanel;
import abcmap.gui.comps.share.StatusBar;
import abcmap.gui.dock.DockBuilder;
import abcmap.gui.dock.comps.DrawIndicatorWidget;
import abcmap.gui.dock.comps.blockitems.DockMenuPanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.dock.comps.blockitems.SubMenuItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.ie.display.zoom.ZoomIn;
import abcmap.gui.ie.position.MoveElementsBottom;
import abcmap.gui.ie.position.MoveElementsByCoordinates;
import abcmap.gui.ie.profiles.OpenProfile;
import abcmap.gui.ie.profiles.SetProfileComment;
import abcmap.gui.ie.profiles.SetProfileTitle;
import abcmap.gui.ie.program.QuitProgram;
import abcmap.gui.ie.project.NewProject;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.project.SaveProject;
import abcmap.gui.ie.recents.OpenRecentProject;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.iegroup.toolbar.DisplayToolbar;
import abcmap.managers.BackgroundTasksManager;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.project.loaders.abm.AbmProjectLoader;
import abcmap.project.loaders.abm.AbmShapesLoader;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.LayoutProperties;
import abcmap.project.utils.LayerSequentialPerformer;
import abcmap.project.utils.ProjectRenderer;
import abcmap.project.writers.AbmDescriptorWriter;
import abcmap.project.writers.AbmProjectWriter;
import abcmap.utils.gui.Lng;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.tool.LastNotificationsPanel;
import abcmap.utils.notifications.tool.NotificationManagerTool;

/**
 * Document d'acces via Eclipse
 * 
 * @author remipassmoilesel
 *
 */
public class A_RapidAccess {

	public A_RapidAccess() {

		// managers
		c(MapManager.class);
		c(ProjectManager.class);
		c(DrawManager.class);
		c(ConfigurationManager.class);
		c(BackgroundTasksManager.class);
		c(GuiManager.class);

		// gui
		c(GuiStyle.class);
		c(GuiColors.class);

		c(StatusBar.class);
		c(AttentionPanel.class);
		c(DrawIndicatorWidget.class);
		c(GuiIcons.class);
		c(Lng.class);
		c(DisplayToolbar.class);
		c(ColorPalette.class);
		c(ProjectRenderer.class);
		c(LayerSelectorPanel.class);
		c(DockBuilder.class);
		c(DockMenuPanel.class);
		c(MapPanel.class);
		c(CoordinatesPanel.class);
		c(SimpleBlockItem.class);
		c(SubMenuItem.class);

		// interaction elements
		c(OpenProject.class);
		c(OpenRecentProject.class);
		c(OpenProfile.class);
		c(ZoomIn.class);
		c(MoveElementsBottom.class);
		c(MoveElementsByCoordinates.class);
		c(QuitProgram.class);
		c(InteractionElement.class);
		c(InteractionElementGroup.class);
		c(SetProfileComment.class);
		c(SetProfileTitle.class);

		// utilitaires pour dessin
		c(LayerSequentialPerformer.class);

		// outils de dessin
		c(ToolLibrary.class);
		c(SelectionTool.class);
		c(MapTool.class);
		c(ShapeMover.class);
		c(ToolContainer.class);
		c(TileTool.class);
		c(RectangleShapeTool.class);
		c(PolypointShapeTool.class);
		c(InteractionHelpPanel.class);
		c(Interaction.class);

		// formes
		c(DrawProperties.class);
		c(Polyline.class);
		c(Label.class);

		// proprietes de serialisation
		c(DrawPropertiesContainer.class);
		c(LayoutProperties.class);

		// lecture de fichiers
		c(AbmProjectLoader.class);
		c(AbmShapesLoader.class);

		// ecriture de fichiers
		c(AbmProjectWriter.class);
		c(AbmDescriptorWriter.class);

		// configuration
		c(Configuration.class);
		c(ConfigurationConstants.class);

		// commandes
		c(NewProject.class);
		c(SaveProject.class);

		// evenements
		c(DrawManagerEvent.class);
		c(MapEvent.class);
		c(RecentHistoryEvent.class);

		// notifications
		c(NotificationManager.class);
		c(NotificationManagerTool.class);
		c(LastNotificationsPanel.class);
		c(Notification.class);

	}

	public void c(Class cl) {
	}
}
