package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook28 extends AbstractWheatTrialReporter{

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
	
	
	//needs fields: plot and code28

}

