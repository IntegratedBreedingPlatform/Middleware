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
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

import java.util.List;

public class StandardVariableSaver extends Saver {

	private static final int CV_VARIABLES = 1040;

	private DaoFactory daoFactory;

	public StandardVariableSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public void delete(final StandardVariable stdVar) throws MiddlewareException {
		this.deleteEnumerations(stdVar.getId(), stdVar.getEnumerations());

		if (stdVar.getCropOntologyId() != null) {
			this.deleteCropOntologyId(stdVar.getProperty().getId(), stdVar.getCropOntologyId());
		}

		this.deleteRelationship(stdVar.getId(), TermId.HAS_TYPE.getId(), stdVar.getDataType());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_SCALE.getId(), stdVar.getScale());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		this.deleteRelationship(stdVar.getId(), TermId.HAS_METHOD.getId(), stdVar.getMethod());

		final List<NameSynonym> nameSynonyms = this.getStandardVariableBuilder().createSynonyms(stdVar.getId());
		this.deleteSynonyms(stdVar.getId(), nameSynonyms);

		if (stdVar.getConstraints() != null) {
			this.deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			this.deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}

		this.deleteCvTerm(daoFactory.getCvTermDao().getById(stdVar.getId()));
	}

	public void deleteConstraints(final StandardVariable stdVar) throws MiddlewareQueryException {
		if (stdVar.getConstraints() != null) {
			this.deleteConstraint(stdVar.getId(), TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			this.deleteConstraint(stdVar.getId(), TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
		}
	}

	public Integer save(final StandardVariable stdVar) throws MiddlewareQueryException {
		final CVTerm varTerm = this.createCvTerm(stdVar);
		final int varId = varTerm.getCvTermId();
		stdVar.setId(varId);

		if (stdVar.getConstraints() != null) {
			final Integer minValueId = this.saveConstraint(varId, TermId.MIN_VALUE.getId(), stdVar.getConstraints().getMinValue());
			final Integer maxValueId = this.saveConstraint(varId, TermId.MAX_VALUE.getId(), stdVar.getConstraints().getMaxValue());
			stdVar.getConstraints().setMinValueId(minValueId);
			stdVar.getConstraints().setMaxValueId(maxValueId);
		}

		this.saveRelationship(varId, TermId.HAS_PROPERTY.getId(), stdVar.getProperty());
		this.saveRelationship(varId, TermId.HAS_SCALE.getId(), stdVar.getScale());
		this.saveRelationship(varId, TermId.HAS_METHOD.getId(), stdVar.getMethod());
		this.saveRelationship(varId, TermId.HAS_TYPE.getId(), stdVar.getDataType());

		if (stdVar.getIsA() != null) {
			this.saveRelationship(varId, TermId.IS_A.getId(), stdVar.getIsA());
		}

		this.saveEnumerations(varId, stdVar);

		return stdVar.getId();
	}

	public void saveConstraints(final int standardVariableId, final VariableConstraints constraints) throws MiddlewareQueryException {
		if (constraints.getMinValueId() == null) { // add
			final Integer minId = this.saveConstraint(standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
			constraints.setMinValueId(minId);
		} else { // update
			this.updateConstraint(constraints.getMinValueId(), standardVariableId, TermId.MIN_VALUE.getId(), constraints.getMinValue());
		}

		if (constraints.getMaxValueId() == null) {// add
			final Integer maxId = this.saveConstraint(standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
			constraints.setMaxValueId(maxId);
		} else { // update
			this.updateConstraint(constraints.getMaxValueId(), standardVariableId, TermId.MAX_VALUE.getId(), constraints.getMaxValue());
		}

	}

	/*
	 * Saves CvTerm and CvTermRelationship in the database Checktypes/Enumerations/Categories are now saved under Scale in the new Ontology
	 * Design See BMS-36 for more information
	 */
	public void saveEnumeration(final StandardVariable variable, final Enumeration enumeration, final Integer cvId)
			throws MiddlewareException {

		this.validateInputEnumeration(variable, enumeration);

		// Save cvterm entry of the new valid value
		final CVTerm cvTerm = this.createCvTerm(enumeration, cvId);
		enumeration.setId(cvTerm.getCvTermId());
		// save cvterm relationship
		this.saveCvTermRelationship(variable.getScale().getId(), TermId.HAS_VALUE.getId(), enumeration.getId());
	}

	private void validateInputEnumeration(final StandardVariable variable, final Enumeration enumeration) throws MiddlewareException {
		final String name = enumeration.getName();
		final String definition = enumeration.getDescription();
		final StringBuilder errorMessage = new StringBuilder("");

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

	private CVTerm createCvTerm(final StandardVariable stdVar) throws MiddlewareQueryException {

		// check to see if term exists in DB before we create a new one
		CVTerm cvTerm = daoFactory.getCvTermDao().getByNameAndCvId(stdVar.getName(), StandardVariableSaver.CV_VARIABLES);

		if (cvTerm == null) {

			cvTerm = new CVTerm();
			cvTerm.setCv(StandardVariableSaver.CV_VARIABLES);
			cvTerm.setName(stdVar.getName());
			cvTerm.setDefinition(stdVar.getDescription());
			cvTerm.setIsObsolete(false);
			cvTerm.setIsRelationshipType(false);
			cvTerm.setIsSystem(false);
			daoFactory.getCvTermDao().save(cvTerm);
		}

		return cvTerm;
	}

	private CVTerm createCvTerm(final Enumeration enumeration, final Integer cvId) throws MiddlewareQueryException {
		final CVTerm cvTerm = new CVTerm();
		cvTerm.setCv(cvId);
		cvTerm.setName(enumeration.getName());
		cvTerm.setDefinition(enumeration.getDescription());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		cvTerm.setIsSystem(false);
		daoFactory.getCvTermDao().save(cvTerm);
		return cvTerm;
	}

	public CV createCv(final StandardVariable variable) throws MiddlewareQueryException {
		final CV cv = new CV();
		cv.setName(String.valueOf(variable.getId()));
		cv.setDefinition(String.valueOf(variable.getName() + " - " + variable.getDescription()));
		this.daoFactory.getCvDao().save(cv);
		return cv;

	}

	private void saveRelationship(final int subjectId, final int typeId, final Term object) throws MiddlewareQueryException {
		if (object != null) {
			// see if this relationship already exists (this saves us from dealing with a Unique Constraint Exception)
			boolean exists = false;
			final CVTermRelationship relationship = daoFactory.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			if (relationship != null && relationship.getObjectId() == object.getId()) {

				// the relationship exists, as stipulated by unique constraint subject-type-object must be unique
				exists = true;
			}
			// save to DB if this relationship does not already exist
			if (!exists) {
				this.saveCvTermRelationship(subjectId, typeId, object.getId());
			}
		} else {
			throw new MiddlewareQueryException(
					"The CvTermRelationship field is required for " + subjectId + " with relationship type of " + typeId);
		}
	}

	private void saveCvTermRelationship(final int subjectId, final int typeId, final int objectId) throws MiddlewareQueryException {
		final CVTermRelationship relationship = new CVTermRelationship();
		relationship.setSubjectId(subjectId);
		relationship.setTypeId(typeId);
		relationship.setObjectId(objectId);
		daoFactory.getCvTermRelationshipDao().save(relationship);
	}

	private Integer saveConstraint(final int cvTermId, final int typeId, final Double constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			final CVTermProperty property = new CVTermProperty();
			property.setTypeId(typeId);
			property.setValue(constraintValue.toString());
			property.setRank(0);
			property.setCvTermId(cvTermId);
			daoFactory.getCvTermPropertyDao().save(property);
			return property.getCvTermPropertyId();
		}
		return null;
	}

	private void updateConstraint(final int constraintId, final int cvTermId, final int typeId, final Double constraintValue)
			throws MiddlewareQueryException {
		if (constraintValue != null) {
			final CVTermProperty property = new CVTermProperty();
			property.setCvTermPropertyId(constraintId);
			property.setTypeId(typeId);
			property.setValue(constraintValue.toString());
			property.setRank(0);
			property.setCvTermId(cvTermId);
			daoFactory.getCvTermPropertyDao().merge(property);
		}
	}

	private void saveEnumerations(final int varId, final StandardVariable variable) throws MiddlewareQueryException {

		final List<Enumeration> enumerations = variable.getEnumerations();

		if (enumerations != null && !enumerations.isEmpty()) {
			for (final Enumeration enumeration : enumerations) {

				if (enumeration.getId() == null) {

					final Integer cvId = this.getEnumerationCvId(variable);
					final CVTerm cvTerm = this.createCvTerm(enumeration, cvId);
					enumeration.setId(cvTerm.getCvTermId());

				}

				this.saveCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());

			}
		}
	}

	private Integer getEnumerationCvId(final StandardVariable variable) throws MiddlewareQueryException {

		// Check if cv entry of enumeration already exists
		// Add cv entry of the standard variable if none found
		Integer cvId = this.daoFactory.getCvDao().getIdByName(String.valueOf(variable.getId()));

		if (cvId == null) {
			cvId = this.createCv(variable).getCvId();
		}

		return cvId;
	}

	public void update(final StandardVariable standardVariable) throws MiddlewareQueryException {
		final CVTerm varTerm = this.getStandardVariableBuilder().getCvTerm(standardVariable.getName(), CvId.VARIABLES.getId());
		varTerm.setName(standardVariable.getName());
		varTerm.setDefinition(standardVariable.getDescription());
		varTerm.setIsObsolete(false);
		varTerm.setIsRelationshipType(false);
		daoFactory.getCvTermDao().update(varTerm);

		final List<CVTermRelationship> relationships = daoFactory.getCvTermRelationshipDao().getBySubject(standardVariable.getId());
		if (relationships != null && !relationships.isEmpty()) {
			for (final CVTermRelationship relationship : relationships) {
				final Integer objectId = this.getRelevantObjectIdBasedOnType(relationship.getTypeId(), standardVariable);
				// STORED_IN can not be updated.

				if (objectId != null && !objectId.equals(relationship.getObjectId())) {
					relationship.setObjectId(objectId);
					daoFactory.getCvTermRelationshipDao().update(relationship);
				}
			}
		}

	}

	protected Integer getRelevantObjectIdBasedOnType(final Integer typeID, final StandardVariable standardVariable) {
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
		}

		return objectId;
	}

	public String validate(final StandardVariable standardVariable, final Operation operation) throws MiddlewareQueryException {

		StringBuilder errorCodes = null;

		final Term term = this.getTermBuilder().findTermByName(standardVariable.getName(), CvId.VARIABLES);
		if (term != null && operation == Operation.ADD) {
			errorCodes = new StringBuilder();
			errorCodes.append(ErrorCode.NON_UNIQUE_NAME.getCode());
		}

		final Integer varId = this.getStandardVariableBuilder().getIdByPropertyScaleMethodRole(standardVariable.getProperty().getId(),
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

	public void saveOrUpdateCropOntologyId(final Integer traitId, final String cropOntologyId) throws MiddlewareQueryException {

		CVTermProperty cropOntologyProperty = daoFactory.getCvTermPropertyDao().getOneByCvTermAndType(traitId, TermId.CROP_ONTOLOGY_ID.getId());
		if (cropOntologyProperty != null && cropOntologyProperty.getValue() != null
				&& cropOntologyProperty.getValue().equalsIgnoreCase(cropOntologyId)) {
			// do nothing, no change
			return;
		}

		if (cropOntologyId == null || "".equals(cropOntologyId)) {
			if (cropOntologyProperty != null) {
				daoFactory.getCvTermPropertyDao().makeTransient(cropOntologyProperty);
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
			}
		}
		if (isForCreate) {
			cropOntologyProperty.setValue(cropOntologyId);
			daoFactory.getCvTermPropertyDao().saveOrUpdate(cropOntologyProperty);
		}

	}

	public void deleteEnumeration(final int varId, final Enumeration enumeration) throws MiddlewareException {
		this.deleteCvTermRelationship(varId, TermId.HAS_VALUE.getId(), enumeration.getId());
		this.deleteCvTerm(daoFactory.getCvTermDao().getById(enumeration.getId()));
	}

	public void deleteEnumerations(final int varId, final List<Enumeration> enumerations) throws MiddlewareException {
		if (enumerations != null && !enumerations.isEmpty()) {
			for (final Enumeration enumeration : enumerations) {
				this.deleteEnumeration(varId, enumeration);
			}
		}
	}

	private void deleteCvTermRelationship(final int subjectId, final int typeId, final int objectId) throws MiddlewareException {
		final Term typeTerm = this.getTermBuilder().get(typeId);
		if (typeTerm != null) {
			final CVTermRelationship cvRelationship =
					daoFactory.getCvTermRelationshipDao().getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
			if (cvRelationship != null) {
				final CVTermRelationshipDao dao = daoFactory.getCvTermRelationshipDao();
				try {
					dao.makeTransient(cvRelationship);
				} catch (final MiddlewareQueryException e) {
					throw new MiddlewareQueryException(e.getMessage(), e);
				}
			}
		}
	}

	public void deleteCropOntologyId(final Integer traitId, final String cropOntologyId) throws MiddlewareQueryException {
		final CVTerm trait = daoFactory.getCvTermDao().getById(traitId);
		if (trait == null) {
			throw new MiddlewareQueryException("Specified trait ID for deletion of crop ontology ID is not valid");
		}

		final List<CVTermProperty> traitProperties = daoFactory.getCvTermPropertyDao().getByCvTermId(traitId);

		for (final CVTermProperty traitProperty : traitProperties) {
			if (traitProperty.getTypeId() == TermId.CROP_ONTOLOGY_ID.getId()) {
				if (traitProperty.getValue() != null && !traitProperty.getValue().equals(cropOntologyId)) {
					daoFactory.getCvTermPropertyDao().makeTransient(traitProperty);
				}
				break;
			}
		}
	}

	private void deleteRelationship(final int subjectId, final int typeId, final Term object) throws MiddlewareException {
		if (object != null) {
			this.deleteCvTermRelationship(subjectId, typeId, object.getId());
		} else {
			throw new MiddlewareQueryException(
					"The CvTermRelationship field is required for " + subjectId + " with relationship type of " + typeId);
		}
	}

	private void deleteSynonyms(final int cvTermId, final List<NameSynonym> nameSynonyms) throws MiddlewareQueryException {
		if (nameSynonyms != null && !nameSynonyms.isEmpty()) {
			final List<CVTermSynonym> cvTermSynonyms = this.daoFactory.getCvTermSynonymDao().getByCvTermId(cvTermId);
			if (cvTermSynonyms != null) {
				for (final CVTermSynonym cvTermSynonym : cvTermSynonyms) {
					this.daoFactory.getCvTermSynonymDao().makeTransient(cvTermSynonym);
				}
			}
		}
	}

	private void deleteConstraint(final int cvTermId, final int typeId, final Double constraintValue) throws MiddlewareQueryException {
		if (constraintValue != null) {
			final List<CVTermProperty> properties = daoFactory.getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId);

			for (final CVTermProperty cvTermProperty : properties) {
				daoFactory.getCvTermPropertyDao().makeTransient(cvTermProperty);
			}
		}
	}

	public void deleteCvTerm(final CVTerm cvTerm) throws MiddlewareQueryException {
		final CVTermDao dao = daoFactory.getCvTermDao();
		try {
			dao.makeTransient(cvTerm);
		} catch (final MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}
}
