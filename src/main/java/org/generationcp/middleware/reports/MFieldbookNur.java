
package org.generationcp.middleware.reports;

import java.util.Map;

public class MFieldbookNur extends AbstractDynamicReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new MFieldbookNur();
		r.setFileNameExpression("Maize_NUR_{site}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFbNur";
	}

	@Override
	public String getTemplateName() {
		return "MFb1_main";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args, String studyName) {
		Map<String, Object> params = super.buildJRParams(args, studyName);
		// add more specific parameters here
		return params;
	}

}
