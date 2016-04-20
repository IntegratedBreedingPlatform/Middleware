
package org.generationcp.middleware.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

public abstract class AbstractNurseryReporter extends AbstractReporter {

    public static final int DEFAULT_OCC_VALUE = 1;

    @SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> buildJRParams(final Map<String, Object> args) {
		final Map<String, Object> params = super.buildJRParams(args);

		final List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get(STUDY_CONDITIONS_KEY);
		MeasurementRow[] entries = {};

		entries = ((Collection<MeasurementRow>) args.get(DATA_SOURCE_KEY)).toArray(entries);

		final int firstEntry = Integer.valueOf(entries[0].getMeasurementData(TermId.ENTRY_NO.getId()).getValue());
		final int lastEntry = Integer.valueOf(entries[entries.length - 1].getMeasurementData(TermId.ENTRY_NO.getId()).getValue());
		final int offset = firstEntry - 1;

		params.put("tid", args.get("studyId"));
		params.put("Ientry", firstEntry);
		params.put("Fentry", lastEntry);
		params.put("offset", offset);
		params.put(PROGRAM_NAME_REPORT_KEY, args.get(PROGRAM_NAME_ARG_KEY));

		for (final MeasurementVariable var : studyConditions) {
			mapBasicStudyValues(var, params, var.getValue());

            mapEnvironmentValue(var, params, var.getValue());
		}

		// TODO: pending mappings

		params.put("dmsIp", "");
		params.put("gmsIp", "");
		params.put("version", "");

		return params;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JRDataSource buildJRDataSource(final Collection<?> args) {

		final List<GermplasmEntry> entries = new ArrayList<>();
		// this null record is added because in Jasper, the record pointer in the data source is incremented by every element that receives
		// it.
		// since the datasource used in entry, is previously passed from occ to entry subreport.
		entries.add(null);

		for (final MeasurementRow row : (Collection<MeasurementRow>) args) {
			final GermplasmEntry entry = new GermplasmEntry();
			for (final MeasurementData dataItem : row.getDataList()) {
				switch (dataItem.getLabel()) {
					case "ENTRY_NO":
						entry.setEntryNum(Integer.valueOf(dataItem.getValue()));
						break;
					case "CROSS":
						entry.setLinea1(dataItem.getValue());
						entry.setLinea2(dataItem.getValue());
						break;
					case "DESIGNATION":
						entry.setLinea3(dataItem.getValue());
						entry.setLinea4(dataItem.getValue());
						break;
					case "PLOT_NO":
						entry.setPlot(Integer.valueOf(dataItem.getValue()));
						break;
				}
			}

			// TODO: pending mappings
			entry.setsEnt(0);
			entry.setsTabbr("");
			entry.setSlocycle("");

			entries.add(entry);
		}

		return new JRBeanCollectionDataSource(Arrays.asList(new Occurrence(entries)));

	}

    protected void mapBasicStudyValues(MeasurementVariable var, Map<String, Object> reportParamMap, String value) {
        final TermId term = TermId.getById(var.getTermId());

        switch (term) {
            case STUDY_NAME:
                reportParamMap.put(STUDY_NAME_REPORT_KEY, var.getValue());
                break;
            case STUDY_TITLE:
                reportParamMap.put(STUDY_TITLE_REPORT_KEY, var.getValue());
                break;
            default:
                // aside from the specified items above, no other terms are recognized as basic study values
                break;
        }

    }

    protected void mapEnvironmentValue(MeasurementVariable var, Map<String, Object> reportParamMap, String value) {
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
                reportParamMap.put(LOCATION_NAME_REPORT_KEY, value);
                break;
            case LOCATION_ID:
                reportParamMap.put(LOCATION_ID_REPORT_KEY, value);
                break;
            case STUDY_INSTITUTE:
                reportParamMap.put(ORGANIZATION_REPORT_KEY, value);
                break;
            // here we have empty blocks for cases where the term ID is non existent, as well as for cases where the term ID is not captured by the previous cases
            case NONEXISTENT:
                break;
            default:
                break;
        }

        if (var.getName().equals(COUNTRY_VARIABLE_NAME)) {
            reportParamMap.put(COUNTRY_VARIABLE_NAME, value);
        } else if (var.getName().equals(LOCATION_ABBREV_VARIABLE_NAME)) {
            reportParamMap.put(LOCATION_ABBREV_VARIABLE_NAME, value);
        } else if (var.getProperty().equalsIgnoreCase("Season")) {
            reportParamMap.put(SEASON_REPORT_KEY, value);
            reportParamMap.put("LoCycle", value);
        }
    }
}
