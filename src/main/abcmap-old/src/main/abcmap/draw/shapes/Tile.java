package abcmap.draw.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.links.LinkRessource;
import abcmap.exceptions.TileAnalyseException;
import abcmap.managers.Log;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.surf.IJFacade;
import abcmap.surf.IntegralImage;
import abcmap.surf.InterestPoint;
import abcmap.utils.PrintUtils;
import abcmap.utils.gui.GuiUtils;
import ij.ImagePlus;

public class Tile extends Image {

	private static final String SERIALIZE_POINTS_EXTENSION = "_pts";
	private static final String TILE_PREFIX = "tile_";

	// marge interieure pour cadre de selection
	private static final int BORDER_SELECTION_MARGIN = 10;
	private static final Stroke BORDER_SELECTION_STROKE = new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[] { 5.0f }, 0.0f);
	private static final Color BORDER_SELECTION_STROKE_COLOR = Color.blue;

	/** Liste des points d'interets */
	private List<InterestPoint> ipts;

	/** Serialisation automatique des points d'interet */
	private boolean serializeIptsListAfterUse;

	/** Mode d'analyse de l'image, pour comparaisons */
	private Integer surfMode;

	private boolean showInterestPoints;
	private int interestPointWidth;
	private Color interestPointColor;
	private Font interestPointFont;

	private boolean debugMode = false;
	private long tileId;
	private int interestPointFontSize;
	private Rectangle selectionRectangle;
	private static long painted = 0;
	private static long tileNumber = 0;

	public Tile() {

		this.serializeIptsListAfterUse = true;

		// id de tuile pour debogage
		this.tileId = tileNumber;
		tileNumber++;

		// mode d'analyse surf pour comparaisons
		this.surfMode = -1;

		// rectangle pour dessin de cadre si selectionnee
		this.selectionRectangle = new Rectangle();

		// dessin des points d'interet
		this.showInterestPoints = false;
		this.interestPointWidth = 4;
		this.interestPointFontSize = 22;
		this.interestPointColor = Color.red;
		this.interestPointFont = new Font(Font.DIALOG, Font.PLAIN, interestPointFontSize);

		refreshShape();
	}

	public Tile(Tile t) {

		this();
		this.setProperties(t.getProperties());

		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Tile == false)
			return false;

		Tile shp = (Tile) obj;

		Object[] toCompare1 = new Object[] { this.maximumBounds, this.sourceFile, this.surfMode,
				this.selected, };

		Object[] toCompare2 = new Object[] { shp.maximumBounds, shp.sourceFile, shp.surfMode,
				shp.selected, };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	@Override
	public void draw(Graphics2D g, String mode) {

		if (debugMode) {
			painted++;
			PrintUtils.p("Paint #" + painted + ", drawing tile " + tileId + ": " + this + ", mode: "
					+ mode);
		}

		// recuperer l'image
		if (bimg == null) {
			if (unserializeImageSafeMode() == false) {
				return;
			}
		}

		// dessiner la tuile
		g.drawImage(bimg, maximumBounds.x, maximumBounds.y, null);

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// dessiner les points d'interet au besoin
		if (showInterestPoints) {

			// recuperer les points si necessaire
			if (ipts == null)
				unserializePointsSafeMode();

			// changer la couleur des graphics
			g.setPaint(interestPointColor);
			g.setColor(interestPointColor);

			// dessiner les points
			for (InterestPoint p : ipts) {
				int x = (int) (p.x - interestPointWidth / 2);
				int y = (int) (p.y - interestPointWidth / 2);
				g.fillOval(x, y, interestPointWidth, interestPointWidth);
			}

			// dessiner
			g.setFont(interestPointFont);
			g.drawString(ipts.size() + " point(s)", 10, interestPointFontSize + 15);

			// casser la reference aux points
			breakIptsRef();

		}

		if (selected && Drawable.RENDER_FOR_DISPLAYING.equals(mode)) {

			// creer un nouveau graphics pour tansparence
			Graphics2D g3 = (Graphics2D) g.create();
			GuiUtils.applyQualityRenderingHints(g3);

			// dessiner un voile vert
			g3.setComposite(GuiUtils.createTransparencyComposite(0.3f));
			g3.setColor(Color.green);
			g3.fill(bounds);

			// ressiner un rectangle dans les tuiles selectionnées
			g.setColor(BORDER_SELECTION_STROKE_COLOR);
			g.setStroke(BORDER_SELECTION_STROKE);
			g.draw(selectionRectangle);
		}

		// casser la reference de l'image
		breakImageRef();

	}

	@Override
	public void refreshShape() {

		// recuperer l'image
		if (bimg == null) {
			if (unserializeImageSafeMode() == false) {
				// pas d'image, retour
				return;
			}
		}

		// Remarque: conserver l'utilisation de rectangle et de maximumbounds
		// même si les tuiles n'ont pas de bordures pour conserver une
		// compatibilité maximum entre les differents objets

		bounds.x = position.x;
		bounds.y = position.y;
		bounds.width = bimg.getWidth();
		bounds.height = bimg.getHeight();

		// rectangle bleu de selection
		selectionRectangle.x = bounds.x + BORDER_SELECTION_MARGIN;
		selectionRectangle.y = bounds.y + BORDER_SELECTION_MARGIN;
		selectionRectangle.width = bounds.width - BORDER_SELECTION_MARGIN * 2;
		selectionRectangle.height = bounds.height - BORDER_SELECTION_MARGIN * 2;

		// mettre à jour les dimensions max
		maximumBounds.x = bounds.x;
		maximumBounds.y = bounds.y;
		maximumBounds.width = bounds.width;
		maximumBounds.height = bounds.height;

		// calculer l'aire d'interaction
		int m = drawm.getInteractionAreaMargin();
		Rectangle boundsWithMargin = new Rectangle(maximumBounds.x - m, maximumBounds.y - m,
				maximumBounds.width + m * 2, maximumBounds.height + m * 2);
		this.interactionArea = new Area(boundsWithMargin);

		breakImageRef();
	}

	protected void breakIptsRef() {
		if (serializeIptsListAfterUse) {
			ipts = null;
		}
	}

	/**
	 * Crée une ImagePlus et la retourne<br>
	 * Ne faire que des références locales !
	 * 
	 * @return
	 * @throws TileAnalyseException
	 */
	private ImagePlus createImagePlus() throws TileAnalyseException {

		// recuperer l'image
		// recuperer l'image
		if (bimg == null) {
			if (unserializeImageSafeMode() == false) {
				throw new TileAnalyseException("Image is null");
			}
		}

		// creer une imageplus
		ImagePlus imp = new ImagePlus("", bimg);

		// Conversion de l'image si necessaire
		if (imp.getProcessor().getBitDepth() != ImagePlus.COLOR_RGB) {
			imp.getProcessor().convertToRGB();
			imp.updateAndDraw();
		}

		// casser la reference vers l'image
		breakImageRef();

		return imp;

	}

	/**
	 * Analyse surf de points d'interets
	 * 
	 * @param t
	 * @return
	 * @throws TileAnalyseException
	 */
	public List<InterestPoint> surfAnalyse() throws TileAnalyseException {

		// creer une image speciale pour analyse
		ImagePlus img = this.createImagePlus();
		if (img == null)
			return null;

		// maj parametre surf utilisés pour analyse
		surfMode = new Integer(confm.getConfiguration().SURF_MODE);

		// analyse et extraire les poijnts d'interet
		IntegralImage intImg = new IntegralImage(img.getProcessor(), true);
		ipts = IJFacade.detectAndDescribeInterestPoints(intImg, importm.getSurfParameters());

		// serialisation des points
		serializePointsSafeMode();

		return ipts;
	}

	private boolean serializePointsSafeMode() {

		for (int i = 0; i < SAFE_MODE_TRYS; i++) {
			try {
				// enregistrer les points
				serializePoints();

				return true;
			} catch (IOException e) {
				Log.error(e);
			}
		}

		Log.error(new IOException("Unable to write ressource: " + getPointsFile().toString()));
		return false;
	}

	/**
	 * Sauvegarde la liste des points ou sauvegarde une liste vide si le champs
	 * de classe contient une reference vers nul
	 * 
	 * @throws IOException
	 */
	private void serializePoints() throws IOException {

		// si les points sont nuls, creer une liste vide
		if (ipts == null)
			ipts = new ArrayList<>();

		// enregistrer les points d'interet
		ObjectOutputStream oos = null;
		FileOutputStream out = null;
		try {

			File f = new File(sourceFile.getAbsolutePath() + SERIALIZE_POINTS_EXTENSION);
			out = new FileOutputStream(f);
			oos = new ObjectOutputStream(out);
			oos.writeObject(ipts);

		}

		catch (IOException e) {
			Log.error(e);
			throw new IOException();
		}

		finally {
			try {
				oos.close();
				out.close();
			} catch (Exception e) {
				Log.error(e);
				throw new IOException();
			}
		}
	}

	private boolean unserializePointsSafeMode() {

		for (int i = 0; i < SAFE_MODE_TRYS; i++) {
			try {
				// extraire les points
				unserializePoints();

				// l'extraction s'est bien passé, arrêt
				return true;

			} catch (IOException e) {
				// Log.error(e);
			}
		}

		Log.error(new IOException("Unable to load ressource: " + getPointsFile().toString()));
		return false;

	}

	private void unserializePoints() throws IOException {

		ObjectInputStream ois = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(getPointsFile());

			ois = new ObjectInputStream(in);
			ipts = (ArrayList<InterestPoint>) ois.readObject();
		}

		catch (IOException | ClassNotFoundException e) {
			Log.error(e);
			throw new IOException();
		}

		finally {
			try {
				ois.close();
				in.close();
			} catch (Exception e) {
				Log.error(e);
				throw new IOException();
			}
		}

	}

	private File getPointsFile() {
		return new File(sourceFile.getAbsolutePath() + SERIALIZE_POINTS_EXTENSION);
	}

	/**
	 * Retourne une copie de la liste.<br>
	 * 
	 * @return
	 */
	public List<InterestPoint> getIptsList() {

		// liste indisponible, deserialisation
		if (ipts == null) {
			unserializePointsSafeMode();

			// liste nulle, retour nul
			if (ipts == null)
				return null;
		}

		// copie de reference de la liste
		List<InterestPoint> rslt = ipts;

		// detruire la reference interne
		breakIptsRef();

		return rslt;
	}

	private void clearAndSaveIptsList() {
		ipts = null;
		serializePointsSafeMode();
	}

	@Override
	public boolean loadAndSaveImage(BufferedImage img) throws IOException {
		return loadAndSaveImage(img, null);
	}

	@Override
	public boolean loadAndSaveImage(File path) throws IOException {
		return loadAndSaveImage(path, null);
	}

	/**
	 * Charger une tuile a partir d'une image puis l'enregistrer dans le projet
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean loadAndSaveImage(File path, Rectangle crop) throws IOException {

		if (path.isFile() == false) {
			throw new IOException("File do not exist: " + path.getAbsolutePath());
		}

		return loadAndSaveImage(ImageIO.read(path), crop);
	}

	/**
	 * Charger une tuile a partir d'une image puis l'enregistrer dans le projet
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean loadAndSaveImage(BufferedImage img, Rectangle crop) throws IOException {

		// si tuile deja enregistree, suppression
		if (sourceFile != null) {
			if (sourceFile.isFile() == true) {
				sourceFile.delete();
			}
		}

		// nouveau nom sous forme date + millisecondes
		String newName = TILE_PREFIX + new SimpleDateFormat("yyyy-M-d-HH-mm_S").format(new Date())
				+ ".jpg";
		sourceFile = Paths
				.get(projectm.getProject().getTempDirectoryFile().getAbsolutePath(), newName)
				.toFile();

		// enregistrer l'image une premiere fois
		this.bimg = img;
		serializeImage();

		// recadrer
		if (crop != null) {
			crop(crop);
		}

		// reinitialiser IPTS puis sauvegarder
		clearAndSaveIptsList();

		// enregistrement
		refreshShape();

		return true;
	}

	@Override
	public void crop(Rectangle r) {
		super.crop(r);

		// reinitialiser les points d'interet
		clearAndSaveIptsList();
	}

	@Override
	public LayerElement duplicate() {
		return new Tile(this);
	}

	public Integer getSurfMode() {
		return new Integer(surfMode);
	}

	public void setSurfMode(Integer surfMode) {
		this.surfMode = surfMode;
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		setPosition(pp.position);
		try {
			setImage(pp.sourceFile);
		} catch (IOException e) {
			Log.error(e);
		}

	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();
		pp.position = maximumBounds.getLocation();
		pp.sourceFile = sourceFile.getName();

		return pp;
	}

	@Override
	public void setDimensions(Dimension dimensions) {
		// impossible de modifier les dimensions d'une tuile
	}

	@Override
	public void setLinkRessource(LinkRessource linkRessources) {
		// pas de lien vers les tuiles
	}

	@Override
	public LinkRessource getLinkRessources() {
		// pas de lien vers les tuiles
		return null;
	}

}
