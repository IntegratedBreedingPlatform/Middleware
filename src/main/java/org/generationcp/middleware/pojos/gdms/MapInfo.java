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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MapInfo implements Serializable{

    private static final long serialVersionUID = 1L;

    private String markerName;
    private String linkageGroup;
    private Float startPosition;

    public MapInfo(String markerName, String linkageGroup, Float startPosition) {
        this.markerName = markerName;
        this.linkageGroup = linkageGroup;
        this.startPosition = startPosition;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    
    public String getLinkageGroup() {
        return linkageGroup;
    }

    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }

    public Float getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Float startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 37).append(markerName).append(linkageGroup).append(startPosition).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MapInfo)) {
            return false;
        }

        MapInfo rhs = (MapInfo) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(markerName, rhs.markerName)
                .append(linkageGroup, rhs.linkageGroup).append(startPosition, rhs.startPosition).isEquals();
    }
    @Override
    public String toString() {
        return  "MapInfo [markerName=" + markerName + ", " +
                "linkageGroup=" + linkageGroup + ", " +
                "startPosition=" + startPosition + "]";
    }

}
