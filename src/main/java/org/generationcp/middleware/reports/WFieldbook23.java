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
		
		@SuppressWarnings("unchecked")
		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>)args.get("studyConditions");
		
		params.put("fb_class", getReportCode());
		params.put("num_reporte", 23);
		params.put("tid", args.get("studyId"));
		
		for(MeasurementVariable var : studyConditions){

			switch(var.getName()){
				case "BreedingProgram" : params.put("program", var.getValue());
					break;
				case "STUDY_NAME" : params.put("trial_abbr", var.getValue());
					break;
				case "STUDY_TITLE" : params.put("trial_name", var.getValue());
					break;
				case "CROP_SEASON" : params.put("cycle", var.getValue());
									 params.put("LoCycle", var.getValue());
					break;
				case "SITE_NAME" : params.put("labbr", var.getValue());
								   params.put("lname", var.getValue());
					break;
				case "TRIAL_INSTANCE" : params.put("occ", Integer.valueOf(var.getValue()));
					break;
				default : 
					params.put("lid", "???");
					params.put("Ientry",  Integer.valueOf(-99));
					params.put("Fentry",  Integer.valueOf(-99));
					params.put("country", "???");
					params.put("offset", "???");
					params.put("organization", "???");
					params.put("version", "???");
					params.put("dms_ip", "???");
					params.put("gms_ip", "???");
					break;
			}
		}
	
		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args){
		
		List<GermplasmEntry> entries = new ArrayList<>();
		//this null record is added because in Jasper, the record pointer in the data source is incremented by every element that receives it.
		//since the datasource used in entry, is previously passed from occ to entry subreport. 
		entries.add(null);
		
		for(MeasurementRow row : (Collection<MeasurementRow>)args){
			GermplasmEntry entry = new GermplasmEntry();
			for(MeasurementData dataItem : row.getDataList()){
				switch(dataItem.getLabel()){
				case "ENTRY_NO" : entry.setEntryNum(Integer.valueOf(dataItem.getValue()));
				case "DESIGNATION" : entry.setSel_hist(dataItem.getValue());
//TODO: pending mappings
//				case "" : entry.setF_cross_name(dataItem.getValue());
//				case "" : entry.setF_sel_hist(dataItem.getValue());
//				case "" : entry.setF_tabbr(dataItem.getValue());
//				case "" : entry.setFlocycle(dataItem.getValue());
//				case "" : entry.setF_ent(Integer.valueOf(dataItem.getValue()));
//
//				case "" : entry.setM_cross_name(dataItem.getValue());
//				case "" : entry.setM_sel_hist(dataItem.getValue());
//				case "" : entry.setM_tabbr(dataItem.getValue());
//				case "" : entry.setMlocycle(dataItem.getValue());
//				case "" : entry.setM_ent(dataItem.getValue());
						break;
					
				}
			}
			
			entries.add(entry);
		}
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(new Occurrence(entries)));
		return dataSource;
	}

}
