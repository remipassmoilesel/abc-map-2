package abcmap.draw.shapes;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.imageio.ImageIO;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.geo.GeoInfoMode;
import abcmap.managers.Log;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class Image extends RectangleShape {

	/** Le nombre d'essais lors d'une operation E/S */
	public static final int SAFE_MODE_TRYS = 3;

	private static final String IMAGE_PREFIX = "image_";

	/** Le fichier source de l'image */
	protected File sourceFile;

	/** Indicateur de calcul des dimensions à partir de l'image */
	protected boolean dimensionsFromImage;

	/** L'image en elle meme, reference a supprimer apres usage */
	protected BufferedImage bimg;

	protected boolean serializeImageAfterUse;

	/** La bordure à dessiner */
	private Rectangle borderRectangle;

	public Image() {
		super();

		// pas de bordure par defaut
		this.stroke.setFgColor(null);

		this.borderRectangle = new Rectangle();

		// des/activer la serialisation de l'image
		this.serializeImageAfterUse = false;

		// l'emplacement du fichier image source
		this.sourceFile = null;

		// par defaut, les dimensions proviennnent de l'image
		this.dimensionsFromImage = true;

		refreshShape();
	}

	protected void breakImageRef() {
		if (serializeImageAfterUse) {
			bimg = null;
		}
	}

	public Image(Image img) {
		this();
		setProperties(img.getProperties());

		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Image == false)
			return false;

		Image shp = (Image) obj;

		Object[] toCompare1 = new Object[] { this.bounds, this.sourceFile, this.selected,
				this.stroke };

		Object[] toCompare2 = new Object[] { shp.bounds, shp.sourceFile, shp.selected, shp.stroke };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	public File getSourceFile() {
		return sourceFile;
	}

	@Override
	public void setDimensions(Dimension dim) {
		super.setDimensions(dim);
		dimensionsFromImage = false;
	}

	@Override
	public void refreshShape() {

		// dimensions de bordures pour calculs
		boolean thereIsBorder = stroke.getFgColor() != null;

		int thickness = thereIsBorder ? stroke.getThickness() : 0;
		int doubleThick = thereIsBorder ? stroke.getThickness() * 2 : 0;
		int halfThick = thereIsBorder ? stroke.getHalfThickness() : 0;

		bounds.x = position.x + thickness;
		bounds.y = position.y + thickness;

		// calculs des bounds par rapport l'image seulement si nouveau
		// chargement
		if (dimensionsFromImage == true) {
			if (bimg == null) {
				if (unserializeImageSafeMode() == false) {
					return;
				}
			}
			bounds.width = bimg.getWidth() + doubleThick;
			bounds.height = bimg.getHeight() + doubleThick;
			dimensionsFromImage = false;
		}

		// rectangle de bordure
		borderRectangle.x = bounds.x - halfThick;
		borderRectangle.y = bounds.y - halfThick;
		borderRectangle.width = bounds.width + thickness;
		borderRectangle.height = bounds.height + thickness;

		// mettre à jour les dimensions max
		maximumBounds.x = borderRectangle.x - halfThick;
		maximumBounds.y = borderRectangle.y - halfThick;
		maximumBounds.width = borderRectangle.width + thickness;
		maximumBounds.height = borderRectangle.height + thickness;

		// calculer l'aire d'interaction
		int m = drawm.getInteractionAreaMargin() + thickness;
		Rectangle boundsWithMargin = new Rectangle(maximumBounds.x - m, maximumBounds.y - m,
				maximumBounds.width + m, maximumBounds.height + m);
		this.interactionArea = new Area(boundsWithMargin);

		// ajuster les poignees
		adjustHandlesToBounds();

		// ajuster les etiquettes geographiques
		updateGeoLabels();
	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// recuperer l'image
		if (bimg == null) {
			if (unserializeImageSafeMode() == false) {
				return;
			}
		}

		// dessiner l'image
		g.drawImage(bimg, bounds.x, bounds.y, bounds.width, bounds.height, null);

		// dessiner le cadre
		if (stroke.getFgColor() != null) {
			g.setStroke(stroke.getSwingStroke());
			g.setColor(stroke.getFgColor());
			g.draw(borderRectangle);
		}

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// dessin des etiquettes geo
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			drawGeoLabels(g);
		}

		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode)) {

			// cadre de selection
			if (selected) {

				g.setColor(drawm.getSelectionColor());
				g.setStroke(drawm.getSelectionStroke());
				g.draw(bounds);

				// dessin des poignes
				drawHandles(g);
			}

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());

		// casser la reference vers l'image
		breakImageRef();

	}

	/**
	 * Charger une image puis l'enregistrer dans le projet
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public void loadImage(String path) throws IOException {
		loadAndSaveImage(new File(path));
	}

	/**
	 * Charger une image puis l'enregistrer dans le projet
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean loadAndSaveImage(File path) throws IOException {

		if (path.isFile() == false) {
			throw new IOException("Le fichier n'existe pas: " + path.getAbsolutePath());
		}

		return loadAndSaveImage(ImageIO.read(path));
	}

	/**
	 * Charger une image puis l'enregistrer
	 * 
	 * @param img
	 * @param crop
	 * @throws IOException
	 */
	public boolean loadAndSaveImage(BufferedImage img) throws IOException {

		// si image deja enregistree, suppression
		if (sourceFile != null) {
			if (sourceFile.isFile() == true) {
				sourceFile.delete();
			}
		}

		// nouveau nom sous forme date + millisecondes
		String newName = IMAGE_PREFIX + new SimpleDateFormat("yyyy-M-d-HH-mm_S").format(new Date())
				+ ".jpg";
		sourceFile = Paths
				.get(projectm.getProject().getTempDirectoryFile().getAbsolutePath(), newName)
				.toFile();

		// ecrire l'image
		Utils.writeImage(img, sourceFile);

		// recuperer les dimensions à partir de l'image
		dimensionsFromImage = true;

		return true;
	}

	/**
	 * Charge une image sans l'enregistrer. Accepte seuelement le nom de
	 * l'image, pas de chemin relatif ou absolu.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void setImage(String path) throws IOException {

		// assigner les champs
		this.sourceFile = Paths.get(projectm.getTempDirectoryPath(), path).toFile();

		// lire l'image
		unserializeImage();

		// veirifer que l'image soit correctment chargée
		if (bimg == null)
			throw new IOException("Unable to load image: " + path);

		// charger les dimensions de l'image par defaut
		this.dimensionsFromImage = true;

		// casser le lien vers l'image
		breakImageRef();

	}

	/**
	 * Lire l'image à partir du fichier source, et stocker une BufferedImage
	 * dans le champs bimg.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected void unserializeImage() throws IOException {

		if (sourceFile == null)
			throw new NullPointerException("Null source");

		bimg = ImageIO.read(sourceFile);
	}

	protected boolean unserializeImageSafeMode() {

		for (int i = 0; i < SAFE_MODE_TRYS; i++) {

			try {
				// extraire les points
				unserializeImage();

				// l'extraction s'est bien passé, arrêt
				return true;

			} catch (IOException e) {

				// erreur lors du chargement, nouvelle tentative
				// Log.error(e);

			} catch (NullPointerException e) {

				// la source est nulle, retour
				// Log.error(e);

				return false;

			}
		}

		Log.error(new IOException("Unable to load ressource: " + this.sourceFile));
		return false;

	}

	protected void serializeImage() throws IOException {

		if (sourceFile == null) {
			throw new NullPointerException("Source is null");
		}

		try {
			Utils.writeImage(bimg, sourceFile);
		}

		catch (Exception e) {

			// erreur lors de l'ecriture
			Log.error(e);
			throw new IOException(e);
		}
	}

	protected boolean serializeImageSafeMode() {
		for (int i = 0; i < SAFE_MODE_TRYS; i++) {
			try {

				serializeImage();

				// l'enregistrement s'est bien passé, arret
				return true;

			} catch (IOException e) {

				// erreur lors du chargement, nouvel essai
				// Log.error(e);

			} catch (NullPointerException e) {

				// la destination est nulle, arret
				// Log.error(e);

				return false;
			}
		}

		Log.error(new IOException("Unable to write ressource: " + this.sourceFile));
		return false;

	}

	public void crop(Rectangle r) {

		// recuperer l'image
		if (bimg == null) {
			if (unserializeImageSafeMode() == false) {
				return;
			}
		}

		int x = r.x;
		int y = r.y;
		int w = r.width;
		int h = r.height;

		// verifier les dimensions de recadrage
		if (r.width > bimg.getWidth()) {
			w = bimg.getWidth();
		}
		if (r.height > bimg.getHeight()) {
			h = bimg.getHeight();
		}

		if (x > w || y > h) {
			Log.error(new IllegalStateException("Incorrect cropping dimensions: " + r));
		}

		else {
			// recadrer l'image
			bimg = bimg.getSubimage(x, y, w, h);
		}

		// sauvegarder la nouvelle image
		serializeImageSafeMode();

		breakImageRef();

	}

	public void resetImage() {

		try {
			setImage(sourceFile.getName());
		} catch (IOException e) {
			Log.error(e);
		}

	}

	@Override
	public LayerElement duplicate() {
		return new Image(this);
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		// fichier source
		sourceFile = new File(pp.sourceFile);

		// dimensions + position
		setPosition(pp.position);
		setDimensions(pp.dimensions);
		setStroke(DrawProperties.createNewWith(pp.stroke));

		// lien
		setLinkRessource(pp.linkRessource);

	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();
		pp.position = getPosition();
		pp.dimensions = bounds.getSize();
		pp.sourceFile = sourceFile.getName();

		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();

		// lien
		pp.linkRessource = linkRessource;

		return pp;
	}
	
	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Image sample = (Image) this.duplicate();

		// dimensionner
		int t = stroke.getThickness();
		sample.setDimensions(maxWidth - t, maxHeight - t);

		// valider leschangements
		sample.refreshShape();

		return sample;
	}

}
