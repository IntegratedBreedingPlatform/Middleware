
package org.generationcp.middleware.reports;

public class WFieldbook43 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook43();
		r.setFileNameExpression("CB_BW_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb43";
	}

	@Override
	public String getTemplateName() {
		return "WFb43_header.jasper";
	}

	// TODO : add intrid to datasource
}
