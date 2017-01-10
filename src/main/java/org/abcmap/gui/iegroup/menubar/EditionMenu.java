package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.copy.Copy;
import org.abcmap.ielements.copy.Cut;
import org.abcmap.ielements.copy.Paste;
import org.abcmap.ielements.copy.PasteAsTile;
import org.abcmap.ielements.edition.DeleteSelectedElements;
import org.abcmap.ielements.edition.Duplicate;
import org.abcmap.ielements.position.MoveElementsBottom;
import org.abcmap.ielements.position.MoveElementsDown;
import org.abcmap.ielements.position.MoveElementsTop;
import org.abcmap.ielements.position.MoveElementsUp;
import org.abcmap.ielements.selection.SelectAll;
import org.abcmap.ielements.selection.UnselectAll;
import org.abcmap.ielements.undoredo.Redo;
import org.abcmap.ielements.undoredo.Undo;


public class EditionMenu extends GroupOfInteractionElements {

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
