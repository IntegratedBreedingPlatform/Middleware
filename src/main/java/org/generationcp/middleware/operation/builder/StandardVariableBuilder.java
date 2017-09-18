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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

public class StandardVariableBuilder extends Builder {

	private static final String DATA_TYPE_NUMERIC = "N";
	private static final String DATA_TYPE_CHARACTER = "C";

	public StandardVariableBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public StandardVariable create(final int standardVariableId, final String programUUID) {

		final Variable variable = this.getOntologyVariableDataManager().getVariable(programUUID, standardVariableId, false, false);

		return this.getStandardVariableTransformer().transformVariable(variable);
	}

	public List<StandardVariable> create(final List<Integer> standardVariableIds, final String programUUID) {
		final List<StandardVariable> standardVariables = new ArrayList<>();
		if (standardVariableIds != null && !standardVariableIds.isEmpty()) {
			for (final Integer id : standardVariableIds) {
				standardVariables.add(this.create(id, programUUID));
			}
		}
		return standardVariables;
	}

	public StandardVariableSummary getStandardVariableSummary(final Integer standardVariableId) {
		StandardVariableSummary summary = null;
		if (standardVariableId != null) {
			summary = this.getStandardVariableDao().getStandardVariableSummary(standardVariableId);
			if (summary != null) {
				this.specialProcessing(Arrays.asList(summary));
			}
		}
		return summary;
	}

	/**
	 * Loads a list of {@link StandardVariableSummary}'s for the given set of standard variable ids from standard_variable_summary database
	 * view.
	 *
	 * @see StandardVariableDao#getStarndardVariableSummaries(List)
	 */
	public List<StandardVariableSummary> getStandardVariableSummaries(final List<Integer> standardVariableIds) {
		final List<StandardVariableSummary> result = new ArrayList<>();
		if (standardVariableIds != null && !standardVariableIds.isEmpty()) {
			final List<StandardVariableSummary> localVariables =
					this.getStandardVariableDao().getStarndardVariableSummaries(standardVariableIds);
			this.specialProcessing(localVariables);
			result.addAll(localVariables);
		}
		return result;
	}

	public List<StandardVariableSummary> getStandardVariableSummariesWithIsAId(final List<Integer> isAIds) {
		final List<StandardVariableSummary> result = new ArrayList<>();
		if (isAIds != null && !isAIds.isEmpty()) {
			final List<StandardVariableSummary> localVariables = this.getStandardVariableDao().getStandardVariableSummaryWithIsAId(isAIds);
			result.addAll(localVariables);
		}
		return result;
	}

	private void specialProcessing(final List<StandardVariableSummary> summaries) {
		if (summaries == null || summaries.isEmpty()) {
			return;
		}

		for (final StandardVariableSummary summary : summaries) {
			// Special hackery for the isA (class) part of the relationship!
			// Earlier isA (class) part of the standard variables ontology star used to be linked to standard variables directly.
			// Now this relationship is linked to the "Property" of the standard variable. (facepalm).
			if (summary.getProperty() != null) {
				final List<CVTermRelationship> propertyCvTermRelationships =
						this.getCvTermRelationshipDao().getBySubject(summary.getProperty().getId());
				final Term isAOfProperty = this.createTerm(propertyCvTermRelationships, TermId.IS_A);
				if (isAOfProperty != null) {
					summary.setIsA(new TermSummary(isAOfProperty.getId(), isAOfProperty.getName(), isAOfProperty.getDefinition()));
				}
			}
		}

	}

	public String getCropOntologyId(final Term term) {
		String cropOntologyId = null;
		final List<TermProperty> termProperties = this.createTermProperties(term.getId());
		if (termProperties != null && !termProperties.isEmpty()) {
			for (final TermProperty termProperty : termProperties) {
				if (TermId.CROP_ONTOLOGY_ID.getId() == termProperty.getTypeId()) {
					cropOntologyId = termProperty.getValue();
					break;
				}
			}
		}
		if (term != null) {
			final CVTermProperty property =
					this.getCvTermPropertyDao().getOneByCvTermAndType(term.getId(), TermId.CROP_ONTOLOGY_ID.getId());
			if (property != null) {
				cropOntologyId = property.getValue();
			}
		}
		return cropOntologyId;
	}

