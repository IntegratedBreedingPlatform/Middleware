
package org.generationcp.middleware.reports;

public class WFieldbook61 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook61();
		r.setFileNameExpression("INT_SEL_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb61";
	}

	@Override
	public String getTemplateName() {
		return "WFb61_header.jasper";
	}

}
