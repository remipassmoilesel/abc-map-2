package org.abcmap.gui.ie.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.gui.ie.analyse.AnalyseSelectedTiles;
import org.abcmap.gui.ie.copy.Copy;
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
