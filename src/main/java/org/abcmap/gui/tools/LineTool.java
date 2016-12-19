package org.abcmap.gui.tools;

import org.abcmap.core.draw.LineBuilder;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.FeatureLayer;

import java.awt.event.MouseEvent;

/**
 * Created by remipassmoilesel on 19/12/16.
 */
public class LineTool extends MapTool{

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        checkProjectOrShowMessage();
    }
}
