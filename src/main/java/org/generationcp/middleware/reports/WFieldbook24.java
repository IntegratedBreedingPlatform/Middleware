
package org.generationcp.middleware.reports;

public class WFieldbook24 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook24();
		r.setFileNameExpression(this.getReportCode() + "-NB_{trialName}-{occ}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb24";
	}

	@Override
	public String getTemplateName() {
		return "WFb24_header.jasper";
	}
}
