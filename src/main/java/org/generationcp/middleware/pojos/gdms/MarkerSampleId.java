/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

/**
 * Marker and Sample Id 
 * 
 */
public class MarkerSampleId implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer markerId;
    
    private Integer markerSampleId;

	public MarkerSampleId(Integer markerId, Integer markerSampleId) {
		this.markerId = markerId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getMarkerId() {
		return markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getMarkerSampleId() {
		return markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkerSampleId other = (MarkerSampleId) obj;
		if (markerId == null) {
			if (other.markerId != null)
				return false;
		} else if (!markerId.equals(other.markerId))
			return false;
		if (markerSampleId == null) {
			if (other.markerSampleId != null)
				return false;
		} else if (!markerSampleId.equals(other.markerSampleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerSampleId [markerId=");
		builder.append(markerId);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append("]");
		return builder.toString();
	}
    


}
