package abcmap.gui.ie.display.zoom;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.InteractionElement;

public abstract class AbstractZoom extends InteractionElement {

	private static final float RESETED_SCALE = 0.7f;
	private static final float SCALE_STEP = 0.1f;

	protected enum Direction {
		IN, OUT, CENTER
	}

	private Direction direction;

	public AbstractZoom(Direction dir) {

		this.direction = dir;

		if (Direction.IN.equals(dir)) {
			label = "Zoom avant";
			help = "Cliquez ici pour zoomer la carte.";
			menuIcon = GuiIcons.SMALLICON_ZOOMIN;
		}

		else if (Direction.OUT.equals(dir)) {
			label = "Zoom arrière";
			help = "Cliquez ici pour dézoomer la carte.";
			menuIcon = GuiIcons.SMALLICON_ZOOMOUT;
		}

		else if (Direction.CENTER.equals(dir)) {
			label = "Remise à zéro de l'affichage";
			help = "Cliquez ici pour remettre à zéro l'affichage.";
			menuIcon = GuiIcons.MAP_MOVECENTER;
		}
	}

	@Override
	public void run() {
		if (Direction.IN.equals(direction)) {
			mapm.addToDisplayScale(SCALE_STEP);
		}

		else if (Direction.OUT.equals(direction)) {
			mapm.addToDisplayScale(-SCALE_STEP);
		}

		else if (Direction.CENTER.equals(direction)) {
			mapm.resetDisplay(RESETED_SCALE);
		}

		mapm.refreshMapComponent();
	}

}
