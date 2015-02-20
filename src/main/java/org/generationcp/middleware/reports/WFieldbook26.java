package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook26 extends AbstractWheatTrialReporter{

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

