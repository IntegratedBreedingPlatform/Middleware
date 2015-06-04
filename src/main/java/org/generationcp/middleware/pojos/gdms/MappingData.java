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
public class MappingData implements Serializable {

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

	public MappingData(Integer markerId, String linkageGroup, Float startPosition, String mapUnit, String mapName, String markerName,
			Integer mapId) {
		this.markerId = markerId;
		this.linkageGroup = linkageGroup;
		this.startPosition = startPosition;
		this.mapUnit = mapUnit;
		this.mapName = mapName;
		this.markerName = markerName;
		this.mapId = mapId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getLinkageGroup() {
		return this.linkageGroup;
	}

	public void setLinkageGroup(String linkageGroup) {
		this.linkageGroup = linkageGroup;
	}

	public float getStartPosition() {
		return this.startPosition;
	}

	public void setStartPosition(float startPosition) {
		this.startPosition = startPosition;
	}

	public String getMapUnit() {
		return this.mapUnit;
	}

	public void setMapUnit(String mapUnit) {
		this.mapUnit = mapUnit;
	}

	public String getMapName() {
		return this.mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMarkerName() {
		return this.markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public Integer getMapId() {
		return this.mapId;
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
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.markerId, rhs.markerId)
				.append(this.linkageGroup, rhs.linkageGroup).append(this.startPosition, rhs.startPosition)
				.append(this.mapUnit, rhs.mapUnit).append(this.mapName, rhs.mapName).append(this.markerName, rhs.markerName).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 71).append(this.markerId).append(this.linkageGroup).append(this.startPosition).append(this.mapUnit)
				.append(this.mapName).append(this.markerName).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingData [markerId=");
		builder.append(this.markerId);
		builder.append(", linkageGroup=");
		builder.append(this.linkageGroup);
		builder.append(", startPosition=");
		builder.append(this.startPosition);
		builder.append(", mapUnit=");
		builder.append(this.mapUnit);
		builder.append(", mapName=");
		builder.append(this.mapName);
		builder.append(", markerName=");
		builder.append(this.markerName);
		builder.append(", mapId=");
		builder.append(this.mapId);
		builder.append("]");
		return builder.toString();
	}

}
