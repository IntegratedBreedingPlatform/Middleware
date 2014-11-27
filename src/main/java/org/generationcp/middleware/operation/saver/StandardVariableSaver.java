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
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.*;
import org.generationcp.middleware.util.StringUtil;

import java.util.List;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;
	
	public StandardVariableSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void delete(StandardVariable stdVar) throws MiddlewareQueryException, MiddlewareException {
	    deleteEnumerations(stdVar.getId(), stdVar.getEnumerations());
	    
	    if (stdVar.getCropOntologyId() != null) {
	        deleteCropOntologyId(stdVar.getProperty().getId(), stdVar.getCropOntologyId());
	    }
	    
	    deleteRelationship(stdVar.getId(), TermId.STORED_IN.getId(), stdVar.getStoredIn());
	    deleteRelationship(stdVar.getId(), TermId.HAS_TYPE.getId(), stdVar.getDataType());
	    deleteRelationship(stdVar.getId(), TermId.HAS_SCALE.getId(), stdVar.getScale());
	    deleteRelationship(stdVar.getId(), TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
	    deleteRelationship(stdVar.getId(), TermId.HAS_METHOD.getId(), stdVar.getMethod());
	    
	    List<NameSynonym> nameSynonyms = getStandardVariableBuilder().createSynonyms(stdVar.getId());	    
	    deleteSynonyms(stdVar.getId(), nameSynonyms);
	    
	    if (stdVar.getConstraints() != null) {
                deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
                deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
            }
	    
	    deleteCvTerm(getCvTermDao().getById(stdVar.getId()));
	}

	public void deleteConstraints(StandardVariable stdVar) throws MiddlewareQueryException{
        if (stdVar.getConstraints() != null) {
        	deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
        	deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
        }
	}
	
	public Integer save(StandardVariable stdVar) throws MiddlewareQueryException {
		CVTerm varTerm = createCvTerm(stdVar);
		int varId = varTerm.getCvTermId();
		stdVar.setId(varId);

        if (stdVar.getConstraints() != null) {
            Integer minValueId = saveConstraint(varId, TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
            Integer maxValueId = saveConstraint(varId, TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
            stdVar.getConstraints().setMinValueId(minValueId);
            stdVar.getConstraints().setMaxValueId(maxValueId);
        }
        
       	saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
       	saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
       	saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
       	saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());
		saveRelationship(varId, TermId.STORED_IN.getId(), stdVar.getStoredIn());

		saveEnumerations(varId, stdVar.getEnumerations());
				
		return stdVar.getId();
	}
	
	public void saveConstraints(int standardVariableId, VariableConstraints constraints) throws MiddlewareQueryException{
	    if (constraints.getMinValueId() == null){ // add
	        Integer minId = saveConstraint(standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
	        constraints.setMinValueId(minId);
	    } else { // update
	        updateConstraint(constraints.getMinValueId(), standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
	    }
	    
        if (constraints.getMaxValueId() == null){// add
            Integer maxId = saveConstraint(standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
            constraints.setMaxValueId(maxId);
        } else { // update
            updateConstraint(constraints.getMaxValueId(), standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
        }
        
	}
	
    public void saveEnumeration(StandardVariable variable, Enumeration enumeration, Integer cvId) 
            throws MiddlewareException, MiddlewareQueryException{

        validateInputEnumeration(variable, enumeration);
        
        //Save cvterm entry of the new valid value
        CVTerm cvTerm = createCvTerm(enumeration, cvId);
        enumeration.setId(cvTerm.getCvTermId());
        
        // save cvterm relationship
        saveCvTermRelationship(variable.getId(), TermId.HAS_VALUE.getId(), enumeration.getId());
    }
    
    private void validateInputEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException{
        String name = enumeration.getName();
        String definition = enumeration.getDescription();
        StringBuffer errorMessage = new StringBuffer("");
        if (name == null || name.equals("")) {
            errorMessage.append("\nname is null or empty");
        }
        if (variable.getEnumerationByName(name) != null){
            errorMessage.append("\nthe value with name = " + name + " already exists.");
        }
        if (definition == null || definition.equals("")) {
            errorMessage.append("\ndefinition is null or empty");
        }
        if (variable.getEnumerationByDescription(definition) != null){
            errorMessage.append("\nthe value with definition = " + definition + " already exists.");
        }
            
        if (errorMessage.length() > 0){
            
            if (enumeration.getId() != null && errorMessage.toString().contains("already exists")
                    && !errorMessage.toString().contains("null or empty")){
                // Ignore. Operation is UPDATE.
            } else {
                throw new MiddlewareException(errorMessage.toString());
            }
        } 

    }
    
	private CVTerm createCvTerm(StandardVariable stdVar) throws MiddlewareQueryException {
		
		// check to see if term exists in DB before we create a new one
		CVTerm cvTerm = getCvTermDao().getByNameAndCvId(stdVar.getName(), CV_VARIABLES);
		
		if(cvTerm == null) {
		
			cvTerm = new CVTerm();
			cvTerm.setCvTermId(getCvTermDao().getNegativeId("cvTermId"));
			cvTerm.setCv(CV_VARIABLES);
			cvTerm.setName(stdVar.getName());
			cvTerm.setDefinition(stdVar.getDescription());
			cvTerm.setIsObsolete(false);
			cvTerm.setIsRelationshipType(false);
			
			getCvTermDao().save(cvTerm);
		}
		
		return cvTerm;
	}
	
    private CVTerm createCvTerm(Enumeration enumeration, Integer cvId) throws MiddlewareQueryException {
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(getCvTermDao().getNegativeId("cvTermId"));
        cvTerm.setCv(cvId);
        cvTerm.setName(enumeration.getName());
        cvTerm.setDefinition(enumeration.getDescription());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);
        getCvTermDao().save(cvTerm);
        return cvTerm;
    }

    public CV createCv(StandardVariable variable) throws MiddlewareQueryException{
        CV cv = new CV();
        cv.setCvId(getCvDao().getNegativeId("cvId"));
        cv.setName(String.valueOf(variable.getId()));
        cv.setDefinition(String.valueOf(variable.getName() + " - " + variable.getDescription()));
        getCvDao().save(cv);
        return cv;

    }
	
	private void saveRelationship(int subjectId, int typeId, Term object) throws MiddlewareQueryException {
		if (object != null) {
			// see if this relationship already exists (this saves us from dealing with a Unique Constraint Exception)
			boolean exists = false;
			CVTermRelationship relationship = getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			if(relationship != null) {
				if(relationship.getObjectId().intValue() == object.getId()) {
					// the relationship exists, as stipulated by unique constraint subject-type-object must be unique
					exists = true;
				}
			}
			// save to DB if this relationship does not already exist
			if(!exists) {
                saveCvTermRelationship(subjectId, typeId, object.getId());
            }
		} else {
			throw new MiddlewareQueryException("The CvTermRelationship field is required for " + subjectId + " with relationship type of " + typeId);
		}
	}
	
	private void saveCvTermRelationship(int subjectId, int typeId, int objectId) throws MiddlewareQueryException {
		
			CVTermRelationship relationship = new CVTermRelationship();
			
			relationship.setCvTermRelationshipId(getCvTermRelationshipDao().getNegativeId("cvTermRelationshipId"));
			relationship.setSubjectId(subjectId);
			relationship.setTypeId(typeId);
			relationship.setObjectId(objectId);
			
			getCvTermRelationshipDao().save(relationship);
			
	}
	
    private Integer saveConstraint(int cvTermId, int typeId, Double constraintValue) throws MiddlewareQueryException {
        if (constraintValue != null) {
            CVTermProperty property = new CVTermProperty();
            int negativeId = getCvTermPropertyDao().getNegativeId("cvTermPropertyId");
            property.setCvTermPropertyId(negativeId);
            property.setTypeId(typeId);
            property.setValue(constraintValue.toString());
            property.setRank(0);
            property.setCvTermId(cvTermId);
            getCvTermPropertyDao().save(property);
            return property.getCvTermPropertyId();
        }     
        return null;
    }
    
    private void updateConstraint(int constraintId, int cvTermId, int typeId, Double constraintValue) throws MiddlewareQueryException {
        if (constraintValue != null) {
            CVTermProperty property = new CVTermProperty();
            property.setCvTermPropertyId(constraintId);
            property.setTypeId(typeId);
            property.setValue(constraintValue.toString());
            property.setRank(0);
            property.setCvTermId(cvTermId);
            getCvTermPropertyDao().merge(property);
        }     
    }
    
	private void saveSynonyms(int cvTermId, List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
		if (nameSynonyms != null && nameSynonyms.size() > 0) {
			for (NameSynonym nameSynonym : nameSynonyms) {
				if (!StringUtil.isEmpty(nameSynonym.getName())) {
					CVTermSynonym cvTermSynonym = new CVTermSynonym();
					
					cvTermSynonym.setCvTermSynonymId(getCvTermSynonymDao().getNegativeId("cvTermSynonymId"));
					cvTermSynonym.setSynonym(nameSynonym.getName());
					cvTermSynonym.setTypeId(nameSynonym.getType().getId());
					cvTermSynonym.setCvTermId(cvTermId);
					
					getCvTermSynonymDao().save(cvTermSynonym);
				}
			}
		}
	}
	
	private void saveEnumerations(int varId, List<Enumeration> enumerations) throws MiddlewareQueryException {
		if (enumerations != null && enumerations.size() > 0) {
			for (Enumeration enumeration : enumerations) {
				saveCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
			}
		}
	}

    public void update(StandardVariable standardVariable) throws MiddlewareQueryException {
        CVTerm varTerm = getStandardVariableBuilder().getCvTerm(standardVariable.getName(), CvId.VARIABLES.getId());
        varTerm.setName(standardVariable.getName());
        varTerm.setDefinition(standardVariable.getDescription());
        varTerm.setIsObsolete(false);
        varTerm.setIsRelationshipType(false);
        getCvTermDao().update(varTerm);
        
        List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(standardVariable.getId());
        if (relationships != null && !relationships.isEmpty()) {
            for (CVTermRelationship relationship : relationships) {
                Integer objectId = null;
                //STORED_IN can not be updated.
                if (relationship.getTypeId() == TermId.HAS_PROPERTY.getId()) {
                    if (standardVariable.getProperty() != null) {
                        objectId = standardVariable.getProperty().getId();
                    }
                } 
                else if (relationship.getTypeId() == TermId.HAS_SCALE.getId()) {
                    if (standardVariable.getScale() != null) {
                        objectId = standardVariable.getScale().getId();
                    }
                }
                else if (relationship.getTypeId() == TermId.HAS_METHOD.getId()) {
                    if (standardVariable.getMethod() != null) {
                        objectId = standardVariable.getMethod().getId();
                    }
                }
                else if (relationship.getTypeId() == TermId.HAS_TYPE.getId()) {
                    if (standardVariable.getDataType() != null) {
                        objectId = standardVariable.getDataType().getId();
                    }
                }
                else if (relationship.getTypeId() == TermId.STORED_IN.getId()) {
                	if (standardVariable.getStoredIn() != null) {
                		objectId = standardVariable.getStoredIn().getId();
                	}
                }

        		if (objectId != null && !objectId.equals(relationship.getObjectId())) {
                    relationship.setObjectId(objectId);
                    getCvTermRelationshipDao().update(relationship);
                }
            }
        }
        
    }
    
    public String validate(StandardVariable standardVariable, Operation operation) throws MiddlewareQueryException {

        StringBuilder errorCodes = null;
        
        Term term = getTermBuilder().findTermByName(standardVariable.getName(), CvId.VARIABLES);
        if (term != null && operation == Operation.ADD) {
            errorCodes = new StringBuilder();
            errorCodes.append(ErrorCode.NON_UNIQUE_NAME.getCode());
        }

        Integer varId = getStandardVariableBuilder().getIdByPropertyScaleMethodRole(
                    standardVariable.getProperty().getId(), 
                    standardVariable.getScale().getId(), 
                    standardVariable.getMethod().getId(),
                    standardVariable.getPhenotypicType());
        if (varId != null && (operation == Operation.ADD || operation == Operation.UPDATE && varId != standardVariable.getId())) {
            if (errorCodes == null) {
                errorCodes = new StringBuilder();
            }
            else {
                errorCodes.append(",");
            }
            errorCodes.append(ErrorCode.NON_UNIQUE_PCM_COMBINATION.getCode());
        }
        
        return errorCodes != null ? errorCodes.toString() : null;
    }

    public void saveOrUpdateCropOntologyId(Integer traitId, String cropOntologyId) throws MiddlewareQueryException {
       
        CVTermProperty cropOntologyProperty = getCvTermPropertyDao().getOneByCvTermAndType(traitId, TermId.CROP_ONTOLOGY_ID.getId());
        if (cropOntologyProperty != null && cropOntologyProperty.getValue() != null 
                && cropOntologyProperty.getValue().equalsIgnoreCase(cropOntologyId)) {
            //do nothing, no change
            return;
        }
        
        if (cropOntologyId == null || "".equals(cropOntologyId)) {
            getCvTermPropertyDao().makeTransient(cropOntologyProperty);
            return;
        }

        boolean isForCreate = true;
        if (cropOntologyProperty == null) {
            if (cropOntologyId == null || "".equals(cropOntologyId.trim())) {
                isForCreate = false;
            }
            else {
                cropOntologyProperty = new CVTermProperty();
                cropOntologyProperty.setCvTermId(traitId);
                cropOntologyProperty.setRank(0);
                cropOntologyProperty.setTypeId(TermId.CROP_ONTOLOGY_ID.getId());
                cropOntologyProperty.setCvTermPropertyId(getCvTermPropertyDao().getNegativeId("cvTermPropertyId"));
            }
        }
        if (isForCreate) {
            cropOntologyProperty.setValue(cropOntologyId);
            getCvTermPropertyDao().saveOrUpdate(cropOntologyProperty);
        }
        
    }
    
    public void deleteEnumeration(int varId, Enumeration enumeration) throws MiddlewareQueryException, MiddlewareException {
        deleteCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
        deleteCvTerm(getCvTermDao().getById(enumeration.getId()));
    }
 
    public void deleteEnumerations(int varId, List<Enumeration> enumerations) throws MiddlewareQueryException, MiddlewareException {
        if (enumerations != null && enumerations.size() > 0) {
            for (Enumeration enumeration : enumerations) {
                deleteEnumeration(varId, enumeration);
            }
        }
    }

    private void deleteCvTermRelationship(int subjectId, int typeId, int objectId) throws MiddlewareQueryException, MiddlewareException {
        Term typeTerm = getTermBuilder().get(typeId);
        if (typeTerm != null) {
            CVTermRelationship cvRelationship = getCvTermRelationshipDao().getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
            if(cvRelationship != null){ 
                if (cvRelationship.getCvTermRelationshipId() >= 0) { 
                    throw new MiddlewareException("Error in deleteCvTermRelationship: Relationship found in central - cannot be deleted.");
                }
    
                CVTermRelationshipDao dao = getCvTermRelationshipDao();
                try {
                    dao.makeTransient(cvRelationship);
                } catch (MiddlewareQueryException e) {
                    throw new MiddlewareQueryException(e.getMessage(), e);
                }
            }
        }
    }
    
    public void deleteCropOntologyId(Integer traitId, String cropOntologyId) throws MiddlewareQueryException {
        if (traitId < 0) {
            CVTerm trait = getCvTermDao().getById(traitId);
            if (trait != null) { 
                List<CVTermProperty> traitProperties = getTermPropertyBuilder().findProperties(traitId);
                if (traitProperties != null) {
                    for (CVTermProperty traitProperty : traitProperties) {
                        if (traitProperty.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
                            if (traitProperty.getValue() != null && !traitProperty.getValue().equals(cropOntologyId)) {
                                getCvTermPropertyDao().makeTransient(traitProperty);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void deleteRelationship(int subjectId, int typeId, Term object) throws MiddlewareQueryException, MiddlewareException {
        if (object != null) {
            deleteCvTermRelationship(subjectId, typeId, object.getId());
        } else {
            throw new MiddlewareQueryException("The CvTermRelationship field is required for " + subjectId + " with relationship type of " + typeId);
        }
    }
    
    private void deleteSynonyms(int cvTermId, List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
        if (nameSynonyms != null && nameSynonyms.size() > 0) {
            List<CVTermSynonym> cvTermSynonyms = getCvTermSynonymDao().getByCvTermId(cvTermId);
            if (cvTermSynonyms != null) {
                for (CVTermSynonym cvTermSynonym : cvTermSynonyms) {
                    getCvTermSynonymDao().makeTransient(cvTermSynonym);
                }
            }
        }
    }
    
    private void deleteConstraint(int cvTermId, int typeId, Double constraintValue) throws MiddlewareQueryException {
        if (constraintValue != null) {
            List<CVTermProperty> properties = getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId);
            
            for(CVTermProperty cvTermProperty : properties) {
                getCvTermPropertyDao().makeTransient(cvTermProperty);
            }
        }           
    }
    
    public void deleteCvTerm(CVTerm cvTerm) throws MiddlewareQueryException {
        CVTermDao dao = getCvTermDao();
        try {
            dao.makeTransient(cvTerm);
        } catch (MiddlewareQueryException e) {
            throw new MiddlewareQueryException(e.getMessage(), e);
        }
    } 
}
