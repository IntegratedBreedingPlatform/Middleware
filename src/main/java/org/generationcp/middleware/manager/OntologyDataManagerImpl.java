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

package org.generationcp.middleware.manager;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.VariableCache;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Transactional
public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(OntologyDataManagerImpl.class);

	public static final String DELETE_TERM_ERROR_MESSAGE = "The term you selected cannot be deleted";

	private DaoFactory daoFactory;

	public OntologyDataManagerImpl() {
	}

	public OntologyDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Term getTermById(final int termId) {
		return this.getTermBuilder().get(termId);
	}

	@Override
	public StandardVariable getStandardVariable(final int stdVariableId, final String programUUID) {
		return this.getStandardVariableBuilder().create(stdVariableId, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariables(final List<Integer> standardVariableIds, final String programUUID) {
		return this.getStandardVariableBuilder().create(standardVariableIds, programUUID);
	}

	@Override
	public List<StandardVariableSummary> getStandardVariableSummaries(final List<Integer> standardVariableIds) {
		return this.getStandardVariableBuilder().getStandardVariableSummaries(standardVariableIds);
	}

	@Override
	public void addStandardVariable(final StandardVariable stdVariable, final String programUUID) {

		final Term existingStdVar = this.findTermByName(stdVariable.getName(), CvId.VARIABLES);
		if (existingStdVar != null) {
			throw new MiddlewareQueryException(String.format(
				"Error in addStandardVariable, Variable with name \"%s\" already exists",
				stdVariable.getName()));
		}

		try {

			// check if scale, property and method exists first
			final Term scale = this.findTermByName(stdVariable.getScale().getName(), CvId.SCALES);
			if (scale == null) {
				stdVariable.setScale(this.getTermSaver().save(stdVariable.getScale().getName(), stdVariable.getScale().getDefinition(),
					CvId.SCALES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new scale with id = " + stdVariable.getScale().getId());
				}
			}

			final Term property = this.findTermByName(stdVariable.getProperty().getName(), CvId.PROPERTIES);
			if (property == null) {
				stdVariable.setProperty(this.getTermSaver().save(stdVariable.getProperty().getName(),
					stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new property with id = " + stdVariable.getProperty().getId());
				}
			}

			final Term method = this.findTermByName(stdVariable.getMethod().getName(), CvId.METHODS);
			if (method == null) {
				stdVariable.setMethod(this.getTermSaver().save(stdVariable.getMethod().getName(), stdVariable.getMethod().getDefinition(),
					CvId.METHODS));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new method with id = " + stdVariable.getMethod().getId());
				}
			}

			if (this.findStandardVariableByTraitScaleMethodNames(stdVariable.getProperty().getName(), stdVariable.getScale().getName(),
				stdVariable.getMethod().getName(), programUUID) == null) {
				this.getStandardVariableSaver().save(stdVariable);
			}

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error in addStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public Set<StandardVariable> findStandardVariablesByNameOrSynonym(final String nameOrSynonym, final String programUUID) {
		final Set<StandardVariable> standardVariables = new HashSet<>();
		standardVariables.addAll(this.getStandardVariablesByNameOrSynonym(nameOrSynonym, programUUID));
		return standardVariables;
	}

	private Set<StandardVariable> getStandardVariablesByNameOrSynonym(final String nameOrSynonym, final String programUUID) {
		final Set<StandardVariable> standardVariables = new HashSet<>();
		final List<Integer> stdVarIds = this.daoFactory.getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, CvId.VARIABLES.getId());
		for (final Integer stdVarId : stdVarIds) {
			standardVariables.add(this.getStandardVariable(stdVarId, programUUID));
		}
		return standardVariables;
	}

	@Override
	public Term findMethodById(final int id) {
		return this.getMethodBuilder().findMethodById(id);
	}

	@Override
	public Term findMethodByName(final String name) {
		return this.getMethodBuilder().findMethodByName(name);
	}

	@Override
	public StandardVariable findStandardVariableByTraitScaleMethodNames(final String property, final String scale, final String method, final String programUUID) {
		final Term termProperty;
		Term termScale;
		final Term termMethod;
		Integer propertyId = null, scaleId = null, methodId = null;

		termProperty = this.findTermByName(property, CvId.PROPERTIES);
		termScale = this.findTermByName(scale, CvId.SCALES);
		termMethod = this.findTermByName(method, CvId.METHODS);

		if (termProperty != null) {
			propertyId = termProperty.getId();
		}

		if (termScale != null) {
			scaleId = termScale.getId();
		}

		if (termMethod != null) {
			methodId = termMethod.getId();
		}

		return this.getStandardVariableBuilder().getByPropertyScaleMethod(propertyId, scaleId, methodId, programUUID);
	}

	@Override
	public List<Term> getAllTermsByCvId(final CvId cvId) {
		return this.getTermBuilder().getTermsByCvId(cvId);
	}

	@Override
	public Term findTermByName(final String name, final CvId cvId) {
		return this.getTermBuilder().findTermByName(name, cvId);
	}

	@Override
	public Term addTerm(final String name, final String definition, final CvId cvId) {
		Term term = this.findTermByName(name, cvId);

		if (term != null) {
			return term;
		}

		try {
			if (CvId.VARIABLES.getId() != cvId.getId()) {

				term = this.getTermSaver().save(name, definition, cvId);

			} else {
				throw new MiddlewareQueryException("Variables cannot be used in this method.");
			}
			return term;
		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in addTerm: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateTerm(final Term term) {

		if (term == null) {
			return;
		}

		try {

			this.getTermSaver().update(term);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateTerms(final List<Term> terms) {

		for (final Term term : terms) {
			this.getTermSaver().update(term);
		}

	}

	@Override
	public List<Term> getDataTypes() {
		final List<Integer> dataTypeIds =
			Arrays.asList(TermId.CLASS.getId(), TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId(),
				TermId.NUMERIC_DBID_VARIABLE.getId(), TermId.CHARACTER_DBID_VARIABLE.getId(), TermId.CHARACTER_VARIABLE.getId(),
				TermId.TIMESTAMP_VARIABLE.getId(), TermId.CATEGORICAL_VARIABLE.getId());
		return this.getTermBuilder().getTermsByIds(dataTypeIds);
	}

	@Override
	public Map<String, StandardVariable> getStandardVariablesForPhenotypicType(
		final PhenotypicType type, final String programUUID, final int start,
		final int numOfRows) {

		final TreeMap<String, StandardVariable> standardVariables = new TreeMap<>();
		List<Integer> localStdVariableIds = new ArrayList<>();

		localStdVariableIds = this.daoFactory.getCvTermDao().getStandardVariableIdsByPhenotypicType(type);

		for (final Integer stdVarId : localStdVariableIds) {
			final StandardVariable sd = this.getStandardVariableBuilder().create(stdVarId, programUUID);
			standardVariables.put(sd.getName(), sd);
		}

		final Set<String> standardVariablesSet = standardVariables.keySet();
		final Object[] list = standardVariablesSet.toArray();

		final String startSD = list[start].toString();

		final int end = start + numOfRows > list.length - 1 ? list.length - 1 : start + numOfRows;
		final String endSD = list[end].toString();

		return standardVariables.subMap(startSD, true, endSD, true);
	}

	@Override
	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(final List<String> headers, final String programUUID) {
		return this.getStandardVariableBuilder().getStandardVariablesInProjects(headers, programUUID);
	}

	@Override
	public List<Term> findTermsByNameOrSynonym(final String nameOrSynonym, final CvId cvId) {
		final List<Term> terms = new ArrayList<>();
		final List<CVTerm> cvTerms = new ArrayList<>();
		List<Integer> termIds = new ArrayList<>();

		termIds = this.daoFactory.getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, cvId.getId());
		for (final Integer id : termIds) {
			cvTerms.add(this.daoFactory.getCvTermDao().getById(id));
		}
		for (final CVTerm cvTerm : cvTerms) {
			terms.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}
		return terms;
	}

	@Override
	public Term addProperty(final String name, final String definition, final int isA) {

		Term term = this.findTermByName(name, CvId.PROPERTIES);

		if (term != null) {
			return term;
		}

		try {

			term = this.getTermSaver().save(name, definition, CvId.PROPERTIES);
			final Term isATerm = this.getTermById(isA);

			if (isATerm != null) {
				this.getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), isATerm.getId());
			} else {
				throw new MiddlewareQueryException("The isA passed is not a valid Class term: " + isA);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
		}

		return term;
	}

	@Override
	public Term addOrUpdateTerm(final String name, final String definition, final CvId cvId) {

		try {

			final Term term = this.saveOrUpdateCvTerm(name, definition, cvId);

			return term;
		} catch (final MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}

	}

	@Override
	public void addOrUpdateCropOntologyID(final Property property, final String cropOntologyID) {
		assert !StringUtils.isEmpty(cropOntologyID);

		try {

			this.getStandardVariableSaver().saveOrUpdateCropOntologyId(property.getTerm().getId(), cropOntologyID);

		} catch (final MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	@Override
	public Term addOrUpdateTermAndRelationship(final String name, final String definition, final CvId cvId, final int typeId, final int objectId) {
		try {
			final Term term = this.saveOrUpdateCvTerm(name, definition, cvId);
			this.saveOrUpdateCvTermRelationship(term.getId(), objectId, typeId);
			return term;
		} catch (final MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	@Override
	public Term updateTermAndRelationship(final Term term, final int typeId, final int objectId) {

		Term updatedTerm = null;

		if (term == null) {
			return null;
		}

		try {
			updatedTerm = this.getTermSaver().update(term);

			this.saveOrUpdateCvTermRelationship(updatedTerm.getId(), objectId, typeId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
		}

		return updatedTerm;
	}

	private Term saveOrUpdateCvTerm(final String name, final String definition, final CvId cvId) {
		Term term = this.findTermByName(name, cvId);

		// If term is not existing, add
		if (term == null) {
			term = this.getTermSaver().save(name, definition, cvId);
		} else {
			// If term is existing, update
			term.setDefinition(definition);
			this.getTermSaver().update(term);
		}
		return term;
	}

	@Override
	public void addCvTermRelationship(final int subjectId, final int objectId, final int typeId) {
		this.getTermRelationshipSaver().save(subjectId, typeId, objectId);
	}

	private void saveOrUpdateCvTermRelationship(final int subjectId, final int objectId, final int typeId) {
		final Term typeTerm = this.getTermById(typeId);
		if (typeTerm != null) {
			final CVTermRelationship cvRelationship =
				this.daoFactory.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			// add the relationship
			if (cvRelationship == null) {
				this.getTermRelationshipSaver().save(subjectId, typeId, objectId);
				// update the existing relationship
			} else {
				cvRelationship.setObjectId(objectId);
				this.getTermRelationshipSaver().saveOrUpdateRelationship(cvRelationship);
			}
		} else {
			throw new MiddlewareException("Error in saveOrUpdateCvTermRelationship: The relationship type passed is not a valid value.");
		}
	}

	@Override
	public Property getProperty(final int termId) {
		final Property property = new Property();

		property.setTerm(this.getTermBuilder().getTermOfProperty(termId, CvId.PROPERTIES.getId()));
		if (property.getTerm() != null) {
			property.setIsA(this.getTermBuilder().getTermOfClassOfProperty(termId, CvId.PROPERTIES.getId(), TermId.IS_A.getId()));
			property.setCropOntologyId(this.getStandardVariableBuilder().getCropOntologyId(property.getTerm()));
		}
		return property;
	}

	@Override
	public Property getProperty(final String name) {
		final Property property = new Property();

		property.setTerm(this.findTermByName(name, CvId.PROPERTIES));
		if (property.getTerm() != null) {
			property.setIsA(this.getTermBuilder().getTermOfClassOfProperty(property.getTerm().getId(), CvId.PROPERTIES.getId(),
				TermId.IS_A.getId()));
		}

		return property;
	}

	@Override
	public List<TraitClassReference> getAllTraitGroupsHierarchy(final boolean includePropertiesAndVariables) {
		return this.getTraitGroupBuilder().getAllTraitGroupsHierarchy(includePropertiesAndVariables);
	}

	@Override
	public List<Term> getTermsByIds(final List<Integer> ids) {
		return this.getTermBuilder().getTermsByIds(ids);
	}

	@Override
	public TraitClass addTraitClass(final String name, final String definition, final int parentTraitClassId) {
		Term term = this.findTermByName(name, CvId.IBDB_TERMS);

		if (term != null) {
			return new TraitClass(term, this.getTermById(parentTraitClassId));
		}

		try {

			term = this.getTermSaver().save(name, definition, CvId.IBDB_TERMS);

			if (term != null) {
				this.getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), parentTraitClassId);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in addTraitClass " + e.getMessage(), e);
		}
		return new TraitClass(term, this.getTermById(parentTraitClassId));

	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(final String programUUID) {
		final Set<StandardVariable> standardVariables = new HashSet<>();
		final List<Term> terms = this.getAllTermsByCvId(CvId.VARIABLES);
		for (final Term term : terms) {
			standardVariables.add(this.getStandardVariable(term.getId(), programUUID));
		}
		return standardVariables;
	}

	@Override
	public List<StandardVariable> getStandardVariables(
		final Integer traitClassId, final Integer propertyId, final Integer methodId, final Integer scaleId,
		final String programUUID) {
		final List<StandardVariable> standardVariables = new ArrayList<>();

		if (traitClassId != null) {
			standardVariables.addAll(this.getStandardVariablesOfTraitClass(traitClassId, programUUID));
			return standardVariables;
		}

		// For property, scale, method
		final List<Integer> standardVariableIds =
			this.daoFactory.getCvTermDao().getStandardVariableIds(traitClassId, propertyId, methodId, scaleId);
		for (final Integer id : standardVariableIds) {
			standardVariables.add(this.getStandardVariable(id, programUUID));
		}

		return standardVariables;
	}

	private List<StandardVariable> getStandardVariablesOfTraitClass(final Integer traitClassId, final String programUUID) {
		final List<StandardVariable> standardVariables = new ArrayList<>();
		final List<PropertyReference> properties = this.daoFactory.getCvTermDao().getPropertiesOfTraitClass(traitClassId);

		final List<Integer> propertyIds = new ArrayList<>();
		for (final PropertyReference property : properties) {
			propertyIds.add(property.getId());
		}

		final Map<Integer, List<StandardVariableReference>> propertyVars =
			this.daoFactory.getCvTermDao().getStandardVariablesOfProperties(propertyIds);

		for (final Integer propId : propertyIds) {
			final List<StandardVariableReference> stdVarRefs = propertyVars.get(propId);
			if (stdVarRefs != null) {
				for (final StandardVariableReference stdVarRef : stdVarRefs) {
					standardVariables.add(this.getStandardVariable(stdVarRef.getId(), programUUID));
				}
			}
		}

		return standardVariables;

	}

	@Override
	public Integer getStandardVariableIdByTermId(final int cvTermId, final TermId termId) {
		return this.getStandardVariableBuilder().getIdByTermId(cvTermId, termId);
	}

	@Override
	public void saveOrUpdateStandardVariable(final StandardVariable standardVariable, final Operation operation) {

		standardVariable.setProperty(this.getTermBuilder().findOrSaveTermByName(standardVariable.getProperty().getName(), CvId.PROPERTIES));
		standardVariable.setScale(this.getTermBuilder().findOrSaveTermByName(standardVariable.getScale().getName(), CvId.SCALES));
		standardVariable.setMethod(this.getTermBuilder().findOrSaveTermByName(standardVariable.getMethod().getName(), CvId.METHODS));

		final String errorCodes = this.getStandardVariableSaver().validate(standardVariable, operation);

		if (errorCodes != null && !errorCodes.isEmpty()) {
			throw new MiddlewareQueryException(errorCodes, "The variable you entered is invalid");
		}

		try {

			if (operation == Operation.ADD) {
				this.getStandardVariableSaver().save(standardVariable);
			} else {
				this.getStandardVariableSaver().update(standardVariable);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public void addOrUpdateStandardVariableConstraints(final int standardVariableId, final VariableConstraints constraints) {

		try {

			this.getStandardVariableSaver().saveConstraints(standardVariableId, constraints);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in addOrUpdateStandardVariableConstraints: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteStandardVariableLocalConstraints(final int standardVariableId) {

		try {

			final StandardVariable stdVar = this.getStandardVariable(standardVariableId, null);

			this.getStandardVariableSaver().deleteConstraints(stdVar);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in deleteStandardVariableLocalConstraints " + e.getMessage(), e);
		}
	}

	@Override
	public Enumeration addStandardVariableEnumeration(final StandardVariable variable, final Enumeration enumeration) {

		if (variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
			throw new MiddlewareException(
				"Error in addStandardVariableEnumeration(). Enumeration with the same name and description exists.");
		}

		final Integer cvId = this.getEnumerationCvId(variable);

		try {

			this.getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in addStandardVariableEnumeration: " + e.getMessage(), e);
		}

		return enumeration;
	}

	@Override
	public void saveOrUpdateStandardVariableEnumeration(final StandardVariable variable, final Enumeration enumeration) {

		if (enumeration.getId() == null && variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
			throw new MiddlewareException("Error in saveOrUpdateStandardVariableEnumeration(). "
				+ "Enumeration id is null and an Enumeration with the same name and description exists." + "Add fails. ");
		}

		final Integer cvId = this.getEnumerationCvId(variable);

		VariableCache.removeFromCache(variable.getId());
		try {

			// Operation is ADD
			if (enumeration.getId() == null) {
				this.getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);

				// Operation is UPDATE
			} else {
				this.getTermSaver().update(new Term(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariableEnumeration: " + e.getMessage(), e);
		}

	}

	private Integer getEnumerationCvId(final StandardVariable variable) {

		// Check if cv entry of enumeration already exists
		// Add cv entry of the standard variable if none found
		Integer cvId = this.daoFactory.getCvDao().getIdByName(String.valueOf(variable.getId()));

		if (cvId == null) {
			cvId = this.getStandardVariableSaver().createCv(variable).getCvId();
		}

		return cvId;
	}

	@Override
	public void deleteStandardVariableEnumeration(final int standardVariableId, final int enumerationId) {

		try {

			final StandardVariable stdVar = this.getStandardVariable(standardVariableId, null);
			this.getStandardVariableSaver().deleteEnumeration(standardVariableId, stdVar.getEnumeration(enumerationId));

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error in deleteStandardVariableEnumeration " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteTerm(final int cvTermId, final CvId cvId) {

		try {

			if (CvId.VARIABLES.getId() != cvId.getId()) {

				this.getTermSaver().delete(this.daoFactory.getCvTermDao().getById(cvTermId), cvId);

			} else {
				throw new MiddlewareQueryException("variables cannot be used in this method");
			}
		} catch (final MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	public Integer retrieveDerivedAnalysisVariable(final Integer originalVariableTermID, final Integer analysisMethodID) {
		if (originalVariableTermID == null || analysisMethodID == null) {
			return null;
		}

		return this.daoFactory.getCvTermRelationshipDao().retrieveAnalysisDerivedVariableID(originalVariableTermID, analysisMethodID);
	}

	@Override
	public void deleteTermAndRelationship(final int cvTermId, final CvId cvId, final int typeId, final int objectId) {

		try {
			if (this.daoFactory.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId) != null) {
				this.validateTermRelationshipsForDeletion(cvTermId);
			}

			if (CvId.VARIABLES.getId() != cvId.getId()) {

				this.deleteCvTermRelationship(cvTermId, typeId);
				this.getTermSaver().delete(this.daoFactory.getCvTermDao().getById(cvTermId), cvId);

			} else {
				throw new MiddlewareQueryException("variables cannot be used in this method");
			}
		} catch (final MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		} catch (final MiddlewareException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	private void validateTermRelationshipsForDeletion(final int cvTermId) {

		if (this.daoFactory.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getTypeId().equals(TermId.IS_A.getId())) {
			if (this.daoFactory.getCvTermDao().getById(
				this.daoFactory.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getSubjectId())
				.getCv()
				.equals(CvId.PROPERTIES.getId())) {
				throw new MiddlewareQueryException(
					ErrorCode.ONTOLOGY_HAS_LINKED_PROPERTY.getCode(),
					OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
			} else {
				throw new MiddlewareQueryException(
					ErrorCode.ONTOLOGY_HAS_IS_A_RELATIONSHIP.getCode(),
					OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
			}
		} else {
			throw new MiddlewareQueryException(
				ErrorCode.ONTOLOGY_HAS_LINKED_VARIABLE.getCode(),
				OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
		}

	}

	private void deleteCvTermRelationship(final int subjectId, final int typeId) {
		final Term typeTerm = this.getTermById(typeId);
		if (typeTerm != null) {
			final CVTermRelationship cvRelationship =
				this.daoFactory.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			if (cvRelationship != null) {
				this.getTermRelationshipSaver().deleteRelationship(cvRelationship);
			}
		}
	}

	@Override
	public List<Property> getAllPropertiesWithTraitClass() {
		final List<Property> properties = this.daoFactory.getCvTermDao().getAllPropertiesWithTraitClass();
		Collections.sort(properties, new Comparator<Property>() {

			@Override
			public int compare(final Property o1, final Property o2) {
				return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
			}
		});
		return properties;
	}

	@Override
	public void deleteStandardVariable(final int stdVariableId) {
		VariableCache.removeFromCache(stdVariableId);

		try {
			final StandardVariable stdVar = this.getStandardVariable(stdVariableId, null);
			this.getStandardVariableSaver().delete(stdVar);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error in deleteStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethod(final String property, final String scale, final String method) {

		final Integer propertyId = this.findTermIdByName(property, CvId.PROPERTIES);
		final Integer scaleId = this.findTermIdByName(scale, CvId.SCALES);
		final Integer methodId = this.findTermIdByName(method, CvId.METHODS);

		return this.getStandardVariableIdByPropertyIdScaleIdMethodId(propertyId, scaleId, methodId);
	}

	@Override
	public Integer getStandardVariableIdByPropertyIdScaleIdMethodId(final Integer propertyId, final Integer scaleId, final Integer methodId) {

		return this.getStandardVariableBuilder().getIdByPropertyScaleMethod(propertyId, scaleId, methodId);

	}

	private Integer findTermIdByName(final String name, final CvId cvType) {
		final Term term = this.findTermByName(name, cvType);
		if (term != null) {
			return term.getId();
		}
		return null;
	}

	@Override
	public boolean validateDeleteStandardVariableEnumeration(final int standardVariableId, final int enumerationId) {
		return this.getStandardVariableBuilder().validateEnumerationUsage(standardVariableId, enumerationId);
	}

	@Override
	public List<NameSynonym> getSynonymsOfTerm(final Integer termId) {
		final List<CVTermSynonym> synonyms = this.getNameSynonymBuilder().findSynonyms(termId);
		return this.getNameSynonymBuilder().create(synonyms);
	}

	@Override
	public boolean isSeedAmountVariable(final String variateProperty) {
		boolean isSeedAmountVar = false;

		if ("INVENTORY AMOUNT".equalsIgnoreCase(variateProperty)) {
			isSeedAmountVar = true;

		} else {
			final List<Term> termsByNameOrSynonym = this.findTermsByNameOrSynonym("INVENTORY AMOUNT", CvId.PROPERTIES);
			if (termsByNameOrSynonym != null && !termsByNameOrSynonym.isEmpty()) {
				final List<NameSynonym> synonymsOfTerm = this.getSynonymsOfTerm(termsByNameOrSynonym.get(0).getId());
				for (final NameSynonym synonym : synonymsOfTerm) {
					if (synonym.getName().equalsIgnoreCase(variateProperty)) {
						isSeedAmountVar = true;
						break;
					}
				}
			}

		}

		return isSeedAmountVar;
	}

	@Override
	public Term findTermByName(final String name, final int cvId) {
		return this.getTermBuilder().findTermByName(name, cvId);
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
