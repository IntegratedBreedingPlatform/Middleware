
package org.generationcp.middleware.service.api.location;

public enum LocationFilters {

	LOCATION_TYPE(" l.ltype ","Long"),
	LOCATIONS_TYPE(" l.ltype ","Long[]"),
	LOCATIONS_ID  (" l.locid ", "Long"),
	LOCATIONS_IDS  (" l.locid ", "Long[]"),
	LOCATION_NAME(" l.lname ","String"),
	LOCATIONS_NAME(" l.lname ","String[]");
	
	private final String statement;
	private final String dataType;
	
	
	public String getStatement() {
		return statement;
	}

	
	public String getDataType() {
		return dataType;
	}

	LocationFilters(final String statement, final String dataType){
		this.statement=statement;
		this.dataType=dataType;
	}
	
}
