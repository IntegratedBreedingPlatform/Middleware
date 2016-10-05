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
 * Placeholder POJO for Mapping Detail Element.
 *
 * @author Joyce Avestro
 *
 */
public class MapDetailElement implements Serializable {

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

	public MapDetailElement(Integer markerCount, Double maxStartPosition, String linkageGroup, String mapName, String mapType,
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
		return this.markerCount;
	}

	public void setMarkerCount(Integer markerCount) {
		this.markerCount = markerCount;
	}

	public Double getMaxStartPosition() {
		return this.maxStartPosition;
	}

	public void setMaxStartPosition(Double maxStartPosition) {
		this.maxStartPosition = maxStartPosition;
	}

	public String getLinkageGroup() {
		return this.linkageGroup;
	}

	public void setLinkageGroup(String linkageGroup) {
		this.linkageGroup = linkageGroup;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapType() {
		return this.mapType;
	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public String getMapDescription() {
		return this.mapDescription;
	}

	public void setMapDescription(String mapDescription) {
		this.mapDescription = mapDescription;
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
		result = prime * result + (this.mapName == null ? 0 : this.mapName.hashCode());
		result = prime * result + (this.mapType == null ? 0 : this.mapType.hashCode());
		result = prime * result + (this.markerCount == null ? 0 : this.markerCount.hashCode());
		result = prime * result + (this.maxStartPosition == null ? 0 : this.maxStartPosition.hashCode());
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
		MapDetailElement other = (MapDetailElement) obj;
		if (this.linkageGroup == null) {
			if (other.linkageGroup != null) {
				return false;
			}
		} else if (!this.linkageGroup.equals(other.linkageGroup)) {
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
		if (this.markerCount == null) {
			if (other.markerCount != null) {
				return false;
			}
		} else if (!this.markerCount.equals(other.markerCount)) {
			return false;
		}
		if (this.maxStartPosition == null) {
			if (other.maxStartPosition != null) {
				return false;
			}
		} else if (!this.maxStartPosition.equals(other.maxStartPosition)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapDetailElement [markerCount=");
		builder.append(this.markerCount);
		builder.append(", maxStartPosition=");
		builder.append(this.maxStartPosition);
		builder.append(", linkageGroup=");
		builder.append(this.linkageGroup);
		builder.append(", mapName=");
		builder.append(this.mapName);
		builder.append(", mapType=");
		builder.append(this.mapType);
		builder.append(", mapDescription=");
		builder.append(this.mapDescription);
		builder.append(", mapUnit=");
		builder.append(this.mapUnit);
		builder.append("]");
		return builder.toString();
	}

}
