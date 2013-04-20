package org.generationcp.middleware.v2.domain;

public enum NameType {

	ALTERNATIVE_ENGLISH(1230),
	PREFERRED_FRENCH(1240),
	ALTERNATIVE_FRENCH(1250);
    
	private int id;

	private NameType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
