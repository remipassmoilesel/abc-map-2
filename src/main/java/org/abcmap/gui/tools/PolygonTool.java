package org.abcmap.gui.tools;

import java.awt.event.MouseEvent;

/**
 * Created by remipassmoilesel on 19/12/16.
 */
public class PolygonTool extends MapTool{

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        getProjectOrShowMessage();
    }
}
