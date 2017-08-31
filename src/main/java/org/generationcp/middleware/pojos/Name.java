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
import javax.persistence.Table;

/**
 * POJO for names table.
 *
 * @author klmanansala
 */
@Entity
@Table(name = "names")
public class Name implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int NSTAT_PREFERRED_NAME = 1;

	// For getGidAndNidByGermplasmNames()
	public static final String GET_NAME_DETAILS_BY_NAME = "SELECT gid, nid, nval " + "FROM names " + "WHERE nval IN (:germplasmNameList)";

	public static final String GET_PREFERRED_IDS_BY_LIST_ID = "SELECT {n.*} " + "FROM listdata ld " + "JOIN names n "
			+ "ON ld.gid = n.gid " + "WHERE n.nstat = 8 " + "AND ld.listid = :listId";

	public static final String GET_PREFERRED_IDS_BY_GIDS = "SELECT gid, nval " + "FROM names " + "WHERE nstat = 8 AND gid IN (:gids)";

	public static final String GET_PREFERRED_NAMES_BY_GIDS = "SELECT gid, nval " + "FROM names " + "WHERE nstat = 1 AND gid IN (:gids)";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "nid")
	private Integer nid;

	@Basic(optional = false)
	@Column(name = "gid")
	private Integer germplasmId;

	@Basic(optional = false)
	@Column(name = "ntype")
	private Integer typeId;

	@Basic(optional = false)
	@Column(name = "nstat")
	private Integer nstat;

	@Basic(optional = false)
	@Column(name = "nuid")
	private Integer userId;

	@Basic(optional = false)
	@Column(name = "nval")
	private String nval;

	@Column(name = "nlocn")
	private Integer locationId;

	@Basic(optional = false)
	@Column(name = "ndate")
	private Integer ndate;

	@Column(name = "nref")
	private Integer referenceId;

	public Name() {
	}

	public Name(Integer nid) {
		super();
		this.nid = nid;
	}

	public Name(Integer nid, Integer germplasmId, Integer typeId, Integer nstat, Integer userId, String nval, Integer locationId,
			Integer ndate, Integer referenceId) {
		super();
		this.nid = nid;
		this.germplasmId = germplasmId;
		this.typeId = typeId;
		this.nstat = nstat;
		this.userId = userId;
		this.nval = nval;
		this.locationId = locationId;
		this.ndate = ndate;
		this.referenceId = referenceId;
	}

	public Integer getNid() {
		return this.nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getNstat() {
		return this.nstat;
	}

	public void setNstat(Integer nstat) {
		this.nstat = nstat;
	}

	public String getNval() {
		return this.nval;
	}

	public void setNval(String nval) {
		this.nval = nval;
	}

	public Integer getNdate() {
		return this.ndate;
	}

	public void setNdate(Integer ndate) {
		this.ndate = ndate;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Name) {
			Name param = (Name) obj;
			if (this.getNid().equals(param.getNid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.getNid();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Name [nid=");
		builder.append(this.nid);
		builder.append(", germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", nstat=");
		builder.append(this.nstat);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", nval=");
		builder.append(this.nval);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", ndate=");
		builder.append(this.ndate);
		builder.append(", referenceId=");
		builder.append(this.referenceId);
		builder.append("]");
		return builder.toString();
	}

}
