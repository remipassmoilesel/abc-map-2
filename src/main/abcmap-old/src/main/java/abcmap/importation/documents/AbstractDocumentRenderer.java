package abcmap.importation.documents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;

public abstract class AbstractDocumentRenderer {

	/**
	 * Retourne une nouvelle instance de tous les objets d'importation
	 * disponbles.
	 * <p>
	 * Retourne une nouvelle instance pour éviter les problèmes d'appels
	 * simultanés.
	 * 
	 * @return
	 */
	public static AbstractDocumentRenderer[] getAvailablesRenderers() {
		return new AbstractDocumentRenderer[] { new PdfRenderer(), new SvgRenderer(),
				new ImageRenderer() };
	}

	/** Facteur d'agrandissement du document à importer */
	protected float factor;

	/** Les numéros de pages à importer, si necessaire */
	protected Integer[] pageNumbersToImport;

	protected ProjectManager projectm;

	public AbstractDocumentRenderer() {

		this.projectm = MainManager.getProjectManager();

		this.factor = 1f;
		this.pageNumbersToImport = new Integer[] { 0 };
	}

	/**
	 * Retourne la liste des extensions de fichiers supprotées par l'objet
	 * d'import. Par exemple: "jpg", "png", ...
	 * <p>
	 * Toutes les extensions doivent être en minuscules.
	 * 
	 * @return
	 */
	public abstract String[] getSupportedExtensions();

	/**
	 * Retourne les dimensions lues directement à partir du fichier source.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract Dimension[] getDocumentDimensions(File file) throws IOException;

	/**
	 * Importer le document et retourner une BufferedImage.
	 * 
	 * @return
	 */
	public abstract BufferedImage[] render(File file) throws IOException;

	/**
	 * Renvoi vrai si l'objet d'import supporte l'extension passée en paramètre.
	 * 
	 * @param extension
	 * @return
	 */
	public boolean isSupportingExtension(String extension) {

		// extension en minuscule
		String[] list = getSupportedExtensions();

		// rechercher et renvoyer
		return Arrays.asList(list).contains(extension.toLowerCase());
	}

	/**
	 * Facteur de grossissement du document à importer.
	 * 
	 * @param factor
	 */
	public void setFactor(float factor) {
		this.factor = factor;
	}

	/**
	 * Facteur de grossissement du document à importer.
	 * 
	 * @param factor
	 */
	public float getFactor() {
		return factor;
	}

	/**
	 * Liste des pages à importer. Si la liste contient 0 alors toutes les pages
	 * seront importées.
	 * <p>
	 * Le compte des pages commene à un.
	 * 
	 * @param str
	 */
	public void setPageNumbersToImport(String str) {

		pageNumbersToImport = Utils.stringToIntArray(str);

		// si la liste contient un zéro, imprimer toutes les pages
		if (Arrays.asList(pageNumbersToImport).contains(0)) {
			pageNumbersToImport = new Integer[] { 0 };
		}
	}

	public Integer[] getPagesNumbersToImport() {
		return pageNumbersToImport;
	}

	/**
	 * Retourne un lecteur compatible avec l'extension ou null si aucun ne
	 * correspond.
	 * 
	 * @param extension
	 * @return
	 */
	public static AbstractDocumentRenderer getRendererFor(String extension) {

		if (extension == null) {
			throw new NullPointerException("Extension is null");
		}

		// minuscules et sans espaces superflus
		extension = extension.toLowerCase().trim();

		// iterer jusqu'à trouver l'extension
		for (AbstractDocumentRenderer renderer : getAvailablesRenderers()) {
			if (renderer.isSupportingExtension(extension)) {
				return renderer;
			}
		}

		// pas de renderer trouvé, retour null
		return null;

	}

}
