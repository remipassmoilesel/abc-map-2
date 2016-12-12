package org.abcmap.gui.ie.export;

import org.abcmap.gui.ie.InteractionElement;

public class PrintLayouts extends InteractionElement {

    public PrintLayouts() {
        this.label = "Imprimer la mise en page...";
        this.help = "Cliquez ici pour imprimer votre carte mise en page. Vous devez au préalable "
                + "mettre en page votre carte à l'aide de l'Assistant demise en page.";
        this.accelerator = shortcuts.PRINT_PROJECT;
    }

    @Override
    public void run() {

        /*

        // pas de lancement dans l'EDT
        GuiUtils.throwIfOnEDT();

        // Verifier le projet et obtenir le calque actif, ou afficher un
        // message d'erreur
        MapLayer layer = checkProjectAndGetActiveLayer();
        if (layer == null) {
            return;
        }

        // eviter les appels intempestifs
        if (threadAccess.askAccess() == false) {
            return;
        }

        // threadAccess.releaseAccess();

        final ArrayList<LayoutPaper> sheets = projectm.getLayouts();
        if (sheets.size() < 1) {
            guim.showProjectWithoutLayoutError();
            threadAccess.releaseAccess();
            return;
        }

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Attention: respecter l'ordre de ces étapes
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // creation du travail d'impression
        PrinterJob prnJob = PrinterJob.getPrinterJob();

        // nommer le projet
        String name = projectm.getMetadatas().PROJECT_TITLE;

        if (name == null || name == "") {
            name = "Projet Abc-Map";
        }
        prnJob.setJobName(name);

        // 1 copie par défaut
        prnJob.setCopies(1);

        // constituer le document à imprimer
        Book book = new Book();
        for (LayoutPaper lay : sheets) {
            book.append(lay, lay.getPageFormat());
        }
        prnJob.setPageable(book);

        // boite de dialogue d'impression
        boolean print = prnJob.printDialog();

        // l'utilisateur à annulé
        if (print == false) {
            threadAccess.releaseAccess();
            return;
        }

        // Determiner la resolution optimale parmi celles qui sont
        // disponibles

        // Resolution pour specification à l'imprimante
        // PAS POUR calculs de taille et d'echelle: tout en 72dpi
        int res = Math.round(ConfigurationConstants.JAVA_RESOLUTION);
        configm.setPrintResolution(res);
        boolean error = false;

        try {

            // trouver et parcourir les résolutions disponibles
            PrinterResolution[] supportedResolutions = (PrinterResolution[]) prnJob
                    .getPrintService()
                    .getSupportedAttributeValues(
                            PrinterResolution.class,
                            null, null);

            for (PrinterResolution sr : supportedResolutions) {
                int[] resolution = sr.getResolution(PrinterResolution.DPI);

                // prise en compte du 1er chiffre seulement
                if (resolution[0] >= ConfigurationConstants.DEFAULT_PRINT_RESOLUTION) {
                    res = resolution[0];
                    break;
                }
            }

        } catch (Exception e) {
            Log.error(e);
            error = true;
        }

        try {

            // attribuer la resolution uniquement si pas d'erreurs
            if (error == false) {
                PrintRequestAttributeSet attr = null;
                attr = new HashPrintRequestAttributeSet();
                attr.add(new PrinterResolution(res, res, ResolutionSyntax.DPI));
                prnJob.print(attr);
            }

            // sinon confirmation puis impression en l'etat
            else {

                // confirmation
                QuestionResult result = SimpleQuestionDialog
                        .askQuestion(
                                null,
                                "Une erreur est survenue lors "
                                        + "de la configuration de l'impression. Il se pourrait que la qualité de votre impression "
                                        + "ne soit pas suffisante. Voulez vous continuer ou annuler votre impression ?");

                // impression
                if (QuestionResult.YES.equals(result.getReturnVal())) {
                    prnJob.print();
                }
            }

        } catch (Exception e) {
            guim.showErrorInBox("Erreur lors de l'impression.");
            Log.error(e);
        }

        threadAccess.releaseAccess();

        */
    }

}
