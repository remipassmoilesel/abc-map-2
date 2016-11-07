package abcmap.importation.robot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

import abcmap.exceptions.MapImportException;
import abcmap.managers.Log;
import abcmap.utils.Utils;

/**
 * Utilitaire de déplacement de carte à la souris. Les déplacements se font avec
 * une part d'aléatoire pour provoquer les chargements de carte en bordure.
 * 
 * @author remipassmoilesel
 *
 */
public class RobotMapMover {

	/**
	 * Delai d'attente après chaque action sur un bouton de la souris, pour
	 * qu'elle soit bien prise en compte
	 */
	public static final int MOUSE_ACTION_WAITING = 300;

	/** Attente entre chaque déplacement */
	public static final int MOUSE_MOVING_WAITING = 20;

	/** Déplacement de souris necessaire pour arreter le deplacement */
	public static final int DISTANCE_FOR_CANCEL = 50;

	/** Minimum possible d'une période aléatoire de déplacement */
	private static final int MIN_PERIOD_PX = 40;

	/** Maximum possible d'une période aléatoire de déplacement */
	private static final int MAX_PERIOD_PX = 70;

	/** Deplacement minimal en px lors du choix aleatoire */
	private static final int MIN_MOVE_PX = 1;

	/** Deplacement maximal en px lors du choix aleatoire */
	private static final int MAX_MOVE_PX = 2;

	/** Ecart minimal en px à la route normale lors du choix aleatoire */
	private static final int MIN_ROUTE_SPACING = 0;

	/** Ecart maximal en px à la route normale lors du choix aleatoire */
	private static final int MAX_ROUTE_SPACING = 8;

	/** Recouvrement en hauteur */
	private int coveringH;

	/** Recouvrement en largeur */
	private int coveringW;

	/** Le rectangle dans lequel le robot deplace l'ecran */
	private Rectangle space;

	/** Le robot de deplacement d'ecran */
	private Robot robot;

	/** Chiffres aléatoires pour déplacement */
	private Random rand;

	/** Temps d'attente optionnel avant deplacement */
	private int sleepTimeBeforeMoving;

	/** Recouvrement relatif des images */
	private float relativeCovering;

	public RobotMapMover() throws MapImportException {

		sleepTimeBeforeMoving = 0;
		relativeCovering = 0.1f;

		rand = new Random();

		try {
			robot = new Robot();
		} catch (AWTException e) {
			Log.error(e);
			throw new MapImportException(MapImportException.ROBOT_INSTATIATION_EXCEPTION);
		}

	}

	/**
	 * Affecter l'espace dans lequel le robot peut déplacer la souris
	 * 
	 * @param rect
	 */
	public void setSpaceRectangle(Rectangle rect) {

		this.space = rect;

		// calcul du recouvrement
		coveringW = Math.round(space.width * relativeCovering);
		coveringH = Math.round(space.height * relativeCovering);

	}

	public void dragToSouth(int nmr) throws MapImportException {

		checkSpace();

		int depx = space.x + space.width / 2;
		int depy = space.y + space.height - coveringH;

		int arrx = depx;
		int arry = space.y + coveringH;

		for (int i = 0; i < nmr; i++) {
			drag(depx, depy, arrx, arry);
		}

	}

	public void dragToWest(int nmr) throws MapImportException {

		checkSpace();

		int depx = space.x + coveringW;
		int depy = space.y + space.height / 2;

		int arrx = space.x + space.width - coveringW;
		int arry = depy;

		for (int i = 0; i < nmr; i++) {
			drag(depx, depy, arrx, arry);
		}
	}

	public void dragToNorth(int nmr) throws MapImportException {

		checkSpace();

		int depx = space.x + space.width / 2;
		int depy = space.y + coveringH;

		int arrx = depx;
		int arry = space.y + space.height - coveringH;

		for (int i = 0; i < nmr; i++) {
			drag(depx, depy, arrx, arry);
		}

	}

	public void dragToEast(int nmr) throws MapImportException {

		checkSpace();

		int depx = space.x + space.width - coveringW;
		int depy = space.y + space.height / 2;

		int arrx = space.x + coveringW;
		int arry = depy;

		for (int i = 0; i < nmr; i++) {
			drag(depx, depy, arrx, arry);
		}

	}

