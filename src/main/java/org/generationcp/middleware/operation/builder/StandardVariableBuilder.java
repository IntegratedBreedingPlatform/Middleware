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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
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
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
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
	    
	    
        setWorkingDatabase(Database.LOCAL);
        List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(standardVariable.getId());
        setWorkingDatabase(Database.CENTRAL);
        cvTermRelationships.addAll(getCvTermRelationshipDao().getBySubject(standardVariable.getId()));
	        
		if (setWorkingDatabase(standardVariable.getId())) {
			standardVariable.setProperty(createTerm(cvTermRelationships, TermId.HAS_PROPERTY));	
			standardVariable.setMethod(createTerm(cvTermRelationships, TermId.HAS_METHOD));
			standardVariable.setScale(createTerm(cvTermRelationships, TermId.HAS_SCALE));
			standardVariable.setDataType(createTerm(cvTermRelationships, TermId.HAS_TYPE));
			standardVariable.setStoredIn(createTerm(cvTermRelationships, TermId.STORED_IN));
			standardVariable.setIsA(createTerm(cvTermRelationships, TermId.IS_A));
			//get isA of property
		    if (standardVariable.getProperty() != null){
		        setWorkingDatabase(standardVariable.getProperty().getId());
				List<CVTermRelationship> propertyCvTermRelationships = 
						getCvTermRelationshipDao().getBySubject(standardVariable.getProperty().getId());
				standardVariable.setIsA(createTerm(propertyCvTermRelationships, TermId.IS_A));
		    }
			if (standardVariable.getStoredIn() != null){
			    standardVariable.setPhenotypicType(createPhenotypicType(standardVariable.getStoredIn().getId()));
			}
			addEnumerations(standardVariable, cvTermRelationships);
		}
	}
	
	private void addEnumerations(StandardVariable standardVariable, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
	    if (hasEnumerations(cvTermRelationships)) {
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					Integer id = cvTermRelationship.getObjectId();
					
					Enumeration newValue = createEnumeration(getCvTerm(id));
					
					if (!isEnumerationValueExists(enumerations, newValue)){
					    enumerations.add(newValue);
					}
				}
			}
			Collections.sort(enumerations);
			standardVariable.setEnumerations(enumerations);
		}
	}
	
    private boolean isEnumerationValueExists(List<Enumeration> enumerations, Enumeration value) {
        for (Enumeration enumeration : enumerations) {
            if (enumeration.getName().equals(value.getName())) {
                return true;
            }
        }
        return false;
    }

	private Enumeration createEnumeration(CVTerm cvTerm) throws MiddlewareQueryException {
		return new Enumeration(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition(), getRank(cvTerm.getCvTermId()));
	}

	private int getRank(int cvTermId) throws MiddlewareQueryException {
	    CVTermProperty property = getTermPropertyBuilder().findPropertyByType(cvTermId, TermId.ORDER.getId());
		if (property != null) {
			return Integer.parseInt(property.getValue());
		}
		return 0;
	}
	
	private String getCropOntologyId(Term term) throws MiddlewareQueryException {
	    String cropOntologyId = null;
	    if (term != null && term.getProperties() != null && term.getProperties().size() > 0) {
	        for (TermProperty termProperty : term.getProperties()) {
	            if (TermId.CROP_ONTOLOGY_ID.getId() == termProperty.getTypeId()) {
	                cropOntologyId = termProperty.getValue();
	                break;
	            }
	        }
	    }
	    if (term != null && term.getId() > 0) {
	        Database database = getActiveDatabase();
	        setWorkingDatabase(Database.LOCAL);
	        CVTermProperty property = getCvTermPropertyDao().getOneByCvTermAndType(term.getId(), TermId.CROP_ONTOLOGY_ID.getId());
	        if (property != null) {
	            cropOntologyId = property.getValue();
	        }
	        setWorkingDatabase(database);
	    }
	    return cropOntologyId;
	}

