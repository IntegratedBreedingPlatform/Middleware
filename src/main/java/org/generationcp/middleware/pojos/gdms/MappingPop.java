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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_mapping_pop table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_mapping_pop")
public class MappingPop implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "dataset_id")
	private Integer datasetId;

	private ParentElement parent;

	@Column(name = "population_size")
	private Integer populationSize;

	@Column(name = "population_type")
	private String populationType;

	@Column(name = "mapdata_desc")
	private String mapDataDescription;

	@Column(name = "scoring_scheme")
	private String scoringScheme;

	@Column(name = "map_id")
	private Integer mapId;

	public MappingPop() {
	}

	public MappingPop(Integer datasetId, String mappingType, Integer parentANId, Integer parentBNId, Integer populationSize,
			String populationType, String mapDataDescription, String scoringScheme, Integer mapId) {
		this.datasetId = datasetId;
		this.parent = new ParentElement(parentANId, parentBNId, mappingType);
		this.populationSize = populationSize;
		this.populationType = populationType;
		this.mapDataDescription = mapDataDescription;
		this.scoringScheme = scoringScheme;
		this.mapId = mapId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public ParentElement getParent() {
		return this.parent;
	}

	public void setParent(ParentElement parent) {
		this.parent = parent;
	}

	public String getMappingType() {
		return this.parent.getMappingType();
	}

	public void setMappingType(String mappingType) {
		this.parent.setMappingType(mappingType);
	}

	public Integer getParentANId() {
		return this.parent.getParentANId();
	}

	public void setParentANId(Integer parentANId) {
		this.parent.setParentANId(parentANId);
	}

	public Integer getParentBNId() {
		return this.parent.getParentBGId();
	}

	public void setParentBGId(Integer parentBGId) {
		this.parent.setParentBGId(parentBGId);
	}

	public Integer getPopulationSize() {
		return this.populationSize;
	}

	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	public String getPopulationType() {
		return this.populationType;
	}

	public void setPopulationType(String populationType) {
		this.populationType = populationType;
	}

	public String getMapDataDescription() {
		return this.mapDataDescription;
	}

	public void setMapDataDescription(String mapDataDescription) {
		this.mapDataDescription = mapDataDescription;
	}

	public String getScoringScheme() {
		return this.scoringScheme;
	}

	public void setScoringScheme(String scoringScheme) {
		this.scoringScheme = scoringScheme;
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
		if (!(obj instanceof MappingPop)) {
			return false;
		}

		MappingPop rhs = (MappingPop) obj;
		return new EqualsBuilder().append(this.datasetId, rhs.datasetId).append(this.parent, rhs.parent)
				.append(this.populationSize, rhs.populationSize).append(this.populationType, rhs.populationType)
				.append(this.mapDataDescription, rhs.mapDataDescription).append(this.scoringScheme, rhs.scoringScheme)
				.append(this.mapId, rhs.mapId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(97, 311).append(this.datasetId).append(this.parent).append(this.populationSize)
				.append(this.populationType).append(this.mapDataDescription).append(this.scoringScheme).append(this.mapId).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingPop [datasetId=");
		builder.append(this.datasetId);
		builder.append(", parent=");
		builder.append(this.parent);
		builder.append(", populationSize=");
		builder.append(this.populationSize);
		builder.append(", populationType=");
		builder.append(this.populationType);
		builder.append(", mapDataDescription=");
		builder.append(this.mapDataDescription);
		builder.append(", scoringScheme=");
		builder.append(this.scoringScheme);
		builder.append(", mapId=");
		builder.append(this.mapId);
		builder.append("]");
		return builder.toString();
	}

}