	private Integer findTermId(final List<CVTermRelationship> cvTermRelationships, final TermId relationship) {
		for (final CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

	private Term createTerm(final List<CVTermRelationship> cvTermRelationships, final TermId relationship) {
		final Integer id = this.findTermId(cvTermRelationships, relationship);
		if (id != null) {
			// add to handle missing cvterm_relationship (i.e. is_a)
			return this.createTerm(id);
		}
		return null;
	}

	private Term createTerm(final Integer id) {
		final CVTerm cvTerm = this.getCvTerm(id);
		return cvTerm != null ? new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition()) : null;
	}

	public List<NameSynonym> createSynonyms(final int cvTermId) {
		final List<CVTermSynonym> synonyms = this.getNameSynonymBuilder().findSynonyms(cvTermId);
		return this.getNameSynonymBuilder().create(synonyms);
	}

	public List<TermProperty> createTermProperties(final int cvTermId) {
		final List<CVTermProperty> cvTermProperties = this.getTermPropertyBuilder().findProperties(cvTermId);
		return this.getTermPropertyBuilder().create(cvTermProperties);
	}

	private CVTerm getCvTerm(final int id) {
		return this.getCvTermDao().getById(id);
	}

	public StandardVariable findOrSave(String name, String description, String propertyName, String scaleName, String methodName,
			PhenotypicType role, VariableType variableType, String dataTypeString, String programUUID) {

		final TermBuilder termBuilder = this.getTermBuilder();
		final Term property = termBuilder.findOrSaveProperty(propertyName, propertyName, null, termBuilder.getDefaultTraitClasses());

		final Term scale = this.getTermBuilder().findOrSaveScale(scaleName, scaleName, this.getDataType(dataTypeString), null, null, null);
		final Term method = this.getTermBuilder().findOrSaveMethod(methodName, methodName);

		final VariableFilter filterOpts = new VariableFilter();
		filterOpts.setProgramUuid(programUUID);
		filterOpts.addPropertyId(property.getId());
		filterOpts.addMethodId(method.getId());
		filterOpts.addScaleId(scale.getId());

		List<Variable> variableList = this.getOntologyVariableDataManager().getWithFilter(filterOpts);
		StandardVariable standardVariable;

		if(variableType == null){
			variableType = OntologyDataHelper.mapFromPhenotype(role, propertyName);
		}

		if (variableList == null || variableList.isEmpty()) {
			final OntologyVariableInfo variableInfo =
					this.createOntologyVariableInfo(name, description, method.getId(), property.getId(), scale.getId(), programUUID, null,
							null, variableType);
			this.getOntologyVariableDataManager().addVariable(variableInfo);
			standardVariable = this.create(variableInfo.getId(), programUUID);
		} else {
			Variable variable = variableList.get(0);
			standardVariable = this.create(variable.getId(), programUUID);
		}

		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(new ArrayList<>(Collections.singletonList(variableType))));

		return standardVariable;
	}

	private String getDataType(final String dataTypeString) {
		if (StandardVariableBuilder.DATA_TYPE_NUMERIC.equals(dataTypeString)) {
			return DataType.NUMERIC_VARIABLE.getName();
		} else if (StandardVariableBuilder.DATA_TYPE_CHARACTER.equals(dataTypeString)) {
			return DataType.CHARACTER_VARIABLE.getName();
		}
		return dataTypeString;
	}

	private OntologyVariableInfo createOntologyVariableInfo(final String name, final String description, final int methodId, final int propertyId, final int scaleId,
			final String programUUID, final String minValue, final String maxValue, VariableType variableType) {
		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(name);
		variableInfo.setDescription(description);
		variableInfo.setMethodId(methodId);
		variableInfo.setPropertyId(propertyId);
		variableInfo.setScaleId(scaleId);
		variableInfo.setProgramUuid(programUUID);

		variableInfo.setExpectedMin(minValue);
		variableInfo.setExpectedMax(maxValue);

		variableInfo.addVariableType(variableType);
		return variableInfo;
	}

