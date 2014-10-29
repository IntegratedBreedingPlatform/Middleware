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
 * Placeholder POJO for Mapping Detail Element.
 * 
 * @author Joyce Avestro
 * 
 */
public class MapDetailElement implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer markerCount;
    
    private Double maxStartPosition;
    
    private String linkageGroup;
    
    private String mapName;
    
    private String mapType;
    
    private String mapDescription;
    
    private String mapUnit;

    public MapDetailElement() {
    }

    
    public MapDetailElement(Integer markerCount, Double maxStartPosition,
			String linkageGroup, String mapName, String mapType,
			String mapDescription, String mapUnit) {
		super();
		this.markerCount = markerCount;
		this.maxStartPosition = maxStartPosition;
		this.linkageGroup = linkageGroup;
		this.mapName = mapName;
		this.mapType = mapType;
		this.mapDescription = mapDescription;
		this.mapUnit = mapUnit;
	}
    
    public Integer getMarkerCount() {
        return markerCount;
    }
    
    public void setMarkerCount(Integer markerCount) {
        this.markerCount = markerCount;
    }

    public Double getMaxStartPosition() {
        return maxStartPosition;
    }

    public void setMaxStartPosition(Double maxStartPosition) {
        this.maxStartPosition = maxStartPosition;
    }
    
    public String getLinkageGroup() {
        return linkageGroup;
    }
    
    public void setLinkageGroup(String linkageGroup) {
        this.linkageGroup = linkageGroup;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
    
    public String getMapType() {
        return mapType;
    }
    
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapDescription() {
		return mapDescription;
	}

	public void setMapDescription(String mapDescription) {
		this.mapDescription = mapDescription;
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
        result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
        result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
        result = prime * result + ((markerCount == null) ? 0 : markerCount.hashCode());
        result = prime * result + ((maxStartPosition == null) ? 0 : maxStartPosition.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        MapDetailElement other = (MapDetailElement) obj;
        if (linkageGroup == null) {
            if (other.linkageGroup != null) {
                return false;
            }
        } else if (!linkageGroup.equals(other.linkageGroup)) {
            return false;
        }
        if (mapName == null) {
            if (other.mapName != null) {
                return false;
            }
        } else if (!mapName.equals(other.mapName)) {
            return false;
        }
        if (mapType == null) {
            if (other.mapType != null) {
                return false;
            }
        } else if (!mapType.equals(other.mapType)) {
            return false;
        }
        if (markerCount == null) {
            if (other.markerCount != null) {
                return false;
            }
        } else if (!markerCount.equals(other.markerCount)) {
            return false;
        }
        if (maxStartPosition == null) {
            if (other.maxStartPosition != null) {
                return false;
            }
        } else if (!maxStartPosition.equals(other.maxStartPosition)) {
            return false;
        }
        return true;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapDetailElement [markerCount=");
		builder.append(markerCount);
		builder.append(", maxStartPosition=");
		builder.append(maxStartPosition);
		builder.append(", linkageGroup=");
		builder.append(linkageGroup);
		builder.append(", mapName=");
		builder.append(mapName);
		builder.append(", mapType=");
		builder.append(mapType);
		builder.append(", mapDescription=");
		builder.append(mapDescription);
		builder.append(", mapUnit=");
		builder.append(mapUnit);
		builder.append("]");
		return builder.toString();
	}
    
}