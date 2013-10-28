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
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

public class StandardVariableBuilder extends Builder {

	
	public StandardVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	// If the standard variable is already in the cache, return. Else, create the variable, add to cache then return
	public StandardVariable create(int standardVariableId) throws MiddlewareQueryException {

		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(standardVariableId);
		CVTerm cvTerm = getCvTerm(standardVariableId);
		if (cvTerm != null) {
			standardVariable.setName(cvTerm.getName());
			standardVariable.setDescription(cvTerm.getDefinition());
			
			addConstraints(standardVariable, cvTerm);
			addRelatedTerms(standardVariable, cvTerm);
			
			if (standardVariable.getProperty() != null) {
			    standardVariable.setCropOntologyId(getCropOntologyId(standardVariable.getProperty()));
			}
		}
		return standardVariable;
	}

	public List<StandardVariable> create(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		
		List<CVTerm> cvTerms = getCvTerms(standardVariableIds);
		for (CVTerm cvTerm : cvTerms){
			if (cvTerm != null) {
				StandardVariable standardVariable = new StandardVariable();
				standardVariable.setId(cvTerm.getCvTermId());
				standardVariable.setName(cvTerm.getName());
				standardVariable.setDescription(cvTerm.getDefinition());
				addConstraints(standardVariable, cvTerm);
				addRelatedTerms(standardVariable, cvTerm);
	            if (standardVariable.getProperty() != null) {
	                standardVariable.setCropOntologyId(getCropOntologyId(standardVariable.getProperty()));
	            }

	            standardVariables.add(standardVariable);
			}
		}
		return standardVariables;
	}

	private void addRelatedTerms(StandardVariable standardVariable, CVTerm cvTerm) throws MiddlewareQueryException {
		if (setWorkingDatabase(standardVariable.getId())) {
			List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(standardVariable.getId());
			standardVariable.setProperty(createTerm(cvTermRelationships, TermId.HAS_PROPERTY));	
			standardVariable.setMethod(createTerm(cvTermRelationships, TermId.HAS_METHOD));
			standardVariable.setScale(createTerm(cvTermRelationships, TermId.HAS_SCALE));
			standardVariable.setDataType(createTerm(cvTermRelationships, TermId.HAS_TYPE));
			standardVariable.setStoredIn(createTerm(cvTermRelationships, TermId.STORED_IN));
			standardVariable.setIsA(createTerm(cvTermRelationships, TermId.IS_A));
			//add handling of null isA
			if(standardVariable.getIsA()==null) {
				//get isA of property
				List<CVTermRelationship> propertyCvTermRelationships = 
						getCvTermRelationshipDao().getBySubject(standardVariable.getProperty().getId());
				standardVariable.setIsA(createTerm(propertyCvTermRelationships, TermId.IS_A));
			}
			standardVariable.setPhenotypicType(createPhenotypicType(standardVariable.getStoredIn().getId()));
			addEnumerations(standardVariable, cvTermRelationships);
		}
	}