	/**
	 * Use OntologyDataHelper to map from PhenotypicType to VariableType correctly.
	 */
	@Deprecated
	public VariableType mapPhenotypicTypeToDefaultVariableType(final PhenotypicType role, final boolean isForAnalysis) {
		if (PhenotypicType.STUDY == role || PhenotypicType.DATASET == role) {
			return VariableType.STUDY_DETAIL;
		} else if (PhenotypicType.TRIAL_ENVIRONMENT == role) {
			return VariableType.ENVIRONMENT_DETAIL;
		} else if (PhenotypicType.GERMPLASM == role) {
			return VariableType.GERMPLASM_DESCRIPTOR;
		} else if (PhenotypicType.TRIAL_DESIGN == role) {
			return VariableType.EXPERIMENTAL_DESIGN;
		} else if (PhenotypicType.VARIATE == role && !isForAnalysis) {
			return VariableType.TRAIT;
		} else if (PhenotypicType.VARIATE == role && isForAnalysis) {
			return VariableType.ANALYSIS;
		}
		return null;
	}

	public StandardVariable getByName(final String name, final String programUUID) {
		final CVTerm cvTerm = this.getCvTermDao().getByName(name);
		if (cvTerm != null && cvTerm.getCvTermId() != null) {
			return this.getStandardVariableBuilder().create(cvTerm.getCvTermId(), programUUID);
		}
		return null;
	}

