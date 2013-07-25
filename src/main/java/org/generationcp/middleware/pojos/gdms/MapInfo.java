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

/**
 * The Class MapInfo. For the details of a MappingData.
 * 
 * @author Joyce Avestro
 * 
 */
public class MapInfo implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer markerId;

    private String markerName;
    
    private String mapName;
    
    private String linkageGroup;
    
    private Float startPosition;

    public MapInfo(Integer markerId, String markerName, String mapName, String linkageGroup, Float startPosition) {
        this.markerId = markerId;
        this.markerName = markerName;
        this.mapName = mapName;
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

    
    public Integer getMarkerId() {
        return markerId;
    }

    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    
    public String getMapName() {
        return mapName;
    }

    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((linkageGroup == null) ? 0 : linkageGroup.hashCode());
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result
                + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result
                + ((markerName == null) ? 0 : markerName.hashCode());
        result = prime * result
                + ((startPosition == null) ? 0 : startPosition.hashCode());
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
        MapInfo rhs = (MapInfo) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(markerId, rhs.markerId)
        .append(linkageGroup, rhs.linkageGroup).append(startPosition, rhs.startPosition)
        .append(mapName, rhs.mapName).append(markerName, rhs.markerName).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MapInfo [markerId=");
        builder.append(markerId);
        builder.append(", markerName=");
        builder.append(markerName);
        builder.append(", mapName=");
        builder.append(mapName);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", startPosition=");
        builder.append(startPosition);
        builder.append("]");
        return builder.toString();
    }
    
    public static MapInfo build(MappingData mapData){
        return new MapInfo(mapData.getMarkerId(), mapData.getMarkerName(), mapData.getMapName(), 
                mapData.getLinkageGroup(), mapData.getStartPosition());
    }
    


}
