package org.generationcp.middleware.service.api.study;


public enum StudyFilters {

	PROGRAM_ID("programUUID"), LOCATION_ID("LOCATION_ID"), SORT_BY_FIELD("SORT_BY_FIELD"), ORDER("ORDER");

	private final String parameter;

	public String getParameter() {
		return parameter;
	}

	StudyFilters(final String parameter){
		this.parameter = parameter;
	}
}