	public void drag(int depx, int depy, int arrx, int arry) throws MapImportException {

		// attente avant debut de deplacement
		Utils.sleep(sleepTimeBeforeMoving);

		// relacher la souris et se placer au point dedépart
		releaseBouton();
		robot.mouseMove(depx, depy);

		// presser le bouton de la souris
		pressBouton();

		// attendre pour etre sur que l'action soit prise en compte
		Utils.sleep(MOUSE_ACTION_WAITING);

		// deplacement de la souris

		// position courante théorique de la souris
		int currx = depx;
		int curry = depy;

		/*
		 * Le deplacement s'effectue par périodes aléatoires ou la vitesse de la
		 * souris est décidée aléatoirement et ou l'écart à la route normale est
		 * aléatoire aussi.
		 */

		// position dans la période
		int periodCur = 0;

		// taille de la période aléatoire
		int periodLenght = getRandomPeriod();

		// vitesse de la souris aléatoire
		int mouseSpeed = getRandomSpeed();

		// ecart à la route aléatoire
		int routeSpacing = getRandomRouteSpacing();

		// boolean firstMovement = true;
		// precision d'arrivee
		int precision = 3;

		while (Utils.approximateEquals(currx, arrx, precision) == false
				&& Utils.approximateEquals(curry, arry, precision) == false) {

			// position courante de la souris
			Point p = MouseInfo.getPointerInfo().getLocation();

			// arreter si l'utilisateur donne un grand coup de souris
			// firstMovement == false &&
			if (Point.distance(p.x, p.y, currx, curry) > DISTANCE_FOR_CANCEL) {
				// relacher le bouton
				releaseBouton();
				throw new MapImportException(MapImportException.ROBOT_IMPORT_MOUSE_CANCELED);
			}

			// la position courante est à plus de 20 pixel de la fin, determiner
			// des valeurs de deplacement aleatoires
			if (periodCur > periodLenght && Point.distance(arrx, arry, currx, curry) > 20) {
				periodLenght = getRandomPeriod();
				mouseSpeed = getRandomSpeed();
				routeSpacing = getRandomRouteSpacing();
				periodCur = 0;
			}

			// la position courante est à moins de 20 pixel de la fin,
			// determiner des valeurs de deplacement standard
			else {
				periodLenght = 0;
				mouseSpeed = 1;
				routeSpacing = 0;
				periodCur = 0;
			}

			// effectuer un mouvement horizontal
			if (arrx != depx) {

				// de haut en bas
				if (arrx > depx) {
					currx += mouseSpeed;
				}

				// de bas en haut
				else if (arrx < depx) {
					currx -= mouseSpeed;
				}

				// s'ecarter de la route "normale"
				curry = arry + routeSpacing;

			}

			// effectuer un mouvement horizontal
			else if (arry != depy) {

				// de gauche à droite
				if (arry > depy) {
					curry += mouseSpeed;
				}

				// de droite à gauche
				else if (arry < depy) {
					curry -= mouseSpeed;
				}

				// s'ecarter de la route "normale"
				currx = arrx + routeSpacing;

			}

			// deplacer la souris
			robot.mouseMove(currx, curry);

			// attendre
			Utils.sleep(MOUSE_MOVING_WAITING);

			// comptabilisation pour périodes
			periodCur++;

		}

		// relacher le bouton
		releaseBouton();

	}

	private void releaseBouton() {

		// arrivée au terme, relacher la souris
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// attendre pour etre sur que l'action soit prise en compte
		Utils.sleep(MOUSE_ACTION_WAITING);

	}

	private void pressBouton() {

		// arrivée au terme, relacher la souris
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// attendre pour etre sur que l'action soit prise en compte
		Utils.sleep(MOUSE_ACTION_WAITING);

	}

	/**
	 * Couverture relative des captures d'écran. En pourcentage.
	 * 
	 * @param relativeCovering
	 */
	public void setRelativeCovering(float relativeCovering) {
		this.relativeCovering = relativeCovering;
	}

	/**
	 * Temps d'arret du thread avant deplacement. En millisecondes.
	 * 
	 * @param sleepTimeBeforeMoving
	 */
	public void setSleepTimeBeforeMoving(int sleepTimeBeforeMoving) {
		this.sleepTimeBeforeMoving = sleepTimeBeforeMoving;
	}

	/**
	 * Envoi une exception si l'espace n'est pas initialisé
	 */
	private void checkSpace() {
		if (space == null) {
			throw new IllegalStateException("Field 'space' is null");
		}
	}

	/**
	 * Determiner une période aléatoire pendant laquelle seront utilisés les
	 * parametres de deplacement.
	 * 
	 * @return
	 */
	private int getRandomPeriod() {
		return rand.nextInt((MAX_PERIOD_PX - MIN_PERIOD_PX) + 1) + MIN_PERIOD_PX;
	}

	/**
	 * Determiner une vitesse aleatoire de deplacement du point de départ au
	 * point d'arrivée.
	 * 
	 * @return
	 */
	private int getRandomSpeed() {
		return rand.nextInt((MAX_MOVE_PX - MIN_MOVE_PX) + 1) + MIN_MOVE_PX;
	}

	/**
	 * Determiner un ecart aleatoire à la route "normale"
	 * 
	 * @return
	 */
	private int getRandomRouteSpacing() {
		return -(MAX_ROUTE_SPACING / 2)
				+ (rand.nextInt((MAX_ROUTE_SPACING - MIN_ROUTE_SPACING) + 1) + MIN_ROUTE_SPACING);
	}
}
