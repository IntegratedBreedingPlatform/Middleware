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


/**
 * The primary identifier of Observation.
 * 
 */
public class ObservationKey{

    private int traitId;
    
    private int germplasmId;
    
    private int environmentId;

    public ObservationKey(int traitId, int germplasmId, int environmentId) {
        super();
        this.traitId = traitId;
        this.germplasmId = germplasmId;
        this.environmentId = environmentId;
    }

    public int getTraitId() {
        return traitId;
    }
    
    public void setTraitId(int traitId) {
        this.traitId = traitId;
    }
    
    public int getGermplasmId() {
        return germplasmId;
    }
    
    public void setGermplasmId(int germplasmId) {
        this.germplasmId = germplasmId;
    }
    
    public int getEnvironmentId() {
        return environmentId;
    }
    
    public void setEnvironmentId(int environmentId) {
        this.environmentId = environmentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + environmentId;
        result = prime * result + germplasmId;
        result = prime * result + traitId;
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
        ObservationKey other = (ObservationKey) obj;
        if (environmentId != other.environmentId)
            return false;
        if (germplasmId != other.germplasmId)
            return false;
        if (traitId != other.traitId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ObservationKey [traitId=");
        builder.append(traitId);
        builder.append(", germplasmId=");
        builder.append(germplasmId);
        builder.append(", environmentId=");
        builder.append(environmentId);
        builder.append("]");
        return builder.toString();
    }
    
}
