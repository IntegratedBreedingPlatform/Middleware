package org.generationcp.middleware.api.brapi.v2.observationunit;

public class ObservationLevelRelationship {

	private String levelCode;
	private String levelName;
	private Integer levelOrder;

	public ObservationLevelRelationship() {
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(final String levelCode) {
		this.levelCode = levelCode;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(final String levelName) {
		this.levelName = levelName;
	}

	public Integer getLevelOrder() {
		return levelOrder;
	}

	public void setLevelOrder(final Integer levelOrder) {
		this.levelOrder = levelOrder;
	}
}
