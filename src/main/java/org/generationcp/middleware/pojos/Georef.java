/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for georef table
 * 
 * @author klmanansala
 */
@Entity
@Table(name = "georef")
public class Georef implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "locid")
    private Integer locid;

    @Column(name = "llpn")
    private Integer llpn;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "alt")
    private Double alt;

    public Georef() {
    }

    public Georef(Integer locid) {
	this.locid = locid;
    }

    public Georef(Integer locid, Integer llpn, Double lat, Double lon,
	    Double alt) {
	super();
	this.locid = locid;
	this.llpn = llpn;
	this.lat = lat;
	this.lon = lon;
	this.alt = alt;
    }

    public Integer getLocid() {
	return locid;
    }

    public void setLocid(Integer locid) {
	this.locid = locid;
    }

    public Integer getLlpn() {
	return llpn;
    }

    public void setLlpn(Integer llpn) {
	this.llpn = llpn;
    }

    public Double getLat() {
	return lat;
    }

    public void setLat(Double lat) {
	this.lat = lat;
    }

    public Double getLon() {
	return lon;
    }

    public void setLon(Double lon) {
	this.lon = lon;
    }

    public Double getAlt() {
	return alt;
    }

    public void setAlt(Double alt) {
	this.alt = alt;
    }

    @Override
    public int hashCode() {
	return this.getLocid();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;

	if (obj instanceof Georef) {
	    Georef param = (Georef) obj;
	    if (this.getLocid() == param.getLocid())
		return true;
	}

	return false;
    }

    @Override
    public String toString() {
	return "Georef [locid=" + locid + ", llpn=" + llpn + ", lat=" + lat
		+ ", lon=" + lon + ", alt=" + alt + "]";
    }

}
