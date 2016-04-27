
package org.generationcp.middleware.reports;

public class WFieldbook25 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook25();
		r.setFileNameExpression("NationalSegregating_Nur-{trialName}_{occ}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb25";
	}

	@Override
	public String getTemplateName() {
		return "WFb25_header.jasper";
	}

}
