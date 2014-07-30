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
 * POJO for mapping_data table.
 * 
 */
@Entity
@Table(name = "gdms_mapping_data")
public class MappingData implements Serializable{

    private static final long serialVersionUID = 1L;
    
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
    
    @Column(name = "map_id")
    private Integer mapId;
    
    public MappingData() {
    }

    public MappingData(Integer markerId, String linkageGroup, Float startPosition, 
            String mapUnit, String mapName, String markerName, Integer mapId) {
        this.markerId = markerId;
        this.linkageGroup = linkageGroup;
        this.startPosition = startPosition;
        this.mapUnit = mapUnit;
        this.mapName = mapName;
        this.markerName = markerName;
        this.mapId = mapId;
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
    
    public Integer getMapId() {
        return mapId;
    }
    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
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
        StringBuilder builder = new StringBuilder();
        builder.append("MappingData [markerId=");
        builder.append(markerId);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", startPosition=");
        builder.append(startPosition);
        builder.append(", mapUnit=");
        builder.append(mapUnit);
        builder.append(", mapName=");
        builder.append(mapName);
        builder.append(", markerName=");
        builder.append(markerName);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append("]");
        return builder.toString();
    }

}
