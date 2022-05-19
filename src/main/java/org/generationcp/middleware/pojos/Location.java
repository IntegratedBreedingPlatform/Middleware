/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * POJO for location table.
 *
 * @author Kevin Manansala, Mark Agarrado, Joyce Avestro
 */
@NamedQueries({@NamedQuery(name = "getAllLocation", query = "FROM Location"),
		@NamedQuery(name = "countAllLocation", query = "SELECT COUNT(l) FROM Location l")})
@Entity
@Table(name = "location")
// JAXB Element Tags for JSON output
@XmlRootElement(name = "location")
@XmlType(propOrder = {"locid", "lname", "typeFname", "labbr", "countryIsofull"})
@XmlAccessorType(XmlAccessType.NONE)
public class Location implements Serializable, Comparable<Location> {

	private static final long serialVersionUID = 1L;

	public static final String GET_ALL_COUNTRY =
		"select l.* from location l, udflds u where l.ltype = u.fldno and u.ftable='LOCATION' and u.fcode='COUNTRY' "
			+ "and exists (select 1 from cntry c where c.cntryid =l.cntryid) order by l.lname";

	public static final String UNSPECIFIED_LOCATION = "Unspecified Location";
	
	public static final Integer[] BREEDING_LOCATION_TYPE_IDS = {410, 411, 412};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "locid")
	@XmlElement(name = "locationId")
	private Integer locid;

	@Basic(optional = false)
	@Column(name = "ltype")
	private Integer ltype;

	@Basic(optional = false)
	@Column(name = "nllp")
	private Integer nllp;

	@Basic(optional = false)
	@Column(name = "lname")
	@XmlElement(name = "name")
	private String lname;

	@Column(name = "labbr")
	@XmlElement(name = "nameAbbreviation")
	private String labbr;

	@Basic(optional = false)
	@Column(name = "snl3id")
	private Integer snl3id;

	@Basic(optional = false)
	@Column(name = "snl2id")
	private Integer snl2id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "snl1id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Location province;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cntryid")
	@NotFound(action = NotFoundAction.IGNORE)
	private Country country;

	@Basic(optional = false)
	@Column(name = "lrplce")
	private Integer lrplce;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "ldefault", columnDefinition = "TINYINT")
	private Boolean ldefault;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JoinColumn(name = "locid")
	private Georef georef;

	@Transient
	private Integer parentLocationId;

	@Transient
	private String parentLocationName;

	@Transient
	private String parentLocationAbbr;

	public static final String COUNT_ALL_BREEDING_LOCATIONS = "SELECT count(*) AS count FROM location WHERE ltype IN (410, 411, 412)";
	public static final String GET_LOCATION_NAMES_BY_GIDS = "SELECT gid, g.glocn, lname " + "FROM germplsm g " + "LEFT JOIN location l "
			+ "ON g.glocn = l.locid " + "WHERE gid IN (:gids)";

	public Location() {
	}

	public Location(final Integer locid) {
		this.locid = locid;
	}

	public Location(final Integer locid, final Integer ltype, final Integer nllp, final String lname, final String labbr,
		final Integer snl3id, final Integer snl2id, final Location province,
		final Country country, final Integer lrplce) {
		super();
		this.locid = locid;
		this.ltype = ltype;
		this.nllp = nllp;
		this.lname = lname;
		this.labbr = labbr;
		this.snl3id = snl3id;
		this.snl2id = snl2id;
		this.province = province;
		this.country = country;
		this.lrplce = lrplce;
	}

	public Integer getLocid() {
		return this.locid;
	}

	public void setLocid(final Integer locid) {
		this.locid = locid;
	}

	public Integer getLtype() {
		return this.ltype;
	}

	public void setLtype(final Integer ltype) {
		this.ltype = ltype;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(final Country country) {
		this.country = country;
	}

	public Integer getNllp() {
		return this.nllp;
	}

	public void setNllp(final Integer nllp) {
		this.nllp = nllp;
	}

	public String getLname() {
		return this.lname;
	}

	public void setLname(final String lname) {
		this.lname = lname;
	}

	public String getLabbr() {
		return this.labbr;
	}

	public void setLabbr(final String labbr) {
		this.labbr = labbr;
	}

	public Integer getSnl3id() {
		return this.snl3id;
	}

	public void setSnl3id(final Integer snl3id) {
		this.snl3id = snl3id;
	}

	public Integer getSnl2id() {
		return this.snl2id;
	}

	public void setSnl2id(final Integer snl2id) {
		this.snl2id = snl2id;
	}

	public Location getProvince() {
		return province;
	}

	public void setProvince(final Location province) {
		this.province = province;
	}

	public Integer getLrplce() {
		return this.lrplce;
	}

	public void setLrplce(final Integer lrplce) {
		this.lrplce = lrplce;
	}

	public Georef getGeoref() {
		return this.georef;
	}

	public void setGeoref(final Georef georef) {
		this.georef = georef;
	}

	public Double getLatitude() {
		if (this.georef != null) {
			return this.georef.getLat();
		}
		return null;
	}

	public void setLatitude(final Double latitude) {
		if (this.georef == null) {
			this.georef = new Georef(this.locid);
		}
		this.georef.setLat(latitude);
	}

	public Double getLongitude() {
		if (this.georef != null) {
			return this.georef.getLon();
		}
		return null;
	}

	public void setLongitude(final Double longitude) {
		if (this.georef == null) {
			this.georef = new Georef(this.locid);
		}
		this.georef.setLon(longitude);
	}

	public Double getAltitude() {
		if (this.georef != null) {
			return this.georef.getAlt();
		}
		return null;
	}

	public void setAltitude(final Double altitude) {
		if (this.georef == null) {
			this.georef = new Georef(this.locid);
		}
		this.georef.setAlt(altitude);
	}

	@Override
	public int hashCode() {
		return this.getLocid();
	}

	/**
	 * @return the parentLocationName
	 */
	 public String getParentLocationName() {
		return this.parentLocationName;
	}

	/**
	 * @param parentLocationName the parentLocationName to set
	 */
	public void setParentLocationName(final String parentLocationName) {
		this.parentLocationName = parentLocationName;
	}

	/**
	 * @return the parentLocationId
	 */
	public Integer getParentLocationId() {
		return this.parentLocationId;
	}

	/**
	 * @param parentLocationId the parentLocationId to set
	 */
	public void setParentLocationId(final Integer parentLocationId) {
		this.parentLocationId = parentLocationId;
	}

	public String getParentLocationAbbr() {
		return this.parentLocationAbbr;
	}

	public void setParentLocationAbbr(final String parentLocationAbbr) {
		this.parentLocationAbbr = parentLocationAbbr;
	}

	public Boolean getLdefault() {
		return ldefault;
	}

	public void setLdefault(final Boolean ldefault) {
		this.ldefault = ldefault;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Location other = (Location) obj;
		if (this.locid == null) {
			return other.getLocid() == null;
		} else
			return this.locid.equals(other.getLocid());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Location [locid=");
		builder.append(this.locid);
		builder.append(", ltype=");
		builder.append(this.ltype);
		builder.append(", nllp=");
		builder.append(this.nllp);
		builder.append(", lname=");
		builder.append(this.lname);
		builder.append(", labbr=");
		builder.append(this.labbr);
		builder.append(", snl3id=");
		builder.append(this.snl3id);
		builder.append(", snl2id=");
		builder.append(this.snl2id);
		builder.append(", lrplce=");
		builder.append(this.lrplce);
		builder.append(", latitude=");
		builder.append(this.georef != null ? this.georef.getLat() : null);
		builder.append(", longitude=");
		builder.append(this.georef != null ? this.georef.getLon() : null);
		builder.append(", altitude=");
		builder.append(this.georef != null ? this.georef.getAlt() : null);
		builder.append(", parentLocationName=");
		builder.append(this.parentLocationName);
		builder.append(", ldefault=");
		builder.append(this.ldefault);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(final Location compareLocation) {

		final String compareName = compareLocation.getLname();

		// ascending order
		return this.lname.compareTo(compareName);

	}

}
