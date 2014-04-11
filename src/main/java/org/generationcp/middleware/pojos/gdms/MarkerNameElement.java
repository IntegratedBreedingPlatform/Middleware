/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
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
 * The Class MarkerNameElement. Contains the pair germplasm id and marker name. 
 * Used in getting marker names by gid.
 * 
 * @author Joyce Avestro
 *
 */
public class MarkerNameElement implements Serializable{
        
    private static final long serialVersionUID = 1L;

    private Integer gId;
    
    private Integer markerId;
    
    private String markerName;
    
    MarkerNameElement(){
    }
   
    public MarkerNameElement(Integer gId, String markerName){
        this.gId = gId;
        this.markerName = markerName;              
    }

    public MarkerNameElement(Integer gId, Integer markerId, String markerName){
        this.gId = gId;
        this.markerId = markerId;
        this.markerName = markerName;              
    }

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public Integer getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
   
    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
   
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gId == null) ? 0 : gId.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerName == null) ? 0 : markerName.hashCode());
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
		MarkerNameElement other = (MarkerNameElement) obj;
		if (gId == null) {
			if (other.gId != null)
				return false;
		} else if (!gId.equals(other.gId))
			return false;
		if (markerId == null) {
			if (other.markerId != null)
				return false;
		} else if (!markerId.equals(other.markerId))
			return false;
		if (markerName == null) {
			if (other.markerName != null)
				return false;
		} else if (!markerName.equals(other.markerName))
			return false;
		return true;
	}

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerNameElement [gId=");
		builder.append(gId);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", markerName=");
		builder.append(markerName);
		builder.append("]");
		return builder.toString();
	}
    
}
