
package org.generationcp.middleware.reports;

public class WFieldbook29 extends AbstractTrialReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook29();
		r.setFileNameExpression("YLD_Nal_Rnd_byEntry{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb29";
	}

	@Override
	public String getTemplateName() {
		return "WFb29_header.jasper";
	}

}
