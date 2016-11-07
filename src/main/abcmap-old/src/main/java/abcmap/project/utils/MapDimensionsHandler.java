package abcmap.project.utils;

import java.awt.Dimension;
import java.awt.Rectangle;

import abcmap.configuration.ConfigurationConstants;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.events.ProjectEvent;
import abcmap.managers.stub.MainManager;
import abcmap.project.Project;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;

public class MapDimensionsHandler {

	/**
	 * Calcule et applique les dimensions maximums dans un thread
	 * <p>
	 * Si forceIfSmaller = true, alors la carte sera redimensionnée même si les
	 * dimensions calculées sont plus petites que les dimensions de la carte.
	 * 
	 * @param forceIfSmaller
	 */
	public static void computeAndFitDimensionsLater(final Project project,
			final boolean forceIfSmaller) {
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {
				proposeDimensions(project, computeMaxDimensions(project), forceIfSmaller);
			}
		});
	}

	/**
	 * Propose des dimensions à la carte.
	 * <p>
	 * Si forceIfSmaller = true, alors la carte sera redimensionnée même si les
	 * dimensions calculées sont plus petites que les dimensions de la carte.
	 * 
	 * @param forceIfSmaller
	 */
	public static void proposeDimensions(Project project, Dimension newDimensions,
			boolean forceIfSmaller) {

		// verifier les dimensions
		if (newDimensions == null) {
			throw new NullPointerException("Dimensions cannot be null");
		}

		if (newDimensions.width == 0 || newDimensions.height == 0) {
			throw new IllegalArgumentException("Dimensions too shorts: width " + newDimensions.width
					+ " height " + newDimensions.height);
		}

		// pas de dimensions en dessous du minimum
		Dimension mmd = ConfigurationConstants.MINIMUM_MAP_DIMENSIONS;
		if (newDimensions.width < mmd.width) {
			newDimensions.width = mmd.width;
		}
		if (newDimensions.height < mmd.height) {
			newDimensions.height = mmd.height;
		}

		Dimension presentDimensions = project.getMapDimensions();

		// dimensions plus petite acceptées seulement si spécifié
		if (forceIfSmaller == false) {

			if (newDimensions.width < presentDimensions.width) {
				newDimensions.width = presentDimensions.width;
			}

			if (newDimensions.height < presentDimensions.height) {
				newDimensions.height = presentDimensions.height;
			}

		}

		// appliquer puis notifier
		if (presentDimensions.equals(newDimensions) == false) {
			project.setMapDimensions(newDimensions);
			fireEvent();
		}
	}

	public static Dimension computeMaxDimensions(Project project) {

		// pas d'action dans l'EDT
		GuiUtils.throwIfOnEDT();

		Dimension rslt = new Dimension(ConfigurationConstants.MINIMUM_MAP_DIMENSIONS);

		// itérer les calques
		for (MapLayer lay : project.getLayers()) {

			// itérer les elements
			for (LayerElement elmt : lay.getAllElements()) {

				// recuperer les dimensions max de l'element
				Rectangle bounds = elmt.getMaximumBounds();

				// calculer les distances
				int distx = bounds.x + bounds.width;
				int disty = bounds.y + bounds.height;

				if (distx > rslt.width)
					rslt.width = distx;

				if (disty > rslt.height)
					rslt.height = disty;

			}

		}

		return rslt;

	}

	private static void fireEvent() {
		MainManager.getProjectManager()
				.fireEvent(new ProjectEvent(ProjectEvent.DIMENSIONS_CHANGED, null));
	}

}
