package org.abcmap.tests.core.utils;

import org.abcmap.TestUtils;
import org.abcmap.core.utils.Utils;
import org.abcmap.core.utils.ZipUtils;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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
        ZipUtils.compress(tempDir, files, archive);

        assertTrue("Compression test", Files.exists(archive));

        // uncompress files
        Path dest = tempDir.resolve("uncompressed");
        Files.createDirectories(dest);
        ZipUtils.uncompress(archive, dest);

        for (int i = 0; i < 10; i++) {
            assertTrue("Uncompression test " + i, Files.exists(dest.resolve("file_" + i + ".txt")));
        }

        // test file visit
        Path folderArch = tempDir.resolve("folder.zip");
        ZipUtils.compressFolder(tempDir, folderArch);

        assertTrue("Walking compression test", Files.exists(folderArch));

        // test file visit
        Path folderArch2 = tempDir.resolve("folder2.zip");
        ZipUtils.walkFileTree(tempDir, folderArch2, (p, attrs) -> {

            if (Utils.checkExtension(p, "txt")) {
                return true;
            }

            return false;
        });

        assertTrue("Walking compression test", Files.exists(folderArch));
    }

}
