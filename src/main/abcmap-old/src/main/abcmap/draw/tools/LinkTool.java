package abcmap.draw.tools;

public class LinkTool extends SelectionTool {

	public LinkTool() {

		// exclure les tuiles
		excludeTiles(true);

		// pas de filtre de forme
		setShapeFilter(null);
	}

}
