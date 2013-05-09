package org.generationcp.middleware.v2.domain;

public enum CvId {

	//Ontology
	METHODS(1020)
	,VARIABLES(1040)
	;
	
	private final int id;
	
	private CvId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}
