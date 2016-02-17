
package org.generationcp.middleware.reports;

public class WFieldbook60 extends AbstractTrialReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook60();
		r.setFileNameExpression("INT_YLD_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb60";
	}

	@Override
	public String getTemplateName() {
		return "WFb60_header.jasper";
	}
}
