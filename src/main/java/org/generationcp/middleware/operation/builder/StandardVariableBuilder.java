/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StandardVariableBuilder extends Builder {

	private static final List<Integer> GERMPLASM_DESCRIPTOR_VARIABLE_IDS_ALLOWED = Arrays.asList(8377, 8250, 8240, 8330, 8340, 8235, 8378, 8201);
	private static final String DATA_TYPE_NUMERIC = "N";
	private static final String DATA_TYPE_CHARACTER = "C";
	private static final String LOCATION_NAME = "LOCATION_NAME";

	private final DaoFactory daoFactory;

	private TermPropertyBuilder termPropertyBuilder;

	public StandardVariableBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		termPropertyBuilder = new TermPropertyBuilder(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public StandardVariable create(final int standardVariableId, final String programUUID) {

		final Variable variable = this.getOntologyVariableDataManager().getVariable(programUUID, standardVariableId, false);

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
			summary = this.daoFactory.getStandardVariableDao().getStandardVariableSummary(standardVariableId);
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
				this.daoFactory.getStandardVariableDao().getStarndardVariableSummaries(standardVariableIds);
			this.specialProcessing(localVariables);
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
					daoFactory.getCvTermRelationshipDao().getBySubject(summary.getProperty().getId());
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

		final CVTermProperty property =
			daoFactory.getCvTermPropertyDao().getOneByCvTermAndType(term.getId(), TermId.CROP_ONTOLOGY_ID.getId());
		if (property != null) {
			cropOntologyId = property.getValue();
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
		final List<CVTermProperty> cvTermProperties = daoFactory.getCvTermPropertyDao().getByCvTermId(cvTermId);
		return this.termPropertyBuilder.create(cvTermProperties);
	}

	private CVTerm getCvTerm(final int id) {
		return daoFactory.getCvTermDao().getById(id);
	}

	public StandardVariable findOrSave(
		final String name, final String description, final String propertyName, final String scaleName, final String methodName,
		final PhenotypicType role, final VariableType variableType, final String dataTypeString, final String programUUID) {

		final TermBuilder termBuilder = this.getTermBuilder();
		final Term property = termBuilder.findOrSaveProperty(propertyName, propertyName, null, termBuilder.getDefaultTraitClasses());

		final Term scale = this.getTermBuilder().findOrSaveScale(scaleName, scaleName, this.getDataType(dataTypeString), null, null, null);
		final Term method = this.getTermBuilder().findOrSaveMethod(methodName, methodName);

		final VariableFilter filterOpts = new VariableFilter();
		filterOpts.setProgramUuid(programUUID);
		filterOpts.addPropertyId(property.getId());
		filterOpts.addMethodId(method.getId());
		filterOpts.addScaleId(scale.getId());

		final List<Variable> variableList = this.getOntologyVariableDataManager().getWithFilter(filterOpts);
		final StandardVariable standardVariable;

		final VariableType newVariableType =
			(variableType == null) ? OntologyDataHelper.mapFromPhenotype(role, propertyName) : variableType;

		if (variableList == null || variableList.isEmpty()) {
			final OntologyVariableInfo variableInfo =
				this.createOntologyVariableInfo(name, description, method.getId(), property.getId(), scale.getId(), programUUID, null,
					null, newVariableType);
			this.getOntologyVariableDataManager().addVariable(variableInfo);
			standardVariable = this.create(variableInfo.getId(), programUUID);
		} else {
			final Variable variable = variableList.get(0);
			standardVariable = this.create(variable.getId(), programUUID);
		}

		standardVariable.setPhenotypicType(role);
		standardVariable.setVariableTypes(new HashSet<>(new ArrayList<>(Collections.singletonList(newVariableType))));

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

	private OntologyVariableInfo createOntologyVariableInfo(
		final String name, final String description, final int methodId,
		final int propertyId, final int scaleId, final String programUUID, final String minValue, final String maxValue,
		final VariableType variableType) {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
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
	 * @param role
	 * @param isForAnalysis
	 * @return
	 * @deprecated Use OntologyDataHelper to map from PhenotypicType to VariableType correctly.
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
		final CVTerm cvTerm = daoFactory.getCvTermDao().getByNameAndCvId(name, CvId.VARIABLES.getId());
		if (cvTerm != null && cvTerm.getCvTermId() != null) {
			return this.getStandardVariableBuilder().create(cvTerm.getCvTermId(), programUUID);
		}
		return null;
	}

	public StandardVariable getByPropertyScaleMethod(
		final Integer propertyId, final Integer scaleId, final Integer methodId,
		final String programUUID) {

		final Integer stdVariableId = this.getIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = this.getStandardVariableBuilder().create(stdVariableId, programUUID);
		}
		return standardVariable;
	}

	public StandardVariable getByPropertyScaleMethodRole(
		final Integer propertyId, final Integer scaleId, final Integer methodId,
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
		stdVariableId = daoFactory.getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId, "DESC");
		return stdVariableId;
	}

	/**
	 * @param headerNames
	 * @param programUUID
	 * @return a map with Property names (In UPPERCASE) as keys and a list of StandardVariable as Value
	 */
	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(final List<String> headerNames, final String programUUID) {
		// Trim all header names
		final List<String> headerNamesTrimmed = new ArrayList<>(Lists.transform(headerNames, new Function<String, String>() {

			public String apply(String s) {
				// Transform header names to uppercase; header names can be in lowercase or combination when defined in file
				return s.toUpperCase().trim();
			}
		}));

		headerNamesTrimmed.remove(StandardVariableBuilder.LOCATION_NAME);

		final Map<String, List<StandardVariable>> standardVariablesInProjects = new HashMap<>();

		// Step 1: Search for DISTINCT standard variables used for projectprop records where projectprop.alias equals input name (eg. REP)
		final Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects =
			this.getStandardVariableIdsWithTypeForProjectProperties(headerNamesTrimmed, programUUID);

		// Exclude header items with result from step 1
		this.removeHeaderNamesWithMatch(headerNamesTrimmed, standardVariableIdsWithTypeInProjects);

		// Step 2: Search variables that match the standard variable's ALIAS (Ontology Level)
		standardVariableIdsWithTypeInProjects
			.putAll(this.getStandardVariableIdsWithTypeByAliasInVariableOverrideTable(headerNamesTrimmed, programUUID));

		// Exclude header items with result from step 2
		this.removeHeaderNamesWithMatch(headerNamesTrimmed, standardVariableIdsWithTypeInProjects);

		// Step 3: Search variables that match the standard variable's NAME (Ontology Level)
		this.getStandardVariableIdsWithTypeForTerms(headerNamesTrimmed, standardVariableIdsWithTypeInProjects);

		// Exclude header items with result from step 3
		this.removeHeaderNamesWithMatch(headerNamesTrimmed, standardVariableIdsWithTypeInProjects);

		// Step 4. If no variable still found from steps 1 to 3, treat the
		// header as a trait / property name.
		// Search for trait with given name and return the standard variables
		// using that trait (if any)
		standardVariableIdsWithTypeInProjects.putAll(this.getStandardVariableIdsForTraits(headerNamesTrimmed));

		// Exclude header items with result from step 4
		this.removeHeaderNamesWithMatch(headerNamesTrimmed, standardVariableIdsWithTypeInProjects);

		for (final Map.Entry<String, Map<Integer, VariableType>> entry : standardVariableIdsWithTypeInProjects.entrySet()) {
			final Map<Integer, VariableType> varIdsWithType = entry.getValue();

			List<StandardVariable> variables = new ArrayList<>();
			if (varIdsWithType != null) {
				final List<Integer> standardVariableIds = new ArrayList<>(varIdsWithType.keySet());
				variables = this.create(standardVariableIds, programUUID);
				this.setRoleOfVariables(variables, varIdsWithType);
			}
			standardVariablesInProjects.put(entry.getKey(), variables);
		}

		// For headers with no match, just assign an empty list.
		for (final String name : headerNamesTrimmed) {
			standardVariablesInProjects.put(name, new ArrayList<StandardVariable>());
		}

		return standardVariablesInProjects;
	}

	private void removeHeaderNamesWithMatch(
		final List<String> headerNames, final Map<String, Map<Integer, VariableType>> headerNametoStandardVariableIdMap) {
		for (final Iterator<String> iterator = headerNames.iterator(); iterator.hasNext(); ) {
			final String name = iterator.next();
			if (headerNametoStandardVariableIdMap.containsKey(name.toUpperCase()) && !headerNametoStandardVariableIdMap
				.get(name).keySet().isEmpty()) {
				// remove from header name if it already has a match
				iterator.remove();
			}
		}
	}

	protected void setRoleOfVariables(final List<StandardVariable> variables, final Map<Integer, VariableType> varIdsWithType) {
		for (final StandardVariable standardVariable : variables) {
			final VariableType type = varIdsWithType.get(standardVariable.getId());
			if (type != null) {
				standardVariable.setPhenotypicType(type.getRole());
			}
		}
	}

	/**
	 * @param variableNames
	 * @return a map with Property names (In UPPERCASE) as keys and a map(variableId, variableType) as Value
	 */
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeForProjectProperties(
		final List<String> variableNames, final String programUUID) {
		if (!variableNames.isEmpty()) {
			return this.daoFactory.getProjectPropertyDAO().getStandardVariableIdsWithTypeByAlias(variableNames, programUUID);
		}
		return new HashMap<>();
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByAliasInVariableOverrideTable(
		final List<String> variableNames, final String programUUID) {
		if (!variableNames.isEmpty()) {
			return this.daoFactory.getVariableProgramOverridesDao().getVariableOverridesByVariableIdsAndProgram(variableNames, programUUID);
		}
		return new HashMap<>();
	}

	public void getStandardVariableIdsWithTypeForTerms(final List<String> termNames, Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects) {
		Map<String, Map<Integer, VariableType>> variables = new HashMap<>();
		if (!termNames.isEmpty()) {
			variables = daoFactory.getCvTermDao().getTermIdsWithTypeByNameOrSynonyms(termNames, CvId.VARIABLES.getId());
		}

		for (Map.Entry<String, Map<Integer, VariableType>> entry : variables.entrySet()) {
			if (entry.getValue().values().contains(VariableType.GERMPLASM_DESCRIPTOR) && !StandardVariableBuilder.GERMPLASM_DESCRIPTOR_VARIABLE_IDS_ALLOWED.contains(entry.getValue().keySet())) {
				continue;
			}
			standardVariableIdsWithTypeInProjects.put(entry.getKey(), entry.getValue());
		}
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsForTraits(final List<String> traitNames) {
		if (!traitNames.isEmpty()) {
			return daoFactory.getCvTermDao().getStandardVariableIdsWithTypeByProperties(traitNames);
		}
		return new HashMap<>();
	}

	public Integer getIdByTermId(final int cvTermId, final TermId termId) {
		Integer stdVariableId = null;
		stdVariableId = daoFactory.getCvTermDao().getStandardVariableIdByTermId(cvTermId, termId);
		return stdVariableId;
	}

	public CVTerm getCvTerm(final String name, final int cvId) {
		return daoFactory.getCvTermDao().getByNameAndCvId(name, cvId);
	}

	public Integer getIdByPropertyScaleMethodRole(
		final Integer propertyId, final Integer scaleId, final Integer methodId,
		final PhenotypicType role) {
		Integer stdVariableId = null;
		stdVariableId = daoFactory.getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
		return stdVariableId;
	}

	public boolean validateEnumerationUsage(final int standardVariableId, final int enumerationId) {
		final Integer storedInId =
			daoFactory.getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), standardVariableId).get(0);
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
		final Set<Integer> geolocationIds =
			new HashSet<>(this.daoFactory.getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		return !geolocationIds.isEmpty();
	}

	private boolean isExistsStocksByTypeAndValue(final Integer factorId, final String value) {
		final Set<Integer> stockIds =
			new HashSet<>(this.daoFactory.getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		return !stockIds.isEmpty();
	}

	private boolean isExistsExperimentsByTypeAndValue(final Integer factorId, final String value) {
		final Set<Integer> experimentIds =
			new HashSet<>(this.daoFactory.getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		return !experimentIds.isEmpty();
	}

	private boolean isExistsPropertyByTypeAndValue(final Integer factorId, final String value) {
		final List<ProjectProperty> properties =
			new ArrayList<>(this.daoFactory.getProjectPropertyDAO().getByTypeAndValue(factorId, value));
		return !properties.isEmpty();
	}

	private boolean isExistsPhenotypeByTypeAndValue(final Integer variateId, final String value, final boolean isEnum) {
		final List<Phenotype> phenotypes = new ArrayList<>(this.daoFactory.getPhenotypeDAO().getByTypeAndValue(variateId, value, isEnum));
		return !phenotypes.isEmpty();
	}

	public List<StandardVariableReference> findAllByProperty(final int propertyId) {
		return new ArrayList<>(daoFactory.getCvTermDao().getStandardVariablesOfProperty(propertyId));
	}
}
