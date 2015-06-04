/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
public class MapInfo implements Serializable, Comparable<MapInfo> {

	private static final long serialVersionUID = 1L;

	private Integer markerId;

	private String markerName;

	private Integer mapId;

	private String mapName;

	private String linkageGroup;

	private Float startPosition;

	private String mapType;

	private String mapUnit;

	public MapInfo(Integer markerId, String markerName, Integer mapId, String mapName, String linkageGroup, Float startPosition,
			String mapType, String mapUnit) {
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
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public String getLinkageGroup() {
		return this.linkageGroup;
	}

	public void setLinkageGroup(String linkageGroup) {
		this.linkageGroup = linkageGroup;
	}

	public Float getStartPosition() {
		return this.startPosition;
	}

	public void setStartPosition(Float startPosition) {
		this.startPosition = startPosition;
	}

	public Integer getMapId() {
		return this.mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getMapType() {
		return this.mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public String getMapUnit() {
		return this.mapUnit;
	}

	public void setMapUnit(String mapUnit) {
		this.mapUnit = mapUnit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.linkageGroup == null ? 0 : this.linkageGroup.hashCode());
		result = prime * result + (this.mapId == null ? 0 : this.mapId.hashCode());
		result = prime * result + (this.mapName == null ? 0 : this.mapName.hashCode());
		result = prime * result + (this.mapType == null ? 0 : this.mapType.hashCode());
		result = prime * result + (this.mapUnit == null ? 0 : this.mapUnit.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerName == null ? 0 : this.markerName.hashCode());
		result = prime * result + (this.startPosition == null ? 0 : this.startPosition.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MapInfo other = (MapInfo) obj;
		if (this.linkageGroup == null) {
			if (other.linkageGroup != null) {
				return false;
			}
		} else if (!this.linkageGroup.equals(other.linkageGroup)) {
			return false;
		}
		if (this.mapId == null) {
			if (other.mapId != null) {
				return false;
			}
		} else if (!this.mapId.equals(other.mapId)) {
			return false;
		}
		if (this.mapName == null) {
			if (other.mapName != null) {
				return false;
			}
		} else if (!this.mapName.equals(other.mapName)) {
			return false;
		}
		if (this.mapType == null) {
			if (other.mapType != null) {
				return false;
			}
		} else if (!this.mapType.equals(other.mapType)) {
			return false;
		}
		if (this.mapUnit == null) {
			if (other.mapUnit != null) {
				return false;
			}
		} else if (!this.mapUnit.equals(other.mapUnit)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerName == null) {
			if (other.markerName != null) {
				return false;
			}
		} else if (!this.markerName.equals(other.markerName)) {
			return false;
		}
		if (this.startPosition == null) {
			if (other.startPosition != null) {
				return false;
			}
		} else if (!this.startPosition.equals(other.startPosition)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapInfo [markerId=");
		builder.append(this.markerId);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append(", mapId=");
		builder.append(this.mapId);
		builder.append(", mapName=");
		builder.append(this.mapName);
		builder.append(", linkageGroup=");
		builder.append(this.linkageGroup);
		builder.append(", startPosition=");
		builder.append(this.startPosition);
		builder.append(", mapType=");
		builder.append(this.mapType);
		builder.append(", mapUnit=");
		builder.append(this.mapUnit);
		builder.append("]");
		return builder.toString();
	}

	public static MapInfo build(MappingData mapData) {
		return new MapInfo(mapData.getMarkerId(), mapData.getMarkerName(), mapData.getMapId(), mapData.getMapName(),
				mapData.getLinkageGroup(), mapData.getStartPosition(), null, null);
	}

	@Override
	public int compareTo(MapInfo mapInfo2) {
		int c = this.getLinkageGroup().compareTo(mapInfo2.getLinkageGroup()); // ascending by linkage group
		if (c == 0) {
			c = this.getStartPosition().compareTo(mapInfo2.getStartPosition()); // ascending by start position
		}
		return c;
	}

}
