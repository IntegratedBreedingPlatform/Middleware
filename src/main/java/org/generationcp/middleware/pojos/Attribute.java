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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * POJO for atributs table.
 *
 * @author klmanansala
 */
@NamedQueries({@NamedQuery(name = "getAttributesByGID",
		query = "FROM Attribute a WHERE a.germplasmId = :gid AND a.typeId <> 9999 AND a.typeId <> 999")})
@Entity
@Table(name = "atributs")
public class Attribute implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GET_BY_GID = "getAttributesByGID";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "aid")
	private Integer aid;

	@Basic(optional = false)
	@Column(name = "gid")
	private Integer germplasmId;

	@Basic(optional = false)
	@Column(name = "atype")
	private Integer typeId;

	@Basic(optional = false)
	@Column(name = "auid")
	private Integer userId;

	@Basic(optional = false)
	@Column(name = "aval")
	private String aval;

	@Column(name = "alocn")
	private Integer locationId;

	@Column(name = "aref")
	private Integer referenceId;

	@Column(name = "adate")
	private Integer adate;

	public Attribute() {
	}

	public Attribute(Integer aid) {
		super();
		this.aid = aid;
	}

	public Attribute(Integer aid, Integer germplasmId, Integer typeId, Integer userId, String aval, Integer locationId,
			Integer referenceId, Integer adate) {
		super();
		this.aid = aid;
		this.germplasmId = germplasmId;
		this.typeId = typeId;
		this.userId = userId;
		this.aval = aval;
		this.locationId = locationId;
		this.referenceId = referenceId;
		this.adate = adate;
	}

	public Integer getAid() {
		return this.aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public Integer getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getAval() {
		return this.aval;
	}

	public void setAval(String aval) {
		this.aval = aval;
	}

	public Integer getAdate() {
		return this.adate;
	}

	public void setAdate(Integer adate) {
		this.adate = adate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Attribute [aid=");
		builder.append(this.aid);
		builder.append(", germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", aval=");
		builder.append(this.aval);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", referenceId=");
		builder.append(this.referenceId);
		builder.append(", adate=");
		builder.append(this.adate);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Attribute) {
			Attribute param = (Attribute) obj;
			if (this.getAid().equals(param.getAid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.getAid();
	}

}
