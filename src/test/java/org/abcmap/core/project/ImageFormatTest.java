package org.abcmap.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.utils.Utils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test if minimal image format are supported on system
 */
public class ImageFormatTest {

    @Test
    public void test() throws IOException {

        List<String> requirements = Arrays.asList("png", "jpg", "jpeg", "tiff");
        List<String> supported = Arrays.asList(Utils.getAllImageSupportedFormats());

        for (String format : requirements) {
            assertTrue("Minimal image formats requirements test: " + format, supported.contains(format.toLowerCase().trim()));
        }

        Path samplesRoot = TestUtils.RESOURCES_DIRECTORY.resolve("pictures");
        Iterator<Path> dit = Files.newDirectoryStream(samplesRoot).iterator();

        int tested = 0;
        while (dit.hasNext()) {

            Path p = dit.next();
            BufferedImage img = ImageIO.read(p.toFile());

            assertTrue("Image reading test: " + p.toString(), img != null);

            tested++;
        }

        assertTrue("Image reading test", tested > 0);
    }


}
