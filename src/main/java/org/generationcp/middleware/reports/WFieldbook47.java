package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook47 extends AbstractReporter{

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

	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		Integer dummyInt = new Integer(777);
		
		params.put("tid", dummyInt);
		params.put("occ", dummyInt);
		params.put("program", "dummy_program");
		params.put("lid", "dummy_lid");
		params.put("trial_name", "dummy_trialName");
		params.put("trial_abbr", "dummy_trial_abbr");
		params.put("LoCycle", "dummy_LoCycle");
		params.put("gms_ip", "dummy_gms_ip");
		params.put("dms_ip", "dummy_dms_ip");
		
		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args){
		
		for(Object o : args){ //only one bean
			Occurrence oc = (Occurrence)o;
			for(Occurrence occ : oc.getOcurrencesList()){
				occ.setTid(778899);
				occ.setOcc(777);
				
				for(GermplasmEntry e : occ.getEntriesList2()){

				}
			}

		}
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(args);
		return dataSource;
	}
	
}

