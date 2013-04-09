package org.generationcp.middleware.pojos.dms;

public enum CVTermId {

	//Standard Variable
	STANDARD_VARIABLE(1070L)
	, STUDY_INFORMATION(1010L)
	, VARIABLE_DESCRIPTION(1060L)
	, GERMPLASM_ENTRY(1040L)
	, TRIAL_DESIGN_INFO(1030L)
	, TRIAL_ENVIRONMENT_INFO(1020L)
	
	//CV Term Relationship
	, HAS_METHOD(1210L)
	, HAS_PROPERTY(1200L)
	, HAS_SCALE(1220L)
	, HAS_TYPE(1105L)
	, HAS_VALUE(1190L)
	, IS_A(1225L)
	, STORED_IN(1244L)
	
	//Study Fields
	, STUDY_NAME(8005L)
	, PM_KEY(8040L)
	, STUDY_TITLE(8007L)
	, STUDY_OBJECTIVE(8030L)
	, PI_ID(8110L)
	, STUDY_TYPE(8070L)
	, START_DATE(8050L)
	, END_DATE(8060L)
	, STUDY_UID(8020L)
	, STUDY_IP(8120L)
	, RELEASE_DATE(8130L)
	
	// Numeric Data Fields
	, NUMERIC_VARIABLE(1110L)
	, DATE_VARIABLE(1117L)
	, NUMERIC_DBID_VARIABLE(1118L)
	, CHARACTER_DBID_VARIABLE(1128L)	
	;
	
	private final Long id;
	
	private CVTermId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return this.id;
	}
	
}
