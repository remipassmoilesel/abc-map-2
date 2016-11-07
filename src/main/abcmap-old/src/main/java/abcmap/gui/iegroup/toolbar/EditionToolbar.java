package abcmap.gui.iegroup.toolbar;

import abcmap.gui.ie.analyse.AnalyseSelectedTiles;
import abcmap.gui.ie.copy.Copy;
import abcmap.gui.ie.copy.Paste;
import abcmap.gui.ie.copy.PasteAsTile;
import abcmap.gui.ie.edition.DeleteSelectedElements;
import abcmap.gui.ie.edition.Duplicate;
import abcmap.gui.ie.position.MoveElementsBottom;
import abcmap.gui.ie.position.MoveElementsDown;
import abcmap.gui.ie.position.MoveElementsTop;
import abcmap.gui.ie.position.MoveElementsUp;
import abcmap.gui.ie.selection.SelectAll;
import abcmap.gui.ie.selection.UnselectAll;
import abcmap.gui.toolbar.Toolbar;

public class EditionToolbar extends Toolbar {

	public EditionToolbar() {
		addInteractionElement(new DeleteSelectedElements());
		addInteractionElement(new Duplicate());
		addInteractionElement(new Copy());
		addInteractionElement(new Paste());
		addInteractionElement(new PasteAsTile());
		addInteractionElement(new SelectAll());
		addInteractionElement(new UnselectAll());
		addInteractionElement(new MoveElementsUp());
		addInteractionElement(new MoveElementsDown());
		addInteractionElement(new MoveElementsTop());
		addInteractionElement(new MoveElementsBottom());
		addInteractionElement(new AnalyseSelectedTiles());
	}

}
