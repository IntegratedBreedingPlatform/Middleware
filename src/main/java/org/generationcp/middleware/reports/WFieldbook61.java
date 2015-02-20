package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook61 extends AbstractWheatTrialReporter{

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
