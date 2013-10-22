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

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;


/**
 * Contains the primary details of a trait class - id, name and description 
 * 
 * @author Joyce Avestro
 *
 */
public class StandardVariableReference extends Reference implements Comparable<StandardVariableReference>{
	
	public StandardVariableReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
	}

	public StandardVariableReference(Integer id, String name, String description) {
		super.setId(id);
		super.setName(name);
		super.setDescription(description);
	}


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StandardVariableReference [id=");
        builder.append(getId());
        builder.append(", name=");
        builder.append(getName());
        builder.append(", description=");
        builder.append(getDescription());
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent){
        Debug.println(indent, "StandardVariableReference: ");
        Debug.println(indent + 3, "Id: " + getId());
        Debug.println(indent + 3, "Name: " + getName());
        Debug.println(indent + 3, "Description: " + getDescription());
    }
	
    /* (non-Javadoc)
     * Sort in ascending order by standard variable name
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(StandardVariableReference compareValue) {
        String compareName = ((StandardVariableReference) compareValue).getName(); 
        return getName().compareTo(compareName);
    }
    
}
