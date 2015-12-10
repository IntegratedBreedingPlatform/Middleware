
package org.generationcp.middleware.reports;

public class WFieldbook41 extends AbstractWheatNurseryReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook41();
		r.setFileNameExpression("YLD_Nal_Rnd_byPlot_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb41";
	}

	@Override
	public String getTemplateName() {
		return "WFb41_header.jasper";
	}

	// TODO : needs to print first and last plot in front page (Iplot & Fplot)
}
