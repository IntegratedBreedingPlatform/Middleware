package org.generationcp.middleware.api.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
	public List<Variable> createAnalysisVariables(final List<Integer> variableIds, final List<String> analysisNames,
		final String variableType) {

		final List<Integer> createdAnalysisVariables = new ArrayList<>();
		final VariableFilter variableFilter = new VariableFilter();
		variableIds.stream().forEach(variableFilter::addVariableId);
		// Get the existing traits
		final Map<Integer, Variable> variablesMap = this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);
		// Create ontology methods for analysis names if not yet present
		final Map<String, CVTerm> methodsMap = this.createOntologyMethodsIfNecessary(analysisNames);

		for (final Map.Entry<Integer, Variable> variableEntry : variablesMap.entrySet()) {
			for (final String analysisName : analysisNames) {
				final Integer existingVariableId = this.daoFactory.getCvTermRelationshipDao()
					.retrieveAnalysisDerivedVariableID(variableEntry.getKey(), methodsMap.get(analysisName).getCvTermId());
				if (existingVariableId == null) {
					createdAnalysisVariables.add(
						this.createAnalysisStandardVariable(variableEntry.getValue(), methodsMap.get(analysisName), variableType));
				} else {
					createdAnalysisVariables.add(existingVariableId);
				}
			}
		}

		final VariableFilter analsysisVariableFilter = new VariableFilter();
		variableIds.stream().forEach(analsysisVariableFilter::addVariableId);
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(analsysisVariableFilter).values().stream()
			.collect(Collectors.toList());
	}

	private Integer createAnalysisStandardVariable(final Variable variable, final CVTerm method, final String variableType) {
		// Create variable
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(variable.getName() + "_" + method.getName(), variable.getDefinition(), CvId.VARIABLES);
		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), variable.getProperty().getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), variable.getScale().getId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());
		// Assign variable type
		this.daoFactory.getCvTermPropertyDao().save(cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableType, 0);
		// Link the new analysis standar variable to the analyzed trait
		this.daoFactory.getCvTermRelationshipDao()
			.save(variable.getId(), TermId.HAS_ANALYSIS_VARIABLE.getId(), cvTermVariable.getCvTermId());
		return cvTermVariable.getCvTermId();
	}

	private Map<String, CVTerm> createOntologyMethodsIfNecessary(final List<String> methodNames) {
		final Map<String, CVTerm> methodsMap =
			this.daoFactory.getCvTermDao().getTermsByNameAndCvId(methodNames, CvId.METHODS.getId()).stream()
				.collect(Collectors.toMap(CVTerm::getName, Function.identity()));

		for (final String methodName : methodNames) {
			if (!methodsMap.containsKey(methodName)) {
				final CVTerm newMethod = new CVTerm();
				newMethod.setName(methodName);
				newMethod.setDefinition(methodName + " - system generated method");
				newMethod.setCv(CvId.METHODS.getId());
				newMethod.setIsObsolete(false);
				newMethod.setIsRelationshipType(false);
				newMethod.setIsSystem(false);
				methodsMap.putIfAbsent(methodName, this.daoFactory.getCvTermDao().save(newMethod));
			}
		}
		return methodsMap;
	}

}
