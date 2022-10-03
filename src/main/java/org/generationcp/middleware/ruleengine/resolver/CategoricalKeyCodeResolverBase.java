
package org.generationcp.middleware.ruleengine.resolver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;
import java.util.Map;

public abstract class CategoricalKeyCodeResolverBase implements KeyComponentValueResolver {

	protected OntologyVariableDataManager ontologyVariableDataManager;

	protected List<MeasurementVariable> conditions;
	protected ObservationUnitRow observationUnitRow;
	protected Map<Integer, MeasurementVariable> environmentVariablesByTermId;

	public CategoricalKeyCodeResolverBase(final OntologyVariableDataManager ontologyVariableDataManager,
		final List<MeasurementVariable> conditions, final ObservationUnitRow observationUnitRow,
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {

		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.observationUnitRow = observationUnitRow;
		this.environmentVariablesByTermId = environmentVariablesByTermId;
		this.conditions = conditions;
	}

	protected abstract TermId getKeyCodeId();

	protected abstract boolean isAbbreviationRequired();

	protected abstract String getDefaultValue();

	protected abstract String getValueFromObservationUnitData(ObservationUnitData observationUnitData);

	protected abstract String getValueFromTrialConditions(MeasurementVariable trialCondition);

	@Override
	public String resolve() {
		String resolvedValue = "";

		MeasurementVariable measurementVariable = null;

		if (this.conditions != null) {
			for (final MeasurementVariable mv : this.conditions) {
				if (mv.getTermId() == this.getKeyCodeId().getId()) {
					measurementVariable = mv;
				}
			}
		}

		if (measurementVariable != null && StringUtils.isNotBlank(measurementVariable.getValue())) {
			final String programUUID = ContextHolder.getCurrentProgramOptional().orElse(null);

			final Variable variable = this.ontologyVariableDataManager
					.getVariable(programUUID, measurementVariable.getTermId(), true);

			for (final TermSummary prefix : variable.getScale().getCategories()) {
				if (measurementVariable.getValue().equals(prefix.getId().toString()) || measurementVariable.getValue()
					.equals(prefix.getDefinition())) {
					resolvedValue = this.isAbbreviationRequired() ? prefix.getName() : prefix.getDefinition();
					break;
				}
			}
		}

		if (this.observationUnitRow != null) {
			for (final ObservationUnitData observationUnitData : this.observationUnitRow.getVariables().values()) {
				if (observationUnitData.getVariableId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromObservationUnitData(observationUnitData);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue) && this.conditions != null) {
			for (final MeasurementVariable trialCondition : this.conditions) {
				if (trialCondition.getTermId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromTrialConditions(trialCondition);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue)) {
			resolvedValue = this.getDefaultValue();
		}

		return resolvedValue;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
