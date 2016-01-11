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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

@Transactional
public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(OntologyDataManagerImpl.class);

	public static final String DELETE_TERM_ERROR_MESSAGE = "The term you selected cannot be deleted";

	public OntologyDataManagerImpl() {
	}

	public OntologyDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Term getTermById(int termId) throws MiddlewareQueryException {
		return this.getTermBuilder().get(termId);
	}

	@Override
	public StandardVariable getStandardVariable(int stdVariableId, String programUUID) throws MiddlewareException {
		return this.getStandardVariableBuilder().create(stdVariableId, programUUID);
	}

	@Override
	public List<StandardVariable> getStandardVariables(List<Integer> standardVariableIds, String programUUID) throws MiddlewareException {
		return this.getStandardVariableBuilder().create(standardVariableIds, programUUID);
	}

	@Override
	public List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().getStandardVariableSummaries(standardVariableIds);
	}

	@Override
	public StandardVariableSummary getStandardVariableSummary(Integer standardVariableId) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().getStandardVariableSummary(standardVariableId);
	}

	@Override
	public void addStandardVariable(StandardVariable stdVariable, String programUUID) throws MiddlewareQueryException {

		Term existingStdVar = this.findTermByName(stdVariable.getName(), CvId.VARIABLES);
		if (existingStdVar != null) {
			throw new MiddlewareQueryException(String.format("Error in addStandardVariable, Variable with name \"%s\" already exists",
					stdVariable.getName()));
		}

		try {

			// check if scale, property and method exists first
			Term scale = this.findTermByName(stdVariable.getScale().getName(), CvId.SCALES);
			if (scale == null) {
				stdVariable.setScale(this.getTermSaver().save(stdVariable.getScale().getName(), stdVariable.getScale().getDefinition(),
						CvId.SCALES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new scale with id = " + stdVariable.getScale().getId());
				}
			}

			Term property = this.findTermByName(stdVariable.getProperty().getName(), CvId.PROPERTIES);
			if (property == null) {
				stdVariable.setProperty(this.getTermSaver().save(stdVariable.getProperty().getName(),
						stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new property with id = " + stdVariable.getProperty().getId());
				}
			}

			Term method = this.findTermByName(stdVariable.getMethod().getName(), CvId.METHODS);
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

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error in addStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public void addStandardVariable(List<StandardVariable> stdVariableList) throws MiddlewareQueryException {

		try {
			for (StandardVariable stdVariable : stdVariableList) {
				this.getStandardVariableSaver().save(stdVariable);
			}
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error in addStandardVariable " + e.getMessage(), e);
		}

	}

	@Override
	public void addStandardVariableForMigrator(StandardVariable stdVariable, String programUUID) throws MiddlewareQueryException {
		Term existingStdVar = this.findTermByName(stdVariable.getName(), CvId.VARIABLES);
		if (existingStdVar != null) {
			throw new MiddlewareQueryException(String.format(
					"Error in addStandardVariableForMigrator, Variable with name \"%s\" already exists", stdVariable.getName()));
		}

		try {
			// check if scale, property and method exists first
			Term scale = this.findTermByName(stdVariable.getScale().getName(), CvId.SCALES);
			if (scale == null) {
				stdVariable.setScale(this.getTermSaver().save(stdVariable.getScale().getName(), stdVariable.getScale().getDefinition(),
						CvId.SCALES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new scale with id = " + stdVariable.getScale().getId());
				}
			} else if (stdVariable.getScale().getId() == 0) {
				stdVariable.setScale(scale);
			}

			Term property = this.findTermByName(stdVariable.getProperty().getName(), CvId.PROPERTIES);
			if (property == null) {
				stdVariable.setProperty(this.getTermSaver().save(stdVariable.getProperty().getName(),
						stdVariable.getProperty().getDefinition(), CvId.PROPERTIES));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new property with id = " + stdVariable.getProperty().getId());
				}
			} else if (stdVariable.getProperty().getId() == 0) {
				stdVariable.setProperty(property);
			}

			Term method = this.findTermByName(stdVariable.getMethod().getName(), CvId.METHODS);
			if (method == null) {
				stdVariable.setMethod(this.getTermSaver().save(stdVariable.getMethod().getName(), stdVariable.getMethod().getDefinition(),
						CvId.METHODS));
				if (OntologyDataManagerImpl.LOG.isDebugEnabled()) {
					OntologyDataManagerImpl.LOG.debug("new method with id = " + stdVariable.getMethod().getId());
				}
			} else if (stdVariable.getMethod().getId() == 0) {
				stdVariable.setMethod(method);
			}

			// FIXME: Need to pass through programUUID
			StandardVariable existingStandardVariable =
					this.findStandardVariableByTraitScaleMethodNames(stdVariable.getProperty().getName(), stdVariable.getScale().getName(),
							stdVariable.getMethod().getName(), programUUID);
			if (existingStandardVariable == null) {
				this.getStandardVariableSaver().save(stdVariable);
			} else {
				stdVariable.setId(existingStandardVariable.getId());
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addStandardVariableForMigrator " + e.getMessage(), e);
		}

	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public Term addMethod(String name, String definition) throws MiddlewareQueryException {
		return this.addTerm(name, definition, CvId.METHODS);
	}

	@Override
	public Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym, String programUUID) throws MiddlewareException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		standardVariables.addAll(this.getStandardVariablesByNameOrSynonym(nameOrSynonym, programUUID));
		return standardVariables;
	}

	private Set<StandardVariable> getStandardVariablesByNameOrSynonym(String nameOrSynonym, String programUUID) throws MiddlewareException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		List<Integer> stdVarIds = this.getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, CvId.VARIABLES.getId());
		for (Integer stdVarId : stdVarIds) {
			standardVariables.add(this.getStandardVariable(stdVarId, programUUID));
		}
		return standardVariables;
	}

	@Override
	public Term findMethodById(int id) throws MiddlewareQueryException {
		return this.getMethodBuilder().findMethodById(id);
	}

	@Override
	public Term findMethodByName(String name) throws MiddlewareQueryException {
		return this.getMethodBuilder().findMethodByName(name);
	}

	@Override
	public StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method, String programUUID)
			throws MiddlewareException {
		Term termProperty, termScale, termMethod;
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
	public List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		return this.getTermBuilder().getTermsByCvId(cvId);
	}

	@Override
	public long countTermsByCvId(CvId cvId) throws MiddlewareQueryException {
		return this.getCvTermDao().countTermsByCvId(cvId);
	}

	@Override
	public List<Term> getMethodsForTrait(Integer traitId) throws MiddlewareQueryException {
		List<Term> methodTerms = new ArrayList<Term>();
		Set<Integer> methodIds = new HashSet<Integer>();
		List<Integer> localMethodIds = this.getCvTermDao().findMethodTermIdsByTrait(traitId);
		if (localMethodIds != null) {
			methodIds.addAll(localMethodIds);
		}
		for (Integer termId : methodIds) {
			methodTerms.add(this.getTermBuilder().get(termId));
		}
		return methodTerms;
	}

	@Override
	public List<Term> getScalesForTrait(Integer traitId) throws MiddlewareQueryException {
		List<Term> scaleTerms = new ArrayList<Term>();
		Set<Integer> scaleIds = new HashSet<Integer>();
		List<Integer> localMethodIds = this.getCvTermDao().findScaleTermIdsByTrait(traitId);
		if (localMethodIds != null) {
			scaleIds.addAll(localMethodIds);
		}
		for (Integer termId : scaleIds) {
			scaleTerms.add(this.getTermBuilder().get(termId));
		}
		return scaleTerms;
	}

	@Override
	public Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException {
		return this.getTermBuilder().findTermByName(name, cvId);
	}

	@Override
	public Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException {
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
		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addTerm: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateTerm(Term term) throws MiddlewareException {

		if (term == null) {
			return;
		}

		try {

			this.getTermSaver().update(term);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Term> getDataTypes() throws MiddlewareQueryException {
		List<Integer> dataTypeIds =
				Arrays.asList(TermId.CLASS.getId(), TermId.NUMERIC_VARIABLE.getId(), TermId.DATE_VARIABLE.getId(),
						TermId.NUMERIC_DBID_VARIABLE.getId(), TermId.CHARACTER_DBID_VARIABLE.getId(), TermId.CHARACTER_VARIABLE.getId(),
						TermId.TIMESTAMP_VARIABLE.getId(), TermId.CATEGORICAL_VARIABLE.getId());
		return this.getTermBuilder().getTermsByIds(dataTypeIds);
	}

	@Override
	public Map<String, StandardVariable> getStandardVariablesForPhenotypicType(PhenotypicType type, String programUUID, int start,
			int numOfRows) throws MiddlewareException {

		TreeMap<String, StandardVariable> standardVariables = new TreeMap<String, StandardVariable>();
		List<Integer> localStdVariableIds = new ArrayList<Integer>();

		localStdVariableIds = this.getCvTermDao().getStandardVariableIdsByPhenotypicType(type);

		for (Integer stdVarId : localStdVariableIds) {
			StandardVariable sd = this.getStandardVariableBuilder().create(stdVarId, programUUID);
			standardVariables.put(sd.getName(), sd);
		}

		Set<String> standardVariablesSet = standardVariables.keySet();
		Object[] list = standardVariablesSet.toArray();

		String startSD = list[start].toString();

		int end = start + numOfRows > list.length - 1 ? list.length - 1 : start + numOfRows;
		String endSD = list[end].toString();

		return standardVariables.subMap(startSD, true, endSD, true);
	}

	@Override
	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers, String programUUID)
			throws MiddlewareException {
		return this.getStandardVariableBuilder().getStandardVariablesInProjects(headers, programUUID);
	}

	@Override
	public List<Term> findTermsByNameOrSynonym(String nameOrSynonym, CvId cvId) throws MiddlewareQueryException {
		List<Term> terms = new ArrayList<Term>();
		List<CVTerm> cvTerms = new ArrayList<CVTerm>();
		List<Integer> termIds = new ArrayList<Integer>();

		termIds = this.getCvTermDao().getTermsByNameOrSynonym(nameOrSynonym, cvId.getId());
		for (Integer id : termIds) {
			cvTerms.add(this.getCvTermDao().getById(id));
		}
		for (CVTerm cvTerm : cvTerms) {
			terms.add(TermBuilder.mapCVTermToTerm(cvTerm));
		}
		return terms;
	}

	@Override
	public Term addProperty(String name, String definition, int isA) throws MiddlewareQueryException {

		Term term = this.findTermByName(name, CvId.PROPERTIES);

		if (term != null) {
			return term;
		}

		try {

			term = this.getTermSaver().save(name, definition, CvId.PROPERTIES);
			Term isATerm = this.getTermById(isA);

			if (isATerm != null) {
				this.getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), isATerm.getId());
			} else {
				throw new MiddlewareQueryException("The isA passed is not a valid Class term: " + isA);
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
		}

		return term;
	}

	@Override
	public Term addOrUpdateTerm(String name, String definition, CvId cvId) throws MiddlewareException {

		try {

			Term term = this.saveOrUpdateCvTerm(name, definition, cvId);

			return term;
		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}

	}

	@Override
	public void addOrUpdateCropOntologyID(Property property, String cropOntologyID) throws MiddlewareQueryException {
		assert !StringUtils.isEmpty(cropOntologyID);

		try {

			this.getStandardVariableSaver().saveOrUpdateCropOntologyId(property.getTerm().getId(), cropOntologyID);

		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	@Override
	public Term addOrUpdateTermAndRelationship(String name, String definition, CvId cvId, int typeId, int objectId)
			throws MiddlewareException {
		try {
			Term term = this.saveOrUpdateCvTerm(name, definition, cvId);
			this.saveOrUpdateCvTermRelationship(term.getId(), objectId, typeId);
			return term;
		} catch (MiddlewareQueryException e) {
			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	@Override
	public Term updateTermAndRelationship(Term term, int typeId, int objectId) throws MiddlewareException {

		Term updatedTerm = null;

		if (term == null) {
			return null;
		}

		try {
			updatedTerm = this.getTermSaver().update(term);

			this.saveOrUpdateCvTermRelationship(updatedTerm.getId(), objectId, typeId);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in updateTerm: " + e.getMessage(), e);
		}

		return updatedTerm;
	}

	private Term saveOrUpdateCvTerm(String name, String definition, CvId cvId) throws MiddlewareException {
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
    public void addCvTermRelationship(int subjectId, int objectId, int typeId) {
        this.getTermRelationshipSaver().save(subjectId, typeId, objectId);
    }

    private void saveOrUpdateCvTermRelationship(int subjectId, int objectId, int typeId) throws MiddlewareException {
		Term typeTerm = this.getTermById(typeId);
		if (typeTerm != null) {
			CVTermRelationship cvRelationship = this.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
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
	public Property getProperty(int termId) throws MiddlewareQueryException {
		Property property = new Property();

		property.setTerm(this.getTermBuilder().getTermOfProperty(termId, CvId.PROPERTIES.getId()));
		if (property.getTerm() != null) {
			property.setIsA(this.getTermBuilder().getTermOfClassOfProperty(termId, CvId.PROPERTIES.getId(), TermId.IS_A.getId()));
			property.setCropOntologyId(this.getStandardVariableBuilder().getCropOntologyId(property.getTerm()));
		}
		return property;
	}

	@Override
	public Property getProperty(String name) throws MiddlewareQueryException {
		Property property = new Property();

		property.setTerm(this.findTermByName(name, CvId.PROPERTIES));
		if (property.getTerm() != null) {
			property.setIsA(this.getTermBuilder().getTermOfClassOfProperty(property.getTerm().getId(), CvId.PROPERTIES.getId(),
					TermId.IS_A.getId()));
		}

		return property;
	}

	@Override
	public long countIsAOfProperties() throws MiddlewareQueryException {
		long count = 0;
		count += this.getCvTermDao().countIsAOfTermsByCvId(CvId.PROPERTIES);
		return count;
	}

	@Override
	public List<TraitClassReference> getAllTraitGroupsHierarchy(boolean includePropertiesAndVariables) throws MiddlewareQueryException {
		return this.getTraitGroupBuilder().getAllTraitGroupsHierarchy(includePropertiesAndVariables);
	}

	@Override
	public List<Term> getTermsByIds(List<Integer> ids) throws MiddlewareQueryException {
		return this.getTermBuilder().getTermsByIds(ids);
	}

	@Override
	public TraitClass addTraitClass(String name, String definition, int parentTraitClassId) throws MiddlewareQueryException {
		Term term = this.findTermByName(name, CvId.IBDB_TERMS);

		if (term != null) {
			return new TraitClass(term, this.getTermById(parentTraitClassId));
		}

		try {

			term = this.getTermSaver().save(name, definition, CvId.IBDB_TERMS);

			if (term != null) {
				this.getTermRelationshipSaver().save(term.getId(), TermId.IS_A.getId(), parentTraitClassId);
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addTraitClass " + e.getMessage(), e);
		}
		return new TraitClass(term, this.getTermById(parentTraitClassId));

	}

	@Override
	public Set<StandardVariable> getAllStandardVariables(String programUUID) throws MiddlewareException {
		Set<StandardVariable> standardVariables = new HashSet<StandardVariable>();
		List<Term> terms = this.getAllTermsByCvId(CvId.VARIABLES);
		for (Term term : terms) {
			standardVariables.add(this.getStandardVariable(term.getId(), programUUID));
		}
		return standardVariables;
	}

	@Override
	public List<StandardVariable> getStandardVariables(Integer traitClassId, Integer propertyId, Integer methodId, Integer scaleId,
			String programUUID) throws MiddlewareException {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();

		if (traitClassId != null) {
			standardVariables.addAll(this.getStandardVariablesOfTraitClass(traitClassId, programUUID));
			return standardVariables;
		}

		// For property, scale, method
		List<Integer> standardVariableIds = this.getCvTermDao().getStandardVariableIds(traitClassId, propertyId, methodId, scaleId);
		for (Integer id : standardVariableIds) {
			standardVariables.add(this.getStandardVariable(id, programUUID));
		}

		return standardVariables;
	}

	private List<StandardVariable> getStandardVariablesOfTraitClass(Integer traitClassId, String programUUID) throws MiddlewareException {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		List<PropertyReference> properties = this.getCvTermDao().getPropertiesOfTraitClass(traitClassId);

		List<Integer> propertyIds = new ArrayList<Integer>();
		for (PropertyReference property : properties) {
			propertyIds.add(property.getId());
		}

		Map<Integer, List<StandardVariableReference>> propertyVars = this.getCvTermDao().getStandardVariablesOfProperties(propertyIds);

		for (Integer propId : propertyIds) {
			List<StandardVariableReference> stdVarRefs = propertyVars.get(propId);
			if (stdVarRefs != null) {
				for (StandardVariableReference stdVarRef : stdVarRefs) {
					standardVariables.add(this.getStandardVariable(stdVarRef.getId(), programUUID));
				}
			}
		}

		return standardVariables;

	}

	@Override
	public Integer getStandardVariableIdByTermId(int cvTermId, TermId termId) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().getIdByTermId(cvTermId, termId);
	}

	@Override
	public void saveOrUpdateStandardVariable(StandardVariable standardVariable, Operation operation) throws MiddlewareException {

		standardVariable.setProperty(this.getTermBuilder().findOrSaveTermByName(standardVariable.getProperty().getName(), CvId.PROPERTIES));
		standardVariable.setScale(this.getTermBuilder().findOrSaveTermByName(standardVariable.getScale().getName(), CvId.SCALES));
		standardVariable.setMethod(this.getTermBuilder().findOrSaveTermByName(standardVariable.getMethod().getName(), CvId.METHODS));

		String errorCodes = this.getStandardVariableSaver().validate(standardVariable, operation);

		if (errorCodes != null && !errorCodes.isEmpty()) {
			throw new MiddlewareQueryException(errorCodes, "The variable you entered is invalid");
		}

		try {

			if (operation == Operation.ADD) {
				this.getStandardVariableSaver().save(standardVariable);
			} else {
				this.getStandardVariableSaver().update(standardVariable);
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public void addOrUpdateStandardVariableConstraints(int standardVariableId, VariableConstraints constraints) throws MiddlewareException {

		try {

			this.getStandardVariableSaver().saveConstraints(standardVariableId, constraints);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addOrUpdateStandardVariableConstraints: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteStandardVariableLocalConstraints(int standardVariableId) throws MiddlewareQueryException {

		try {

			StandardVariable stdVar = this.getStandardVariable(standardVariableId, null);

			this.getStandardVariableSaver().deleteConstraints(stdVar);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in deleteStandardVariableLocalConstraints " + e.getMessage(), e);
		}
	}

	@Override
	public Enumeration addStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException {

		if (variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
			throw new MiddlewareException(
					"Error in addStandardVariableEnumeration(). Enumeration with the same name and description exists.");
		}

		Integer cvId = this.getEnumerationCvId(variable);

		try {

			this.getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in addStandardVariableEnumeration: " + e.getMessage(), e);
		}

		return enumeration;
	}

	@Override
	public void saveOrUpdateStandardVariableEnumeration(StandardVariable variable, Enumeration enumeration) throws MiddlewareException {

		if (enumeration.getId() == null && variable.getEnumeration(enumeration.getName(), enumeration.getDescription()) != null) {
			throw new MiddlewareException("Error in saveOrUpdateStandardVariableEnumeration(). "
					+ "Enumeration id is null and an Enumeration with the same name and description exists." + "Add fails. ");
		}

		Integer cvId = this.getEnumerationCvId(variable);

		VariableCache.removeFromCache(variable.getId());
		try {

			// Operation is ADD
			if (enumeration.getId() == null) {
				this.getStandardVariableSaver().saveEnumeration(variable, enumeration, cvId);

				// Operation is UPDATE
			} else {
				this.getTermSaver().update(new Term(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in saveOrUpdateStandardVariableEnumeration: " + e.getMessage(), e);
		}

	}

	private Integer getEnumerationCvId(StandardVariable variable) throws MiddlewareQueryException {

		// Check if cv entry of enumeration already exists
		// Add cv entry of the standard variable if none found
		Integer cvId = this.getCvDao().getIdByName(String.valueOf(variable.getId()));

		if (cvId == null) {
			cvId = this.getStandardVariableSaver().createCv(variable).getCvId();
		}

		return cvId;
	}

	@Override
	public void deleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException {

		try {

			StandardVariable stdVar = this.getStandardVariable(standardVariableId, null);
			this.getStandardVariableSaver().deleteEnumeration(standardVariableId, stdVar.getEnumeration(enumerationId));

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error in deleteStandardVariableEnumeration " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteTerm(int cvTermId, CvId cvId) throws MiddlewareQueryException {

		try {

			if (CvId.VARIABLES.getId() != cvId.getId()) {

				this.getTermSaver().delete(this.getCvTermDao().getById(cvTermId), cvId);

			} else {
				throw new MiddlewareQueryException("variables cannot be used in this method");
			}
		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		}
	}

	public Integer retrieveDerivedAnalysisVariable(Integer originalVariableTermID, Integer analysisMethodID) {
        if (originalVariableTermID == null || analysisMethodID == null) {
            return null;
        }

        return getCvTermRelationshipDao().retrieveAnalysisDerivedVariableID(originalVariableTermID, analysisMethodID);
	}

	@Override
	public void deleteTermAndRelationship(int cvTermId, CvId cvId, int typeId, int objectId) throws MiddlewareQueryException {

		try {
			if (this.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId) != null) {
				this.validateTermRelationshipsForDeletion(cvTermId);
			}

			if (CvId.VARIABLES.getId() != cvId.getId()) {

				this.deleteCvTermRelationship(cvTermId, typeId);
				this.getTermSaver().delete(this.getCvTermDao().getById(cvTermId), cvId);

			} else {
				throw new MiddlewareQueryException("variables cannot be used in this method");
			}
		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException(e.getCode(), e);
		} catch (MiddlewareException e) {

			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	private void validateTermRelationshipsForDeletion(int cvTermId) throws MiddlewareQueryException {

		if (this.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getTypeId().equals(TermId.IS_A.getId())) {
			if (this.getCvTermDao().getById(this.getCvTermRelationshipDao().getRelationshipByObjectId(cvTermId).getSubjectId()).getCv()
					.equals(CvId.PROPERTIES.getId())) {
				throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_LINKED_PROPERTY.getCode(),
						OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
			} else {
				throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_IS_A_RELATIONSHIP.getCode(),
						OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
			}
		} else {
			throw new MiddlewareQueryException(ErrorCode.ONTOLOGY_HAS_LINKED_VARIABLE.getCode(),
					OntologyDataManagerImpl.DELETE_TERM_ERROR_MESSAGE);
		}

	}

	private void deleteCvTermRelationship(int subjectId, int typeId) throws MiddlewareException {
		Term typeTerm = this.getTermById(typeId);
		if (typeTerm != null) {
			CVTermRelationship cvRelationship = this.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(subjectId, typeId);
			if (cvRelationship != null) {
				this.getTermRelationshipSaver().deleteRelationship(cvRelationship);
			}
		}
	}

	@Override
	public List<Property> getAllPropertiesWithTraitClass() throws MiddlewareQueryException {
		List<Property> properties = this.getCvTermDao().getAllPropertiesWithTraitClass();
		Collections.sort(properties, new Comparator<Property>() {

			@Override
			public int compare(Property o1, Property o2) {
				return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
			}
		});
		return properties;
	}

	@Override
	public void deleteStandardVariable(int stdVariableId) throws MiddlewareQueryException {
		VariableCache.removeFromCache(stdVariableId);

		try {
			StandardVariable stdVar = this.getStandardVariable(stdVariableId, null);
			this.getStandardVariableSaver().delete(stdVar);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error in deleteStandardVariable " + e.getMessage(), e);
		}
	}

	@Override
	public Integer getStandardVariableIdByPropertyScaleMethod(String property, String scale, String method) throws MiddlewareQueryException {

		Integer propertyId = this.findTermIdByName(property, CvId.PROPERTIES);
		Integer scaleId = this.findTermIdByName(scale, CvId.SCALES);
		Integer methodId = this.findTermIdByName(method, CvId.METHODS);

		return this.getStandardVariableIdByPropertyIdScaleIdMethodId(propertyId, scaleId, methodId);
	}

	@Override
	public Integer getStandardVariableIdByPropertyIdScaleIdMethodId(Integer propertyId, Integer scaleId, Integer methodId)
			throws MiddlewareQueryException {

		return this.getStandardVariableBuilder().getIdByPropertyScaleMethod(propertyId, scaleId, methodId);

	}

	private Integer findTermIdByName(String name, CvId cvType) throws MiddlewareQueryException {
		Term term = this.findTermByName(name, cvType);
		if (term != null) {
			return term.getId();
		}
		return null;
	}

	@Override
	public boolean validateDeleteStandardVariableEnumeration(int standardVariableId, int enumerationId) throws MiddlewareQueryException {
		return this.getStandardVariableBuilder().validateEnumerationUsage(standardVariableId, enumerationId);
	}

	@Override
	public List<NameSynonym> getSynonymsOfTerm(Integer termId) throws MiddlewareQueryException {
		List<CVTermSynonym> synonyms = this.getNameSynonymBuilder().findSynonyms(termId);
		return this.getNameSynonymBuilder().create(synonyms);
	}

	@Override
	public boolean isSeedAmountVariable(String variateProperty) throws MiddlewareQueryException {
		boolean isSeedAmountVar = false;

		if ("INVENTORY AMOUNT".equalsIgnoreCase(variateProperty)) {
			isSeedAmountVar = true;

		} else {
			List<Term> termsByNameOrSynonym = this.findTermsByNameOrSynonym("INVENTORY AMOUNT", CvId.PROPERTIES);
			if (termsByNameOrSynonym != null && !termsByNameOrSynonym.isEmpty()) {
				List<NameSynonym> synonymsOfTerm = this.getSynonymsOfTerm(termsByNameOrSynonym.get(0).getId());
				for (NameSynonym synonym : synonymsOfTerm) {
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
	public Integer getCVIdByName(String name) throws MiddlewareQueryException {
		return this.getCvDao().getIdByName(name);
	}

	@Override
	public Term findTermByName(String name, int cvId) throws MiddlewareQueryException {
		return this.getTermBuilder().findTermByName(name, cvId);
	}

}
