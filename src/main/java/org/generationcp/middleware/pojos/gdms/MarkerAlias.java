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

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_marker_alias table.
 *
 * @author Dennis Billano <b>File Created</b>: March 7, 2013
 */
@Entity
@Table(name = "gdms_marker_alias")
public class MarkerAlias implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "markeralias_id")
	private Integer markerAliasId;

	@Basic(optional = false)
	@Column(name = "marker_id")
	private Integer markerId;

	@Basic(optional = false)
	@Column(name = "alias")
	private String alias;

	public MarkerAlias() {
	}

	public MarkerAlias(Integer markerAliasId, Integer markerId, String alias) {
		this.markerAliasId = markerAliasId;
		this.markerId = markerId;
		this.alias = alias;
	}

	public Integer getMarkerAliasId() {
		return this.markerAliasId;
	}

	public void setMarkerAliasId(Integer markerAliasId) {
		this.markerAliasId = markerAliasId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof MarkerAlias)) {
			return false;
		}

		MarkerAlias rhs = (MarkerAlias) obj;
		return new EqualsBuilder().append(this.markerAliasId, rhs.markerAliasId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(61, 131).append(this.markerAliasId).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerAlias [markerAliasId=");
		builder.append(this.markerAliasId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", alias=");
		builder.append(this.alias);
		builder.append("]");
		return builder.toString();
	}

}
