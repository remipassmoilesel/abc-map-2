package org.abcmap.core.utils;

import org.abcmap.TestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 20/11/16.
 */
public class ZipUtilsTest {

    @Test
    public void tests() throws IOException {

        Path tempDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("zipTest");
        FileUtils.deleteDirectory(tempDir.toFile());
        Files.createDirectories(tempDir);

        // create files to compress
        ArrayList<Path> files = new ArrayList<Path>();
        for (int i = 0; i < 10; i++) {
            Path p = tempDir.resolve("file_" + i + ".txt");
            Files.copy(ZipUtilsTest.class.getResourceAsStream("/files/if.txt"), p);
            files.add(p);
        }

        assertTrue("Preparation test", files.size() == 10);

        Path archive = tempDir.resolve("archive.zip");

        // compress files
        ZipUtils.compress(files, archive);

        assertTrue("Compression test", Files.exists(archive));

        // uncompress files
        Path dest = tempDir.resolve("uncompressed");
        Files.createDirectories(dest);
        ZipUtils.uncompress(archive, dest);

        for (int i = 0; i < 10; i++) {
            assertTrue("Uncompression test " + i, Files.exists(dest.resolve("file_" + i + ".txt")));
        }
    }

}
