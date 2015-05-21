
package org.generationcp.middleware.reports;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementVariable;

public class MFieldbookTrial extends AbstractDynamicReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new MFieldbookTrial();
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
	public Map<String, Object> buildJRParams(Map<String, Object> args) {
		Map<String, Object> params = super.buildJRParams(args);

		@SuppressWarnings("unchecked")
		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>) args.get("studyConditions");

		for (MeasurementVariable var : studyConditions) {
			switch (var.getName()) {

				case "SITE_NAME":
					params.put("location", var.getValue());
					params.put("country", "???");
					params.put("environment", "???");
					break;
				case "START_DATE":
					params.put("plantingDate", var.getValue());
					break;
				case "STUDY_NAME":
					params.put("trialName", var.getValue());
					params.put("netPlotLength", "???");
					params.put("distanceBetweenStations", "???");
					params.put("distanceBetweenRows", "???");
					params.put("rowsHarvested", "???");
					params.put("collaborator", "???");
					break;
				case "BreedingProgram":
					params.put("breedingProgram", var.getValue());
					break;
				case "END_DATE":
					params.put("harvestDate", var.getValue());
					break;
			}
		}

		return params;
	}

}