	public StandardVariable getByPropertyScaleMethod(final Integer propertyId, final Integer scaleId, final Integer methodId,
			final String programUUID) {

		final Integer stdVariableId = this.getIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = this.getStandardVariableBuilder().create(stdVariableId, programUUID);
		}
		return standardVariable;
	}

	public StandardVariable getByPropertyScaleMethodRole(final Integer propertyId, final Integer scaleId, final Integer methodId,
			final PhenotypicType role, final String programUUID) {

		final Integer stdVariableId = this.getIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
		StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = this.getStandardVariableBuilder().create(stdVariableId, programUUID);
		}
		return standardVariable;
	}

	public Integer getIdByPropertyScaleMethod(final Integer propertyId, final Integer scaleId, final Integer methodId) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId, "DESC");
		return stdVariableId;
	}

	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(final List<String> headerNames, final String programUUID) {

		final Map<String, List<StandardVariable>> standardVariablesInProjects = new HashMap<>();
		Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects = new HashMap<String, Map<Integer, VariableType>>();

		// Step 1: Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
		standardVariableIdsWithTypeInProjects = this.getStandardVariableIdsWithTypeForProjectProperties(headerNames);

		// Step 2: If no variable found, search for cvterm (standard variables) with given name.
		// Exclude header items with result from step 1
		final List<String> headerNamesNotFoundInProjectProperty = new ArrayList<>();
		for (final String name : headerNames) {

			if (!standardVariableIdsWithTypeInProjects.containsKey(name.toUpperCase())
					|| standardVariableIdsWithTypeInProjects.get(name.toUpperCase()).keySet().isEmpty()) {
				headerNamesNotFoundInProjectProperty.add(name);
			}

		}

		standardVariableIdsWithTypeInProjects.putAll(this.getStandardVariableIdsWithTypeForTerms(headerNamesNotFoundInProjectProperty));
		// Step 3. If no variable still found for steps 1 and 2, treat the
		// header as a trait / property name.
		// Search for trait with given name and return the standard variables
		// using that trait (if any)

		// Exclude header items with result from step 2
		final List<String> headerNamesNotFoundInProjectPropAndTerms = new ArrayList<>();
		for (final String name : headerNames) {

			if (!standardVariableIdsWithTypeInProjects.containsKey(name.toUpperCase())
					|| standardVariableIdsWithTypeInProjects.get(name.toUpperCase()).keySet().isEmpty()) {
				headerNamesNotFoundInProjectPropAndTerms.add(name);
			}

		}

		standardVariableIdsWithTypeInProjects.putAll(this.getStandardVariableIdsForTraits(headerNamesNotFoundInProjectPropAndTerms));

		for (final String name : headerNames) {
			final String upperName = name.toUpperCase();
			final Map<Integer, VariableType> varIdsWithType = standardVariableIdsWithTypeInProjects.get(upperName);

			List<StandardVariable> variables = new ArrayList<>();
			if (varIdsWithType != null) {
				final List<Integer> standardVariableIds = new ArrayList<>(varIdsWithType.keySet());
				variables = this.create(standardVariableIds, programUUID);
				this.setRoleOfVariables(variables, varIdsWithType);
			}
			standardVariablesInProjects.put(name, variables);
		}
		return standardVariablesInProjects;
	}

	protected void setRoleOfVariables(final List<StandardVariable> variables, final Map<Integer, VariableType> varIdsWithType) {
		for (final StandardVariable standardVariable : variables) {
			final VariableType type = varIdsWithType.get(standardVariable.getId());
			if (type != null) {
				standardVariable.setPhenotypicType(type.getRole());
			}
		}
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeForProjectProperties(final List<String> propertyNames) {
		return this.getProjectPropertyDao().getStandardVariableIdsWithTypeByPropertyNames(propertyNames);
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeForTerms(final List<String> termNames) {
		return this.getCvTermDao().getTermIdsWithTypeByNameOrSynonyms(termNames, CvId.VARIABLES.getId());

	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsForTraits(final List<String> traitNames) {
		return this.getCvTermDao().getStandardVariableIdsWithTypeByProperties(traitNames);
	}

	public Integer getIdByTermId(final int cvTermId, final TermId termId) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandardVariableIdByTermId(cvTermId, termId);
		return stdVariableId;
	}

	public CVTerm getCvTerm(final String name, final int cvId) {
		return this.getCvTermDao().getByNameAndCvId(name, cvId);
	}

	public Integer getIdByPropertyScaleMethodRole(final Integer propertyId, final Integer scaleId, final Integer methodId,
			final PhenotypicType role) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
		return stdVariableId;
	}

	public boolean validateEnumerationUsage(final int standardVariableId, final int enumerationId) {
		final Integer storedInId =
				this.getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), standardVariableId).get(0);
		final String value = String.valueOf(enumerationId);
		if (storedInId == TermId.STUDY_INFO_STORAGE.getId() || storedInId == TermId.DATASET_INFO_STORAGE.getId()) {
			return !this.isExistsPropertyByTypeAndValue(standardVariableId, value);
		} else if (storedInId == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			return !this.isExistsStocksByTypeAndValue(standardVariableId, value);
		} else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			return !this.isExistsGeolocationByTypeAndValue(standardVariableId, value);
		} else if (storedInId == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
			return !this.isExistsExperimentsByTypeAndValue(standardVariableId, value);
		} else if (storedInId == TermId.CATEGORICAL_VARIATE.getId()) {
			return !this.isExistsPhenotypeByTypeAndValue(standardVariableId, value, true);
		} else {
			throw new MiddlewareQueryException("Not a valid categorical variable - " + standardVariableId);
		}
	}

	private boolean isExistsGeolocationByTypeAndValue(final int factorId, final String value) {
		final Set<Integer> geolocationIds = new HashSet<Integer>();
		geolocationIds.addAll(this.getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		return !geolocationIds.isEmpty();
	}

	private boolean isExistsStocksByTypeAndValue(final Integer factorId, final String value) {
		final Set<Integer> stockIds = new HashSet<>();
		stockIds.addAll(this.getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		return !stockIds.isEmpty();
	}

	private boolean isExistsExperimentsByTypeAndValue(final Integer factorId, final String value) {
		final Set<Integer> experimentIds = new HashSet<>();
		experimentIds.addAll(this.getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		return !experimentIds.isEmpty();
	}

	private boolean isExistsPropertyByTypeAndValue(final Integer factorId, final String value) {
		final List<ProjectProperty> properties = new ArrayList<>();
		properties.addAll(this.getProjectPropertyDao().getByTypeAndValue(factorId, value));
		return !properties.isEmpty();
	}

	private boolean isExistsPhenotypeByTypeAndValue(final Integer variateId, final String value, final boolean isEnum) {
		final List<Phenotype> phenotypes = new ArrayList<>();
		phenotypes.addAll(this.getPhenotypeDao().getByTypeAndValue(variateId, value, isEnum));
		return !phenotypes.isEmpty();
	}

	public List<StandardVariableReference> findAllByProperty(final int propertyId) {
		final List<StandardVariableReference> list = new ArrayList<>();
		list.addAll(this.getCvTermDao().getStandardVariablesOfProperty(propertyId));
		return list;
	}
}
