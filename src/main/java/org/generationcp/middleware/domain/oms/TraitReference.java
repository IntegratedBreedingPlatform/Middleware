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
package org.generationcp.middleware.domain.oms;

import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;


/**
 * Contains the primary details of a trait class - id, name, description, plus the list of properties 
 * 
 * @author Joyce Avestro
 *
 */
public class TraitReference extends Reference {
    
    private List<PropertyReference> properties;

	
	public TraitReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
	}

	public TraitReference(Integer id, String name, String description) {
		super.setId(id);
		super.setName(name);
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
}
