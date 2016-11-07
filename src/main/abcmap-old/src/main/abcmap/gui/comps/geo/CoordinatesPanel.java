package abcmap.gui.comps.geo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;
import abcmap.exceptions.InvalidInputException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.gui.comps.textfields.DecimalTextField;
import abcmap.gui.comps.textfields.IntegerTextField;
import abcmap.gui.comps.textfields.NumberTextField;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;

/**
 * Panneau de saisie de coordonnées. Possibilité de les saisir en pixel ou en
 * degres.
 * 
 * @author remipassmoilesel
 *
 */
public class CoordinatesPanel extends JPanel implements
		HasListenerHandler<ActionListener> {

	private DecimalTextField txtPixelLat;
	private DecimalTextField txtPixelLng;
	private DecimalTextField txtDecimalLat;
	private DecimalTextField txtDecimalLng;
	private IntegerTextField txtDMDLatDeg;
	private DecimalTextField txtDMDLatMin;
	private IntegerTextField txtDMDLngDeg;
	private DecimalTextField txtDMDLngMin;
	private IntegerTextField txtDMSLatDeg;
	private IntegerTextField txtDMSLatMin;
	private DecimalTextField txtDMSLatSec;
	private IntegerTextField txtDMSLngDeg;
	private IntegerTextField txtDMSLngMin;
	private IntegerTextField txtDMSLngSec;
	private NumberTextField[] allTextFields;
	private DegreesConversionListener degreesConversionListener;
	private NumberTextField[] degreesTextFields;
	private boolean showExceptions;
	private boolean showInsertedValues;
	private boolean degreesFormEnabled;
	private ListenerHandler<ActionListener> listenerHandler;

	public CoordinatesPanel() {
		super(new MigLayout("insets 5"));

		// autoriser la saisie dans les champs de coordonnées en degrés
		degreesFormEnabled = true;

		// debogage
		showExceptions = false;
		showInsertedValues = false;

		// gestionnaire d'action
		listenerHandler = new ListenerHandler<ActionListener>();

		// Coordonnées en pixels
		GuiUtils.addLabel("Pixels: ", this, "span, wrap");

		// latitude en pixels
		txtPixelLat = new DecimalTextField(9);
		txtPixelLat.setName(GeoConstants.PIXEL_LAT.toString());

		// longitude en pixels
		txtPixelLng = new DecimalTextField(9);
		txtPixelLng.setName(GeoConstants.PIXEL_LNG.toString());

		// ajouter au panneau + separateur
		addLatitudeLabel();
		add(txtPixelLat, "span, wrap");

		addLongitudeLabel();
		add(txtPixelLng, "span, wrap");

		addSeparator();

		// Coordonnées en degrés décimaux
		GuiUtils.addLabel("Degrés décimaux: ", this, "span, wrap");

		// latitude en degres decimaux
		txtDecimalLat = new DecimalTextField(9);
		txtDecimalLat.setName(GeoConstants.DECIMAL_LAT.toString());

		// longitude en degres decimaux
		txtDecimalLng = new DecimalTextField(9);
		txtDecimalLng.setName(GeoConstants.DECIMAL_LNG.toString());

		// ajouter au panneau + separateur
		addLatitudeLabel();
		add(txtDecimalLat, "span, wrap");

		addLongitudeLabel();
		add(txtDecimalLng, "span, wrap");

		addSeparator();

		// Coordonnées en degrés et minutes décimales
		GuiUtils.addLabel("Degrés et minutes décimales:", this, "span, wrap");

		// latitude en degres
		txtDMDLatDeg = new IntegerTextField(3);
		txtDMDLatDeg.setName(GeoConstants.DMD_LAT_DEG.toString());

		// latitude en minutes decimales
		txtDMDLatMin = new DecimalTextField(5);
		txtDMDLatMin.setName(GeoConstants.DMD_LAT_MIN.toString());

		// longitude en degres
		txtDMDLngDeg = new IntegerTextField(3);
		txtDMDLngDeg.setName(GeoConstants.DMD_LNG_DEG.toString());

		// longitude en minutes decimales
		txtDMDLngMin = new DecimalTextField(5);
		txtDMDLngMin.setName(GeoConstants.DMD_LNG_MIN.toString());

		// ajout au panneau + separateur
		addLatitudeLabel();
		add(txtDMDLatDeg);
		add(txtDMDLatMin, "span, wrap");

		addLongitudeLabel();
		add(txtDMDLngDeg);
		add(txtDMDLngMin, "span, wrap");

		addSeparator();

		// Coordonnées en degrés, minutes et secondes
		GuiUtils.addLabel("Degrés, minutes, secondes:", this, "span, wrap");

		// Latitude en degrés
		txtDMSLatDeg = new IntegerTextField(3);
		txtDMSLatDeg.setName(GeoConstants.DMS_LAT_DEG.toString());

		// Latitude en minutes
		txtDMSLatMin = new IntegerTextField(3);
		txtDMSLatMin.setName(GeoConstants.DMS_LAT_MIN.toString());

		// Latitude en secondes decimales
		txtDMSLatSec = new DecimalTextField(3);
		txtDMSLatSec.setName(GeoConstants.DMS_LAT_SEC.toString());

		// Longitude en degrés
		txtDMSLngDeg = new IntegerTextField(3);
		txtDMSLngDeg.setName(GeoConstants.DMS_LNG_DEG.toString());

		// Longitude en minutes
		txtDMSLngMin = new IntegerTextField(3);
		txtDMSLngMin.setName(GeoConstants.DMS_LNG_MIN.toString());

		// Longitude en secondes decimales
		txtDMSLngSec = new IntegerTextField(3);
		txtDMSLngSec.setName(GeoConstants.DMS_LNG_SEC.toString());

		// ajout au panneau
		addLatitudeLabel();
		add(txtDMSLatDeg);
		add(txtDMSLatMin);
		add(txtDMSLatSec, "span, wrap");

		addLongitudeLabel();
		add(txtDMSLngDeg);
		add(txtDMSLngMin);
		add(txtDMSLngSec, "span, wrap");

		// tableaux de champs de texte
		allTextFields = new NumberTextField[] { txtPixelLat, txtPixelLng,
				txtDecimalLat, txtDecimalLng, txtDMDLatDeg, txtDMDLatMin,
				txtDMDLngDeg, txtDMDLngMin, txtDMSLatDeg, txtDMSLatMin,
				txtDMSLatSec, txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec, };

		degreesTextFields = new NumberTextField[] { txtDecimalLat,
				txtDecimalLng, txtDMDLatDeg, txtDMDLatMin, txtDMDLngDeg,
				txtDMDLngMin, txtDMSLatDeg, txtDMSLatMin, txtDMSLatSec,
				txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec, };

		// objet de conversion de coordonnées en degres
		this.degreesConversionListener = new DegreesConversionListener();
	}

	/**
	 * Activer la conversion automatique des champs en degrés. Si l'utilisateur
	 * saisie des coordonnées dans un des champs en degrés, les coordonnées
	 * seront converties dans les autres champs en degrés.
	 * 
	 * @param state
	 */
	public void setDegreesConversionEnabled(boolean state) {

		// activer la conversion
		if (state) {
			addDegreesListener(degreesConversionListener);
		}

		// desactiver la conversion
		else {
			removeDegreesListener(degreesConversionListener);
		}

	}

	/**
	 * Converti la saisie utilisateur d'un format aux autres (de degrés décimaux
	 * vers dms etc.. par exemple)
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class DegreesConversionListener extends KeyListenerUtil {

		// objet permettant la conversion des valeurs
		private Coordinate coords = new Coordinate();

		@Override
		public void keyReleased(KeyEvent e) {

			NumberTextField src = (NumberTextField) e.getSource();

			// tenter d'attribuer des valeurs à un objet de coordonnées pour
			// conversion si erreur, la saisie est invalide

			// les valeurs saisies sont en degrés
			if (isDegreesField(src)) {
				try {
					coords.setDegreeValue(txtDecimalLat.getDoubleValue(),
							txtDecimalLng.getDoubleValue());
				} catch (InvalidInputException e1) {
					if (showExceptions)
						Log.debug(e1);
				}
			}

			// les valeurs ont été saisies en degrés et minutes decimales
			else if (isDMDField(src)) {
				try {
					coords.setDegreeMinuteValue(txtDMDLatDeg.getDoubleValue(),
							txtDMDLatMin.getDoubleValue(),
							txtDMDLngDeg.getDoubleValue(),
							txtDMDLngDeg.getDoubleValue());
				} catch (InvalidInputException e1) {
					if (showExceptions)
						Log.debug(e1);
				}
			}

			// les valeurs ont été saisies en degrés, minutes et secondes
			// decimales
			else if (isDMSField(src)) {
				try {
					coords.setDMSValue(txtDMSLatDeg.getDoubleValue(),
							txtDMSLatMin.getDoubleValue(),
							txtDMSLatSec.getDoubleValue(),
							txtDMSLngDeg.getDoubleValue(),
							txtDMSLngMin.getDoubleValue(),
							txtDMSLngSec.getDoubleValue());
				} catch (InvalidInputException e1) {
					if (showExceptions)
						Log.debug(e1);
				}
			}

			// affecter les valeurs en pixels
			try {
				coords.setPixelValue(txtPixelLat.getDoubleValue(),
						txtPixelLng.getDoubleValue());
			} catch (InvalidInputException e1) {
				if (showExceptions)
					Log.debug(e1);
			}

			// affecter les nouvelles coordonnées au panneau
			setCoordinates(coords);
		}

	}

	/**
	 * Affecter les valeurs de l'objet coordonnées passé en parametre aux champs
	 * de texte.
	 * 
	 * @param coords
	 */
	public void setCoordinates(Coordinate coords) {

		if (coords == null) {
			throw new NullPointerException("Coordinates cannot be null");
		}

		if (showInsertedValues) {
			System.out.println(coords);
		}

		// recuperer les coordonnees en degres decimaux
		Point2D dg = coords.getRoundedDegreesPoint();

		// recuperer les coordonnees en degres et minutes decimales
		double[] cdmd = coords.getDegreesMinutesCoords();

		// recuperer les coordonnees en degres, minutes et secondes
		double[] cdms = coords.getDMSCoords();

		Double[] values = new Double[] {
				//
				coords.latitudePx, coords.longitudePx,
				//
				dg.getY(), dg.getX(),
				//
				cdmd[0], cdmd[1], cdmd[2], cdmd[3],
				//
				cdms[0], cdms[1], cdms[2],
				//
				cdms[3], cdms[4], cdms[5], };

		NumberTextField[] fields = new NumberTextField[] {
				//
				txtPixelLat, txtPixelLng,
				//
				txtDecimalLat, txtDecimalLng,
				//
				txtDMDLatDeg, txtDMDLatMin, txtDMDLngDeg, txtDMDLngMin,
				//
				txtDMSLatDeg, txtDMSLatMin, txtDMSLatSec,
				//
				txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec, };

		for (int i = 0; i < fields.length; i++) {

			// champs a modifier eventuellement
			JTextComponent f = (JTextComponent) fields[i];

			// valeur à inserer eventuellement
			Double v = values[i];

			// verifier si l'on doit changer les valeurs
			boolean changeValue = true;
			try {
				// comparaison des valeurs au format double
				// si les valeurs sont égales, ne pas changer
				if (((NumberTextField) f).getDoubleValue().equals(
						v.doubleValue())) {
					changeValue = false;
				}
			} catch (InvalidInputException e) {
				if (showExceptions) {
					Log.debug(e);
				}
			}

			// modifier la valeur uniquement si necessaire
			// et uniquement si pas de focus dessus
			if (changeValue == true && f.isFocusOwner() == false) {

				String strValue;
				// le champs cible n'accepte que les entiers
				if (f instanceof IntegerTextField) {
					strValue = String.valueOf(v.intValue());
				}
				// le champs cible accepte les decimales
				else {
					strValue = Double.toString(v);
				}

				GuiUtils.changeText(f, strValue);
			}

		}

		fireCoordinatesChanged();

	}

	/**
	 * Retourne les valeurs saisies sous forme de coordonnées. Les coordonnées
	 * sont calculées à partir des saisies des champs en degrés décimaux.
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	public Coordinate getCoordinates() throws InvalidInputException {
		Coordinate coord = new Coordinate(txtDecimalLat.getDoubleValue(),
				txtDecimalLng.getDoubleValue(), 0, 0);

		return coord;
	}

	private void fireCoordinatesChanged() {
		listenerHandler.fireEvent(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, ""));
	}

	/**
	 * Retourne les valeurs brutes sous forme de tableau associatif
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	public HashMap<GeoConstants, Double> getRawValues()
			throws InvalidInputException {
		HashMap<GeoConstants, Double> vals = new HashMap<GeoConstants, Double>();
		for (NumberTextField dtf : allTextFields) {
			GeoConstants name = GeoConstants.valueOf(((JTextComponent) dtf)
					.getName());
			vals.put(name, dtf.getDoubleValue());
		}
		return vals;
	}

	public void addDocumentListener(DocumentListener listener) {
		for (NumberTextField dtf : allTextFields) {
			((JTextComponent) dtf).getDocument().addDocumentListener(listener);
		}
	}

	public void removeDocumentListener(DocumentListener listener) {
		for (NumberTextField dtf : allTextFields) {
			((JTextComponent) dtf).getDocument().removeDocumentListener(
					listener);
		}
	}

	public void addDegreesListener(KeyListener listener) {
		for (NumberTextField dtf : degreesTextFields) {
			((JTextComponent) dtf).addKeyListener(listener);
		}
	}

	public void removeDegreesListener(KeyListener listener) {
		for (NumberTextField dtf : degreesTextFields) {
			((JTextComponent) dtf).removeKeyListener(listener);
		}
	}

	public NumberTextField[] getAllTextFields() {
		return allTextFields;
	}

	/**
	 * Autoriser ou non la saisie dans les champs en degrés.
	 * 
	 * @param state
	 */
	public void setDegreesFormEnabled(boolean state) {

		this.degreesFormEnabled = state;

		for (NumberTextField ntf : degreesTextFields) {
			((JTextComponent) ntf).setEnabled(false);
		}

	}

	public boolean isDegreesFormEnabled() {
		return degreesFormEnabled;
	}

	/*
	 * Utilitaires pour construction du GUI
	 */

	private void addLatitudeLabel() {
		GuiUtils.addLabel("<html>Latitude: </html>", this);
	}

	private void addLongitudeLabel() {
		GuiUtils.addLabel("<html>Longitude: </html>", this);
	}

	private void addSeparator() {
		add(new JPanel(), "wrap");
	}

	/*
	 * Utilitaires pour manipulations
	 */

	private boolean isDegreesField(NumberTextField src) {
		return txtDecimalLat.equals(src) || txtDecimalLng.equals(src);
	}

	private boolean isDMDField(NumberTextField dtf) {
		return txtDMDLatDeg.equals(dtf) || txtDMDLatMin.equals(dtf)
				|| txtDMDLngDeg.equals(dtf) || txtDMDLngMin.equals(dtf);

	}

	private boolean isDMSField(NumberTextField dtf) {
		return txtDMSLatDeg.equals(dtf) || txtDMSLatMin.equals(dtf)
				|| txtDMSLatSec.equals(dtf) || txtDMSLngDeg.equals(dtf)
				|| txtDMSLngMin.equals(dtf) || txtDMSLngSec.equals(dtf);
	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}
}
