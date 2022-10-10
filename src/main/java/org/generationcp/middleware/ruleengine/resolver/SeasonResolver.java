
package org.generationcp.middleware.ruleengine.resolver;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Revolves Season value for Nurseries and Trials.
 */
public class SeasonResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(SeasonResolver.class);

	public SeasonResolver(final OntologyVariableDataManager ontologyVariableDataManager,
		final List<MeasurementVariable> conditions, final ObservationUnitRow observationUnitRow,
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {

		super(ontologyVariableDataManager, conditions, observationUnitRow, environmentVariablesByTermId);
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

		return Optional.absent();

	}

	@Override
	protected String getValueFromTrialConditions(final MeasurementVariable variable) {
		if (TermId.SEASON_VAR.getId() == variable.getTermId()) {
			return this.getValue(variable.getValue(), variable.getPossibleValues());
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
