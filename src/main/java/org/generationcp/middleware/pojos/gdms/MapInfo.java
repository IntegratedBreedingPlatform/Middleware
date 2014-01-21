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
 * The Class MapInfo. For the details of a MappingData.
 * 
 * @author Joyce Avestro
 * 
 */
public class MapInfo implements Serializable, Comparable<MapInfo>{

    private static final long serialVersionUID = 1L;
    
    private Integer markerId;

    private String markerName;
    
    private Integer mapId;
    
    private String mapName;
    
    private String linkageGroup;
    
    private Float startPosition;
    
    private String mapType;
    
    private String mapUnit;

    public MapInfo(Integer markerId, String markerName, Integer mapId, String mapName, 
            String linkageGroup, Float startPosition, String mapType, String mapUnit) {
        this.markerId = markerId;
        this.markerName = markerName;
        this.mapId = mapId;
        this.mapName = mapName;
        this.linkageGroup = linkageGroup;
        this.startPosition = startPosition;
        this.mapType = mapType;
        this.mapUnit = mapUnit;
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
    
    public Integer getMapId() {
        return mapId;
    }

    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getMarkerId() {
        return markerId;
    }

    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public String getMapType() {
        return mapType;
    }
    
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapUnit() {
        return mapUnit;
    }
    
    public void setMapUnit(String mapUnit) {
        this.mapUnit = mapUnit;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((linkageGroup == null) ? 0 : linkageGroup.hashCode());
        result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
        result = prime * result + ((mapUnit == null) ? 0 : mapUnit.hashCode());
        result = prime * result + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result + ((markerName == null) ? 0 : markerName.hashCode());
        result = prime * result + ((startPosition == null) ? 0 : startPosition.hashCode());
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
        MapInfo other = (MapInfo) obj;
        if (linkageGroup == null) {
            if (other.linkageGroup != null)
                return false;
        } else if (!linkageGroup.equals(other.linkageGroup))
            return false;
        if (mapId == null) {
            if (other.mapId != null)
                return false;
        } else if (!mapId.equals(other.mapId))
            return false;
        if (mapName == null) {
            if (other.mapName != null)
                return false;
        } else if (!mapName.equals(other.mapName))
            return false;
        if (mapType == null) {
            if (other.mapType != null)
                return false;
        } else if (!mapType.equals(other.mapType))
            return false;
        if (mapUnit == null) {
            if (other.mapUnit != null)
                return false;
        } else if (!mapUnit.equals(other.mapUnit))
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
        if (startPosition == null) {
            if (other.startPosition != null)
                return false;
        } else if (!startPosition.equals(other.startPosition))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MapInfo [markerId=");
        builder.append(markerId);
        builder.append(", markerName=");
        builder.append(markerName);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append(", mapName=");
        builder.append(mapName);
        builder.append(", linkageGroup=");
        builder.append(linkageGroup);
        builder.append(", startPosition=");
        builder.append(startPosition);
        builder.append(", mapType=");
        builder.append(mapType);
        builder.append(", mapUnit=");
        builder.append(mapUnit);
        builder.append("]");
        return builder.toString();
    }
    
    public static MapInfo build(MappingData mapData){
        return new MapInfo(mapData.getMarkerId(), mapData.getMarkerName(), mapData.getMapId(), mapData.getMapName(), 
                mapData.getLinkageGroup(), mapData.getStartPosition(), null, null);
    }
    
    @Override
    public int compareTo(MapInfo mapInfo2) {
        int c = this.getLinkageGroup().compareTo(mapInfo2.getLinkageGroup()); // ascending by linkage group
        if (c == 0){
            c = this.getStartPosition().compareTo(mapInfo2.getStartPosition()); // ascending by start position
        }
        return c;
    }



}
