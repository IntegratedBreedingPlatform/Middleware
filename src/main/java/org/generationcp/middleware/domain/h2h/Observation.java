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


public class Observation{

    private ObservationKey id;
    
    private String value;

    public Observation(ObservationKey id) {
        this.id = id;
        this.value = null;
    }
    
    public Observation(ObservationKey id, String value) {
        this.id = id;
        this.value = value;
    }

    public ObservationKey getId() {
        return id;
    }
    
    public void setId(ObservationKey id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Observation other = (Observation) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Observation [id=");
        builder.append(id);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }
    
    public void print(int indent){
        Debug.println(indent, "Observation: ");
        id.print(indent + 3);
        Debug.println(indent + 3, "Value: " + getValue());        
    }
    
}
