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
    
    private String traitName;
    
    private int traitId;
    
    private String description;
    
    private long locationCount;
    
    private long germplasmCount;
    
    private long observationCount;
    
    public TraitInfo() {
    }

    public TraitInfo(String traitName, int traitId, String description,
            long locationCount, long germplasmCount, long observationCount) {
        super();
        this.traitName = traitName;
        this.traitId = traitId;
        this.description = description;
        this.locationCount = locationCount;
        this.germplasmCount = germplasmCount;
        this.observationCount = observationCount;
    }

    public String getTraitName() {
        return traitName;
    }
    
    public void setTraitName(String traitName) {
        this.traitName = traitName;
    }
    
    public int getTraitId() {
        return traitId;
    }
    
    public void setTraitId(int traitId) {
        this.traitId = traitId;
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
        result = prime * result + traitId;
        result = prime * result
                + ((traitName == null) ? 0 : traitName.hashCode());
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
        if (traitId != other.traitId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TraitInfo [traitName=");
        builder.append(traitName);
        builder.append(", traitId=");
        builder.append(traitId);
        builder.append(", description=");
        builder.append(description);
        builder.append(", locationCount=");
        builder.append(locationCount);
        builder.append(", germplasmCount=");
        builder.append(germplasmCount);
        builder.append(", observationCount=");
        builder.append(observationCount);
        builder.append("]");
        return builder.toString();
    }
    
    public void print(int indent){
        Debug.println(indent, toString());
    }

    @Override
    // Sort in ascending order by trait id
    public int compareTo(TraitInfo compareValue) { 
        int compareId = ((TraitInfo) compareValue).getTraitId(); 
        return Integer.valueOf(getTraitId()).compareTo(compareId);
    }

}
