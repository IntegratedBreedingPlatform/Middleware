package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook29 extends AbstractWheatTrialReporter{

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
