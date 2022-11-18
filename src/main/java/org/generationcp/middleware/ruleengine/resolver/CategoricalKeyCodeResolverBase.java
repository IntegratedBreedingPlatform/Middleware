
package org.generationcp.middleware.ruleengine.resolver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CategoricalKeyCodeResolverBase implements KeyComponentValueResolver {

	protected OntologyVariableDataManager ontologyVariableDataManager;

	protected final List<MeasurementVariable> studyEnvironmentVariables;
	protected final Collection<ObservationUnitData> observations;
	protected final Map<Integer, MeasurementVariable> environmentVariablesByTermId;

	public CategoricalKeyCodeResolverBase(final OntologyVariableDataManager ontologyVariableDataManager,
		final List<MeasurementVariable> studyEnvironmentVariables, final Collection<ObservationUnitData> observations,
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {

		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.observations = observations;
		this.environmentVariablesByTermId = environmentVariablesByTermId;
		this.studyEnvironmentVariables = studyEnvironmentVariables;
	}

	protected abstract TermId getKeyCodeId();

	protected abstract boolean isAbbreviationRequired();

	protected abstract String getDefaultValue();

	protected abstract String getValueFromObservationUnitData(ObservationUnitData observationUnitData);

	protected abstract String getValueFromStudyEnvironmentVariable(MeasurementVariable studyEnvironmentVariable);

	@Override
	public String resolve() {
		String resolvedValue = "";

		MeasurementVariable measurementVariable = null;

		if (this.studyEnvironmentVariables != null) {
			for (final MeasurementVariable mv : this.studyEnvironmentVariables) {
				if (mv.getTermId() == this.getKeyCodeId().getId()) {
					measurementVariable = mv;
				}
			}
		}

		if (measurementVariable != null && StringUtils.isNotBlank(measurementVariable.getValue())) {
			final Variable variable = this.getVariableByTermId(measurementVariable.getTermId());

			for (final TermSummary prefix : variable.getScale().getCategories()) {
				if (measurementVariable.getValue().equals(prefix.getId().toString()) || measurementVariable.getValue()
					.equals(prefix.getDefinition())) {
					resolvedValue = this.isAbbreviationRequired() ? prefix.getName() : prefix.getDefinition();
					break;
				}
			}
		}

		if (!CollectionUtils.isEmpty(this.observations)) {
			for (final ObservationUnitData observationUnitData : this.observations) {
				if (observationUnitData.getVariableId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromObservationUnitData(observationUnitData);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue) && this.studyEnvironmentVariables != null) {
			for (final MeasurementVariable studyEnvironmentVariable : this.studyEnvironmentVariables) {
				if (studyEnvironmentVariable.getTermId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromStudyEnvironmentVariable(studyEnvironmentVariable);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue)) {
			resolvedValue = this.getDefaultValue();
		}

		return resolvedValue;
	}

	private Variable getVariableByTermId(final Integer termId) {
		if (AdvanceContext.getVariablesByTermId().containsKey(termId)) {
			return AdvanceContext.getVariablesByTermId().get(termId);
		}
		return this.ontologyVariableDataManager.getVariable(ContextHolder.getCurrentProgramOptional().orElse(null),termId, true);
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
