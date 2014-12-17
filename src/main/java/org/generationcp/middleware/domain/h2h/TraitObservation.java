/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.domain.h2h;

import org.generationcp.middleware.util.Debug;

public class TraitObservation {
	private int id; // trait_id
	private String traitValue; 
	private int gid ;
	private int observationId; //  nd_experiment id
	private String locationName; // name of the location where the value was observed
	private Integer locationId;
	
	public TraitObservation(int id, String traitValue) {
		this.id = id;
		this.traitValue = traitValue;
	}
	
	public TraitObservation(int id, String traitValue, int gid, int observationId, String locationName, Integer locationId ){
		this.id = id;
		this.traitValue = traitValue;
		this.gid = gid;
		this.observationId = observationId;
		this.locationName = locationName;
		this.locationId = locationId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTraitValue() {
		return traitValue;
	}

	public void setTraitValue(String traitValue) {
		this.traitValue = traitValue;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getObservationId() {
		return observationId;
	}

	public void setObservationId(int observationId) {
		this.observationId = observationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
    public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TraitObservation [");
        builder.append("id=");
        builder.append(id);
        builder.append(", traitValue=");
        builder.append(traitValue);
        builder.append(", gid=");
        builder.append(gid);
        builder.append(", observationId=");
        builder.append(observationId);
        builder.append(", locationName=");
        builder.append(locationName);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append("]");
        return builder.toString();
    }
    
    public void print(int indent){
        Debug.println(indent, getId() + ":");
        Debug.println(indent + 3, "Trait Value: " + getTraitValue());
        Debug.println(indent + 3, "GID: " + getGid());
        Debug.println(indent + 3, "Observation ID: " + getObservationId());
        Debug.println(indent + 3, "Location Name: " + getLocationName());
    }
    
    

}
