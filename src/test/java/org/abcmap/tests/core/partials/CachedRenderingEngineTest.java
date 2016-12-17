package org.abcmap.tests.core.partials;

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

/**
 * Created by remipassmoilesel on 17/12/16.
 */
public class CachedRenderingEngineTest {

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

        // render project
        long before = System.currentTimeMillis();
        CachedRenderingEngine renderer = new CachedRenderingEngine(project);
        renderer.prepareMap(project.getMaximumBounds(), imageDimension, 1);

//        renderer.waitForRendering();

        renderer.paint((Graphics2D) bimg.getGraphics());
        long renderingTime = System.currentTimeMillis() - before;

        System.out.println(renderingTime);

        GuiUtils.showImage(bimg);

        Thread.sleep(50000);
    }
}
