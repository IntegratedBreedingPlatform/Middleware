
package org.generationcp.middleware.reports;

public class WFieldbook28 extends AbstractNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook28();
		r.setFileNameExpression("Rnd_Nal_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb28";
	}

	@Override
	public String getTemplateName() {
		return "WFb28_header.jasper";
	}

	// TODO : needs fields: plot and code28

}
