package org.abcmap.tests.core.utils;

import org.abcmap.TestUtils;
import org.abcmap.core.utils.Utils;
import org.abcmap.tests.core.tile.TileComposerTest;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 25/01/17.
 */
public class UtilsTest {

    @Test
    public void tests() throws IOException {

        // write image test
        InputStream source = TileComposerTest.class.getResourceAsStream("/tiles/osm_1.png");
        Path destinationPath = TestUtils.PLAYGROUND_DIRECTORY.resolve("writedImage.png");
        BufferedImage image = ImageIO.read(source);

        Utils.writeImage(image, destinationPath);

        assertTrue("Image writing test: ", Files.isRegularFile(destinationPath));
    }

}
