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

	public StandardVariableBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public StandardVariable create(int standardVariableId, String programUUID) {
		Variable variable = this.getOntologyVariableDataManager().getVariable(programUUID, standardVariableId, false);
		return this.getStandardVariableTransformer().transformVariable(variable);
	}

	public List<StandardVariable> create(List<Integer> standardVariableIds, String programUUID) {
		List<StandardVariable> standardVariables = new ArrayList<StandardVariable>();
		if (standardVariableIds != null && !standardVariableIds.isEmpty()) {
			for (Integer id : standardVariableIds) {
				standardVariables.add(this.create(id, programUUID));
			}
		}
		return standardVariables;
	}

	public StandardVariableSummary getStandardVariableSummary(Integer standardVariableId) {
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
	public List<StandardVariableSummary> getStandardVariableSummaries(List<Integer> standardVariableIds) {
		List<StandardVariableSummary> result = new ArrayList<StandardVariableSummary>();
		if (standardVariableIds != null && !standardVariableIds.isEmpty()) {
			List<StandardVariableSummary> localVariables = this.getStandardVariableDao().getStarndardVariableSummaries(standardVariableIds);
			this.specialProcessing(localVariables);
			result.addAll(localVariables);
		}
		return result;
	}

	public List<StandardVariableSummary> getStandardVariableSummariesWithIsAId(List<Integer> isAIds) {
		List<StandardVariableSummary> result = new ArrayList<StandardVariableSummary>();
		if (isAIds != null && !isAIds.isEmpty()) {
			List<StandardVariableSummary> localVariables = this.getStandardVariableDao().getStandardVariableSummaryWithIsAId(isAIds);
			result.addAll(localVariables);
		}
		return result;
	}

	private void specialProcessing(List<StandardVariableSummary> summaries) {
		if (summaries == null || summaries.isEmpty()) {
			return;
		}

		for (StandardVariableSummary summary : summaries) {
			// Special hackery for the isA (class) part of the relationship!
			// Earlier isA (class) part of the standard variables ontology star used to be linked to standard variables directly.
			// Now this relationship is linked to the "Property" of the standard variable. (facepalm).
			if (summary.getProperty() != null) {
				List<CVTermRelationship> propertyCvTermRelationships =
						this.getCvTermRelationshipDao().getBySubject(summary.getProperty().getId());
				Term isAOfProperty = this.createTerm(propertyCvTermRelationships, TermId.IS_A);
				if (isAOfProperty != null) {
					summary.setIsA(new TermSummary(isAOfProperty.getId(), isAOfProperty.getName(), isAOfProperty.getDefinition()));
				}
			}
		}

	}

	public String getCropOntologyId(Term term) {
		String cropOntologyId = null;
		List<TermProperty> termProperties = this.createTermProperties(term.getId());
		if (termProperties != null && !termProperties.isEmpty()) {
			for (TermProperty termProperty : termProperties) {
				if (TermId.CROP_ONTOLOGY_ID.getId() == termProperty.getTypeId()) {
					cropOntologyId = termProperty.getValue();
					break;
				}
			}
		}
		if (term != null) {
			CVTermProperty property = this.getCvTermPropertyDao().getOneByCvTermAndType(term.getId(), TermId.CROP_ONTOLOGY_ID.getId());
			if (property != null) {
				cropOntologyId = property.getValue();
			}
		}
		return cropOntologyId;
	}

	private Integer findTermId(List<CVTermRelationship> cvTermRelationships, TermId relationship) {
		for (CVTermRelationship cvTermRelationship : cvTermRelationships) {
			if (cvTermRelationship.getTypeId().equals(relationship.getId())) {
				return cvTermRelationship.getObjectId();
			}
		}
		return null;
	}

	private Term createTerm(List<CVTermRelationship> cvTermRelationships, TermId relationship) {
		Integer id = this.findTermId(cvTermRelationships, relationship);
		if (id != null) {
			// add to handle missing cvterm_relationship (i.e. is_a)
			return this.createTerm(id);
		}
		return null;
	}

	private Term createTerm(Integer id) {
		CVTerm cvTerm = this.getCvTerm(id);
		return cvTerm != null ? new Term(cvTerm.getCvTermId(), cvTerm.getName(), cvTerm.getDefinition()) : null;
	}

	public List<NameSynonym> createSynonyms(int cvTermId) {
		List<CVTermSynonym> synonyms = this.getNameSynonymBuilder().findSynonyms(cvTermId);
		return this.getNameSynonymBuilder().create(synonyms);
	}

	public List<TermProperty> createTermProperties(int cvTermId) {
		List<CVTermProperty> cvTermProperties = this.getTermPropertyBuilder().findProperties(cvTermId);
		return this.getTermPropertyBuilder().create(cvTermProperties);
	}

	private CVTerm getCvTerm(int id) {
		return this.getCvTermDao().getById(id);
	}

	public StandardVariable findOrSave(String name, String description, String propertyName, String scaleName, String methodName,
			PhenotypicType role, String dataTypeString, String programUUID) {

		TermBuilder termBuilder = this.getTermBuilder();
		Term property = termBuilder.findOrSaveProperty(propertyName, propertyName, null, termBuilder.getDefaultTraitClasses());

		Term scale = this.getTermBuilder().findOrSaveScale(scaleName, scaleName, this.getDataType(dataTypeString), null, null, null);
		Term method = this.getTermBuilder().findOrSaveMethod(methodName, methodName);

		VariableFilter filterOpts = new VariableFilter();
		filterOpts.setProgramUuid(programUUID);
		filterOpts.addPropertyId(property.getId());
		filterOpts.addMethodId(method.getId());
		filterOpts.addScaleId(scale.getId());

		List<Variable> variableList = this.getOntologyVariableDataManager().getWithFilter(filterOpts);
		StandardVariable standardVariable = null;
		if (variableList == null || variableList.isEmpty()) {
			OntologyVariableInfo variableInfo =
					this.createOntologyVariableInfo(name, description, method.getId(), property.getId(), scale.getId(), programUUID, null,
							null, role);
			this.getOntologyVariableDataManager().addVariable(variableInfo);
			standardVariable = this.create(variableInfo.getId(), programUUID);
			standardVariable.setPhenotypicType(role);
		} else {
			Variable variable = variableList.get(0);
			standardVariable = this.create(variable.getId(), programUUID);
			standardVariable.setPhenotypicType(role);
		}

		return standardVariable;
	}

	private String getDataType(String dataTypeString) {
		if (DATA_TYPE_NUMERIC.equals(dataTypeString)) {
			return DataType.NUMERIC_VARIABLE.getName();
		} else if (DATA_TYPE_CHARACTER.equals(dataTypeString)) {
			return DataType.CHARACTER_VARIABLE.getName();
		}
		return dataTypeString;
	}

	private OntologyVariableInfo createOntologyVariableInfo(String name, String description, int methodId, int propertyId, int scaleId,
			String programUUID, String minValue, String maxValue, PhenotypicType role) {
		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(name);
		variableInfo.setDescription(description);
		variableInfo.setMethodId(methodId);
		variableInfo.setPropertyId(propertyId);
		variableInfo.setScaleId(scaleId);
		variableInfo.setProgramUuid(programUUID);

		variableInfo.setExpectedMin(minValue);
		variableInfo.setExpectedMax(maxValue);

		variableInfo.addVariableType(this.mapPhenotypicTypeToDefaultVariableType(role));
		return variableInfo;
	}

	public VariableType mapPhenotypicTypeToDefaultVariableType(PhenotypicType role) {
		if (PhenotypicType.STUDY == role || PhenotypicType.DATASET == role) {
			return VariableType.STUDY_DETAIL;
		} else if (PhenotypicType.TRIAL_ENVIRONMENT == role) {
			return VariableType.ENVIRONMENT_DETAIL;
		} else if (PhenotypicType.GERMPLASM == role) {
			return VariableType.GERMPLASM_DESCRIPTOR;
		} else if (PhenotypicType.TRIAL_DESIGN == role) {
			return VariableType.EXPERIMENTAL_DESIGN;
		} else if (PhenotypicType.VARIATE == role) {
			return VariableType.TRAIT;
		}
		return null;
	}

	public StandardVariable getByName(String name, String programUUID) {
		CVTerm cvTerm = this.getCvTermDao().getByName(name);
		if (cvTerm != null && cvTerm.getCvTermId() != null) {
			return this.getStandardVariableBuilder().create(cvTerm.getCvTermId(), programUUID);
		}
		return null;
	}

	public StandardVariable getByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId, String programUUID) {

		Integer stdVariableId = this.getIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = this.getStandardVariableBuilder().create(stdVariableId, programUUID);
		}
		return standardVariable;
	}

	public StandardVariable getByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role,
			String programUUID) {

		Integer stdVariableId = this.getIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
		StandardVariable standardVariable = null;
		if (stdVariableId != null) {
			standardVariable = this.getStandardVariableBuilder().create(stdVariableId, programUUID);
		}
		return standardVariable;
	}

	public Integer getIdByPropertyScaleMethod(Integer propertyId, Integer scaleId, Integer methodId) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId, "DESC");
		return stdVariableId;
	}

	public Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers, String programUUID) {

		Map<String, List<StandardVariable>> standardVariablesInProjects = new HashMap<String, List<StandardVariable>>();
		Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects = new HashMap<String, Map<Integer, VariableType>>();

		// Step 1: Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
		List<String> names = headers;
		standardVariableIdsWithTypeInProjects = this.getStandardVariableIdsWithTypeForProjectProperties(names);

		// Step 2: If no variable found, search for cvterm (standard variables) with given name.
		// Exclude header items with result from step 1
		names = new ArrayList<String>();
		for (String name : headers) {
			Set<Integer> varIds = standardVariableIdsWithTypeInProjects.get(name.toUpperCase()).keySet();
			if (varIds == null || varIds.isEmpty()) {
				names.add(name);
			}
		}

		standardVariableIdsWithTypeInProjects.putAll(this.getStandardVariableIdsWithTypeForTerms(names));
		// Step 3. If no variable still found for steps 1 and 2, treat the
		// header as a trait / property name.
		// Search for trait with given name and return the standard variables
		// using that trait (if any)

		// Exclude header items with result from step 2
		names = new ArrayList<String>();
		for (String name : headers) {
			Set<Integer> varIds = standardVariableIdsWithTypeInProjects.get(name.toUpperCase()).keySet();
			if (varIds == null || varIds.isEmpty()) {
				names.add(name);
			}
		}

		standardVariableIdsWithTypeInProjects.putAll(this.getStandardVariableIdsForTraits(names));
		for (String name : headers) {
			String upperName = name.toUpperCase();
			Map<Integer, VariableType> varIdsWithType = standardVariableIdsWithTypeInProjects.get(upperName);

			List<StandardVariable> variables = new ArrayList<StandardVariable>();
			if (varIdsWithType != null) {
				List<Integer> standardVariableIds = new ArrayList<Integer>(varIdsWithType.keySet());
				variables = this.create(standardVariableIds, programUUID);
				this.setRoleOfVariables(variables, varIdsWithType);
			}
			standardVariablesInProjects.put(name, variables);
		}
		return standardVariablesInProjects;
	}

	private void setRoleOfVariables(List<StandardVariable> variables, Map<Integer, VariableType> varIdsWithType) {
		for (StandardVariable standardVariable : variables) {
			VariableType type = varIdsWithType.get(standardVariable.getId());
			standardVariable.setPhenotypicType(type.getRole());
		}
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeForProjectProperties(List<String> propertyNames) {
		return this.getProjectPropertyDao().getStandardVariableIdsWithTypeByPropertyNames(propertyNames);
	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeForTerms(List<String> termNames) {
		return this.getCvTermDao().getTermIdsWithTypeByNameOrSynonyms(termNames, CvId.VARIABLES.getId());

	}

	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsForTraits(List<String> traitNames) {
		return this.getCvTermDao().getStandardVariableIdsWithTypeByProperties(traitNames);
	}

	public Integer getIdByTermId(int cvTermId, TermId termId) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandardVariableIdByTermId(cvTermId, termId);
		return stdVariableId;
	}

	public CVTerm getCvTerm(String name, int cvId) {
		return this.getCvTermDao().getByNameAndCvId(name, cvId);
	}

	public Integer getIdByPropertyScaleMethodRole(Integer propertyId, Integer scaleId, Integer methodId, PhenotypicType role) {
		Integer stdVariableId = null;
		stdVariableId = this.getCvTermDao().getStandadardVariableIdByPropertyScaleMethodRole(propertyId, scaleId, methodId, role);
		return stdVariableId;
	}

	public boolean validateEnumerationUsage(int standardVariableId, int enumerationId) {
		Integer storedInId =
				this.getCvTermRelationshipDao().getObjectIdByTypeAndSubject(TermId.STORED_IN.getId(), standardVariableId).get(0);
		String value = String.valueOf(enumerationId);
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

	private boolean isExistsGeolocationByTypeAndValue(int factorId, String value) {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		geolocationIds.addAll(this.getGeolocationPropertyDao().getGeolocationIdsByPropertyTypeAndValue(factorId, value));
		return !geolocationIds.isEmpty();
	}

	private boolean isExistsStocksByTypeAndValue(Integer factorId, String value) {
		Set<Integer> stockIds = new HashSet<Integer>();
		stockIds.addAll(this.getStockPropertyDao().getStockIdsByPropertyTypeAndValue(factorId, value));
		return !stockIds.isEmpty();
	}

	private boolean isExistsExperimentsByTypeAndValue(Integer factorId, String value) {
		Set<Integer> experimentIds = new HashSet<Integer>();
		experimentIds.addAll(this.getExperimentPropertyDao().getExperimentIdsByPropertyTypeAndValue(factorId, value));
		return !experimentIds.isEmpty();
	}

	private boolean isExistsPropertyByTypeAndValue(Integer factorId, String value) {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		properties.addAll(this.getProjectPropertyDao().getByTypeAndValue(factorId, value));
		return !properties.isEmpty();
	}

	private boolean isExistsPhenotypeByTypeAndValue(Integer variateId, String value, boolean isEnum) {
		List<Phenotype> phenotypes = new ArrayList<Phenotype>();
		phenotypes.addAll(this.getPhenotypeDao().getByTypeAndValue(variateId, value, isEnum));
		return !phenotypes.isEmpty();
	}

	public List<StandardVariableReference> findAllByProperty(int propertyId) {
		List<StandardVariableReference> list = new ArrayList<StandardVariableReference>();
		list.addAll(this.getCvTermDao().getStandardVariablesOfProperty(propertyId));
		return list;
	}
}
