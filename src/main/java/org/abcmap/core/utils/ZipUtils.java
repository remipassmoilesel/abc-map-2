package org.abcmap.core.utils;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
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
    public static void compress(Path root, List<Path> files, Path archiveDestination) throws IOException {

        if(root == null){
            root = Paths.get(".");
        }

        OutputStream output = Files.newOutputStream(archiveDestination);

        ZipArchiveOutputStream zos = null;
        try {
            zos = (ZipArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, output);
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        for (Path p : files) {

            int toRemove = root.toAbsolutePath().normalize().toString().length();
            String entryName = p.toAbsolutePath().normalize().toString().substring(toRemove + 1);

            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
            zos.putArchiveEntry(entry);

            if (Files.isRegularFile(p)) {
                InputStream is = Files.newInputStream(p);
                IOUtils.copy(is, zos);
                is.close();
            }
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

            Files.createDirectories(f.getParent());

            OutputStream out = Files.newOutputStream(f);
            IOUtils.copy(in, out);
            out.close();
            result.add(f);
            entry = in.getNextZipEntry();
        }
        in.close();

        return result;
    }

    /**
     * Walk a file tree recursively and compress selected files
     * <p>
     * If a file must be compressed, ZipVisitor must return true
     * <p>
     * If a file must not be compressed, ZipVisitor must return false
     * <p>
     * If visit have to stop and cancel, ZipVisitor return null
     * <p>
     * /!\ This doesnt walk directories
     *
     * @param start
     * @param destination
     * @param visitor
     * @throws IOException
     */
    public static void walkFileTree(Path start, Path destination, ZipVisitor visitor) throws IOException {

        ArrayList<Path> toCompress = new ArrayList<>();
        boolean[] cancel = new boolean[]{false};

        Files.walkFileTree(start, new FileVisitorAdapter() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return evaluateFile(file, attrs);
            }

            private FileVisitResult evaluateFile(Path file, BasicFileAttributes attrs) {
                Boolean result = visitor.processFile(file, attrs);

                // stop visiting
                if (result == null) {
                    cancel[0] = true;
                    return FileVisitResult.TERMINATE;
                }

                // compress file
                else if (result == true) {

                    toCompress.add(file);

                    return FileVisitResult.CONTINUE;
                }

                return FileVisitResult.CONTINUE;
            }
        });

        if (cancel[0]) {
            return;
        }

        compress(start, toCompress, destination);

    }

    /**
     * Compress a whole directory
     *
     * @param source
     * @param destination
     * @throws IOException
     */
    public static void compressFolder(Path source, Path destination) throws IOException {

        OutputStream archiveStream = new FileOutputStream(destination.toFile());
        ArchiveOutputStream archive = null;
        try {
            archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream);
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        Collection<File> fileList = FileUtils.listFiles(source.toFile(), null, true);

        for (File file : fileList) {

            int toRemove = source.toAbsolutePath().normalize().toString().length();
            String entryName = file.toPath().toAbsolutePath().normalize().toString().substring(toRemove + 1);

            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
            archive.putArchiveEntry(entry);

            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

            IOUtils.copy(input, archive);
            input.close();
            archive.closeArchiveEntry();
        }

        archive.finish();
        archiveStream.close();
    }
}
