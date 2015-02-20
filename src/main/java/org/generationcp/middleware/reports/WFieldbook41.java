package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook41 extends AbstractWheatTrialReporter{

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

	
	// needs to print first and last plot in front page (Iplot & Fplot)
}

