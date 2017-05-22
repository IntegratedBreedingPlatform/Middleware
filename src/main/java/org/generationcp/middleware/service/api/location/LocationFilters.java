
package org.generationcp.middleware.service.api.location;

public enum LocationFilters {

	LOCATION_TYPE (" l.ltype ","LTYPE"),
	LOCATION_ID  (" l.locid ","LOCID"),
	LOCATION_NAME (" l.lname ","LNAME");
	
	private final String statement;
	private final String parameter;

	
	public String getStatement() {
		return statement;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	LocationFilters(final String statement,final String parameter){
		this.statement=statement;
		this.parameter=parameter;
	}
	
}
