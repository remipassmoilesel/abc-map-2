package abcmap.events;

import abcmap.utils.notifications.Notification;

public class ProjectEvent extends Notification {
	public static final String NEW_PROJECT_LOADED = "NEW_PROJECT_LOADED";

	public static final String PROJECT_CHANGED = "PROJECT_CHANGED";
	public static final String PROJECT_SAVED = "PROJECT_SAVED";
	public static final String PROJECT_CLOSED = "PROJECT_CLOSED";
	public static final String SELECTION_CHANGED = "SELECTION_CHANGED";
	public static final String DIMENSIONS_CHANGED = "DIMENSIONS_CHANGED";
	public static final String ELEMENTS_CHANGED = "ELEMENTS_CHANGED";
	public static final String LAYOUTS_LIST_CHANGED = "LAYOUTS_CHANGED";
	public static final String LAYERS_LIST_CHANGED = "LAYERS_LIST_CHANGED";
	public static final String METADATAS_CHANGED = "METADATAS_CHANGED";

	public static final String LAYER_LOADED = "LAYER_LOADED";

	public static final String NAME_CHANGED = "NAME_CHANGED";
	public static final String VISIBILITY_CHANGED = "VISIBILITY_CHANGED";
	public static final String OPACITY_CHANGED = "OPACITY_CHANGED";

	public static final String TILE_REFUSED_LIST_UPDATED = "TILE_REFUSED_LIST_UPDATED";

	public ProjectEvent(String name, Object value) {
		super(name, value);
	}

	public static boolean isNewProjectLoadedEvent(Notification arg) {
		return testEvent(arg, NEW_PROJECT_LOADED);
	}

	public static boolean isCloseProjectEvent(Notification arg) {
		return testEvent(arg, PROJECT_CLOSED);
	}

	private static boolean testEvent(Notification event, String name) {

		if (event == null)
			return false;

		if (event instanceof ProjectEvent == false)
			return false;

		return event.getName().equals(name);

	}

}
