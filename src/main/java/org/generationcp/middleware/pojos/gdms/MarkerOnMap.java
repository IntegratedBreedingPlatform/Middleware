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
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_markers_onmap table.
 *
 * @author Dennis Billano
 */
@Entity
@Table(name = "gdms_markers_onmap")
public class MarkerOnMap implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "map_id")
    private Integer mapId;  
    
    @Id
    @Column(name = "marker_id")
    private Integer markerId;

    @Column(name = "start_position")
    private Float startPosition;
    
    @Column(name = "end_position")
    private Float endPosition;

    @Column(name = "map_unit")
    private String mapUnit;

    @Column(name = "linkage_group")
    private String linkageGroup;
    
    public MarkerOnMap() {
        super();
    }

    public MarkerOnMap(Integer mapId, Integer markerId, Float startPosition, Float endPosition, String mapUnit, String linkageGroup) {
        super();
        this.mapId = mapId;
        this.markerId = markerId;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.mapUnit = mapUnit;
        this.linkageGroup = linkageGroup;
    }
    
    public Integer getMapId() {
        return mapId;
    }
    
    public void setMapId(Integer markerId) {
        this.mapId = mapId;
    }

    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public Float getStartPosition() {
        return startPosition;
    }
    
    public void setStartPosition(Float startPosition) {
        this.startPosition = startPosition;
    }

    public Float getEndPosition() {
        return endPosition;
    }
    
    public void setEndPosition(Float endPosition) {
        this.startPosition = endPosition;
    }

    public String getMapUnit() {
        return mapUnit;
    }
    
    public void setMapUnit(String mapUnit) {
        this.mapUnit = mapUnit;
    }

    public String getLinkageGroup() {
        return linkageGroup;
    }
    
    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 127).append(markerId).toHashCode();
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
        if (!(obj instanceof MarkerOnMap)) {
            return false;
        }

        MarkerOnMap rhs = (MarkerOnMap) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(markerId, rhs.markerId).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerOnMap [mapId=");
        builder.append(mapId);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append(", startPosition=");
        builder.append(startPosition);
        builder.append(", endPosition=");
        builder.append(endPosition);
        builder.append(", mapUnit=");
        builder.append(mapUnit);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append("]");
        return builder.toString();
    }
    
}
