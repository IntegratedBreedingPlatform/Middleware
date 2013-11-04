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
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;

public class TraitGroupBuilder extends Builder {

	public TraitGroupBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

    /** 
     * Gets the Trait Classes with properties and standard variables in a hierarchy 
     * from both Central and Local databases based on the given class type.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
	@Deprecated
	public List<TraitClassReference> buildTraitGroupHierarchy(TermId classType) throws MiddlewareQueryException {

        // Step 1: Get all Trait Classes from Central and Local
	    List<TraitClassReference> traitClasses = getTraitClasses(classType);
	    	    
        // Step 2: Get all Trait Class Properties from Central and Local
        setPropertiesOfTraitClasses(Database.CENTRAL, traitClasses);
        setPropertiesOfTraitClasses(Database.LOCAL, traitClasses);

        // Step 3: Get all StandardVariables of Properties from Central and Local
        for (TraitClassReference traitClass : traitClasses){
            setStandardVariablesOfProperties(Database.CENTRAL, traitClass.getProperties());
            setStandardVariablesOfProperties(Database.LOCAL, traitClass.getProperties());
        }

        // Step 4: Build and sort tree
//        traitClasses = buildTree(traitClasses, classType.getId());
//        sortTree(traitClasses);
        

	    return traitClasses;
	}
	
    /** 
     * Gets all the Trait Classes with properties and standard variables in a hierarchical structure 
     * from both Central and Local databases
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    public List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariables) throws MiddlewareQueryException {

        // Step 1: Get all Trait Classes from Central and Local
        List<TraitClassReference> traitClasses = getAllTraitClasses();
        
        if(includePropertiesAndVariables){
            // Step 2: Get all Trait Class Properties from Central and Local
            setPropertiesOfTraitClasses(Database.CENTRAL, traitClasses);
            setPropertiesOfTraitClasses(Database.LOCAL, traitClasses);
    
            // Step 3: Get all StandardVariables of Properties from Central and Local
            for (TraitClassReference traitClass : traitClasses){
                setStandardVariablesOfProperties(Database.CENTRAL, traitClass.getProperties());
                setStandardVariablesOfProperties(Database.LOCAL, traitClass.getProperties());
            }
        }

        // Step 4: Build and sort tree
        traitClasses = buildTree(traitClasses, TermId.IBDB_CLASS.getId());
        sortTree(traitClasses);
        
        return traitClasses;
    }        
    
    
    /** 
     * Gets all the Trait Classes in a hierarchical structure from both Central and Local databases
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    @Deprecated
    public List<TraitClassReference> getAllTraitClassesHierarchy() throws MiddlewareQueryException {
        List<TraitClassReference> traitClasses = getAllTraitClasses();
        traitClasses = buildTree(traitClasses, TermId.IBDB_CLASS.getId());
        sortTree(traitClasses);
        return traitClasses;
    }
	
    /** 
     * Gets all Trait Classes in a flat table form from Central and Local
     * 
     * @return
     * @throws MiddlewareQueryException
     */
	private List<TraitClassReference> getAllTraitClasses() throws MiddlewareQueryException {
	    List<TraitClassReference> traitClasses = new ArrayList<TraitClassReference>();
        setWorkingDatabase(Database.CENTRAL);
        traitClasses.addAll(getCvTermDao().getAllTraitClasses());  
        setWorkingDatabase(Database.LOCAL);
        traitClasses.addAll(getCvTermDao().getAllTraitClasses());  
        Collections.sort(traitClasses);
        return traitClasses;
	}
	
	
    private List<TraitClassReference> buildTree(List<TraitClassReference> traitClasses, int parentTraitClassId){
        List<TraitClassReference> childrenTraitClasses = new ArrayList<TraitClassReference>();
        for (TraitClassReference traitClass : traitClasses){
            if (traitClass.getParentTraitClassId() == parentTraitClassId){
                traitClass.setTraitClassChildren(buildTree(traitClasses, traitClass.getId()) );
                childrenTraitClasses.add(traitClass);
            }
        }
        return childrenTraitClasses;
    }

    private void sortTree(List<TraitClassReference> traitClasses){
        for (TraitClassReference traitClass : traitClasses){
            sortChildren(traitClass);
        }
    }
    
    private void sortChildren(TraitClassReference traitClass){
        traitClass.sortTraitClassChildren();
        for (TraitClassReference child : traitClass.getTraitClassChildren()){
            sortChildren(child);
        }
    }
    
	/**
	 * Gets trait classes from central and local of the given class type
	 * 
	 * @param classType
	 * @return
	 * @throws MiddlewareQueryException
	 */
    @Deprecated
	public List<TraitClassReference> getTraitClasses(TermId classType) throws MiddlewareQueryException {
        List<TraitClassReference> traitClasses = new ArrayList<TraitClassReference>();
        setWorkingDatabase(Database.CENTRAL);
        traitClasses.addAll(getCvTermDao().getTraitClasses(classType));  
        setWorkingDatabase(Database.LOCAL);
        traitClasses.addAll(getCvTermDao().getTraitClasses(classType));  
        Collections.sort(traitClasses);
        return traitClasses;
    }

    private void setPropertiesOfTraitClasses(Database instance, List<TraitClassReference> traitClasses) throws MiddlewareQueryException{
        
        setWorkingDatabase(instance);
        
        List<Integer> traitClassIds = new ArrayList<Integer>();
        for (TraitClassReference traitClass : traitClasses){
            traitClassIds.add(traitClass.getId());
        }
        Collections.sort(traitClassIds);
        
        Map<Integer, List<PropertyReference>> retrievedProperties = getCvTermDao().getPropertiesOfTraitClasses(traitClassIds);
        
        if (!retrievedProperties.isEmpty()){
            for (TraitClassReference traitClass : traitClasses){
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
