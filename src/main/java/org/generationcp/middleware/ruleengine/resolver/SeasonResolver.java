
package org.generationcp.middleware.ruleengine.resolver;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Revolves Season value for Nurseries and Trials.
 */
public class SeasonResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(SeasonResolver.class);

	public SeasonResolver(final OntologyVariableDataManager ontologyVariableDataManager,
		final List<MeasurementVariable> studyEnvironmentVariables, final Collection<ObservationUnitData> observations,
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {

		super(ontologyVariableDataManager, studyEnvironmentVariables, observations, environmentVariablesByTermId);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.SEASON_VAR;
	}

	@Override
	protected boolean isAbbreviationRequired() {
		return true;
	}

	@Override
	protected String getDefaultValue() {
		// Default the season to current year and month.
		final SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		final String currentYearAndMonth = formatter.format(new java.util.Date());
		SeasonResolver.LOG.debug(
				"No Crop_season_Code(8371) variable was found or it is present but no value is set. Defaulting [SEASON] with: {}.",
				currentYearAndMonth);
		return currentYearAndMonth;
	}

	@Override
	protected String getValueFromObservationUnitData(final ObservationUnitData observationUnitData) {
		return this.getValue(observationUnitData.getValue(),
			this.environmentVariablesByTermId.get(observationUnitData.getVariableId()).getPossibleValues());
	}

	protected Optional<ValueReference> findValueReferenceByDescription(final String description,
			final List<ValueReference> possibleValues) {

		if (possibleValues != null) {
			for (final ValueReference valueReference : possibleValues) {
				if (valueReference.getDescription().equals(description)) {
					return Optional.of(valueReference);
				}
			}
		}

		return Optional.empty();

	}

	@Override
	protected String getValueFromStudyEnvironmentVariable(final MeasurementVariable studyEnvironmentVariable) {
		if (TermId.SEASON_VAR.getId() == studyEnvironmentVariable.getTermId()) {
			return this.getValue(studyEnvironmentVariable.getValue(), studyEnvironmentVariable.getPossibleValues());
		}
		return "";
	}

	private String getValue(final String value, final List<ValueReference> possibleValues) {
		final Optional<ValueReference> valueReferenceOptional = this.findValueReferenceByDescription(value, possibleValues);

		if (valueReferenceOptional.isPresent()) {
			return valueReferenceOptional.get().getName();
		} else {
			return value;
		}
	}

}
