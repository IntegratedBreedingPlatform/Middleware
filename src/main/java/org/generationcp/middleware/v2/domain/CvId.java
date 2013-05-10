package org.generationcp.middleware.v2.domain;

public enum CvId {

	//Ontology
	IBDB_TERMS(1000)
	,PROPERTIES(1010)
	,METHODS(1020)
	,SCALES(1030)
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
