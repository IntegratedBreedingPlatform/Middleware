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

import java.util.List;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.util.StringUtil;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;
	
	public StandardVariableSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Integer save(StandardVariable stdVar) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		CVTerm varTerm = createCvTerm(stdVar);
		int varId = varTerm.getCvTermId();
		stdVar.setId(varId);

		//getCvTermDao().save(varTerm);		
		
        if (stdVar.getConstraints() != null) {
            saveConstraint(varId, TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
            saveConstraint(varId, TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
        }
        
		saveSynonyms(varId, stdVar.getNameSynonyms());

		//getCvTermDao().update(varTerm);
		
		saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
		saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
		saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());
		saveRelationship(varId, TermId.STORED_IN.getId(), stdVar.getStoredIn());
		if(stdVar.getIsA()!=null) {//optional
			saveRelationship(varId, TermId.IS_A.getId(), stdVar.getIsA());
		}
		if (stdVar.getCropOntologyId() != null) {
		    saveOrUpdateCropOntologyId(stdVar.getProperty().getId(), stdVar.getCropOntologyId());
		}
		
		saveEnumerations(varId, stdVar.getEnumerations());
		
		return stdVar.getId();
	}
	
	private CVTerm createCvTerm(StandardVariable stdVar) throws MiddlewareQueryException {
		CVTerm cvTerm = new CVTerm();
		
		cvTerm.setCvTermId(getCvTermDao().getNegativeId("cvTermId"));
		cvTerm.setCv(CV_VARIABLES);
		cvTerm.setName(stdVar.getName());
		cvTerm.setDefinition(stdVar.getDescription());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		
		getCvTermDao().save(cvTerm);
		return cvTerm;
	}
	
	private void saveRelationship(int subjectId, int typeId, Term object) throws MiddlewareQueryException {
		if (object != null) {
			saveCvTermRelationship(subjectId, typeId, object.getId());
			
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
	
	private void saveConstraint(int cvTermId, int typeId, Integer constraintValue) throws MiddlewareQueryException {
        if (constraintValue != null) {
            CVTermProperty property = new CVTermProperty();
            
            property.setCvTermPropertyId(getCvTermPropertyDao().getNegativeId("cvTermPropertyId"));
            property.setTypeId(typeId);
            property.setValue(constraintValue.toString());
            property.setRank(0);
            property.setCvTermId(cvTermId);
            
            getCvTermPropertyDao().save(property);
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
        setWorkingDatabase(Database.LOCAL);
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
                else if (relationship.getTypeId() == TermId.IS_A.getId()) {
                    if (standardVariable.getIsA() != null) {
                        objectId = standardVariable.getIsA().getId();
                    }
                } 
                
                if (objectId != null && !objectId.equals(relationship.getObjectId())) {
                    relationship.setObjectId(objectId);
                    getCvTermRelationshipDao().update(relationship);
                }
            }
        }
        
        if (standardVariable.getCropOntologyId() != null) {
            saveOrUpdateCropOntologyId(standardVariable.getProperty().getId(), standardVariable.getCropOntologyId());
        }
    }
    
    public String validate(StandardVariable standardVariable, Operation operation) throws MiddlewareQueryException {

        StringBuilder errorCodes = null;
        
        Term term = getTermBuilder().findTermByName(standardVariable.getName(), CvId.VARIABLES);
        if (term != null && (operation == Operation.ADD || operation == Operation.UPDATE && term.getId() != standardVariable.getId())) {
            errorCodes = new StringBuilder();
            errorCodes.append(ErrorCode.NON_UNIQUE_NAME.getCode());
        }

        Integer varId = getStandardVariableBuilder().getIdByPropertyScaleMethod(
                    standardVariable.getProperty().getId(), 
                    standardVariable.getScale().getId(), 
                    standardVariable.getMethod().getId());
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

    //TODO: to be determined if crop ontology id should be at the property level or at the standard variable level
    public void saveOrUpdateCropOntologyId(Integer traitId, String cropOntologyId) throws MiddlewareQueryException {
        if (traitId < 0) {
            CVTerm trait = getCvTermDao().getById(traitId);
            if (trait != null) { 
                boolean found = false;
                List<CVTermProperty> traitProperties = getTermPropertyBuilder().findProperties(traitId);
                if (traitProperties != null) {
                    for (CVTermProperty traitProperty : traitProperties) {
                        if (traitProperty.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
                            found = true;
                            if (traitProperty.getValue() != null && !traitProperty.getValue().equals(cropOntologyId)) {
                                if (cropOntologyId == null || "".equals(cropOntologyId)) {
                                    getCvTermPropertyDao().makeTransient(traitProperty);
                                } else {
                                    traitProperty.setValue(cropOntologyId);
                                    getCvTermPropertyDao().update(traitProperty);
                                }
                            }
                            break;
                        }
                    }
                }
                if (!found) {
                    CVTermProperty traitProperty = new CVTermProperty();
                    traitProperty.setCvTermId(traitId);
                    traitProperty.setTypeId(TermId.CROP_ONTOLOGY_ID.getId());
                    traitProperty.setValue(cropOntologyId);
                    traitProperty.setRank(0);
                    traitProperty.setCvTermPropertyId(getCvTermPropertyDao().getNegativeId("cvTermPropertyId"));
                    getCvTermPropertyDao().save(traitProperty);
                }
            }
        }
    }
}
