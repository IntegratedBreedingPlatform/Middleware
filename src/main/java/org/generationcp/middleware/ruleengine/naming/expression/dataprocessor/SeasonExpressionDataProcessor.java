
package org.generationcp.middleware.ruleengine.naming.expression.dataprocessor;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SeasonExpressionDataProcessor implements ExpressionDataProcessor {

	@Override
	public void processEnvironmentLevelData(final AdvancingSource source, final AdvanceStudyRequest advanceStudyRequest,
		final List<MeasurementVariable> conditions,
		final List<MeasurementVariable> constants) {

		final Map<Integer, String> measurementVariablesValues = new HashMap<>();
		for (final MeasurementVariable mv : conditions) {
			this.addValueToMeasurementVariablesValues(mv.getValue(), mv.getPossibleValues(), mv.getTermId(), measurementVariablesValues);
		}
		source.setSeason(this.getValueOfPrioritySeasonVariable(measurementVariablesValues));
	}

	@Override
	public void processPlotLevelData(final AdvancingSource source, final ObservationUnitRow row) {
		if (StringUtils.isBlank(source.getSeason())
				&& source.getTrailInstanceObservation() != null && source.getTrailInstanceObservation().getDataList() != null) {
			final Map<Integer, String> measurementVariablesValues = new HashMap<>();
			for (final MeasurementData measurementData : source.getTrailInstanceObservation().getDataList()) {
				final int termId = measurementData.getMeasurementVariable().getTermId();
				final List<ValueReference> possibleValues = measurementData.getMeasurementVariable().getPossibleValues();
				this.addValueToMeasurementVariablesValues(measurementData.getValue(), possibleValues, termId, measurementVariablesValues);
			}
			source.setSeason(this.getValueOfPrioritySeasonVariable(measurementVariablesValues));
		}
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

	String getSeasonName(final String value, final List<ValueReference> possibleValues) {

		if (possibleValues != null && !possibleValues.isEmpty()) {
			for (final ValueReference valueReference : possibleValues) {
				// The Season Code variable is categorical type, it's value should be the id of the season (valid value).
				// But Season Code's value can also be the text description of the season, so we also need to find the valid value by description.
				if ((StringUtils.isNumeric(value) && valueReference.getId().intValue() == Integer.parseInt(value)) || valueReference.getDescription().equals(value)) {
					return valueReference.getName();
				}
			}
		}
		// if the value is not in the possible values (valid values), just return it as is.
		return value;
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

}
