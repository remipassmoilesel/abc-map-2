//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.02.01 à 10:57:08 AM CET 
//


package org.abcmap.core.importation.gpx.schema.gpxv1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;


/**
 * 
 * 	 Two lat/lon pairs defining the extent of an element.
 *     
 * 
 * <p>Classe Java pour boundsType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="boundsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="minlat" use="required" type="{http://www.topografix.com/GPX/1/1}latitudeType" />
 *       &lt;attribute name="minlon" use="required" type="{http://www.topografix.com/GPX/1/1}longitudeType" />
 *       &lt;attribute name="maxlat" use="required" type="{http://www.topografix.com/GPX/1/1}latitudeType" />
 *       &lt;attribute name="maxlon" use="required" type="{http://www.topografix.com/GPX/1/1}longitudeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "boundsType")
public class BoundsType {

    @XmlAttribute(name = "minlat", required = true)
    protected BigDecimal minlat;
    @XmlAttribute(name = "minlon", required = true)
    protected BigDecimal minlon;
    @XmlAttribute(name = "maxlat", required = true)
    protected BigDecimal maxlat;
    @XmlAttribute(name = "maxlon", required = true)
    protected BigDecimal maxlon;

    /**
     * Obtient la valeur de la propriété minlat.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMinlat() {
        return minlat;
    }

    /**
     * Définit la valeur de la propriété minlat.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMinlat(BigDecimal value) {
        this.minlat = value;
    }

    /**
     * Obtient la valeur de la propriété minlon.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMinlon() {
        return minlon;
    }

    /**
     * Définit la valeur de la propriété minlon.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMinlon(BigDecimal value) {
        this.minlon = value;
    }

    /**
     * Obtient la valeur de la propriété maxlat.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMaxlat() {
        return maxlat;
    }

    /**
     * Définit la valeur de la propriété maxlat.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMaxlat(BigDecimal value) {
        this.maxlat = value;
    }

    /**
     * Obtient la valeur de la propriété maxlon.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMaxlon() {
        return maxlon;
    }

    /**
     * Définit la valeur de la propriété maxlon.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMaxlon(BigDecimal value) {
        this.maxlon = value;
    }

}
