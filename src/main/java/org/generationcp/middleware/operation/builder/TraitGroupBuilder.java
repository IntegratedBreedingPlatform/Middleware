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
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;

public class TraitGroupBuilder extends Builder {

	public TraitGroupBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

    /** 
     * Gets all Trait Classes with properties and standard variables in a hierarchy from both Central and Local databases
     * 
     * @return
     * @throws MiddlewareQueryException
     */

	public List<TraitReference> buildTraitGroupHierarchy() throws MiddlewareQueryException {

        // Step 1: Get all Trait Classes from Central and Local
	    List<TraitReference> traitClasses = getAllTraitClasses();
	    
        // Step 2: Get all Trait Class Properties from Central and Local
        setPropertiesOfTraitClasses(Database.CENTRAL, traitClasses);
        setPropertiesOfTraitClasses(Database.LOCAL, traitClasses);

        // Step 3: Get all StandardVariables of Properties from Central and Local
        for (TraitReference traitClass : traitClasses){
            setStandardVariablesOfProperties(Database.CENTRAL, traitClass.getProperties());
            setStandardVariablesOfProperties(Database.LOCAL, traitClass.getProperties());
        }

	    return traitClasses;
	}
	
    /** 
     * Gets all Trait Classes from Central and Local
     * 
     * @return
     * @throws MiddlewareQueryException
     */
	public List<TraitReference> getAllTraitClasses() throws MiddlewareQueryException {
        List<TraitReference> traitClasses = new ArrayList<TraitReference>();
        traitClasses.addAll(getTraitClasses(Database.CENTRAL));
        traitClasses.addAll(getTraitClasses(Database.LOCAL));
        Collections.sort(traitClasses);
        return traitClasses;
	}
	
	
    private List<TraitReference> getTraitClasses(Database instance) throws MiddlewareQueryException{
        setWorkingDatabase(instance);
        return getCvTermDao().getTraitClasses();
    }

    private void setPropertiesOfTraitClasses(Database instance, List<TraitReference> traitClasses) throws MiddlewareQueryException{
        
        setWorkingDatabase(instance);
        
        List<Integer> traitClassIds = new ArrayList<Integer>();
        for (TraitReference traitClass : traitClasses){
            traitClassIds.add(traitClass.getId());
        }
        Collections.sort(traitClassIds);
        
        Map<Integer, List<PropertyReference>> retrievedProperties = getCvTermDao().getPropertiesOfTraitClasses(traitClassIds);
        
        if (!retrievedProperties.isEmpty()){
            for (TraitReference traitClass : traitClasses){
                List<PropertyReference> traitClassProperties = traitClass.getProperties();
                if (traitClassProperties != null && retrievedProperties.get(traitClass.getId()) != null){
                    traitClassProperties.addAll(retrievedProperties.get(traitClass.getId()));
                    traitClass.setProperties(traitClassProperties);
                }
                Collections.sort(traitClass.getProperties());
            }
        }

    }

    private void setStandardVariablesOfProperties(Database instance, List<PropertyReference> traitClassProperties) throws MiddlewareQueryException{
        setWorkingDatabase(instance);
        
        List<Integer> propertyIds = new ArrayList<Integer>();
        for (PropertyReference property : traitClassProperties){
            propertyIds.add(property.getId());
        }
        Collections.sort(propertyIds);
        
        Map<Integer, List<StandardVariableReference>> retrievedVariables = getCvTermDao().getStandardVariablesOfProperties(propertyIds);

        if (!retrievedVariables.isEmpty()){
            for (PropertyReference property : traitClassProperties){
                List<StandardVariableReference> propertyVariables = property.getStandardVariables();
                if (propertyVariables != null && retrievedVariables.get(property.getId()) != null){
                    propertyVariables.addAll(retrievedVariables.get(property.getId()));
                    property.setStandardVariables(propertyVariables);
                }
                Collections.sort(property.getStandardVariables());
            }
        }

    }

    
}
