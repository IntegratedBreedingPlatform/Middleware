package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.report.SiteEntry;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class MShipList extends AbstractDynamicReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new MShipList();
		r.setFileNameExpression("Maize_SHIPM_{shipId}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFbShipList";
	}

	@Override
	public String getTemplateName() {
		return "MFb3_main";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>)args.get("studyConditions");
		
		for(MeasurementVariable var : studyConditions){
			switch(var.getName()){
				case "TRIAL_INSTANCE" :params.put("recLastName", "???");
					params.put("recFirstName", "???");
					params.put("institution", "???");
					params.put("shippingAddress", "???");
					params.put("country", "???");
					params.put("contactNumber", "???");
					params.put("phytoInstr", "???");
					params.put("shippingInstr", "???");
					params.put("shipFrom", "???"); break;
				case "STUDY_NAME" : params.put("shipId", var.getValue()); //is an assembled code, udes for generating filenam.
					params.put("prepDate", "???");
					params.put("carrier", "???");
					params.put("airwayBill", "???");
					params.put("dateSent", "???");
					params.put("quantity", "???");
					params.put("commments", "???"); break;
			}
		}
		return params;
	}

	
}
