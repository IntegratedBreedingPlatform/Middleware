package org.generationcp.middleware.reports;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook23 extends AbstractReporter{

	/**
	 * Enforces obtaining instances through the Factory
	 */
	protected WFieldbook23(){}
	
	@Override
	public Reporter createReporter() {
		return new WFieldbook23();
	}

	@Override
	public String getReportCode() {
		return "WFb23";
	}

	@Override
	public String getTemplateName() {
		return "WFb23_header.jasper";
	}

	
	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		Integer dummyInt = new Integer(111);
		
		params.put("tid", dummyInt);
		params.put("occ", 123);
		params.put("lid", "lid");
		params.put("LoCycle", "LoCycle");
		params.put("program", "program");
		params.put("trial_name", "trial_name");
		params.put("trial_abbr", "trial_abbr");
		params.put("num_reporte", dummyInt);
		
		params.put("Ientry", dummyInt);
		params.put("Fentry", dummyInt);
		params.put("lname", "lname");
		params.put("labbr", "labbr");
		params.put("country", "country");
		params.put("cycle", "cycleA");
		params.put("fb_class", "fb_class");

		params.put("offset", dummyInt);
		params.put("organization", "organization");
		params.put("version", "version");
		
		params.put("dms_ip", "dms_ip");
		params.put("gms_ip", "gms_ip");
		
		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args){
		//TODO add custom data source beans for this report
		JRDataSource dataSource = new JRBeanCollectionDataSource(args);
		return dataSource;
	}

}
