package org.generationcp.middleware.reports;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.Map;


public class ReportParameterMapper {
    public static final int DEFAULT_OCC_VALUE = 1;

    public void mapEnvironmentValue(MeasurementVariable var, Map<String, Object> reportParamMap, String value) {
        final TermId term = TermId.getById(var.getTermId());

        switch (term) {
            case TRIAL_INSTANCE_FACTOR:
                if ("".equalsIgnoreCase(value)) {
                    reportParamMap.put("occ", DEFAULT_OCC_VALUE);
                } else {
                    reportParamMap.put("occ", Integer.valueOf(value));
                }
                break;
            case TRIAL_LOCATION:
                reportParamMap.put(AbstractReporter.LOCATION_NAME_REPORT_KEY, value);
                break;
            case LOCATION_ID:
                reportParamMap.put(AbstractReporter.LOCATION_ID_REPORT_KEY, value);
                break;
            case STUDY_INSTITUTE:
                reportParamMap.put(AbstractReporter.ORGANIZATION_REPORT_KEY, value);
                break;
            // here we have empty blocks for cases where the term ID is non existent, as well as for cases where the term ID is not captured by the previous cases
            case NONEXISTENT:
                break;
            default:
                break;
        }

        if (var.getName().equals(AbstractReporter.COUNTRY_VARIABLE_NAME)) {
            reportParamMap.put(AbstractReporter.COUNTRY_VARIABLE_NAME, value);
        } else if (var.getName().equals(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME)) {
            reportParamMap.put(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME, value);
        } else if (var.getProperty().equalsIgnoreCase("Season")) {
            reportParamMap.put(AbstractReporter.SEASON_REPORT_KEY, value);
            reportParamMap.put("LoCycle", value);
        }
    }
}
