package org.generationcp.middleware.v2.pojos;

public enum StudyPropertyConstants {

	USER_ID (CVTermId.STUDY_UID.getId(), "USERID", "User ID")
	, STUDY_TYPE (CVTermId.STUDY_TYPE.getId(), "TYPE", "Study type")
	, START_DATE (CVTermId.START_DATE.getId(), "START", "Start date")
	;
	
	private Integer cvTermId;
	private String name;
	private String description;
	
	StudyPropertyConstants(Integer cvTermId, String name, String description) {
		this.cvTermId = cvTermId;
		this.name = name;
		this.description = description;
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
}
