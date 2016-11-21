package org.abcmap.core.utils;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by remipassmoilesel on 20/11/16.
 */
public class ZipUtils {

    /**
     * Compress a list of files to the specified destination
     *
     * @param files
     * @param archiveDestination
     * @throws IOException
     */
    public static void compress(List<Path> files, Path archiveDestination) throws IOException {

        OutputStream output = Files.newOutputStream(archiveDestination);

        ZipArchiveOutputStream zos = null;
        try {
            zos = (ZipArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, output);
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        for (Path p : files) {
            ZipArchiveEntry entry = new ZipArchiveEntry(p.getFileName().toString());
            zos.putArchiveEntry(entry);
            InputStream is = Files.newInputStream(p);
            IOUtils.copy(is, zos);
            is.close();
            zos.closeArchiveEntry();
        }

        zos.close();
    }

    /**
     * Uncompress an archive to the destination directory
     *
     * @param archive
     * @param destinationDirectory
     * @return
     * @throws IOException
     */
    public static List<Path> uncompress(Path archive, Path destinationDirectory) throws IOException {
        List<Path> result = new ArrayList<>();
        InputStream inputStream = Files.newInputStream(archive);
        ZipArchiveInputStream in = new ZipArchiveInputStream(inputStream);
        ZipArchiveEntry entry = in.getNextZipEntry();
        while (entry != null) {
            Path f = destinationDirectory.resolve(entry.getName());
            OutputStream out = Files.newOutputStream(f);
            IOUtils.copy(in, out);
            out.close();
            result.add(f);
            entry = in.getNextZipEntry();
        }
        in.close();

        return result;
    }
}
