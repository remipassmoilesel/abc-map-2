package org.abcmap.ielements.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.ielements.analyse.AnalyseSelectedTiles;
import org.abcmap.ielements.copy.Copy;
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
