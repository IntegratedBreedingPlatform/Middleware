package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook43 extends AbstractWheatTrialReporter{

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
	
	//ad intrid to datasource
}

