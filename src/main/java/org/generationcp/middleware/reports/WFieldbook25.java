package org.generationcp.middleware.reports;

import java.util.Collection;
import java.util.Map;

import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WFieldbook25 extends AbstractReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook25();
		r.setFileNameExpression("NationalSegregating_Nur:{tid}");
		
		return r;
	}

	@Override
	public String getReportCode() {
		return "WFb25";
	}

	@Override
	public String getTemplateName() {
		return "WFb25_header.jasper";
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
//					e.setS_ent(11);
//					e.setEntryNum(22);
//					e.setS_tabbr("dummy_t_abbr");
//					e.setSlocycle("dummy_sloCycle");
//					e.setLinea1("linea1");
//					e.setLinea2("linea2");
//					e.setLinea3("linea3");
//					e.setLinea4("linea4");
//					e.setLinea5("linea5");
				}
			}
			
//			for(GermplasmEntry ge : oc.getEntriesList()){
//
//			}
		}
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(args);
		return dataSource;
	}
	
}

