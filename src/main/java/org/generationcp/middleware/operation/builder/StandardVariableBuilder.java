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

import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

import java.util.*;

public class StandardVariableBuilder extends Builder {

	
	public StandardVariableBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}
	
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
	
	// FIXME : this is a select in a loop - (Create(id) is a DB select)
	public List<StandardVariable> create(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		if(standardVariableIds != null && !standardVariableIds.isEmpty()) {
			for (Integer id : standardVariableIds){
				standardVariables.add(create(id));
			}
		}
		return standardVariables;
	}
	
	public StandardVariableSummary getStandardVariableSummary(Integer standardVariableId) throws MiddlewareQueryException {
		StandardVariableSummary summary = null;
		if(standardVariableId != null) {
			summary =  getStandardVariableDao().getStandardVariableSummary(standardVariableId);
			if(summary != null) {
				specialProcessing(Arrays.asList(summary));
			}
		}
		return summary;
	}
	
	/**
	 * Loads a list of {@link StandardVariableSummary}'s for the given set of standard variable ids from standard_variable_summary database view.
	 * 
	 * @see StandardVariableDao#getStarndardVariableSummaries(List)
	 */
	public List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		List<StandardVariableSummary> result = new ArrayList<StandardVariableSummary>();
		if(standardVariableIds != null && !standardVariableIds.isEmpty()) {
			List<StandardVariableSummary> localVariables = getStandardVariableDao().getStarndardVariableSummaries(standardVariableIds);
			specialProcessing(localVariables);
			result.addAll(localVariables);
		}
		return result;
	}
	
	private void specialProcessing(List<StandardVariableSummary> summaries) throws MiddlewareQueryException {
		if(summaries == null || summaries.isEmpty()) {
			return;
		}
		
		for(StandardVariableSummary summary : summaries) {
			//Special hackery for the isA (class) part of the relationship!
			//Earlier isA (class) part of the standard variables ontology star used to be linked to standard variables directly.
			//Now this relationship is linked to the "Property" of the standard variable. (facepalm).
			if (summary.getProperty() != null){
				List<CVTermRelationship> propertyCvTermRelationships = getCvTermRelationshipDao().getBySubject(summary.getProperty().getId());
				Term isAOfProperty = createTerm(propertyCvTermRelationships, TermId.IS_A);
				if(isAOfProperty != null) {
					summary.setIsA(new TermSummary(isAOfProperty.getId(), isAOfProperty.getName(), isAOfProperty.getDefinition()));
				}
		    }
		}
		
	}

	private void addRelatedTerms(StandardVariable standardVariable, CVTerm cvTerm) throws MiddlewareQueryException {
	    
        List<CVTermRelationship> cvTermRelationships  = getCvTermRelationshipDao().getBySubject(standardVariable.getId());
		standardVariable.setProperty(createTerm(cvTermRelationships, TermId.HAS_PROPERTY));	
		standardVariable.setMethod(createTerm(cvTermRelationships, TermId.HAS_METHOD));
		standardVariable.setScale(createTerm(cvTermRelationships, TermId.HAS_SCALE));
		standardVariable.setDataType(createTerm(cvTermRelationships, TermId.HAS_TYPE));
		standardVariable.setStoredIn(createTerm(cvTermRelationships, TermId.STORED_IN));
		standardVariable.setIsA(createTerm(cvTermRelationships, TermId.IS_A));
	    if (standardVariable.getProperty() != null){
			List<CVTermRelationship> propertyCvTermRelationships = 
					getCvTermRelationshipDao().getBySubject(standardVariable.getProperty().getId());
			standardVariable.setIsA(createTerm(propertyCvTermRelationships, TermId.IS_A));
	    }
		if (standardVariable.getStoredIn() != null){
		    standardVariable.setPhenotypicType(createPhenotypicType(standardVariable.getStoredIn().getId()));
		}
		// Enumerations - Future candidate for separating out from StandardVariable as a "details" concept. Not a huge overhead at the moment.
		addEnumerations(standardVariable, cvTermRelationships);
	}
	
	private void addEnumerations(StandardVariable standardVariable, List<CVTermRelationship> cvTermRelationships) throws MiddlewareQueryException {
	    if (hasEnumerations(cvTermRelationships)) {
	    	Map<Integer, Integer> overridenEnumerations = new HashMap<Integer, Integer>();
			List<Enumeration> enumerations = new ArrayList<Enumeration>();
			for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
				if (cvTermRelationship.getTypeId().equals(TermId.HAS_VALUE.getId())) {
					Integer id = cvTermRelationship.getObjectId();
					
					Enumeration newValue = createEnumeration(getCvTerm(id));
					
					Enumeration existingMatch = getExistingEnumeration(enumerations, newValue);

					if (existingMatch == null) {
					    enumerations.add(newValue);
					} else {
						overridenEnumerations.put(newValue.getId(), existingMatch.getId());
					}
				}
			}
			Collections.sort(enumerations);
			standardVariable.setEnumerations(enumerations);
			standardVariable.setOverridenEnumerations(overridenEnumerations);
		}
	}
	
    private Enumeration getExistingEnumeration(List<Enumeration> enumerations, Enumeration value) {
        for (Enumeration enumeration : enumerations) {
            if (enumeration.getName().equals(value.getName())) {
                return enumeration;
            }
        }
        return null;
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
	
	public String getCropOntologyId(Term term) throws MiddlewareQueryException {
	    String cropOntologyId = null;
	    List<TermProperty> termProperties = createTermProperties(term.getId());
	    if (termProperties != null && !termProperties.isEmpty()) {
	        for (TermProperty termProperty : termProperties) {
	            if (TermId.CROP_ONTOLOGY_ID.getId() == termProperty.getTypeId()) {
	                cropOntologyId = termProperty.getValue();
	                break;
	            }
	        }
	    }
	    if (term != null) {
	        CVTermProperty property = getCvTermPropertyDao().getOneByCvTermAndType(term.getId(), TermId.CROP_ONTOLOGY_ID.getId());
	        if (property != null) {
	            cropOntologyId = property.getValue();
	        }
	    }
	    return cropOntologyId;
	}

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
		if(id!=null) {
			//add to handle missing cvterm_relationship (i.e. is_a)
			return createTerm(id);
		}
		return null;
	}

	private Term createTerm(Integer id) throws MiddlewareQueryException {
		CVTerm cvTerm = getCvTerm(id);
		return cvTerm != null 
		        ? new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition()) 
		        : null;
	}
	
	public List<NameSynonym> createSynonyms(int cvTermId) throws MiddlewareQueryException {
	    List<CVTermSynonym> synonyms = getNameSynonymBuilder().findSynonyms(cvTermId);
	    return getNameSynonymBuilder().create(synonyms);
	}
	
	public List<TermProperty> createTermProperties(int cvTermId) throws MiddlewareQueryException {
	    List<CVTermProperty> cvTermProperties = getTermPropertyBuilder().findProperties(cvTermId);
	    return getTermPropertyBuilder().create(cvTermProperties);
	}

	private CVTerm getCvTerm(int id) throws MiddlewareQueryException {
	    return getCvTermDao().getById(id);
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
			throws MiddlewareException {
		
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
			Integer storedInId;
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
				default:
					storedInId = null;
			}
			if (storedInId != null){
				storedIn = getTermBuilder().get(storedInId);
			}
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
		stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId, "DESC");
	    return stdVariableId;
	}

	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers) 
			throws MiddlewareQueryException {
		
		Map<String, List<StandardVariable>> standardVariablesInProjects = new HashMap<String, List<StandardVariable>>();
		Map<String, Set<Integer>> standardVariableIdsInProjects = new HashMap<String, Set<Integer>>();

		// Step 1: Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
		List<String> names = headers;
		standardVariableIdsInProjects = getStandardVariableIdsForProjectProperties(names);

		// Step 2: If no variable found, search for cvterm (standard variables) with given name.
		// Exclude header items with result from step 1
		names = new ArrayList<String>();
		for (String name : headers){
			Set<Integer> varIds = standardVariableIdsInProjects.get(name.toUpperCase());
			if (varIds == null || varIds.isEmpty()){
				names.add(name);
			}			
		}
		
		standardVariableIdsInProjects.putAll(getStandardVariableIdsForTerms(names));
		// Step 3. If no variable still found for steps 1 and 2, treat the header as a trait / property name. 
		// Search for trait with given name and return the standard variables using that trait (if any)

		// Exclude header items with result from step 2
		names = new ArrayList<String>();
		for (String name : headers){
			Set<Integer> varIds = standardVariableIdsInProjects.get(name.toUpperCase());
			if (varIds == null || varIds.isEmpty()){
				names.add(name);
			}			
		}
		
		standardVariableIdsInProjects.putAll(getStandardVariableIdsForTraits(names));
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
        stdVariableId = getCvTermDao().getStandardVariableIdByTermId(cvTermId, termId);
        return stdVariableId;
    }

    public CVTerm getCvTerm(String name, int cvId) throws MiddlewareQueryException {
        return getCvTermDao().getByNameAndCvId(name, cvId);
    }

    public Integer getIdByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role) throws MiddlewareQueryException {
        Integer stdVariableId = null;
        stdVariableId = getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
        return stdVariableId;
    }

    public boolean validateEnumerationUsage(int standardVariableId, int enumerationId) throws MiddlewareQueryException {
    	Integer storedInId = getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), standardVariableId).get(0);
    	String value = String.valueOf(enumerationId);
    	if (storedInId == TermId.STUDY_INFO_STORAGE.getId() || storedInId == TermId.DATASET_INFO_STORAGE.getId()) {
    		return !isExistsPropertyByTypeAndValue(standardVariableId, value);
    	} else if (storedInId == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
    		return !isExistsStocksByTypeAndValue(standardVariableId, value);
    	} else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
    		return !isExistsGeolocationByTypeAndValue(standardVariableId, value);
    	} else if (storedInId == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
    		return !isExistsExperimentsByTypeAndValue(standardVariableId, value);
    	} else if (storedInId == TermId.CATEGORICAL_VARIATE.getId()) {
    		return !isExistsPhenotypeByTypeAndValue(standardVariableId, value, true);
    	} else {
    		throw new MiddlewareQueryException("Not a valid categorical variable - " + standardVariableId);
    	}
    }
    
    private boolean isExistsGeolocationByTypeAndValue(int factorId, String value) throws MiddlewareQueryException {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		geolocationIds.addAll(getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		return !geolocationIds.isEmpty();
	}    

	private boolean isExistsStocksByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		stockIds.addAll(getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		return !stockIds.isEmpty();
	}
	
	private boolean isExistsExperimentsByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		Set<Integer> experimentIds = new HashSet<Integer>();
		experimentIds.addAll(getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		return !experimentIds.isEmpty();
	}
	
	private boolean isExistsPropertyByTypeAndValue(Integer factorId, String value) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		properties.addAll(getProjectPropertyDao().getByTypeAndValue(factorId, value));
		return !properties.isEmpty();
	}
	
	private boolean isExistsPhenotypeByTypeAndValue(Integer variateId, String value, boolean isEnum) throws MiddlewareQueryException {
		List<Phenotype> phenotypes = new ArrayList<Phenotype>();
		phenotypes.addAll(getPhenotypeDao().getByTypeAndValue(variateId, value, isEnum));
		return !phenotypes.isEmpty();
	}
	
	public List<StandardVariableReference> findAllByProperty(int propertyId) throws MiddlewareQueryException {
		List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		list.addAll(getCvTermDao().getStandardVariablesOfProperty(propertyId));
		return list;
	}
}
