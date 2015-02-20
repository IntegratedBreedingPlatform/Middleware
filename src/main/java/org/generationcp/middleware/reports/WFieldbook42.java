package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook42 extends AbstractWheatTrialReporter{

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

	
	//ad intrid in datasource
}

