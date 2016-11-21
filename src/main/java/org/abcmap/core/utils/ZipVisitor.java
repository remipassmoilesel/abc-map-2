package org.abcmap.core.utils;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by remipassmoilesel on 21/11/16.
 */
public interface ZipVisitor {

    /**
     * Process file.
     * <p>
     * Return to true to archive this file,
     * <p>
     * Return false to not archive file,
     * <p>
     * Return null to stop walking
     *
     * @param p
     * @param attrs
     * @return
     */
    public Boolean processFile(Path p, BasicFileAttributes attrs);

}