/*	private CVTermProperty findProperty(List<CVTermProperty> properties, int typeId) {
		if (properties != null) {
			for (CVTermProperty property : properties) {
				if (property.getTypeId() == typeId) {
					return property;
				}
			}
		}
		return null;
	}
*/
	private boolean hasEnumerations(List<CVTermRelationship> cvTermRelationships) {
		return findTermId(cvTermRelationships, TermId.HAS_VALUE) != null;
	}

	private void addConstraints(StandardVariable standardVariable, CVTerm cvTerm) throws MiddlewareQueryException {
	    List<CVTermProperty> properties = getTermPropertyBuilder().findProperties(cvTerm.getCvTermId());
		if (properties != null && !properties.isEmpty()) {
            Double minValue = null;
            Double maxValue = null;
            Integer minValueId = null;
            Integer maxValueId = null;
			
		     for (CVTermProperty property : properties) {
		         if (property.getTypeId().equals(TermId.MIN_VALUE.getId()) && minValue == null){ 
                         minValue = Double.parseDouble(property.getValue());
                         minValueId = property.getCvTermPropertyId();
		         }
                 if (property.getTypeId().equals(TermId.MAX_VALUE.getId()) && maxValue == null){  
                         maxValue = Double.parseDouble(property.getValue());
                         maxValueId = property.getCvTermPropertyId();
                 }
		     }
    
			if (minValue != null || maxValue != null) {
				standardVariable.setConstraints(new VariableConstraints(minValueId, maxValueId, minValue, maxValue));
			}
		}
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
		                    createSynonyms(cvTerm.getCvTermId()), createTermProperties(cvTerm.getCvTermId())) 
		        : null;
	}
	
	private List<NameSynonym> createSynonyms(int cvTermId) throws MiddlewareQueryException {
	    List<CVTermSynonym> synonyms = getNameSynonymBuilder().findSynonyms(cvTermId);
	    return getNameSynonymBuilder().create(synonyms);
	}
	
	private List<TermProperty> createTermProperties(int cvTermId) throws MiddlewareQueryException {
	    List<CVTermProperty> cvTermProperties = getTermPropertyBuilder().findProperties(cvTermId);
	    return getTermPropertyBuilder().create(cvTermProperties);
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
        
        StandardVariable standardVariable = getByPropertyScaleMethodRole(property.getId(), scale.getId(), method.getId(), role);
		
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
	
    public StandardVariable getByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role) 
            throws MiddlewareQueryException {
        
        Integer stdVariableId =  getIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
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
	
	public Integer getIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
            Integer stdVariableId = null;
            if (setWorkingDatabase(Database.LOCAL)) {
                stdVariableId = getCvTermDao().getStandardVariableIdByTermId(cvTermId, termId);
            }
            return stdVariableId;
        }

    public CVTerm getCvTerm(String name, int cvId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        CVTerm term = getCvTermDao().getByNameAndCvId(name, cvId);
        if (term == null) {
            setWorkingDatabase(Database.LOCAL);
            term = getCvTermDao().getByNameAndCvId(name, cvId);
        }
        return term;
    }

    public Integer getIdByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role) 
    throws MiddlewareQueryException {
    
        Integer stdVariableId = null;
        if (setWorkingDatabase(Database.LOCAL)) {
            stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(
                    propertyId, scaleId, methodId, role);
            
            if (stdVariableId == null) {
                if (setWorkingDatabase(Database.CENTRAL)) {
                    stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(
                            propertyId, scaleId, methodId, role);
                }
            }
        }
        return stdVariableId;
    }

    public boolean validateEnumerationUsage(int standardVariableId, int enumerationId) throws MiddlewareQueryException {
    	setWorkingDatabase(standardVariableId);
    	Integer storedInId = getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), standardVariableId).get(0);
    	String value = String.valueOf(enumerationId);
    	if (storedInId == TermId.STUDY_INFO_STORAGE.getId() || storedInId == TermId.DATASET_INFO_STORAGE.getId()) {
    		return isExistsPropertyByTypeAndValue(standardVariableId, value);
    	}
    	else if (storedInId == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
    		return isExistsStocksByTypeAndValue(standardVariableId, value);
    	}
    	else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
    		return isExistsGeolocationByTypeAndValue(standardVariableId, value);
    	}
    	else if (storedInId == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
    		return isExistsExperimentsByTypeAndValue(standardVariableId, value);
    	}
    	else if (storedInId == TermId.CATEGORICAL_VARIATE.getId()) {
    		return isExistsPhenotypeByTypeAndValue(standardVariableId, value, true);
    	}
    	else {
    		throw new MiddlewareQueryException("Not a valid categorical variable - " + standardVariableId);
    	}
    }
    
    private boolean isExistsGeolocationByTypeAndValue(int factorId, String value) throws MiddlewareQueryException {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		geolocationIds.addAll(getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		geolocationIds.addAll(getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		return !geolocationIds.isEmpty();
	}    

	private boolean isExistsStocksByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		stockIds.addAll(getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		stockIds.addAll(getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		return !stockIds.isEmpty();
	}
	
	private boolean isExistsExperimentsByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> experimentIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		experimentIds.addAll(getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		experimentIds.addAll(getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		return !experimentIds.isEmpty();
	}
	
	private boolean isExistsPropertyByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		setWorkingDatabase(Database.CENTRAL);
		properties.addAll(getProjectPropertyDao().getByTypeAndValue(factorId, value));
		setWorkingDatabase(Database.LOCAL);
		properties.addAll(getProjectPropertyDao().getByTypeAndValue(factorId, value));
		return !properties.isEmpty();
	}
	
	private boolean isExistsPhenotypeByTypeAndValue(Integer variateId, String value, boolean isEnum) throws MiddlewareQueryException {
		List<Phenotype> phenotypes = new ArrayList<Phenotype>();
		setWorkingDatabase(Database.CENTRAL);
		phenotypes.addAll(getPhenotypeDao().getByTypeAndValue(variateId, value, isEnum));
		setWorkingDatabase(Database.LOCAL);
		phenotypes.addAll(getPhenotypeDao().getByTypeAndValue(variateId, value, isEnum));
		return !phenotypes.isEmpty();
	}
	
}
