package org.generationcp.middleware.api.ontology;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Replaces {@link OntologyVariableDataManagerImpl}
 */
@Transactional
@Service
public class OntologyVariableServiceImpl implements OntologyVariableService {

	private final DaoFactory daoFactory;

	public OntologyVariableServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Map<Integer, Variable> getVariablesWithFilterById(final VariableFilter variableFilter) {
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);
	}

	@Override
	public List<Integer> createAnalysisVariables(final List<Integer> variableIds, final List<String> analysisNames,
		final String variableType) {

		final List<Integer> analysisVariableIds = new ArrayList<>();
		final VariableFilter variableFilter = new VariableFilter();
		variableIds.stream().forEach(variableFilter::addVariableId);
		// Get the existing trait variables
		final Map<Integer, Variable> variablesMap = this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);
		// Create ontology methods for analysis names if not yet present, will also return methods if already present
		final Map<String, CVTerm> methodsMap = this.createOntologyMethodsIfNecessary(analysisNames);
		// Check if the analysis variables associated to trait variables are already present
		final MultiKeyMap existingAnalysisMethodsOfTraitsMap = this.daoFactory.getCvTermRelationshipDao()
			.retrieveAnalysisMethodsOfTraits(variableIds, methodsMap.values().stream().map(CVTerm::getCvTermId).collect(
				Collectors.toList()));

		// Create analysis variable for every trait and analysis methods combination
		for (final Map.Entry<Integer, Variable> variableEntry : variablesMap.entrySet()) {
			for (final String analysisName : analysisNames) {
				final CVTerm method = methodsMap.get(analysisName);
				// If analysis variable already exists for specific trait, do not create new, just return the existing id of analysis variable
				if (existingAnalysisMethodsOfTraitsMap.containsKey(variableEntry.getKey(), method.getCvTermId())) {
					analysisVariableIds.add(
						(Integer) existingAnalysisMethodsOfTraitsMap.get(variableEntry.getKey(), method.getCvTermId()));
				} else { // else, create new analysis variable
					analysisVariableIds.add(
						this.createAnalysisStandardVariable(variableEntry.getValue(), method, variableType));
				}
			}
		}
		return analysisVariableIds;
	}

	@Override
	public Multimap<Integer, VariableType> getVariableTypesOfVariables(final List<Integer> variableIds) {
		final Multimap<Integer, VariableType> variableTypesMultimap = ArrayListMultimap.create();
		final List<CVTermProperty> properties =
			this.daoFactory.getCvTermPropertyDao().getByCvTermIdsAndType(variableIds, TermId.VARIABLE_TYPE.getId());
		for (final CVTermProperty property : properties) {
			variableTypesMultimap.put(property.getCvTermId(), VariableType.getByName(property.getValue()));
		}
		return variableTypesMultimap;
	}

	private Integer createAnalysisStandardVariable(final Variable traitVariable, final CVTerm method, final String variableType) {
		String analysisVariableName = traitVariable.getName() + "_" + method.getName();

		/** It's possible that the analysisVariableName to be saved is already used by other variables,
		 * in that case append _1 to the name to make sure it's unique. This is the same logic found in
		 * {@link org.generationcp.commons.service.impl.BreedingViewImportServiceImpl#createAnalysisVariable(org.generationcp.middleware.domain.dms.DMSVariableType, java.lang.String, org.generationcp.middleware.domain.oms.Term, java.lang.String, int, boolean)}
		 **/
		final List<Integer> existingTerms =
			this.daoFactory.getCvTermDao().getTermsByNameOrSynonym(analysisVariableName, CvId.VARIABLES.getId());
		if (CollectionUtils.isNotEmpty(existingTerms)) {
			analysisVariableName += "_1";
		}

		// Create analysis variable
		final CVTerm analysisVariable = this.daoFactory.getCvTermDao()
			.save(analysisVariableName, traitVariable.getDefinition(), CvId.VARIABLES);
		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(analysisVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), traitVariable.getProperty().getId());
		// Assuming that the analyzed trait's scale is numeric
		this.daoFactory.getCvTermRelationshipDao()
			.save(analysisVariable.getCvTermId(), TermId.HAS_SCALE.getId(), traitVariable.getScale().getId());
		this.daoFactory.getCvTermRelationshipDao().save(analysisVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());
		// Assign variable type
		this.daoFactory.getCvTermPropertyDao().save(analysisVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableType, 0);
		// Link the new analysis variable to the analyzed trait
		this.daoFactory.getCvTermRelationshipDao()
			.save(traitVariable.getId(), TermId.HAS_ANALYSIS_VARIABLE.getId(), analysisVariable.getCvTermId());
		return analysisVariable.getCvTermId();
	}

	private Map<String, CVTerm> createOntologyMethodsIfNecessary(final List<String> analysisNames) {

		// Get the existing methods
		final Map<String, CVTerm> methodsMap = new CaseInsensitiveMap();
		this.daoFactory.getCvTermDao().getTermsByNameAndCvId(analysisNames, CvId.METHODS.getId()).stream().forEach(method -> methodsMap.put(
			method.getName(), method));

		for (final String analysisName : analysisNames) {
			// Create analysis method if not yet present
			if (!methodsMap.containsKey(analysisName)) {
				final CVTerm newMethod = new CVTerm();
				newMethod.setName(analysisName);
				newMethod.setDefinition(analysisName + " - system generated method");
				newMethod.setCv(CvId.METHODS.getId());
				newMethod.setIsObsolete(false);
				newMethod.setIsRelationshipType(false);
				newMethod.setIsSystem(false);
				methodsMap.putIfAbsent(analysisName, this.daoFactory.getCvTermDao().save(newMethod));
			}
		}
		return methodsMap;
	}

}
