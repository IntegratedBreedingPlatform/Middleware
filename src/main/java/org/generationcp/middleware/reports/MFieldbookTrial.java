
package org.generationcp.middleware.reports;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

public class MFieldbookTrial extends AbstractTrialReporter {

    public static final String PLANTING_DATE_REPORT_KEY = "plantingDate";
    public static final String[] UNIQUE_REPORT_KEYS = (String[]) Arrays.asList("distanceBetweenStations", "rowsHarvested", "collaborator",
            PLANTING_DATE_REPORT_KEY, "harvestDate", "distanceBetweenRows", "netPlotLength").toArray();
    public static final String COLLABORATOR_REPORT_KEY = "collaborator";

    @Override
	public Reporter createReporter() {
		final Reporter r = new MFieldbookTrial();
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

		// this report uses a different key to refer to location name, season, and program name, so we just set the retrieved value from
		// previous computation to the expected key
		params.put("location", params.get(LOCATION_NAME_REPORT_KEY));
		params.put("season", params.get(SEASON_REPORT_KEY));
		params.put("breedingProgram", params.get(PROGRAM_NAME_REPORT_KEY));

		@SuppressWarnings("unchecked")
		final List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get(STUDY_CONDITIONS_KEY);
        @SuppressWarnings("unchecked")
        final List<MeasurementRow> trialObservations = (List<MeasurementRow>) args.get(STUDY_OBSERVATIONS_KEY);

        // attempt to extract values from the study conditions
		for (final MeasurementVariable var : studyConditions) {
            mapReportValue(var, params, var.getValue());
		}

        // attempt to extract values from the observations. only the value from the first measurement row is necessary
		if (trialObservations.size() > 0) {

			for (final MeasurementData data : trialObservations.get(0).getDataList()) {
				mapReportValue(data.getMeasurementVariable(), params, data.getValue());
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

            if (PLANTING_DATE_REPORT_KEY.equals(uniqueReportKey)){
                // we put in a blank string with 8 characters for planting date because the report expects a date with yyyymmdd format, and performs substring operations
                params.put(PLANTING_DATE_REPORT_KEY, "        ");
            } else {
                params.put(uniqueReportKey, "");
            }
		}
	}

    /**
     * Created a separate method for mapping values to expected report keys as values can be extracted from either the study conditions or from
     * environment values that can be extracted from the trial observations
     * @param var
     * @param reportParamMap
     * @param value
     */
    protected void mapReportValue(MeasurementVariable var, Map<String, Object> reportParamMap, String value) {
        final TermId term = TermId.getById(var.getTermId());
        if (term != null) {
            switch (term) {
                case TRIAL_LOCATION:
                    reportParamMap.put("location", value);
                    break;
            }
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
                reportParamMap.put("harvestDate", value);
                break;
        }

        switch (var.getProperty().toUpperCase()) {
            case "PLOT LENGTH":
                reportParamMap.put("netPlotLength", value);
                break;
            case "ROW SPACING":
                reportParamMap.put("distanceBetweenRows", value);
                break;
            case "SEASON" :
                reportParamMap.put("season", value);
        }
    }
}