	private void addEnumerations(StandardVariable standardVariable, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
		if (hasEnumerations(cvTermRelationships)) {
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					Integer id = cvTermRelationship.getObjectId();
					enumerations.add(createEnumeration(getCvTerm(id)));
				}
			}
			Collections.sort(enumerations);
			standardVariable.setEnumerations(enumerations);
		}
	}

	private Enumeration createEnumeration(CVTerm cvTerm) {
		return new Enumeration(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition(), getRank(cvTerm));
	}

	private int getRank(CVTerm cvTerm) {
		CVTermProperty property = findProperty(cvTerm.getProperties(), TermId.ORDER.getId());
		if (property != null) {
			return Integer.parseInt(property.getValue());
		}
		return 0;
	}
	
	private String getCropOntologyId(Term term) {
	    if (term != null && term.getProperties() != null && term.getProperties().size() > 0) {
	        for (TermProperty termProperty : term.getProperties()) {
	            if (TermId.CROP_ONTOLOGY_ID.getId() == termProperty.getTypeId()) {
	                return termProperty.getValue();
	            }
	        }
	    }
	    return null;
	}

	private CVTermProperty findProperty(List<CVTermProperty> properties, int typeId) {
		if (properties != null) {
			for (CVTermProperty property : properties) {
				if (property.getTypeId() == typeId) {
					return property;
				}
			}
		}
		return null;
	}

	private boolean hasEnumerations(List<CVTermRelationship> cvTermRelationships) {
		return findTermId(cvTermRelationships, TermId.HAS_VALUE) != null;
	}

	private void addConstraints(StandardVariable standardVariable, CVTerm cvTerm) {
		if (cvTerm.getProperties() != null && !cvTerm.getProperties().isEmpty()) {
			Integer minValue = getPropertyValue(cvTerm.getProperties(), TermId.MIN_VALUE);
			Integer maxValue = getPropertyValue(cvTerm.getProperties(), TermId.MAX_VALUE);
			if (minValue != null || maxValue != null) {
				standardVariable.setConstraints(new VariableConstraints(minValue, maxValue));
			}
		}
	}

	private Integer getPropertyValue(List<CVTermProperty> properties, TermId termId) {
		for (CVTermProperty property : properties) {
			if (property.getTypeId().equals(termId.getId())) {
				return Integer.parseInt(property.getValue());
			}
		}
		return null;
	}
	
	private Integer findTermId(List<CVTermRelationship> cvTermRelationships, TermId relationship) {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

	private Term createTerm(List<CVTermRelationship> cvTermRelationships, TermId relationship) throws MiddlewareQueryException {
		Integer id = findTermId(cvTermRelationships, relationship);
		if(id!=null) { //add to handle missing cvterm_relationship (i.e. is_a)
			return createTerm(id);
		}
		return null;
	}

	private Term createTerm(Integer id) throws MiddlewareQueryException {
		CVTerm cvTerm = getCvTerm(id);
		return cvTerm != null 
		        ? new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition(), 
		                    createSynonyms(cvTerm.getSynonyms()), createTermProperties(cvTerm.getProperties())) 
		        : null;
	}
	
	private List<NameSynonym> createSynonyms(List<CVTermSynonym> synonyms) {
		List<NameSynonym> nameSynonyms = null;
		if (synonyms != null && synonyms.size() > 0) {
			nameSynonyms = new ArrayList<NameSynonym>();
			for (CVTermSynonym synonym : synonyms) {
				nameSynonyms.add(new NameSynonym(synonym.getSynonym(), NameType.find(synonym.getTypeId())));
			}
		}
		return nameSynonyms;
	}
	
	private List<TermProperty> createTermProperties(List<CVTermProperty> properties) {
	    List<TermProperty> termProperties = null;
	    if (properties != null && properties.size() > 0) {
	        termProperties = new ArrayList<TermProperty>();
	        for (CVTermProperty property : properties) {
	            termProperties.add(new TermProperty(property.getCvTermPropertyId(), property.getTypeId(), property.getValue(), property.getRank()));
	        }
	    }
	    return termProperties;
	}

	private CVTerm getCvTerm(int id) throws MiddlewareQueryException {
		if (setWorkingDatabase(id)) {
		    return getCvTermDao().getById(id);
		}
		return null;
	}
	
	private List<CVTerm> getCvTerms(List<Integer> ids) throws MiddlewareQueryException {
		if (setWorkingDatabase(ids.get(0))) {
		    return getCvTermDao().getByIds(ids);
		}
		return null;
	}
	
	private PhenotypicType createPhenotypicType(int storedInTerm) {
		for (PhenotypicType phenotypicType : PhenotypicType.values()) {
			if (phenotypicType.getTypeStorages().contains(storedInTerm)) {
				return phenotypicType;
			}
		}
		return null;
	}
	
	public StandardVariable findOrSave(String name, String description, String propertyName, String scaleName, 
			String methodName, PhenotypicType role, String dataTypeString) 
			throws MiddlewareQueryException, MiddlewareException {
		
        Term property = getTermBuilder().findOrSaveTermByName(propertyName, CvId.PROPERTIES);
        Term scale = getTermBuilder().findOrSaveTermByName(scaleName, CvId.SCALES);
        Term method = getTermBuilder().findOrSaveTermByName(methodName, CvId.METHODS);
        
        StandardVariable standardVariable = getByPropertyScaleMethod(property.getId(), scale.getId(), method.getId());
		
        if (standardVariable == null) {
			standardVariable = new StandardVariable();
			standardVariable.setName(name);
			standardVariable.setDescription(description);
			standardVariable.setProperty(property);
			standardVariable.setScale(scale);
			standardVariable.setMethod(method);
			standardVariable.setDataType(getDataType(dataTypeString));
			standardVariable.setStoredIn(getStorageTypeTermByPhenotypicType(role));
			
			Integer standardVariableId = getStandardVariableSaver().save(standardVariable);
        	standardVariable = getStandardVariableBuilder().create(standardVariableId);
        }
        
		return standardVariable;
	}
	
	private Term getDataType(String dataTypeString) throws MiddlewareQueryException {
        Term dataType = null;
        if (dataTypeString != null) {
        	dataType = ("N".equals(dataTypeString)  
        			? getTermBuilder().get(TermId.NUMERIC_VARIABLE.getId())
        			: getTermBuilder().get(TermId.CHARACTER_VARIABLE.getId()));
        }
        return dataType;
	}
	
	private Term getStorageTypeTermByPhenotypicType(PhenotypicType phenotypicType) throws MiddlewareQueryException {
		Term storedIn = null;
		if (phenotypicType != null) {
			Integer storedInId = null;
			switch (phenotypicType) {
				case STUDY : storedInId = TermId.STUDY_INFO_STORAGE.getId();
					break;
				case DATASET : storedInId = TermId.DATASET_INFO_STORAGE.getId();
					break;
				case GERMPLASM : storedInId = TermId.GERMPLASM_ENTRY_STORAGE.getId();
					break;
				case TRIAL_DESIGN : storedInId = TermId.TRIAL_DESIGN_INFO_STORAGE.getId();
					break;
				case TRIAL_ENVIRONMENT : storedInId = TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId();
					break;
				case VARIATE: storedInId = TermId.OBSERVATION_VARIATE.getId();
				    break;
			}
			storedIn = getTermBuilder().get(storedInId);
		} else {
			storedIn = getTermBuilder().get(TermId.OBSERVATION_VARIATE.getId());
		}
		return storedIn;
	}
	
	public StandardVariable getByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId) throws MiddlewareQueryException {
        
		Integer stdVariableId =  getIdByPropertyScaleMethod(propertyId, scaleId, methodId);
        StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = getStandardVariableBuilder().create(stdVariableId);
		}
		return standardVariable;
	}
	
	public Integer getIdByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId) throws MiddlewareQueryException {
		Integer stdVariableId = null;
	    if (setWorkingDatabase(Database.LOCAL)) {
			stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(
					propertyId, scaleId, methodId, "DESC");
			
			if (stdVariableId == null) {
				if (setWorkingDatabase(Database.CENTRAL)) {
					stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(
							propertyId, scaleId, methodId, "ASC");
				}
			}
		}
	    return stdVariableId;
	}

	
	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers) 
			throws MiddlewareQueryException {
		Map<String, List<StandardVariable>> standardVariablesInProjects = new HashMap<String, List<StandardVariable>>();
		
		Map<String, Set<Integer>> standardVariableIdsInProjects = new HashMap<String, Set<Integer>>();

		// Step 1: Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
		List<String> names = headers;
		if (setWorkingDatabase(Database.LOCAL)) {
			standardVariableIdsInProjects = getStandardVariableIdsForProjectProperties(names);
		}
		if (setWorkingDatabase(Database.CENTRAL)){
			Map<String, Set<Integer>> stdVarIdsRetrieved = getStandardVariableIdsForProjectProperties(names);
			
			// Combine the items retrieved from local and central
			for (String name: names){
				name = name.toUpperCase();
				Set<Integer> varIds = standardVariableIdsInProjects.get(name);
				if (varIds == null || varIds.size() == 0){
					standardVariableIdsInProjects.put(name, stdVarIdsRetrieved.get(name));
				} else {
					if (stdVarIdsRetrieved != null && stdVarIdsRetrieved.get(name) != null){
						varIds.addAll(stdVarIdsRetrieved.get(name));
					}
					standardVariableIdsInProjects.put(name, varIds);
				}
			}
			
		}

		// Step 2: If no variable found, search for cvterm (standard variables) with given name.
		
		// Exclude header items with result from step 1
		names = new ArrayList<String>();
		for (String name : headers){
			Set<Integer> varIds = standardVariableIdsInProjects.get(name.toUpperCase());
			if (varIds == null || varIds.size() == 0){
				names.add(name);
			}			
		}
		
		if (setWorkingDatabase(Database.LOCAL)) {
			standardVariableIdsInProjects.putAll(getStandardVariableIdsForTerms(names));
		}
		if (setWorkingDatabase(Database.CENTRAL)) {
			Map<String, Set<Integer>> stdVarIdsRetrieved = getStandardVariableIdsForTerms(names);
			
			// Combine the items retrieved from local and central
			for (String name: names){
				name = name.toUpperCase();
				Set<Integer> varIds = standardVariableIdsInProjects.get(name);
				if (varIds == null || varIds.size() == 0){
					standardVariableIdsInProjects.put(name, stdVarIdsRetrieved.get(name));
				} else {
					if (stdVarIdsRetrieved != null && stdVarIdsRetrieved.get(name) != null){
						varIds.addAll(stdVarIdsRetrieved.get(name));
					}
					standardVariableIdsInProjects.put(name, varIds);
				}
			}
		}
						
		// Step 3. If no variable still found for steps 1 and 2, treat the header as a trait / property name. 
		// Search for trait with given name and return the standard variables using that trait (if any)

		// Exclude header items with result from step 2
		names = new ArrayList<String>();
		for (String name : headers){
			Set<Integer> varIds = standardVariableIdsInProjects.get(name.toUpperCase());
			if (varIds == null || varIds.size() == 0){
				names.add(name);
			}			
		}
		
		if (setWorkingDatabase(Database.LOCAL)) {
			standardVariableIdsInProjects.putAll(getStandardVariableIdsForTraits(names));
        }
		if (setWorkingDatabase(Database.CENTRAL)) {
			Map<String, Set<Integer>> stdVarIdsRetrieved = getStandardVariableIdsForTraits(names);
			
			// Combine the items retrieved from local and central
			for (String name: names){
				name = name.toUpperCase();
				Set<Integer> varIds = standardVariableIdsInProjects.get(name);
				if (varIds == null || varIds.size() == 0){
					standardVariableIdsInProjects.put(name, stdVarIdsRetrieved.get(name));
				} else {
					if (stdVarIdsRetrieved != null && stdVarIdsRetrieved.get(name) != null){
						varIds.addAll(stdVarIdsRetrieved.get(name));
					}
					standardVariableIdsInProjects.put(name, varIds);
				}
			}
        }

		// Build map 
		for (String name : headers){
			String upperName = name.toUpperCase();
			Set<Integer> varIds = standardVariableIdsInProjects.get(upperName);
			
			List<StandardVariable> variables = new ArrayList<StandardVariable>();
			if (varIds != null){
				List<Integer> standardVariableIds = new ArrayList<Integer>(varIds);
				variables = create(standardVariableIds);
			}
			standardVariablesInProjects.put(name, variables);
		
		}
		
		return standardVariablesInProjects;
	}

	
	public Map<String, Set<Integer>> getStandardVariableIdsForProjectProperties(List<String> propertyNames)
		throws MiddlewareQueryException{
		return getProjectPropertyDao().getStandardVariableIdsByPropertyNames(propertyNames);
	}
	
	public Map<String, Set<Integer>> getStandardVariableIdsForTerms(List<String> termNames)
		throws MiddlewareQueryException{
		return getCvTermDao().getTermsByNameOrSynonyms(termNames, CvId.VARIABLES.getId());
		
	}
	
	public Map<String, Set<Integer>> getStandardVariableIdsForTraits(List<String> traitNames)
			throws MiddlewareQueryException{
		return getCvTermDao().getStandardVariableIdsByProperties(traitNames);
	}
	

}
