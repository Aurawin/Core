//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.17 at 02:16:50 PM CST 
//


package com.aurawin.core.rsr.transport.methods.http.webDAV;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{dav}lockscope"/>
 *         &lt;element ref="{dav}locktype"/>
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
    "lockscope",
    "locktype"
})
@XmlRootElement(name = "lockentry")
public class Lockentry {

    @XmlElement(required = true)
    protected Lockscope lockscope;
    @XmlElement(required = true)
    protected Locktype locktype;

    /**
     * Gets the value of the lockscope property.
     * 
     * @return
     *     possible object is
     *     {@link Lockscope }
     *     
     */
    public Lockscope getLockscope() {
        return lockscope;
    }

    /**
     * Sets the value of the lockscope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lockscope }
     *     
     */
    public void setLockscope(Lockscope value) {
        this.lockscope = value;
    }

    /**
     * Gets the value of the locktype property.
     * 
     * @return
     *     possible object is
     *     {@link Locktype }
     *     
     */
    public Locktype getLocktype() {
        return locktype;
    }

    /**
     * Sets the value of the locktype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Locktype }
     *     
     */
    public void setLocktype(Locktype value) {
        this.locktype = value;
    }

}
