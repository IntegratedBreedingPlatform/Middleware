package org.generationcp.middleware.ruleengine.naming.resolver;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeasonDataResolver {

	/**
	 * Returns the season value at study level.
	 *
	 * @param studyEnvironmentVariables
	 * @return the season
	 */
	public String resolveStudyLevelData(final List<MeasurementVariable> studyEnvironmentVariables) {
		final Map<Integer, String> measurementVariablesValues = new HashMap<>();
		studyEnvironmentVariables.forEach(mv -> this
			.addValueToMeasurementVariablesValues(mv.getValue(), mv.getPossibleValues(), mv.getTermId(), measurementVariablesValues));
		return this.getValueOfPrioritySeasonVariable(measurementVariablesValues);
	}

	/**
	 * Resolves season data at environment level. The required data will be set into the provided {@link NewAdvancingSource}
	 *
	 * @param source
	 * @param plotDataVariablesByTermId
	 */
	public void resolveEnvironmentLevelData(final NewAdvancingSource source,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		if (StringUtils.isBlank(source.getSeason()) && DataResolverHelper
			.checkHasTrailInstanceObservations(source.getTrailInstanceObservation())) {
			final Map<Integer, String> measurementVariablesValues = new HashMap<>();
			source.getTrailInstanceObservation().getEnvironmentVariables().values().forEach(observationUnitData -> {
				final int termId = observationUnitData.getVariableId();
				final MeasurementVariable measurementVariable = plotDataVariablesByTermId.get(termId);
				if (measurementVariable != null) {
					final List<ValueReference> possibleValues = measurementVariable.getPossibleValues();
					this.addValueToMeasurementVariablesValues(observationUnitData.getValue(), possibleValues, termId,
						measurementVariablesValues);
				}
			});
			source.setSeason(this.getValueOfPrioritySeasonVariable(measurementVariablesValues));
		}
	}

	private void addValueToMeasurementVariablesValues(final String value, final List<ValueReference> possibleValues, final int termId,
		final Map<Integer, String> measurementVariablesValues) {
		if (StringUtils.isNotBlank(value)) {
			if (termId == TermId.SEASON_VAR.getId()) {
				final String seasonVarValue = this.getSeasonName(value, possibleValues);
				measurementVariablesValues.put(termId, seasonVarValue);
			} else {
				measurementVariablesValues.put(termId, value);
			}
		}
	}

	String getSeasonName(final String value, final List<ValueReference> possibleValues) {
		if (!CollectionUtils.isEmpty(possibleValues)) {
			for (final ValueReference valueReference : possibleValues) {
				// The Season Code variable is categorical type, it's value should be the id of the season (valid value).
				// But Season Code's value can also be the text description of the season, so we also need to find the valid value by description.
				if ((StringUtils.isNumeric(value) && valueReference.getId() == Integer.parseInt(value)) || valueReference
					.getDescription().equals(value)) {
					return valueReference.getName();
				}
			}
		}
		// if the value is not in the possible values (valid values), just return it as is.
		return value;
	}

	private String getValueOfPrioritySeasonVariable(final Map<Integer, String> measurementVariablesValues) {
		if (measurementVariablesValues.get(TermId.SEASON_MONTH.getId()) != null) {
			return measurementVariablesValues.get(TermId.SEASON_MONTH.getId());
		} else if (measurementVariablesValues.get(TermId.SEASON_VAR_TEXT.getId()) != null) {
			return measurementVariablesValues.get(TermId.SEASON_VAR_TEXT.getId());
		} else if (measurementVariablesValues.get(TermId.SEASON_VAR.getId()) != null) {
			return measurementVariablesValues.get(TermId.SEASON_VAR.getId());
		}
		return "";
	}

}
