//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2017.02.01 à 10:57:08 AM CET 
//


package org.abcmap.core.importation.gpx.schema.gpxv1;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.topografix.com/GPX/1/0}emailType" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="keywords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bounds" type="{http://www.topografix.com/GPX/1/0}boundsType" minOccurs="0"/>
 *         &lt;element name="wpt" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                   &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
 *                   &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
 *                   &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *                   &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
 *                   &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
 *                 &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="rte" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *                   &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="rtept" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                             &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
 *                             &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                             &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
 *                             &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *                             &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
 *                             &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
 *                           &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="trk" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                   &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *                   &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="trkseg" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="trkpt" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                                       &lt;element name="course" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
 *                                       &lt;element name="speed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
 *                                       &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *                                       &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
 *                                       &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *                                       &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
 *                                       &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
 *                                     &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0" />
 *       &lt;attribute name="creator" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "desc",
    "author",
    "email",
    "url",
    "urlname",
    "time",
    "keywords",
    "bounds",
    "wpt",
    "rte",
    "trk",
    "any"
})
@XmlRootElement(name = "gpx")
public class Gpx {

    protected String name;
    protected String desc;
    protected String author;
    protected String email;
    @XmlSchemaType(name = "anyURI")
    protected String url;
    protected String urlname;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;
    protected String keywords;
    protected BoundsType bounds;
    protected List<Wpt> wpt;
    protected List<Rte> rte;
    protected List<Trk> trk;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "creator", required = true)
    protected String creator;

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété desc.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Définit la valeur de la propriété desc.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Obtient la valeur de la propriété author.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété email.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit la valeur de la propriété email.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Obtient la valeur de la propriété url.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUrl() {
        return url;
    }

    /**
     * Définit la valeur de la propriété url.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Obtient la valeur de la propriété urlname.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUrlname() {
        return urlname;
    }

    /**
     * Définit la valeur de la propriété urlname.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUrlname(String value) {
        this.urlname = value;
    }

    /**
     * Obtient la valeur de la propriété time.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Définit la valeur de la propriété time.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Obtient la valeur de la propriété keywords.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Définit la valeur de la propriété keywords.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeywords(String value) {
        this.keywords = value;
    }

    /**
     * Obtient la valeur de la propriété bounds.
     *
     * @return
     *     possible object is
     *     {@link BoundsType }
     *
     */
    public BoundsType getBounds() {
        return bounds;
    }

    /**
     * Définit la valeur de la propriété bounds.
     *
     * @param value
     *     allowed object is
     *     {@link BoundsType }
     *
     */
    public void setBounds(BoundsType value) {
        this.bounds = value;
    }

    /**
     * Gets the value of the wpt property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wpt property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWpt().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gpx.Wpt }
     *
     *
     */
    public List<Wpt> getWpt() {
        if (wpt == null) {
            wpt = new ArrayList<Wpt>();
        }
        return this.wpt;
    }

    /**
     * Gets the value of the rte property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rte property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRte().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gpx.Rte }
     *
     *
     */
    public List<Rte> getRte() {
        if (rte == null) {
            rte = new ArrayList<Rte>();
        }
        return this.rte;
    }

    /**
     * Gets the value of the trk property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trk property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrk().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gpx.Trk }
     *
     *
     */
    public List<Trk> getTrk() {
        if (trk == null) {
            trk = new ArrayList<Trk>();
        }
        return this.trk;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        if (version == null) {
            return "1.0";
        } else {
            return version;
        }
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété creator.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Définit la valeur de la propriété creator.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreator(String value) {
        this.creator = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
     *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="rtept" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *                   &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
     *                   &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *                   &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
     *                   &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
     *                   &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
     *                   &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
     *                 &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "cmt",
        "desc",
        "src",
        "url",
        "urlname",
        "number",
        "any",
        "rtept"
    })
    public static class Rte {

        protected String name;
        protected String cmt;
        protected String desc;
        protected String src;
        @XmlSchemaType(name = "anyURI")
        protected String url;
        protected String urlname;
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger number;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        protected List<Rtept> rtept;

        /**
         * Obtient la valeur de la propriété name.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getName() {
            return name;
        }

        /**
         * Définit la valeur de la propriété name.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Obtient la valeur de la propriété cmt.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCmt() {
            return cmt;
        }

        /**
         * Définit la valeur de la propriété cmt.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCmt(String value) {
            this.cmt = value;
        }

        /**
         * Obtient la valeur de la propriété desc.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Définit la valeur de la propriété desc.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDesc(String value) {
            this.desc = value;
        }

        /**
         * Obtient la valeur de la propriété src.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getSrc() {
            return src;
        }

        /**
         * Définit la valeur de la propriété src.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setSrc(String value) {
            this.src = value;
        }

        /**
         * Obtient la valeur de la propriété url.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUrl() {
            return url;
        }

        /**
         * Définit la valeur de la propriété url.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Obtient la valeur de la propriété urlname.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUrlname() {
            return urlname;
        }

        /**
         * Définit la valeur de la propriété urlname.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUrlname(String value) {
            this.urlname = value;
        }

        /**
         * Obtient la valeur de la propriété number.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getNumber() {
            return number;
        }

        /**
         * Définit la valeur de la propriété number.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setNumber(BigInteger value) {
            this.number = value;
        }

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         *
         *
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

        /**
         * Gets the value of the rtept property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the rtept property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRtept().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Gpx.Rte.Rtept }
         *
         *
         */
        public List<Rtept> getRtept() {
            if (rtept == null) {
                rtept = new ArrayList<Rtept>();
            }
            return this.rtept;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
         *         &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
         *         &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
         *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
         *         &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
         *         &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
         *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
         *       &lt;/sequence>
         *       &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
         *       &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ele",
            "time",
            "magvar",
            "geoidheight",
            "name",
            "cmt",
            "desc",
            "src",
            "url",
            "urlname",
            "sym",
            "type",
            "fix",
            "sat",
            "hdop",
            "vdop",
            "pdop",
            "ageofdgpsdata",
            "dgpsid",
            "any"
        })
        public static class Rtept {

            protected BigDecimal ele;
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar time;
            protected BigDecimal magvar;
            protected BigDecimal geoidheight;
            protected String name;
            protected String cmt;
            protected String desc;
            protected String src;
            @XmlSchemaType(name = "anyURI")
            protected String url;
            protected String urlname;
            protected String sym;
            protected String type;
            protected String fix;
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger sat;
            protected BigDecimal hdop;
            protected BigDecimal vdop;
            protected BigDecimal pdop;
            protected BigDecimal ageofdgpsdata;
            @XmlSchemaType(name = "integer")
            protected Integer dgpsid;
            @XmlAnyElement(lax = true)
            protected List<Object> any;
            @XmlAttribute(name = "lat", required = true)
            protected BigDecimal lat;
            @XmlAttribute(name = "lon", required = true)
            protected BigDecimal lon;

            /**
             * Obtient la valeur de la propriété ele.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getEle() {
                return ele;
            }

            /**
             * Définit la valeur de la propriété ele.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setEle(BigDecimal value) {
                this.ele = value;
            }

            /**
             * Obtient la valeur de la propriété time.
             *
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public XMLGregorianCalendar getTime() {
                return time;
            }

            /**
             * Définit la valeur de la propriété time.
             *
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public void setTime(XMLGregorianCalendar value) {
                this.time = value;
            }

            /**
             * Obtient la valeur de la propriété magvar.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getMagvar() {
                return magvar;
            }

            /**
             * Définit la valeur de la propriété magvar.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setMagvar(BigDecimal value) {
                this.magvar = value;
            }

            /**
             * Obtient la valeur de la propriété geoidheight.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getGeoidheight() {
                return geoidheight;
            }

            /**
             * Définit la valeur de la propriété geoidheight.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setGeoidheight(BigDecimal value) {
                this.geoidheight = value;
            }

            /**
             * Obtient la valeur de la propriété name.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getName() {
                return name;
            }

            /**
             * Définit la valeur de la propriété name.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Obtient la valeur de la propriété cmt.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getCmt() {
                return cmt;
            }

            /**
             * Définit la valeur de la propriété cmt.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setCmt(String value) {
                this.cmt = value;
            }

            /**
             * Obtient la valeur de la propriété desc.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getDesc() {
                return desc;
            }

            /**
             * Définit la valeur de la propriété desc.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setDesc(String value) {
                this.desc = value;
            }

            /**
             * Obtient la valeur de la propriété src.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getSrc() {
                return src;
            }

            /**
             * Définit la valeur de la propriété src.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setSrc(String value) {
                this.src = value;
            }

            /**
             * Obtient la valeur de la propriété url.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getUrl() {
                return url;
            }

            /**
             * Définit la valeur de la propriété url.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setUrl(String value) {
                this.url = value;
            }

            /**
             * Obtient la valeur de la propriété urlname.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getUrlname() {
                return urlname;
            }

            /**
             * Définit la valeur de la propriété urlname.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setUrlname(String value) {
                this.urlname = value;
            }

            /**
             * Obtient la valeur de la propriété sym.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getSym() {
                return sym;
            }

            /**
             * Définit la valeur de la propriété sym.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setSym(String value) {
                this.sym = value;
            }

            /**
             * Obtient la valeur de la propriété type.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getType() {
                return type;
            }

            /**
             * Définit la valeur de la propriété type.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Obtient la valeur de la propriété fix.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getFix() {
                return fix;
            }

            /**
             * Définit la valeur de la propriété fix.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setFix(String value) {
                this.fix = value;
            }

            /**
             * Obtient la valeur de la propriété sat.
             *
             * @return
             *     possible object is
             *     {@link BigInteger }
             *
             */
            public BigInteger getSat() {
                return sat;
            }

            /**
             * Définit la valeur de la propriété sat.
             *
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *
             */
            public void setSat(BigInteger value) {
                this.sat = value;
            }

            /**
             * Obtient la valeur de la propriété hdop.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getHdop() {
                return hdop;
            }

            /**
             * Définit la valeur de la propriété hdop.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setHdop(BigDecimal value) {
                this.hdop = value;
            }

            /**
             * Obtient la valeur de la propriété vdop.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getVdop() {
                return vdop;
            }

            /**
             * Définit la valeur de la propriété vdop.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setVdop(BigDecimal value) {
                this.vdop = value;
            }

            /**
             * Obtient la valeur de la propriété pdop.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getPdop() {
                return pdop;
            }

            /**
             * Définit la valeur de la propriété pdop.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setPdop(BigDecimal value) {
                this.pdop = value;
            }

            /**
             * Obtient la valeur de la propriété ageofdgpsdata.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getAgeofdgpsdata() {
                return ageofdgpsdata;
            }

            /**
             * Définit la valeur de la propriété ageofdgpsdata.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setAgeofdgpsdata(BigDecimal value) {
                this.ageofdgpsdata = value;
            }

            /**
             * Obtient la valeur de la propriété dgpsid.
             *
             * @return
             *     possible object is
             *     {@link Integer }
             *
             */
            public Integer getDgpsid() {
                return dgpsid;
            }

            /**
             * Définit la valeur de la propriété dgpsid.
             *
             * @param value
             *     allowed object is
             *     {@link Integer }
             *
             */
            public void setDgpsid(Integer value) {
                this.dgpsid = value;
            }

            /**
             * Gets the value of the any property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the any property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAny().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Object }
             *
             *
             */
            public List<Object> getAny() {
                if (any == null) {
                    any = new ArrayList<Object>();
                }
                return this.any;
            }

            /**
             * Obtient la valeur de la propriété lat.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getLat() {
                return lat;
            }

            /**
             * Définit la valeur de la propriété lat.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setLat(BigDecimal value) {
                this.lat = value;
            }

            /**
             * Obtient la valeur de la propriété lon.
             *
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *
             */
            public BigDecimal getLon() {
                return lon;
            }

            /**
             * Définit la valeur de la propriété lon.
             *
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *
             */
            public void setLon(BigDecimal value) {
                this.lon = value;
            }

        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
     *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="trkseg" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="trkpt" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *                             &lt;element name="course" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
     *                             &lt;element name="speed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
     *                             &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *                             &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
     *                             &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
     *                             &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
     *                             &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *                           &lt;/sequence>
     *                           &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
     *                           &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "cmt",
        "desc",
        "src",
        "url",
        "urlname",
        "number",
        "any",
        "trkseg"
    })
    public static class Trk {

        protected String name;
        protected String cmt;
        protected String desc;
        protected String src;
        @XmlSchemaType(name = "anyURI")
        protected String url;
        protected String urlname;
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger number;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        protected List<Trkseg> trkseg;

        /**
         * Obtient la valeur de la propriété name.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getName() {
            return name;
        }

        /**
         * Définit la valeur de la propriété name.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Obtient la valeur de la propriété cmt.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCmt() {
            return cmt;
        }

        /**
         * Définit la valeur de la propriété cmt.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCmt(String value) {
            this.cmt = value;
        }

        /**
         * Obtient la valeur de la propriété desc.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Définit la valeur de la propriété desc.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDesc(String value) {
            this.desc = value;
        }

        /**
         * Obtient la valeur de la propriété src.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getSrc() {
            return src;
        }

        /**
         * Définit la valeur de la propriété src.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setSrc(String value) {
            this.src = value;
        }

        /**
         * Obtient la valeur de la propriété url.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUrl() {
            return url;
        }

        /**
         * Définit la valeur de la propriété url.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Obtient la valeur de la propriété urlname.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUrlname() {
            return urlname;
        }

        /**
         * Définit la valeur de la propriété urlname.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUrlname(String value) {
            this.urlname = value;
        }

        /**
         * Obtient la valeur de la propriété number.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getNumber() {
            return number;
        }

        /**
         * Définit la valeur de la propriété number.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setNumber(BigInteger value) {
            this.number = value;
        }

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         *
         *
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

        /**
         * Gets the value of the trkseg property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the trkseg property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTrkseg().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Gpx.Trk.Trkseg }
         *
         *
         */
        public List<Trkseg> getTrkseg() {
            if (trkseg == null) {
                trkseg = new ArrayList<Trkseg>();
            }
            return this.trkseg;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="trkpt" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
         *                   &lt;element name="course" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
         *                   &lt;element name="speed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
         *                   &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
         *                   &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
         *                   &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
         *                   &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
         *                   &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
         *                 &lt;/sequence>
         *                 &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
         *                 &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "trkpt"
        })
        public static class Trkseg {

            protected List<Trkpt> trkpt;

            /**
             * Gets the value of the trkpt property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the trkpt property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getTrkpt().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Gpx.Trk.Trkseg.Trkpt }
             *
             *
             */
            public List<Trkpt> getTrkpt() {
                if (trkpt == null) {
                    trkpt = new ArrayList<Trkpt>();
                }
                return this.trkpt;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
             *         &lt;element name="course" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
             *         &lt;element name="speed" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
             *         &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
             *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
             *         &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
             *         &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
             *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
             *       &lt;/sequence>
             *       &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
             *       &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "ele",
                "time",
                "course",
                "speed",
                "magvar",
                "geoidheight",
                "name",
                "cmt",
                "desc",
                "src",
                "url",
                "urlname",
                "sym",
                "type",
                "fix",
                "sat",
                "hdop",
                "vdop",
                "pdop",
                "ageofdgpsdata",
                "dgpsid",
                "any"
            })
            public static class Trkpt {

                protected BigDecimal ele;
                @XmlSchemaType(name = "dateTime")
                protected XMLGregorianCalendar time;
                protected BigDecimal course;
                protected BigDecimal speed;
                protected BigDecimal magvar;
                protected BigDecimal geoidheight;
                protected String name;
                protected String cmt;
                protected String desc;
                protected String src;
                @XmlSchemaType(name = "anyURI")
                protected String url;
                protected String urlname;
                protected String sym;
                protected String type;
                protected String fix;
                @XmlSchemaType(name = "nonNegativeInteger")
                protected BigInteger sat;
                protected BigDecimal hdop;
                protected BigDecimal vdop;
                protected BigDecimal pdop;
                protected BigDecimal ageofdgpsdata;
                @XmlSchemaType(name = "integer")
                protected Integer dgpsid;
                @XmlAnyElement(lax = true)
                protected List<Object> any;
                @XmlAttribute(name = "lat", required = true)
                protected BigDecimal lat;
                @XmlAttribute(name = "lon", required = true)
                protected BigDecimal lon;

                /**
                 * Obtient la valeur de la propriété ele.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getEle() {
                    return ele;
                }

                /**
                 * Définit la valeur de la propriété ele.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setEle(BigDecimal value) {
                    this.ele = value;
                }

                /**
                 * Obtient la valeur de la propriété time.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getTime() {
                    return time;
                }

                /**
                 * Définit la valeur de la propriété time.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setTime(XMLGregorianCalendar value) {
                    this.time = value;
                }

                /**
                 * Obtient la valeur de la propriété course.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getCourse() {
                    return course;
                }

                /**
                 * Définit la valeur de la propriété course.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setCourse(BigDecimal value) {
                    this.course = value;
                }

                /**
                 * Obtient la valeur de la propriété speed.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getSpeed() {
                    return speed;
                }

                /**
                 * Définit la valeur de la propriété speed.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setSpeed(BigDecimal value) {
                    this.speed = value;
                }

                /**
                 * Obtient la valeur de la propriété magvar.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getMagvar() {
                    return magvar;
                }

                /**
                 * Définit la valeur de la propriété magvar.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setMagvar(BigDecimal value) {
                    this.magvar = value;
                }

                /**
                 * Obtient la valeur de la propriété geoidheight.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getGeoidheight() {
                    return geoidheight;
                }

                /**
                 * Définit la valeur de la propriété geoidheight.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setGeoidheight(BigDecimal value) {
                    this.geoidheight = value;
                }

                /**
                 * Obtient la valeur de la propriété name.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Définit la valeur de la propriété name.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setName(String value) {
                    this.name = value;
                }

                /**
                 * Obtient la valeur de la propriété cmt.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCmt() {
                    return cmt;
                }

                /**
                 * Définit la valeur de la propriété cmt.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCmt(String value) {
                    this.cmt = value;
                }

                /**
                 * Obtient la valeur de la propriété desc.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getDesc() {
                    return desc;
                }

                /**
                 * Définit la valeur de la propriété desc.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setDesc(String value) {
                    this.desc = value;
                }

                /**
                 * Obtient la valeur de la propriété src.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSrc() {
                    return src;
                }

                /**
                 * Définit la valeur de la propriété src.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSrc(String value) {
                    this.src = value;
                }

                /**
                 * Obtient la valeur de la propriété url.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUrl() {
                    return url;
                }

                /**
                 * Définit la valeur de la propriété url.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setUrl(String value) {
                    this.url = value;
                }

                /**
                 * Obtient la valeur de la propriété urlname.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUrlname() {
                    return urlname;
                }

                /**
                 * Définit la valeur de la propriété urlname.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setUrlname(String value) {
                    this.urlname = value;
                }

                /**
                 * Obtient la valeur de la propriété sym.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSym() {
                    return sym;
                }

                /**
                 * Définit la valeur de la propriété sym.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSym(String value) {
                    this.sym = value;
                }

                /**
                 * Obtient la valeur de la propriété type.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Définit la valeur de la propriété type.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setType(String value) {
                    this.type = value;
                }

                /**
                 * Obtient la valeur de la propriété fix.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getFix() {
                    return fix;
                }

                /**
                 * Définit la valeur de la propriété fix.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setFix(String value) {
                    this.fix = value;
                }

                /**
                 * Obtient la valeur de la propriété sat.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigInteger }
                 *     
                 */
                public BigInteger getSat() {
                    return sat;
                }

                /**
                 * Définit la valeur de la propriété sat.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigInteger }
                 *     
                 */
                public void setSat(BigInteger value) {
                    this.sat = value;
                }

                /**
                 * Obtient la valeur de la propriété hdop.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getHdop() {
                    return hdop;
                }

                /**
                 * Définit la valeur de la propriété hdop.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setHdop(BigDecimal value) {
                    this.hdop = value;
                }

                /**
                 * Obtient la valeur de la propriété vdop.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getVdop() {
                    return vdop;
                }

                /**
                 * Définit la valeur de la propriété vdop.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setVdop(BigDecimal value) {
                    this.vdop = value;
                }

                /**
                 * Obtient la valeur de la propriété pdop.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getPdop() {
                    return pdop;
                }

                /**
                 * Définit la valeur de la propriété pdop.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setPdop(BigDecimal value) {
                    this.pdop = value;
                }

                /**
                 * Obtient la valeur de la propriété ageofdgpsdata.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getAgeofdgpsdata() {
                    return ageofdgpsdata;
                }

                /**
                 * Définit la valeur de la propriété ageofdgpsdata.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setAgeofdgpsdata(BigDecimal value) {
                    this.ageofdgpsdata = value;
                }

                /**
                 * Obtient la valeur de la propriété dgpsid.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Integer }
                 *     
                 */
                public Integer getDgpsid() {
                    return dgpsid;
                }

                /**
                 * Définit la valeur de la propriété dgpsid.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Integer }
                 *     
                 */
                public void setDgpsid(Integer value) {
                    this.dgpsid = value;
                }

                /**
                 * Gets the value of the any property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the any property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getAny().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Object }
                 * 
                 * 
                 */
                public List<Object> getAny() {
                    if (any == null) {
                        any = new ArrayList<Object>();
                    }
                    return this.any;
                }

                /**
                 * Obtient la valeur de la propriété lat.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getLat() {
                    return lat;
                }

                /**
                 * Définit la valeur de la propriété lat.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setLat(BigDecimal value) {
                    this.lat = value;
                }

                /**
                 * Obtient la valeur de la propriété lon.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getLon() {
                    return lon;
                }

                /**
                 * Définit la valeur de la propriété lon.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setLon(BigDecimal value) {
                    this.lon = value;
                }

            }

        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ele" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *         &lt;element name="magvar" type="{http://www.topografix.com/GPX/1/0}degreesType" minOccurs="0"/>
     *         &lt;element name="geoidheight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="cmt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="src" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
     *         &lt;element name="urlname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="sym" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fix" type="{http://www.topografix.com/GPX/1/0}fixType" minOccurs="0"/>
     *         &lt;element name="sat" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
     *         &lt;element name="hdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="vdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="pdop" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="ageofdgpsdata" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="dgpsid" type="{http://www.topografix.com/GPX/1/0}dgpsStationType" minOccurs="0"/>
     *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="lat" use="required" type="{http://www.topografix.com/GPX/1/0}latitudeType" />
     *       &lt;attribute name="lon" use="required" type="{http://www.topografix.com/GPX/1/0}longitudeType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ele",
        "time",
        "magvar",
        "geoidheight",
        "name",
        "cmt",
        "desc",
        "src",
        "url",
        "urlname",
        "sym",
        "type",
        "fix",
        "sat",
        "hdop",
        "vdop",
        "pdop",
        "ageofdgpsdata",
        "dgpsid",
        "any"
    })
    public static class Wpt {

        protected BigDecimal ele;
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar time;
        protected BigDecimal magvar;
        protected BigDecimal geoidheight;
        protected String name;
        protected String cmt;
        protected String desc;
        protected String src;
        @XmlSchemaType(name = "anyURI")
        protected String url;
        protected String urlname;
        protected String sym;
        protected String type;
        protected String fix;
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger sat;
        protected BigDecimal hdop;
        protected BigDecimal vdop;
        protected BigDecimal pdop;
        protected BigDecimal ageofdgpsdata;
        @XmlSchemaType(name = "integer")
        protected Integer dgpsid;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        @XmlAttribute(name = "lat", required = true)
        protected BigDecimal lat;
        @XmlAttribute(name = "lon", required = true)
        protected BigDecimal lon;

        /**
         * Obtient la valeur de la propriété ele.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEle() {
            return ele;
        }

        /**
         * Définit la valeur de la propriété ele.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEle(BigDecimal value) {
            this.ele = value;
        }

        /**
         * Obtient la valeur de la propriété time.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getTime() {
            return time;
        }

        /**
         * Définit la valeur de la propriété time.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setTime(XMLGregorianCalendar value) {
            this.time = value;
        }

        /**
         * Obtient la valeur de la propriété magvar.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMagvar() {
            return magvar;
        }

        /**
         * Définit la valeur de la propriété magvar.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMagvar(BigDecimal value) {
            this.magvar = value;
        }

        /**
         * Obtient la valeur de la propriété geoidheight.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getGeoidheight() {
            return geoidheight;
        }

        /**
         * Définit la valeur de la propriété geoidheight.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setGeoidheight(BigDecimal value) {
            this.geoidheight = value;
        }

        /**
         * Obtient la valeur de la propriété name.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Définit la valeur de la propriété name.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Obtient la valeur de la propriété cmt.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCmt() {
            return cmt;
        }

        /**
         * Définit la valeur de la propriété cmt.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCmt(String value) {
            this.cmt = value;
        }

        /**
         * Obtient la valeur de la propriété desc.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Définit la valeur de la propriété desc.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDesc(String value) {
            this.desc = value;
        }

        /**
         * Obtient la valeur de la propriété src.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrc() {
            return src;
        }

        /**
         * Définit la valeur de la propriété src.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrc(String value) {
            this.src = value;
        }

        /**
         * Obtient la valeur de la propriété url.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrl() {
            return url;
        }

        /**
         * Définit la valeur de la propriété url.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Obtient la valeur de la propriété urlname.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrlname() {
            return urlname;
        }

        /**
         * Définit la valeur de la propriété urlname.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrlname(String value) {
            this.urlname = value;
        }

        /**
         * Obtient la valeur de la propriété sym.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSym() {
            return sym;
        }

        /**
         * Définit la valeur de la propriété sym.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSym(String value) {
            this.sym = value;
        }

        /**
         * Obtient la valeur de la propriété type.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Définit la valeur de la propriété type.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Obtient la valeur de la propriété fix.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFix() {
            return fix;
        }

        /**
         * Définit la valeur de la propriété fix.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFix(String value) {
            this.fix = value;
        }

        /**
         * Obtient la valeur de la propriété sat.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getSat() {
            return sat;
        }

        /**
         * Définit la valeur de la propriété sat.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setSat(BigInteger value) {
            this.sat = value;
        }

        /**
         * Obtient la valeur de la propriété hdop.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getHdop() {
            return hdop;
        }

        /**
         * Définit la valeur de la propriété hdop.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setHdop(BigDecimal value) {
            this.hdop = value;
        }

        /**
         * Obtient la valeur de la propriété vdop.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getVdop() {
            return vdop;
        }

        /**
         * Définit la valeur de la propriété vdop.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setVdop(BigDecimal value) {
            this.vdop = value;
        }

        /**
         * Obtient la valeur de la propriété pdop.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getPdop() {
            return pdop;
        }

        /**
         * Définit la valeur de la propriété pdop.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setPdop(BigDecimal value) {
            this.pdop = value;
        }

        /**
         * Obtient la valeur de la propriété ageofdgpsdata.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getAgeofdgpsdata() {
            return ageofdgpsdata;
        }

        /**
         * Définit la valeur de la propriété ageofdgpsdata.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setAgeofdgpsdata(BigDecimal value) {
            this.ageofdgpsdata = value;
        }

        /**
         * Obtient la valeur de la propriété dgpsid.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getDgpsid() {
            return dgpsid;
        }

        /**
         * Définit la valeur de la propriété dgpsid.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setDgpsid(Integer value) {
            this.dgpsid = value;
        }

        /**
         * Gets the value of the any property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

        /**
         * Obtient la valeur de la propriété lat.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getLat() {
            return lat;
        }

        /**
         * Définit la valeur de la propriété lat.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setLat(BigDecimal value) {
            this.lat = value;
        }

        /**
         * Obtient la valeur de la propriété lon.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getLon() {
            return lon;
        }

        /**
         * Définit la valeur de la propriété lon.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setLon(BigDecimal value) {
            this.lon = value;
        }

    }

}
