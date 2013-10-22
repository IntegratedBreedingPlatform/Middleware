/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.oms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;


/**
 * Contains the primary details of a trait class - id, name, description, plus the list of properties 
 * 
 * @author Joyce Avestro
 *
 */
public class TraitReference extends Reference implements Comparable<TraitReference>{
    
    private List<PropertyReference> properties;

    public TraitReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
		properties = new ArrayList<PropertyReference>();
	}

	public TraitReference(Integer id, String name, String description) {
	    this(id, name);
		super.setDescription(description);
	}

    /**
     * @return the properties
     */
    public List<PropertyReference> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<PropertyReference> properties) {
        this.properties = properties;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TraitReference [id=");
        builder.append(getId());
        builder.append(", name=");
        builder.append(getName());
        builder.append(", description=");
        builder.append(getDescription());
        builder.append(", properties=");
        builder.append(properties);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent){
        Debug.println(indent, "TraitReference: ");
        Debug.println(indent + 3, "Id: " + getId());
        Debug.println(indent + 3, "Name: " + getName());
        Debug.println(indent + 3, "Description: " + getDescription());
        Debug.println(indent + 3, "Properties: ");
        for (PropertyReference property : properties){
            property.print(indent + 6);
        }
        if (properties.isEmpty()){
            Debug.println(indent + 6, "None");
        }
    }
    
    /* (non-Javadoc)
     * Sort in ascending order by trait group name
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TraitReference compareValue) {
        String compareName = ((TraitReference) compareValue).getName(); 
        return getName().compareTo(compareName);
    }

}
