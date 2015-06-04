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

/**
 * The Class MarkerNameElement. Contains the pair germplasm id and marker name. Used in getting marker names by gid.
 *
 * @author Joyce Avestro
 *
 */
public class MarkerNameElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer gId;

	private Integer markerId;

	private String markerName;

	MarkerNameElement() {
	}

	public MarkerNameElement(Integer gId, String markerName) {
		this.gId = gId;
		this.markerName = markerName;
	}

	public MarkerNameElement(Integer gId, Integer markerId, String markerName) {
		this.gId = gId;
		this.markerId = markerId;
		this.markerName = markerName;
	}

	public Integer getgId() {
		return this.gId;
	}

	public void setgId(Integer gId) {
		this.gId = gId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getMarkerName() {
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.gId == null ? 0 : this.gId.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerName == null ? 0 : this.markerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MarkerNameElement other = (MarkerNameElement) obj;
		if (this.gId == null) {
			if (other.gId != null) {
				return false;
			}
		} else if (!this.gId.equals(other.gId)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerName == null) {
			if (other.markerName != null) {
				return false;
			}
		} else if (!this.markerName.equals(other.markerName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerNameElement [gId=");
		builder.append(this.gId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append("]");
		return builder.toString();
	}

}
