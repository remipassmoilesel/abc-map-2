package org.abcmap.gui.components.geo;

import com.vividsolutions.jts.geom.Coordinate;
import net.miginfocom.swing.MigLayout;
import org.abcmap.core.geo.GeoConstants;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.textfields.DecimalTextField;
import org.abcmap.gui.components.textfields.IntegerTextField;
import org.abcmap.gui.components.textfields.NumberTextField;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Panel which allow to grap coordinates
 */
public class CoordinatesPanel extends JPanel implements HasListenerHandler<ActionListener> {

    /**
     * If set to true, show input exceptions (for debug purposes)
     */
    private boolean showExceptions;
    /**
     * If set to true , show values inserted (for debug purposes)
     */
    private boolean showInsertedValues;
    /**
     * If set to true, enable degrees form
     */
    private boolean degreesFormEnabled;

    // text fields

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

    private ListenerHandler<ActionListener> listenerHandler;

    public CoordinatesPanel() {
        super(new MigLayout("insets 5"));

        // debug vars
        showExceptions = false;
        showInsertedValues = false;

        degreesFormEnabled = true;
        listenerHandler = new ListenerHandler<>();

        GuiUtils.addLabel("Pixels: ", this, "span, wrap");

        // pixel latitude
        txtPixelLat = new DecimalTextField(9);
        txtPixelLat.setName(GeoConstants.PIXEL_LAT.toString());

        // pixel longitude
        txtPixelLng = new DecimalTextField(9);
        txtPixelLng.setName(GeoConstants.PIXEL_LNG.toString());

        addLatitudeLabel();
        add(txtPixelLat, "span, wrap");

        addLongitudeLabel();
        add(txtPixelLng, "span, wrap");

        addSeparator();

        // latitude decimal degrees
        GuiUtils.addLabel("Degrés décimaux: ", this, "span, wrap");

        txtDecimalLat = new DecimalTextField(9);
        txtDecimalLat.setName(GeoConstants.DECIMAL_LAT.toString());

        // longitude decimal degres
        txtDecimalLng = new DecimalTextField(9);
        txtDecimalLng.setName(GeoConstants.DECIMAL_LNG.toString());

        addLatitudeLabel();
        add(txtDecimalLat, "span, wrap");

        addLongitudeLabel();
        add(txtDecimalLng, "span, wrap");

        addSeparator();

        // degrees and decimal minutes latitude
        GuiUtils.addLabel("Degrés et minutes décimales:", this, "span, wrap");

        txtDMDLatDeg = new IntegerTextField(3);
        txtDMDLatDeg.setName(GeoConstants.DMD_LAT_DEG.toString());

        txtDMDLatMin = new DecimalTextField(5);
        txtDMDLatMin.setName(GeoConstants.DMD_LAT_MIN.toString());

        // degrees and decimal minutes longitude
        txtDMDLngDeg = new IntegerTextField(3);
        txtDMDLngDeg.setName(GeoConstants.DMD_LNG_DEG.toString());

        txtDMDLngMin = new DecimalTextField(5);
        txtDMDLngMin.setName(GeoConstants.DMD_LNG_MIN.toString());

        addLatitudeLabel();
        add(txtDMDLatDeg);
        add(txtDMDLatMin, "span, wrap");

        addLongitudeLabel();
        add(txtDMDLngDeg);
        add(txtDMDLngMin, "span, wrap");

        addSeparator();

        GuiUtils.addLabel("Degrés, minutes, secondes:", this, "span, wrap");

        // degrees minutes seconds latitude
        txtDMSLatDeg = new IntegerTextField(3);
        txtDMSLatDeg.setName(GeoConstants.DMS_LAT_DEG.toString());

        txtDMSLatMin = new IntegerTextField(3);
        txtDMSLatMin.setName(GeoConstants.DMS_LAT_MIN.toString());

        txtDMSLatSec = new DecimalTextField(3);
        txtDMSLatSec.setName(GeoConstants.DMS_LAT_SEC.toString());

        // degrees minutes seconds longitude
        txtDMSLngDeg = new IntegerTextField(3);
        txtDMSLngDeg.setName(GeoConstants.DMS_LNG_DEG.toString());

        txtDMSLngMin = new IntegerTextField(3);
        txtDMSLngMin.setName(GeoConstants.DMS_LNG_MIN.toString());

        txtDMSLngSec = new IntegerTextField(3);
        txtDMSLngSec.setName(GeoConstants.DMS_LNG_SEC.toString());

        addLatitudeLabel();
        add(txtDMSLatDeg);
        add(txtDMSLatMin);
        add(txtDMSLatSec, "span, wrap");

        addLongitudeLabel();
        add(txtDMSLngDeg);
        add(txtDMSLngMin);
        add(txtDMSLngSec, "span, wrap");


        allTextFields = new NumberTextField[]{txtPixelLat, txtPixelLng,
                txtDecimalLat, txtDecimalLng, txtDMDLatDeg, txtDMDLatMin,
                txtDMDLngDeg, txtDMDLngMin, txtDMSLatDeg, txtDMSLatMin,
                txtDMSLatSec, txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec,};

        degreesTextFields = new NumberTextField[]{txtDecimalLat,
                txtDecimalLng, txtDMDLatDeg, txtDMDLatMin, txtDMDLngDeg,
                txtDMDLngMin, txtDMSLatDeg, txtDMSLatMin, txtDMSLatSec,
                txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec,};

        this.degreesConversionListener = new DegreesConversionListener();
    }

