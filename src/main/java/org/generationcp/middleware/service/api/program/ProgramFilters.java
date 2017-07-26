package org.generationcp.middleware.service.api.program;

public enum ProgramFilters {
	PROGRAM_NAME ("projectName"),
	CROP_TYPE("cropType");

	private final String statement;


	public String getStatement() {
		return statement;
	}

	ProgramFilters(final String statement){
		this.statement = statement;
	}
}
