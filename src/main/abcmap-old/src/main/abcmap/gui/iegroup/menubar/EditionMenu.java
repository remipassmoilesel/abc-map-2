package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.copy.Copy;
import abcmap.gui.ie.copy.Cut;
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
import abcmap.gui.ie.undoredo.Redo;
import abcmap.gui.ie.undoredo.Undo;
import abcmap.gui.iegroup.InteractionElementGroup;

public class EditionMenu extends InteractionElementGroup {

	public EditionMenu() {

		label = "Edition";

		addInteractionElement(new Undo());
		addInteractionElement(new Redo());
		addSeparator();
		addInteractionElement(new Copy());
		addInteractionElement(new Cut());
		addInteractionElement(new Paste());
		addInteractionElement(new PasteAsTile());
		addSeparator();
		addInteractionElement(new Duplicate());
		addInteractionElement(new DeleteSelectedElements());
		addSeparator();
		addInteractionElement(new SelectAll());
		addInteractionElement(new UnselectAll());
		addSeparator();
		addInteractionElement(new MoveElementsUp());
		addInteractionElement(new MoveElementsDown());
		addInteractionElement(new MoveElementsTop());
		addInteractionElement(new MoveElementsBottom());
	}

}
