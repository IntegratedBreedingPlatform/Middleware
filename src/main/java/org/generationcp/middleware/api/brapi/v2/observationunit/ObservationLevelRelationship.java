package org.generationcp.middleware.api.brapi.v2.observationunit;

public class ObservationLevelRelationship {

	private String levelCode;
	private String levelName;
	private Integer levelOrder;

	public ObservationLevelRelationship() {
	}

	public ObservationLevelRelationship(final String levelCode, final String levelName, final Integer levelOrder) {
		this.levelCode = levelCode;
		this.levelName = levelName;
		this.levelOrder = levelOrder;
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
