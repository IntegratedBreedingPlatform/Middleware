
package org.generationcp.middleware.reports;

import com.google.common.base.Strings;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.List;
import java.util.Map;

public class MaizeTrialManifest extends AbstractDynamicReporter {

	public static final String PLANTING_DATE_REPORT_KEY = "plantingDate";
	public static final String DISTANCE_BETWEEN_STATIONS_REPORT_KEY = "distanceBetweenStations";
	public static final String ROWS_HARVESTED_REPORT_KEY = "rowsHarvested";
	public static final String COLLABORATOR_REPORT_KEY = "collaborator";
	public static final String HARVEST_DATE_REPORT_KEY = "harvestDate";
	public static final String DISTANCE_BETWEEN_ROWS_REPORT_KEY = "distanceBetweenRows";
	public static final String NET_PLOT_LENGTH_REPORT_KEY = "netPlotLength";
	protected static final String[] UNIQUE_REPORT_KEYS =
		new String[] {DISTANCE_BETWEEN_STATIONS_REPORT_KEY, ROWS_HARVESTED_REPORT_KEY, COLLABORATOR_REPORT_KEY, PLANTING_DATE_REPORT_KEY,
			HARVEST_DATE_REPORT_KEY, DISTANCE_BETWEEN_ROWS_REPORT_KEY, NET_PLOT_LENGTH_REPORT_KEY};
	public static final String MAIZE_MANIFEST_PROGRAM_KEY = "breedingProgram";

    protected ReportParameterMapper parameterMapper = new ReportParameterMapper();

	@Override
	public Reporter createReporter() {
		final Reporter r = new MaizeTrialManifest();
		r.setFileNameExpression("Maize_TRIAL_{trialName}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFbTrial";
	}

	@Override
	public String getTemplateName() {
		return "MFb2_main";
	}

	@Override
	public Map<String, Object> buildJRParams(final Map<String, Object> args) {
		final Map<String, Object> params = super.buildJRParams(args);

		params.put(MAIZE_MANIFEST_PROGRAM_KEY, args.get(PROGRAM_NAME_ARG_KEY));

        final List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get(STUDY_CONDITIONS_KEY);
        for (final MeasurementVariable studyCondition : studyConditions) {
            this.parameterMapper.mapBasicStudyValues(studyCondition, params);
            mapEnvironmentValue(studyCondition, params, studyCondition.getValue());
        }

        // update trial report data extraction logic so that study environment entries are also retrieved from the
        // trial environment
        @SuppressWarnings("unchecked")
        final List<MeasurementRow> trialObservations = (List<MeasurementRow>) args.get(STUDY_OBSERVATIONS_KEY);
        // attempt to extract values from the observations. currently, only the value from the first measurement row is used
        if (!trialObservations.isEmpty()) {

            for (final MeasurementData data : trialObservations.get(0).getDataList()) {
                mapEnvironmentValue(data.getMeasurementVariable(), params, data.getValue());
            }
        }

		// ensure that null values are not shown for fields whose variables are not present in the trial / not yet implemented
		// TODO : look into possibly implementing this as well for the other reports in the system
		this.provideBlankValues(params);

		return params;
	}

	protected void provideBlankValues(final Map<String, Object> params) {
		for (final String uniqueReportKey : UNIQUE_REPORT_KEYS) {
			if (params.containsKey(uniqueReportKey)) {
				continue;
			}

			if (PLANTING_DATE_REPORT_KEY.equals(uniqueReportKey)) {
				// we put in a blank string with 8 characters for planting date because the report expects a date with yyyymmdd format, and
				// performs substring operations
				params.put(PLANTING_DATE_REPORT_KEY, Strings.repeat(" ", 8));
			} else {
				params.put(uniqueReportKey, "");
			}
		}
	}

	/**
	 *
	 * @param var
	 * @param reportParamMap
	 * @param value
	 */
	protected void mapEnvironmentValue(final MeasurementVariable var, final Map<String, Object> reportParamMap, final String value) {
        // the previous mapping logic available in nurseries / trials are applied
        // dev note : this functionality is maintained within this class instead of creating a subclass of the ReportParameterMapper
        // as there is currently no reliable way of
        this.parameterMapper.mapEnvironmentValue(var, reportParamMap, value);
		final TermId term = TermId.getById(var.getTermId());

		if (term == TermId.TRIAL_LOCATION) {
            reportParamMap.put("location", value);
		}

		switch (var.getName().toUpperCase()) {
			case "COLLABORATOR":
				reportParamMap.put(COLLABORATOR_REPORT_KEY, value);
				break;
			case "ENVIRONMENT":
				reportParamMap.put("environment", value);
				break;
			case "PLANTINGDATE":
				reportParamMap.put(PLANTING_DATE_REPORT_KEY, value);
				break;
			case "HARVESTDATE":
				reportParamMap.put(HARVEST_DATE_REPORT_KEY, value);
				break;
            default:
                // no default behaviour, added only for SonarCube checking
		}

		switch (var.getProperty().toUpperCase()) {
			case "PLOT LENGTH":
				reportParamMap.put(NET_PLOT_LENGTH_REPORT_KEY, value);
				break;
			case "ROW SPACING":
				reportParamMap.put(DISTANCE_BETWEEN_ROWS_REPORT_KEY, value);
				break;
			case "SEASON":
				reportParamMap.put("season", value);
                break;
            default:
                // no default behaviour, added only for SonarCube checking
		}
	}

}
