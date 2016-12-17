package org.abcmap.core.project.layouts;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.nio.file.Path;

/**
 * Utility used when printing layout sheets
 * <p>
 * This class can be added to a Java Book
 */
public class LayoutPaper extends PageFormat implements Printable {

    public LayoutPaper(LayoutSheet sheet, Path tmp) {

    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        return 0;
    }
}
