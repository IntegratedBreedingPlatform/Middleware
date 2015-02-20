package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class MFieldbookNur extends AbstractDynamicReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new MFieldbookNur();
		r.setFileNameExpression("Maize_NUR_{site}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFbNur";
	}

	@Override
	public String getTemplateName() {
		return "MFb1_main";
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		//add more specific parameters here 
		
		return params;
	}

}
