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


/**
 * Contains the details of a trait - name, id, description, 
 * number of locations, germplasms and observations.
 */
public class TraitInfo  implements Comparable<TraitInfo>{
    
    private int id;
    
    private String name;
    
    private String description;
    
    private long locationCount;
    
    private long germplasmCount;
    
    private long observationCount;
    
    private TraitType type;
    
    public TraitInfo() {
    }

    public TraitInfo( int id, String name){
        this.id = id;
        this.name = name;
    }
        
    public TraitInfo( int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }
        
    public TraitInfo( int id, String name, String description,
            long locationCount, long germplasmCount, long observationCount) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.locationCount = locationCount;
        this.germplasmCount = germplasmCount;
        this.observationCount = observationCount;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getLocationCount() {
        return locationCount;
    }
    
    public void setLocationCount(long locationCount) {
        this.locationCount = locationCount;
    }
    
    public long getGermplasmCount() {
        return germplasmCount;
    }
    
    public void setGermplasmCount(long germplasmCount) {
        this.germplasmCount = germplasmCount;
    }
    
    public long getObservationCount() {
        return observationCount;
    }
    
    public void setType(TraitType type) {
        this.type = type;
    }

    public TraitType getType() {
    	//TODO set the type. return numeric for now only
    	type = TraitType.NUMERIC;
        return type;
    }
    
    public void setObservationCount(long observationCount) {
        this.observationCount = observationCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + (int) (germplasmCount ^ (germplasmCount >>> 32));
        result = prime * result
                + (int) (locationCount ^ (locationCount >>> 32));
        result = prime * result
                + (int) (observationCount ^ (observationCount >>> 32));
        result = prime * result + id;
        result = prime * result
                + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TraitInfo other = (TraitInfo) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TraitInfo [");
        builder.append("traitId=");
        builder.append(id);
        builder.append(", traitName=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", locationCount=");
        builder.append(locationCount);
        builder.append(", germplasmCount=");
        builder.append(germplasmCount);
        builder.append(", observationCount=");
        builder.append(observationCount);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }
    
    public void print(int indent){
        Debug.println(indent, getEntityName() + ":");
        Debug.println(indent + 3, "Trait Id: " + getId());
        Debug.println(indent + 3, "Trait Name: " + getName());
        Debug.println(indent + 3, "Description: " + getDescription());
        Debug.println(indent + 3, "Location Count: " + getLocationCount());
        Debug.println(indent + 3, "Germplasm Count: " + getGermplasmCount());
        Debug.println(indent + 3, "Observation Count: " + getObservationCount());
        Debug.println(indent + 3, "Trait Type: " + getType());
    }
    
    private String getEntityName() {
        return this.getClass().getName();
    }

    @Override
    // Sort in ascending order by trait id
    public int compareTo(TraitInfo compareValue) { 
        int compareId = ((TraitInfo) compareValue).getId(); 
        return Integer.valueOf(getId()).compareTo(compareId);
    }
    


}
