package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layouts.LayoutManagerException;
import org.abcmap.core.project.layouts.LayoutPaper;
import org.abcmap.core.project.layouts.LayoutSheet;
import org.abcmap.core.rendering.CachedRenderingEngine;
import org.abcmap.core.rendering.RenderingException;
import org.abcmap.core.utils.Utils;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class LayoutManager {

    private static final CustomLogger logger = LogManager.getLogger(LayoutManager.class);

    private final ProjectManager pman;
    private final TempFilesManager tmpm;
    private final DialogManager dialm;
    private final ReentrantLock printLock;

    public LayoutManager() {
        pman = MainManager.getProjectManager();
        tmpm = MainManager.getTempFilesManager();
        dialm = MainManager.getDialogManager();
        printLock = new ReentrantLock();
    }

    /**
     * Print layouts of project. If project is not initialized, throw an exception.
     * <p>
     * If another print operation is in progress, throw an exception
     *
     * @throws IOException
     * @throws LayoutManagerException
     */
    public void printLayouts() throws IOException, LayoutManagerException {

        if (printLock.tryLock() == false) {
            throw new LayoutManagerException(LayoutManagerException.ALREADY_PRINTING);
        }

        try {

            if (pman.isInitialized() == false) {
                throw new LayoutManagerException(LayoutManagerException.PROJECT_NON_INITIALIZED);
            }

            Project project = pman.getProject();

            // draw layouts in files
            CachedRenderingEngine renderingEngine = new CachedRenderingEngine(project);

            // draw all layouts
            ArrayList<LayoutSheet> layouts = project.getLayouts();
            ArrayList<LayoutPaper> papers = new ArrayList<>();
            for (LayoutSheet sheet : layouts) {

                // create an image and render layout on
                int w = (int) Utils.millimeterToPixel(sheet.getWidthMm(), 72);
                int h = (int) Utils.millimeterToPixel(sheet.getWidthMm(), 72);

                Dimension dim = new Dimension(w, h);
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                // render map in
                ReferencedEnvelope env = sheet.getEnvelope();
                try {
                    renderingEngine.prepareMap(env, dim, sheet.getScale());
                } catch (RenderingException e) {
                    logger.error(e);
                }
                renderingEngine.waitForRendering();
                renderingEngine.paint((Graphics2D) img.getGraphics());

                // write image to disk
                Path tmp = tmpm.createTemporaryFile("layout_", ".png");
                ImageIO.write(img, "png", Files.newOutputStream(tmp));

                System.out.println(tmp);

                // create paper which can be print
                papers.add(new LayoutPaper(sheet, tmp));

            }

            // create a new print job
            PrinterJob prnJob = PrinterJob.getPrinterJob();

            // name print operation
            String name = project.getMetadataContainer().getValue(PMConstants.TITLE);
            if (name == null || name.isEmpty()) {
                name = "Projet Abc-Map";
            }
            prnJob.setJobName(name);

            // one copy by default
            prnJob.setCopies(1);

            // constitute book to print
            Book book = new Book();
            for (LayoutPaper lay : papers) {
                book.append(lay, lay);
            }

            // /!\ always affect book after first operations
            prnJob.setPageable(book);

            if (prnJob.printDialog()) {

                // find best resolution available
                int res = 0;

                // iterate available resolutions
                PrinterResolution[] supportedResolutions = (PrinterResolution[]) prnJob
                        .getPrintService()
                        .getSupportedAttributeValues(javax.print.attribute.standard.PrinterResolution.class, null, null);

                for (PrinterResolution sr : supportedResolutions) {
                    int[] resolution = sr.getResolution(PrinterResolution.DPI);

                    // use first number
                    if (resolution[0] >= ConfigurationConstants.DEFAULT_PRINT_RESOLUTION) {
                        res = resolution[0];
                        break;
                    }
                }

                // set resolution
                PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                attr.add(new PrinterResolution(res, res, ResolutionSyntax.DPI));

                // print
                try {
                    prnJob.print(attr);
                } catch (PrinterException e) {
                    dialm.showErrorInBox("Erreur lors de l'impression");
                    logger.error(e);
                }
            }

            // TODO: delete temp images

        }

        // unlock
        finally {
            printLock.unlock();
        }
    }
}
