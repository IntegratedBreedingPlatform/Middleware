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

/**
 * The Class MapInfo. For the details of a MappingData.
 * 
 * @author Joyce Avestro
 * 
 */
public class MapInfo implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The marker name. */
    private String markerName;
    
    /** The linkage group. */
    private String linkageGroup;
    
    /** The start position. */
    private Float startPosition;

    /**
     * Instantiates a new map info.
     *
     * @param markerName the marker name
     * @param linkageGroup the linkage group
     * @param startPosition the start position
     */
    public MapInfo(String markerName, String linkageGroup, Float startPosition) {
        this.markerName = markerName;
        this.linkageGroup = linkageGroup;
        this.startPosition = startPosition;
    }

    /**
     * Gets the marker name.
     *
     * @return the marker name
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * Sets the marker name.
     *
     * @param markerName the new marker name
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    
    /**
     * Gets the linkage group.
     *
     * @return the linkage group
     */
    public String getLinkageGroup() {
        return linkageGroup;
    }

    /**
     * Sets the linkage group.
     *
     * @param linkageGroup the new linkage group
     */
    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }

    /**
     * Gets the start position.
     *
     * @return the start position
     */
    public Float getStartPosition() {
        return startPosition;
    }

    /**
     * Sets the start position.
     *
     * @param startPosition the new start position
     */
    public void setStartPosition(Float startPosition) {
        this.startPosition = startPosition;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 37).append(markerName).append(linkageGroup).append(startPosition).toHashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        StringBuilder builder = new StringBuilder();
        builder.append("MapInfo [markerName=");
        builder.append(markerName);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", startPosition=");
        builder.append(startPosition);
        builder.append("]");
        return builder.toString();
    }

}
