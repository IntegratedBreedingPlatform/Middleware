
package org.generationcp.middleware.reports;

public class WFieldbook26 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook26();
		r.setFileNameExpression("CB-nal_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb26";
	}

	@Override
	public String getTemplateName() {
		return "WFb26_header.jasper";
	}

}
