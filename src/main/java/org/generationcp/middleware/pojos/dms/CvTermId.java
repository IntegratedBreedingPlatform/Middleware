package org.generationcp.middleware.pojos.dms;

public enum CvTermId {

	//Standard Variable
	STANDARD_VARIABLE(1070L)
	, STUDY_INFORMATION(1010L)
	, VARIABLE_DESCRIPTION(1060L)
	
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
	;
	
	private final Long id;
	
	private CvTermId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return this.id;
	}
	
		
	public static CvTermId toCVTermId(Long id) {
		
		for (CvTermId field : CvTermId.values()) {
			if (field.getId().equals(id)) {
				return field;
			}
		}
		return null;
	}
}
