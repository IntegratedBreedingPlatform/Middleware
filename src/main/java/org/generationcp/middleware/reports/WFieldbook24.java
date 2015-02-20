package org.generationcp.middleware.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook24 extends AbstractWheatTrialReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook24();
		r.setFileNameExpression(this.getReportCode()+"-NB_{trial_name}-{occ}");
		
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb24";
	}

	@Override
	public String getTemplateName() {
		return "WFb24_header.jasper";
	}
}

