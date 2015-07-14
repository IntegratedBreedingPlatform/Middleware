
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
import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

public abstract class AbstractWheatTrialReporter extends AbstractReporter {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args) {
		Map<String, Object> params = super.buildJRParams(args);

		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get("studyConditions");
		MeasurementRow[] entries = {};

		entries = ((Collection<MeasurementRow>) args.get("dataSource")).toArray(entries);

		int firstEntry = Integer.valueOf(entries[0].getMeasurementData("ENTRY_NO").getValue());
		int lastEntry = Integer.valueOf(entries[entries.length - 1].getMeasurementData("ENTRY_NO").getValue());
		int offset = firstEntry - 1;

		params.put("tid", args.get("studyId"));
		params.put("Ientry", firstEntry);
		params.put("Fentry", lastEntry);
		params.put("offset", offset);

		for (MeasurementVariable var : studyConditions) {

			switch (var.getName()) {
				case "BreedingProgram":
					params.put("program", var.getValue());
					break;
				case "STUDY_NAME":
					params.put("trialAbbr", var.getValue());
					break;
				case "STUDY_TITLE":
					params.put("trialName", var.getValue());
					break;
				case "CROP_SEASON":
					params.put("cycle", var.getValue());
					params.put("LoCycle", var.getValue());
					break;
				case "TRIAL_INSTANCE":
					if ("".equalsIgnoreCase(var.getValue())) {
						params.put("occ", Integer.valueOf(0));
					} else {
						params.put("occ", Integer.valueOf(var.getValue()));
					}
					break;
				case "LOCATION_NAME":
					params.put("lname", var.getValue());
					break;
				case "LOCATION_NAME_ID":
					params.put("lid", var.getValue());
					break;
				case "STUDY_INSTITUTE":
					params.put("organization", var.getValue());
					break;
				default:
					params.put("dmsIp", "???");
					params.put("gmsIp", "???");
					params.put("version", "v-1");
					break;
			}
		}

		return params;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JRDataSource buildJRDataSource(Collection<?> args) {

		List<GermplasmEntry> entries = new ArrayList<>();
		// this null record is added because in Jasper, the record pointer in the data source is incremented by every element that receives
		// it.
		// since the datasource used in entry, is previously passed from occ to entry subreport.
		entries.add(null);

		for (MeasurementRow row : (Collection<MeasurementRow>) args) {
			GermplasmEntry entry = new GermplasmEntry();
			for (MeasurementData dataItem : row.getDataList()) {
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
					// TODO: pending mappings
					default:
						entry.setSEnt(-99);
						entry.setSTabbr("???");
						entry.setSlocycle("???");

				}
			}

			entries.add(entry);
		}

		JRDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(new Occurrence(entries)));
		return dataSource;

	}

}
