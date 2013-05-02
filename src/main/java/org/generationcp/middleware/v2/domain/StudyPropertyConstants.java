package org.generationcp.middleware.v2.domain;


public enum StudyPropertyConstants {

	USER_ID (CVTermId.STUDY_UID.getId(), "USERID", "User ID", 1)
	, STUDY_TYPE (CVTermId.STUDY_TYPE.getId(), "TYPE", "Study type", 2)
	, START_DATE (CVTermId.START_DATE.getId(), "START", "Start date", 3)
	;
	
	private Integer cvTermId;
	private String name;
	private String description;
	private Integer rank;
	
	StudyPropertyConstants(Integer cvTermId, String name, String description, Integer rank) {
		this.cvTermId = cvTermId;
		this.name = name;
		this.description = description;
		this.rank = rank;
	}
	
	public Integer getCvTermId() {
		return this.cvTermId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public Integer getRank() {
		return this.rank;
	}
	
	
}
