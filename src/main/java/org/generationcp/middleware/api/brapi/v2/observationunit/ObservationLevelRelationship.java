package org.generationcp.middleware.api.brapi.v2.observationunit;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ObservationLevelRelationship {

	@JsonIgnore
	private Integer experimentId;
	private String levelCode;
	private String levelName;
	private Integer levelOrder;

	public ObservationLevelRelationship() {
	}

	public ObservationLevelRelationship(final Integer experimentId, final String levelCode, final String levelName, final Integer levelOrder) {
		this.experimentId = experimentId;
		this.levelCode = levelCode;
		this.levelName = levelName;
		this.levelOrder = levelOrder;
	}

	public Integer getExperimentId() {
		return this.experimentId;
	}

	public void setExperimentId(final Integer experimentId) {
		this.experimentId = experimentId;
	}

	public String getLevelCode() {
		return this.levelCode;
	}

	public void setLevelCode(final String levelCode) {
		this.levelCode = levelCode;
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(final String levelName) {
		this.levelName = levelName;
	}

	public Integer getLevelOrder() {
		return this.levelOrder;
	}

	public void setLevelOrder(final Integer levelOrder) {
		this.levelOrder = levelOrder;
	}
}
