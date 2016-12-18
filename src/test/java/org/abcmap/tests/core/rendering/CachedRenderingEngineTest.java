package org.abcmap.tests.core.rendering;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.rendering.CachedRenderingEngine;
import org.abcmap.gui.utils.GuiUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 17/12/16.
 */
public class CachedRenderingEngineTest {

    private boolean showInWindow = false;

    @BeforeClass
    public static void beforeTests() throws IOException, InterruptedException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws Exception {

        // create a fake project
        ProjectManager pman = MainManager.getProjectManager();
        pman.createFakeProject();
        Project project = pman.getProject();

        // create an image where project will be rendered
        Dimension imageDimension = new Dimension(3000, 3000);
        BufferedImage bimg = new BufferedImage(imageDimension.width, imageDimension.height, BufferedImage.TYPE_INT_ARGB);

        // test waiting
        long before = System.currentTimeMillis();
        CachedRenderingEngine renderer = new CachedRenderingEngine(project);
        renderer.prepareMap(project.getMaximumBounds(), imageDimension);

        renderer.waitForRendering();

        renderer.paint((Graphics2D) bimg.getGraphics());
        long renderingTime = System.currentTimeMillis() - before;

        assertTrue("Rendering wait test: " + renderingTime, renderingTime > 1);

        if (showInWindow) {
            GuiUtils.showImage(bimg);
            Thread.sleep(50000);
        }

    }
}
