package org.abcmap.gui;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GuiIcons {

    private static final CustomLogger logger = LogManager.getLogger(GuiIcons.class);

    private static final String ROOT = "icons/";
    private static final String BUTTONS_DIR = ROOT + "buttons/";
    private static final String GROUPS_DIR = ROOT + "groups/";
    private static final String LAYERS_DIR = ROOT + "layers/";
    private static final String WIZARD_DIR = ROOT + "wizard/";
    private static final String TOOLS_DIR = ROOT + "tools/";
    private static final String INTERACTIONS_DIR = ROOT + "interactions/";
    private static final String SMALLS_DIR = ROOT + "smalls/";
    private static final String VOTE_DIR = ROOT + "vote/";
    private static final String SOCIAL_NET_DIR = ROOT + "social_networks/";
    private static final String SPLASH_DIR = ROOT + "splash/";
    private static final String DIALOG_DIR = ROOT + "dialog/";
    private static final String MAP_DIR = ROOT + "map/";
    private static final String GEOLOC_DIR = ROOT + "geoloc/";
    private static final String IMPORT_DIR = ROOT + "import/";
    private static final String ZORDER_DIR = ROOT + "zorder/";
    private static final String ALIGN_DIR = ROOT + "align/";
    private static final String EDITION_DIR = ROOT + "edition/";
    private static final String DISPLAY_DIR = ROOT + "display/";
    private static final String PROJECTS_DIR = ROOT + "projects/";

    /**
     * Return icon at specified path or if not found an empty icon
     *
     * @param ressourcePath
     * @return
     */
    private static ImageIcon getIcon(String ressourcePath) {

        try {
            return new ImageIcon(ImageIO.read(ClassLoader.getSystemResource(ressourcePath)));
        } catch (Exception e) {
            logger.error(e);
        }

        return new ImageIcon();
    }

	/*
     *
	 */

    public static final ImageIcon DI_NAVB_CLOSE = getIcon(BUTTONS_DIR + "navb_close.png");
    public static final ImageIcon DI_NAVB_EXPAND_INFOS = getIcon(BUTTONS_DIR + "navb_expand_infos.png");
    public static final ImageIcon DI_NAVB_NEXT = getIcon(BUTTONS_DIR + "navb_next.png");
    public static final ImageIcon DI_NAVB_PREVIOUS = getIcon(BUTTONS_DIR + "navb_previous.png");

	/*
     *
	 */

    public static final ImageIcon DI_ITEM_HELP = getIcon(BUTTONS_DIR + "group_item_info.png");
    public static final ImageIcon DI_HIDE_ITEM = getIcon(BUTTONS_DIR + "group_item_hide.png");
    public static final ImageIcon DI_SHOW_ITEM = getIcon(BUTTONS_DIR + "group_item_show.png");

    /*
     *
     */
    public static final ImageIcon DEFAULT_GROUP_ICON = getIcon(GROUPS_DIR + "default_group_icon.png");
    public static final ImageIcon GROUP_PLUGINS = getIcon(GROUPS_DIR + "group_plugins.png");
    public static final ImageIcon GROUP_PROJECT = getIcon(GROUPS_DIR + "group_project.png");
    public static final ImageIcon GROUP_IMPORT = getIcon(GROUPS_DIR + "group_import.png");
    public static final ImageIcon GROUP_GEOLOC = getIcon(GROUPS_DIR + "group_geoloc.png");
    public static final ImageIcon GROUP_DRAW = getIcon(GROUPS_DIR + "group_draw.png");
    public static final ImageIcon GROUP_COLOR_PALETTE = getIcon(GROUPS_DIR + "group_color_palette.png");
    public static final ImageIcon GROUP_SETTINGS = getIcon(GROUPS_DIR + "group_settings.png");
    public static final ImageIcon GROUP_EXPORT = getIcon(GROUPS_DIR + "group_export.png");
    public static final ImageIcon GROUP_LAYERS = getIcon(GROUPS_DIR + "group_layers.png");
    public static final ImageIcon GROUP_CONFIG_PROFILE = getIcon(GROUPS_DIR + "group_config_profile.png");
    public static final ImageIcon GROUP_WIZARD = getIcon(GROUPS_DIR + "group_wizard.png");
    public static final ImageIcon GROUP_LAYOUT = getIcon(GROUPS_DIR + "group_layout.png");
    public static final ImageIcon GROUP_OBJECT_POSITION = getIcon(GROUPS_DIR + "group_object_position.png");

    /*
     *
     */
    public static final ImageIcon NAVIGATION_WIDGET = DEFAULT_GROUP_ICON;
    public static final ImageIcon CUSTOM_COLOR_BUTTON = getIcon(BUTTONS_DIR + "custom_color_button.png");

	/*
     *
	 */

    public static final ImageIcon LAYER_ADD = getIcon(LAYERS_DIR + "layer_add.png");
    public static final ImageIcon LAYER_REMOVE = getIcon(LAYERS_DIR + "layer_remove.png");
    public static final ImageIcon LAYER_RENAME = getIcon(LAYERS_DIR + "layer_rename.png");
    public static final ImageIcon LAYER_UP = getIcon(LAYERS_DIR + "layer_up.png");
    public static final ImageIcon LAYER_DOWN = getIcon(LAYERS_DIR + "layer_down.png");
//    public static final ImageIcon LAYER_TOP = getIcon(LAYERS_DIR + "layer_top.png");
//    public static final ImageIcon LAYER_BOTTOM = getIcon(LAYERS_DIR + "layer_bottom.png");
//    public static final ImageIcon LAYER_IS_VISIBLE = getIcon(LAYERS_DIR + "layer_is_visible.png");
//    public static final ImageIcon LAYER_IS_INVISIBLE = getIcon(LAYERS_DIR + "layer_is_invisible.png");
    public static final ImageIcon LAYER_VISIBILITY_BUTTON = getIcon(LAYERS_DIR + "layer_visibility_button.png");

	/*
     *
	 */

    public static final ImageIcon WIZARD_HOME = getIcon(WIZARD_DIR + "wizard_home.png");
    public static final ImageIcon WIZARD_NEW_WINDOW = getIcon(WIZARD_DIR + "wizard-new-window.png");
    public static final ImageIcon WIZARD_NEXT = getIcon(WIZARD_DIR + "wizard_next.png");
    public static final ImageIcon WIZARD_PREVIOUS = getIcon(WIZARD_DIR + "wizard_previous.png");

	/*
     *
	 */

    public static final ImageIcon TOOL_SELECTION = getIcon(TOOLS_DIR + "tool_selection.png");
    public static final ImageIcon TOOL_TEXT = getIcon(TOOLS_DIR + "tool_text.png");
    public static final ImageIcon TOOL_ELLIPSE = getIcon(TOOLS_DIR + "tool_ellipse.png");
    public static final ImageIcon TOOL_RECTANGLE = getIcon(TOOLS_DIR + "tool_rectangle.png");
    public static final ImageIcon TOOL_POLYGON = getIcon(TOOLS_DIR + "tool_polygone.png");
    public static final ImageIcon TOOL_POLYLINE = getIcon(TOOLS_DIR + "tool_polyline.png");
    public static final ImageIcon TOOL_IMAGE = getIcon(TOOLS_DIR + "tool_image.png");
    public static final ImageIcon TOOL_SYMBOL = getIcon(TOOLS_DIR + "tool_symbol.png");
    public static final ImageIcon TOOL_TILE = getIcon(TOOLS_DIR + "tool_tile.png");
    public static final ImageIcon TOOL_GEOREF = getIcon(TOOLS_DIR + "tool_georeference.png");
    public static final ImageIcon TOOL_LINK = getIcon(TOOLS_DIR + "tool_link.png");
    public static final ImageIcon TOOL_LEGEND = getIcon(TOOLS_DIR + "tool_legend.png");

	/*
     *
	 */

    public static final ImageIcon INTERACTION_SIMPLECLICK = getIcon(INTERACTIONS_DIR + "interaction_simpleclick.png");
    public static final ImageIcon INTERACTION_DOUBLECLICK = getIcon(INTERACTIONS_DIR + "interaction_doubleclick.png");
    public static final ImageIcon INTERACTION_DRAG = getIcon(INTERACTIONS_DIR + "interaction_drag.png");
    public static final ImageIcon INTERACTION_PRESSCTRL = getIcon(INTERACTIONS_DIR + "interaction_pressctrl.png");
    public static final ImageIcon INTERACTION_PRESSMAJ = getIcon(INTERACTIONS_DIR + "interaction_pressmaj.png");
    public static final ImageIcon LINK_MARK = getIcon(INTERACTIONS_DIR + "link_mark.png");

	/*
     *
	 */

    public static final ImageIcon DEFAULT_TOOLBAR_BUTTON_ICON = getIcon(ROOT + "default_toolbar_group_icon.png");

    public static final ImageIcon SMALLICON_NEWPROJECT = getIcon(PROJECTS_DIR + "smallicon_newproject.png");
    public static final ImageIcon SMALLICON_OPENPROJECT = getIcon(PROJECTS_DIR + "smallicon_openproject.png");
    public static final ImageIcon SMALLICON_CLOSEPROJECT = getIcon(PROJECTS_DIR + "smallicon_closeproject.png");
    public static final ImageIcon SMALLICON_SAVE = getIcon(PROJECTS_DIR + "smallicon_save.png");
    public static final ImageIcon SMALLICON_SAVEAS = getIcon(PROJECTS_DIR + "smallicon_saveas.png");

    public static final ImageIcon SMALLICON_PASTE = getIcon(EDITION_DIR + "smallicon_paste.png");
    public static final ImageIcon SMALLICON_PASTEASTILE = getIcon(EDITION_DIR + "smallicon_pasteastile.png");
    public static final ImageIcon SMALLICON_COPY = getIcon(EDITION_DIR + "smallicon_copy.png");
    public static final ImageIcon SMALLICON_DUPLICATE = getIcon(EDITION_DIR + "smallicon_duplicate.png");
    public static final ImageIcon SMALLICON_SELECTALL = getIcon(EDITION_DIR + "smallicon_selectall.png");
    public static final ImageIcon SMALLICON_UNSELECTALL = getIcon(EDITION_DIR + "smallicon_unselectall.png");
    public static final ImageIcon SMALLICON_DELETE = getIcon(EDITION_DIR + "smallicon_delete.png");
    public static final ImageIcon SMALLICON_REANALYSE = getIcon(EDITION_DIR + "smallicon_reanalyse.png");

    public static final ImageIcon SMALLICON_DOWN = getIcon(ZORDER_DIR + "smallicon_down.png");
    public static final ImageIcon SMALLICON_UP = getIcon(ZORDER_DIR + "smallicon_up.png");
    public static final ImageIcon SMALLICON_BOTTOM = getIcon(ZORDER_DIR + "smallicon_bottom.png");
    public static final ImageIcon SMALLICON_TOP = getIcon(ZORDER_DIR + "smallicon_top.png");

    public static final ImageIcon SMALLICON_ZOOMIN = getIcon(DISPLAY_DIR + "smallicon_zoomin.png");
    public static final ImageIcon SMALLICON_ZOOMOUT = getIcon(DISPLAY_DIR + "smallicon_zoomout.png");
    public static final ImageIcon SMALLICON_DISPLAYMAP = getIcon(DISPLAY_DIR + "smallicon_displaymap.png");
    public static final ImageIcon SMALLICON_DISPLAYLAYOUT = getIcon(DISPLAY_DIR + "smallicon_displaylayout.png");
    public static final ImageIcon SMALLICON_DISPLAYREFUSEDTILES = getIcon(DISPLAY_DIR + "smallicon_displayrefusedtiles.png");

    public static final ImageIcon ALIGN_TOP = getIcon(ALIGN_DIR + "align_top.png");
    public static final ImageIcon ALIGN_BOTTOM = getIcon(ALIGN_DIR + "align_bottom.png");
    public static final ImageIcon ALIGN_LEFT = getIcon(ALIGN_DIR + "align_left.png");
    public static final ImageIcon ALIGN_RIGHT = getIcon(ALIGN_DIR + "align_right.png");
    public static final ImageIcon ALIGN_MIDDLE_HORIZONTAL = getIcon(ALIGN_DIR + "align_middle_horizontal.png");
    public static final ImageIcon ALIGN_MIDDLE_VERTICAL = getIcon(ALIGN_DIR + "align_middle_vertical.png");
    public static final ImageIcon DISTRIBUTE_HORIZONTAL = getIcon(ALIGN_DIR + "distribute_horizontal.png");
    public static final ImageIcon DISTRIBUTE_VERTICAL = getIcon(ALIGN_DIR + "distribute_vertical.png");

	/*
     *
	 */

    public static final ImageIcon WINDOW_ICON = getIcon(ROOT + "window_icon.png");

	/*
	 * 
	 */

    public static final ImageIcon VOTE_0 = getIcon(VOTE_DIR + "vote_0.png");
    public static final ImageIcon VOTE_1 = getIcon(VOTE_DIR + "vote_1.png");
    public static final ImageIcon VOTE_2 = getIcon(VOTE_DIR + "vote_2.png");

    public static final ImageIcon SOCIAL_FACEBOOK = getIcon(SOCIAL_NET_DIR + "social_facebook.png");
    public static final ImageIcon SOCIAL_GOOGLE = getIcon(SOCIAL_NET_DIR + "social_google.png");
    public static final ImageIcon SOCIAL_TWEETER = getIcon(SOCIAL_NET_DIR + "social_tweeter.png");
    public static final ImageIcon SOCIAL_PINTEREST = getIcon(SOCIAL_NET_DIR + "social_pinterest.png");
    public static final ImageIcon SOCIAL_LINKEDIN = getIcon(SOCIAL_NET_DIR + "social_linkedin.png");
    public static final ImageIcon SOCIAL_WWW = getIcon(SOCIAL_NET_DIR + "social_www.png");
    public static final ImageIcon SOCIAL_YOUTUBE = getIcon(SOCIAL_NET_DIR + "social_youtube.png");
    public static final ImageIcon SOCIAL_PAYPAL = getIcon(SOCIAL_NET_DIR + "social_paypal.png");

	/*
	 * 
	 */

    public static final ImageIcon SPLASH_SCREEN = getIcon(SPLASH_DIR + "splash_screen.png");
    public static final ImageIcon QUIT_PROGRAM = DEFAULT_GROUP_ICON;
    public static final ImageIcon ATTENTION = getIcon(ROOT + "attention.png");

	/*
	 * 
	 */

    public static final ImageIcon DIALOG_INFORMATION_ICON = getIcon(DIALOG_DIR + "dialog_information_icon.png");
    public static final ImageIcon DIALOG_QUESTION_ICON = getIcon(DIALOG_DIR + "dialog_question_icon.png");
    public static final ImageIcon DIALOG_ERROR_ICON = getIcon(DIALOG_DIR + "dialog_error_icon.png");

	/*
	 * 
	 */

    public static final ImageIcon CROP_INFORMATIONS = getIcon(IMPORT_DIR + "crop_informations.png");

	/*
	 * 
	 */

    public static final ImageIcon MAP_MOVEUP = getIcon(MAP_DIR + "map_moveup.png");
    public static final ImageIcon MAP_MOVEDOWN = getIcon(MAP_DIR + "map_movedown.png");
    public static final ImageIcon MAP_MOVELEFT = getIcon(MAP_DIR + "map_moveleft.png");
    public static final ImageIcon MAP_MOVERIGHT = getIcon(MAP_DIR + "map_moveright.png");
    public static final ImageIcon MAP_MOVECENTER = getIcon(MAP_DIR + "map_movecenter.png");
    public static final ImageIcon MAP_ZOOMIN = getIcon(MAP_DIR + "smallicon_zoomin.png");
    public static final ImageIcon MAP_ZOOMOUT = getIcon(MAP_DIR + "smallicon_zoomout.png");

    /*
     *
     */
    public static final ImageIcon GEOLOC_MARK_ACTIVE = getIcon(GEOLOC_DIR + "geoloc_mark_active.png");
    public static final ImageIcon GEOLOC_MARK_INACTIVE = getIcon(GEOLOC_DIR + "geoloc_mark_inactive.png");
}
