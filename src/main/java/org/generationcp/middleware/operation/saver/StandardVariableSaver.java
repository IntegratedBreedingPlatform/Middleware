/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;

	public StandardVariableSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void delete(StandardVariable stdVar) throws MiddlewareException {
		this.deleteEnumerations(stdVar.getId(), stdVar.getEnumerations());

		if (stdVar.getCropOntologyId() != null) {
			this.deleteCropOntologyId(stdVar.getProperty().getId(), stdVar.getCropOntologyId());
		}

		this.deleteRelationship(stdVar.getId(), TermId.STORED_IN.getId(), stdVar.getStoredIn());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_TYPE.getId(), stdVar.getDataType());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_SCALE.getId(), stdVar.getScale());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_METHOD.getId(), stdVar.getMethod());

		List<NameSynonym> nameSynonyms = this.getStandardVariableBuilder().createSynonyms(stdVar.getId());
		this.deleteSynonyms(stdVar.getId(), nameSynonyms);

		if (stdVar.getConstraints() != null) {
			this.deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			this.deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}

		this.deleteCvTerm(this.getCvTermDao().getById(stdVar.getId()));
	}

	public void deleteConstraints(StandardVariable stdVar) throws MiddlewareQueryException {
		if (stdVar.getConstraints() != null) {
			this.deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			this.deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}
	}

	public Integer save(StandardVariable stdVar) throws MiddlewareQueryException {
		CVTerm varTerm = this.createCvTerm(stdVar);
		int varId = varTerm.getCvTermId();
		stdVar.setId(varId);

		if (stdVar.getConstraints() != null) {
			Integer minValueId = this.saveConstraint(varId, TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			Integer maxValueId = this.saveConstraint(varId, TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
			stdVar.getConstraints().setMinValueId(minValueId);
			stdVar.getConstraints().setMaxValueId(maxValueId);
		}

		this.saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		this.saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
		this.saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
		this.saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());
		this.saveRelationship(varId, TermId.STORED_IN.getId(), stdVar.getStoredIn());

		if (stdVar.getIsA() != null) {
			this.saveRelationship(varId, TermId.IS_A.getId(), stdVar.getIsA());
		}

		this.saveEnumerations(varId, stdVar.getEnumerations());

		return stdVar.getId();
	}

	public void saveConstraints(int standardVariableId, VariableConstraints constraints) throws MiddlewareQueryException {
		if (constraints.getMinValueId() == null) { // add
			Integer minId = this.saveConstraint(standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
			constraints.setMinValueId(minId);
		} else { // update
			this.updateConstraint(constraints.getMinValueId(), standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
		}

		if (constraints.getMaxValueId() == null) {// add
			Integer maxId = this.saveConstraint(standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
			constraints.setMaxValueId(maxId);
		} else { // update
			this.updateConstraint(constraints.getMaxValueId(), standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
		}

	}

	public void saveEnumeration(StandardVariable variable, Enumeration enumeration, Integer cvId) throws MiddlewareException {

		this.validateInputEnumeration(variable, enumeration);

		// Save cvterm entry of the new valid value
		CVTerm cvTerm = this.createCvTerm(enumeration, cvId);
		enumeration.setId(cvTerm.getCvTermId());

		// save cvterm relationship
		this.saveCvTermRelationship(variable.getId(), TermId.HAS_VALUE.getId(), enumeration.getId());
	}

	private void validateInputEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException {
		String name = enumeration.getName();
		String definition = enumeration.getDescription();
		StringBuilder errorMessage = new StringBuilder("");

		if (StringUtils.isEmpty(name)) {
			errorMessage.append("\nname is null or empty");
		}

		if (variable.getEnumerationByName(name) != null) {
			errorMessage.append("\nthe value with name = " + name + " already exists.");
		}

		if (StringUtils.isEmpty(definition)) {
			errorMessage.append("\ndefinition is null or empty");
		}
		if (variable.getEnumerationByDescription(definition) != null) {
			errorMessage.append("\nthe value with definition = " + definition + " already exists.");
		}

		if (errorMessage.length() > 0) {

			if (enumeration.getId() != null && errorMessage.toString().contains("already exists")
					&& !errorMessage.toString().contains("null or empty")) {
				// Ignore. Operation is UPDATE.
			} else {
				throw new MiddlewareException(errorMessage.toString());
			}
		}

	}

	private CVTerm createCvTerm(StandardVariable stdVar) throws MiddlewareQueryException {

		// check to see if term exists in DB before we create a new one
		CVTerm cvTerm = this.getCvTermDao().getByNameAndCvId(stdVar.getName(), StandardVariableSaver.CV_VARIABLES);

		if (cvTerm == null) {

			cvTerm = new CVTerm();
			cvTerm.setCv(StandardVariableSaver.CV_VARIABLES);
			cvTerm.setName(stdVar.getName());
			cvTerm.setDefinition(stdVar.getDescription());
			cvTerm.setIsObsolete(false);
			cvTerm.setIsRelationshipType(false);

			this.getCvTermDao().save(cvTerm);
		}

		return cvTerm;
	}

	private CVTerm createCvTerm(Enumeration enumeration, Integer cvId) throws MiddlewareQueryException {
		CVTerm cvTerm = new CVTerm();
		cvTerm.setCv(cvId);
		cvTerm.setName(enumeration.getName());
		cvTerm.setDefinition(enumeration.getDescription());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		this.getCvTermDao().save(cvTerm);
		return cvTerm;
	}

	public CV createCv(StandardVariable variable) throws MiddlewareQueryException {
		CV cv = new CV();
		cv.setName(String.valueOf(variable.getId()));
		cv.setDefinition(String.valueOf(variable.getName() + " - " + variable.getDescription()));
		this.getCvDao().save(cv);
		return cv;

	}

	private void saveRelationship(int subjectId, int typeId, Term object) throws MiddlewareQueryException {
		if (object != null) {
			// see if this relationship already exists (this saves us from dealing with a Unique Constraint Exception)
			boolean exists = false;
			CVTermRelationship relationship = this.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			if (relationship != null && relationship.getObjectId() == object.getId()) {

				// the relationship exists, as stipulated by unique constraint subject-type-object must be unique
				exists = true;
			}
			// save to DB if this relationship does not already exist
			if (!exists) {
				this.saveCvTermRelationship(subjectId, typeId, object.getId());
			}
		} else {
			throw new MiddlewareQueryException("The CvTermRelationship field is required for " + subjectId + " with relationship type of "
					+ typeId);
		}
	}

	private void saveCvTermRelationship(int subjectId, int typeId, int objectId) throws MiddlewareQueryException {

		CVTermRelationship relationship = new CVTermRelationship();

		relationship.setCvTermRelationshipId(this.getCvTermRelationshipDao().getNextId("cvTermRelationshipId"));
		relationship.setSubjectId(subjectId);
		relationship.setTypeId(typeId);
		relationship.setObjectId(objectId);

		this.getCvTermRelationshipDao().save(relationship);

	}

	private Integer saveConstraint(int cvTermId, int typeId, Double constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			CVTermProperty property = new CVTermProperty();
			int nextId = this.getCvTermPropertyDao().getNextId("cvTermPropertyId");
			property.setCvTermPropertyId(nextId);
			property.setTypeId(typeId);
			property.setValue(constraintValue.toString());
			property.setRank(0);
			property.setCvTermId(cvTermId);
			this.getCvTermPropertyDao().save(property);
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
			this.getCvTermPropertyDao().merge(property);
		}
	}

	private void saveEnumerations(int varId, List<Enumeration> enumerations) throws MiddlewareQueryException {
		if (enumerations != null && !enumerations.isEmpty()) {
			for (Enumeration enumeration : enumerations) {
				this.saveCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
			}
		}
	}

	public void update(StandardVariable standardVariable) throws MiddlewareQueryException {
		CVTerm varTerm = this.getStandardVariableBuilder().getCvTerm(standardVariable.getName(), CvId.VARIABLES.getId());
		varTerm.setName(standardVariable.getName());
		varTerm.setDefinition(standardVariable.getDescription());
		varTerm.setIsObsolete(false);
		varTerm.setIsRelationshipType(false);
		this.getCvTermDao().update(varTerm);

		List<CVTermRelationship> relationships = this.getCvTermRelationshipDao().getBySubject(standardVariable.getId());
		if (relationships != null && !relationships.isEmpty()) {
			for (CVTermRelationship relationship : relationships) {
				Integer objectId = this.getRelevantObjectIdBasedOnType(relationship.getTypeId(), standardVariable);
				// STORED_IN can not be updated.

				if (objectId != null && !objectId.equals(relationship.getObjectId())) {
					relationship.setObjectId(objectId);
					this.getCvTermRelationshipDao().update(relationship);
				}
			}
		}

	}

	protected Integer getRelevantObjectIdBasedOnType(final Integer typeID, StandardVariable standardVariable) {
		Integer objectId = null;

		if (typeID == TermId.HAS_PROPERTY.getId()) {
			if (standardVariable.getProperty() != null) {
				objectId = standardVariable.getProperty().getId();
			}
		} else if (typeID == TermId.HAS_SCALE.getId()) {
			if (standardVariable.getScale() != null) {
				objectId = standardVariable.getScale().getId();
			}
		} else if (typeID == TermId.HAS_METHOD.getId()) {
			if (standardVariable.getMethod() != null) {
				objectId = standardVariable.getMethod().getId();
			}
		} else if (typeID == TermId.HAS_TYPE.getId()) {
			if (standardVariable.getDataType() != null) {
				objectId = standardVariable.getDataType().getId();
			}
		} else if (typeID == TermId.STORED_IN.getId()) {
			if (standardVariable.getStoredIn() != null) {
				objectId = standardVariable.getStoredIn().getId();
			}
		}

		return objectId;
	}

	public String validate(StandardVariable standardVariable, Operation operation) throws MiddlewareQueryException {

		StringBuilder errorCodes = null;

		Term term = this.getTermBuilder().findTermByName(standardVariable.getName(), CvId.VARIABLES);
		if (term != null && operation == Operation.ADD) {
			errorCodes = new StringBuilder();
			errorCodes.append(ErrorCode.NON_UNIQUE_NAME.getCode());
		}

		Integer varId =
				this.getStandardVariableBuilder().getIdByPropertyScaleMethodRole(standardVariable.getProperty().getId(),
						standardVariable.getScale().getId(), standardVariable.getMethod().getId(), standardVariable.getPhenotypicType());
		if (varId != null && (operation == Operation.ADD || operation == Operation.UPDATE && varId != standardVariable.getId())) {
			if (errorCodes == null) {
				errorCodes = new StringBuilder();
			} else {
				errorCodes.append(",");
			}
			errorCodes.append(ErrorCode.NON_UNIQUE_PCM_COMBINATION.getCode());
		}

		return errorCodes != null ? errorCodes.toString() : null;
	}

	public void saveOrUpdateCropOntologyId(Integer traitId, String cropOntologyId) throws MiddlewareQueryException {

		CVTermProperty cropOntologyProperty = this.getCvTermPropertyDao().getOneByCvTermAndType(traitId, TermId.CROP_ONTOLOGY_ID.getId());
		if (cropOntologyProperty != null && cropOntologyProperty.getValue() != null
				&& cropOntologyProperty.getValue().equalsIgnoreCase(cropOntologyId)) {
			// do nothing, no change
			return;
		}

		if (cropOntologyId == null || "".equals(cropOntologyId)) {
			if (cropOntologyProperty != null) {
				this.getCvTermPropertyDao().makeTransient(cropOntologyProperty);
			}
			return;
		}

		boolean isForCreate = true;
		if (cropOntologyProperty == null) {
			if (cropOntologyId == null || "".equals(cropOntologyId.trim())) {
				isForCreate = false;
			} else {
				cropOntologyProperty = new CVTermProperty();
				cropOntologyProperty.setCvTermId(traitId);
				cropOntologyProperty.setRank(0);
				cropOntologyProperty.setTypeId(TermId.CROP_ONTOLOGY_ID.getId());
				cropOntologyProperty.setCvTermPropertyId(this.getCvTermPropertyDao().getNextId("cvTermPropertyId"));
			}
		}
		if (isForCreate) {
			cropOntologyProperty.setValue(cropOntologyId);
			this.getCvTermPropertyDao().saveOrUpdate(cropOntologyProperty);
		}

	}

	public void deleteEnumeration(int varId, Enumeration enumeration) throws MiddlewareException {
		this.deleteCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
		this.deleteCvTerm(this.getCvTermDao().getById(enumeration.getId()));
	}

	public void deleteEnumerations(int varId, List<Enumeration> enumerations) throws MiddlewareException {
		if (enumerations != null && !enumerations.isEmpty()) {
			for (Enumeration enumeration : enumerations) {
				this.deleteEnumeration(varId, enumeration);
			}
		}
	}

	private void deleteCvTermRelationship(int subjectId, int typeId, int objectId) throws MiddlewareException {
		Term typeTerm = this.getTermBuilder().get(typeId);
		if (typeTerm != null) {
			CVTermRelationship cvRelationship =
					this.getCvTermRelationshipDao().getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
			if (cvRelationship != null) {
				CVTermRelationshipDao dao = this.getCvTermRelationshipDao();
				try {
					dao.makeTransient(cvRelationship);
				} catch (MiddlewareQueryException e) {
					throw new MiddlewareQueryException(e.getMessage(), e);
				}
			}
		}
	}

	public void deleteCropOntologyId(Integer traitId, String cropOntologyId) throws MiddlewareQueryException {
		CVTerm trait = this.getCvTermDao().getById(traitId);
		if (trait == null) {
			throw new MiddlewareQueryException("Specified trait ID for deletion of crop ontology ID is not valid");
		}

		List<CVTermProperty> traitProperties = this.getTermPropertyBuilder().findProperties(traitId);

		for (CVTermProperty traitProperty : traitProperties) {
			if (traitProperty.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
				if (traitProperty.getValue() != null && !traitProperty.getValue().equals(cropOntologyId)) {
					this.getCvTermPropertyDao().makeTransient(traitProperty);
				}
				break;
			}
		}
	}

	private void deleteRelationship(int subjectId, int typeId, Term object) throws MiddlewareException {
		if (object != null) {
			this.deleteCvTermRelationship(subjectId, typeId, object.getId());
		} else {
			throw new MiddlewareQueryException("The CvTermRelationship field is required for " + subjectId + " with relationship type of "
					+ typeId);
		}
	}

	private void deleteSynonyms(int cvTermId, List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
		if (nameSynonyms != null && !nameSynonyms.isEmpty()) {
			List<CVTermSynonym> cvTermSynonyms = this.getCvTermSynonymDao().getByCvTermId(cvTermId);
			if (cvTermSynonyms != null) {
				for (CVTermSynonym cvTermSynonym : cvTermSynonyms) {
					this.getCvTermSynonymDao().makeTransient(cvTermSynonym);
				}
			}
		}
	}

	private void deleteConstraint(int cvTermId, int typeId, Double constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			List<CVTermProperty> properties = this.getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId);

			for (CVTermProperty cvTermProperty : properties) {
				this.getCvTermPropertyDao().makeTransient(cvTermProperty);
			}
		}
	}

	public void deleteCvTerm(CVTerm cvTerm) throws MiddlewareQueryException {
		CVTermDao dao = this.getCvTermDao();
		try {
			dao.makeTransient(cvTerm);
		} catch (MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
}
