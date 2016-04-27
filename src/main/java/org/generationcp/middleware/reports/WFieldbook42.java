
package org.generationcp.middleware.reports;

public class WFieldbook42 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook42();
		r.setFileNameExpression("GB_basic_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb42";
	}

	@Override
	public String getTemplateName() {
		return "WFb42_header.jasper";
	}

	// TODO : add intrid in datasource
}
