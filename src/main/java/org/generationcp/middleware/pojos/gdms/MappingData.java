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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for mapping_data table
 * 
 */
@Entity
@Table(name = "mapping_data")
public class MappingData implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final String GET_MAP_INFO_BY_MAP_NAME = "select marker_name, linkage_group, start_position " +
            "from mapping_data where map_name = :mapName order by linkage_group, start_position , marker_name";
            
    @Id
    @Column(name = "marker_id")
    private Integer markerId;

    @Column(name = "linkage_group")
    private String linkageGroup;

    @Column(name = "start_position")
    private Float startPosition;

    @Column(name = "map_unit")
    private String mapUnit;

    @Column(name = "map_name")
    private String mapName;

    @Column(name = "marker_name")
    private String markerName;
    
    public MappingData() {
    }

    public MappingData(Integer markerId, String linkageGroup, Float startPosition, 
            String mapUnit, String mapName, String markerName) {
        this.markerId = markerId;
        this.linkageGroup = linkageGroup;
        this.startPosition = startPosition;
        this.mapUnit = mapUnit;
        this.mapName = mapName;
        this.markerName = markerName;
    }
        
    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public String getLinkageGroup() {
        return linkageGroup;
    }

    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }

    public float getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(float startPosition) {
        this.startPosition = startPosition;
    }
    
    public String getMapUnit() {
        return mapUnit;
    }
    
    public void setMapUnit(String mapUnit) {
        this.mapUnit = mapUnit;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public String getMarkerName() {
        return markerName;
    }
    
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MappingData)) {
            return false;
        }

        MappingData rhs = (MappingData) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(markerId, rhs.markerId)
                .append(linkageGroup, rhs.linkageGroup).append(startPosition, rhs.startPosition)
                .append(mapUnit, rhs.mapUnit).append(mapName, rhs.mapName)
                .append(markerName, rhs.markerName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 71).append(markerId).append(linkageGroup)
                .append(startPosition).append(mapUnit).append(mapName).append(markerName).toHashCode();
    }

    @Override
    public String toString() {
        return "MappingData [markerId=" + markerId + ", " + 
                "linkageGroup=" + linkageGroup + ", " + 
                "startPosition=" + startPosition + ", " + 
                "mapUnit=" + mapUnit + ", " + 
                "mapName=" + mapName + ", " + 
                "markerName=" + markerName + "]";
    }

}