    /**
     * Enable or disable automatic conversion between fields
     *
     * @param state
     */
    public void setDegreesConversionEnabled(boolean state) {

        if (state) {
            addDegreesListener(degreesConversionListener);
        }

        // disable
        else {
            removeDegreesListener(degreesConversionListener);
        }

    }

    /**
     * Convert coordinates
     */
    private class DegreesConversionListener extends KeyAdapter {

        private Coordinate coords = new Coordinate();

        @Override
        public void keyReleased(KeyEvent e) {

            /*
            NumberTextField src = (NumberTextField) e.getSource();

            if (isDegreesField(src)) {
                try {
                    coords.setDegreeValue(txtDecimalLat.getDoubleValue(),
                            txtDecimalLng.getDoubleValue());
                } catch (InvalidInputException e1) {
                    if (showExceptions)
                        Log.debug(e1);
                }
            }

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


            try {
                coords.setPixelValue(txtPixelLat.getDoubleValue(),
                        txtPixelLng.getDoubleValue());
            } catch (InvalidInputException e1) {
                if (showExceptions)
                    Log.debug(e1);
            }


            setCoordinates(coords);
            */
        }

    }

    /**
     * Set field values
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
        /*

        Point2D dg = coords.getRoundedDegreesPoint();

        double[] cdmd = coords.getDegreesMinutesCoords();

        double[] cdms = coords.getDMSCoords();

        Double[] values = new Double[]{
                //
                coords.latitudePx, coords.longitudePx,
                //
                dg.getY(), dg.getX(),
                //
                cdmd[0], cdmd[1], cdmd[2], cdmd[3],
                //
                cdms[0], cdms[1], cdms[2],
                //
                cdms[3], cdms[4], cdms[5],};

        NumberTextField[] fields = new NumberTextField[]{
                //
                txtPixelLat, txtPixelLng,
                //
                txtDecimalLat, txtDecimalLng,
                //
                txtDMDLatDeg, txtDMDLatMin, txtDMDLngDeg, txtDMDLngMin,
                //
                txtDMSLatDeg, txtDMSLatMin, txtDMSLatSec,
                //
                txtDMSLngDeg, txtDMSLngMin, txtDMSLngSec,};

        for (int i = 0; i < fields.length; i++) {

            JTextComponent f = (JTextComponent) fields[i];

            Double v = values[i];


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
        */
        fireCoordinatesChanged();

    }

    /**
     * Get current values
     *
     * @return
     * @throws InvalidInputException
     */
    public Coordinate getCoordinates() throws InvalidInputException {
        return new Coordinate(txtDecimalLat.getDoubleValue(), txtDecimalLng.getDoubleValue());
    }

    private void fireCoordinatesChanged() {
        listenerHandler.fireEvent(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, ""));
    }

    /**
     * Get all values in an associative map
     *
     * @return
     * @throws InvalidInputException
     */
    public HashMap<GeoConstants, Double> getRawValues() throws InvalidInputException {
        HashMap<GeoConstants, Double> vals = new HashMap<>();
        for (NumberTextField dtf : allTextFields) {
            GeoConstants name = GeoConstants.valueOf(((JTextComponent) dtf).getName());
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
     * Enable / disable degrees form
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

        Misc utilities for GUI building

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
