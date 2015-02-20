package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook60 extends AbstractWheatTrialReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook60();
		r.setFileNameExpression("INT_YLD_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb60";
	}

	@Override
	public String getTemplateName() {
		return "WFb60_header.jasper";
	}
	
	
	//needs replication_no and subblock (entryNum and plot already provided)
}