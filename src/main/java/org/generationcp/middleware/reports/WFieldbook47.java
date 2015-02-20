package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook47 extends AbstractWheatTrialReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook47();
		r.setFileNameExpression("CC_NalMov_byEntry_{tid}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb47";
	}

	@Override
	public String getTemplateName() {
		return "WFb47_header.jasper";
	}
	
	//get parents and parents origins
}
