package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.copy.Copy;
import org.abcmap.gui.ie.copy.Cut;
import org.abcmap.gui.ie.copy.Paste;
import org.abcmap.gui.ie.copy.PasteAsTile;
import org.abcmap.gui.ie.edition.DeleteSelectedElements;
import org.abcmap.gui.ie.edition.Duplicate;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsBottom;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsDown;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsTop;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsUp;
import org.abcmap.gui.toProcess.gui.ie.selection.SelectAll;
import org.abcmap.gui.toProcess.gui.ie.selection.UnselectAll;
import org.abcmap.gui.toProcess.gui.ie.undoredo.Redo;
import org.abcmap.gui.toProcess.gui.ie.undoredo.Undo;


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
