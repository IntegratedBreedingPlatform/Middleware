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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for locdes table.
 *
 * @author klmanansala
 */
@Entity
@Table(name = "locdes")
public class Locdes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ldid")
	private Integer ldid;

	@Column(name = "locid")
	private Integer locationId;

	@Column(name = "dtype")
	private Integer typeId;

	@Column(name = "duid")
	private Integer userId;

	@Basic(optional = false)
	@Column(name = "dval")
	private String dval;

	@Basic(optional = false)
	@Column(name = "ddate")
	private Integer ddate;

	@Column(name = "dref")
	private Integer referenceId;

	public Locdes() {
	}

	public Integer getLdid() {
		return this.ldid;
	}

	public Locdes(Integer ldid, Integer locationId, Integer typeId, Integer userId, String dval, Integer ddate, Integer referenceId) {
		super();
		this.ldid = ldid;
		this.locationId = locationId;
		this.typeId = typeId;
		this.userId = userId;
		this.dval = dval;
		this.ddate = ddate;
		this.referenceId = referenceId;
	}

	public void setLdid(Integer ldid) {
		this.ldid = ldid;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
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

	public Integer getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getDval() {
		return this.dval;
	}

	public void setDval(String dval) {
		this.dval = dval;
	}

	public Integer getDdate() {
		return this.ddate;
	}

	public void setDdate(Integer ddate) {
		this.ddate = ddate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Locdes) {
			Locdes param = (Locdes) obj;
			if (this.getLdid().equals(param.getLdid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.getLdid();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Locdes [ldid=");
		builder.append(this.ldid);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", dval=");
		builder.append(this.dval);
		builder.append(", ddate=");
		builder.append(this.ddate);
		builder.append(", referenceId=");
		builder.append(this.referenceId);
		builder.append("]");
		return builder.toString();
	}

}
