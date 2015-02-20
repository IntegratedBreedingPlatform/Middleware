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

public class WFieldbook25 extends AbstractWheatTrialReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new WFieldbook25();
		r.setFileNameExpression("NationalSegregating_Nur-{trial_name}_{occ}");
		
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

//	@Override
//	public Map<String, Object> buildJRParams(Map<String,Object> args){
//		Map<String, Object> params = super.buildJRParams(args);
//		
//		@SuppressWarnings("unchecked")
//		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>)args.get("studyConditions");
//		
//		params.put("tid", args.get("studyId"));
//		
//		for(MeasurementVariable var : studyConditions){
//
//			switch(var.getName()){
//				case "BreedingProgram" : params.put("program", var.getValue());
//					break;
//				case "STUDY_NAME" : params.put("trial_abbr", var.getValue());
//					break;
//				case "STUDY_TITLE" : params.put("trial_name", var.getValue());
//					break;
//				case "CROP_SEASON" : params.put("cycle", var.getValue());
//									 params.put("LoCycle", var.getValue());
//					break;
//				case "TRIAL_INSTANCE" : params.put("occ", Integer.valueOf(var.getValue()));
//					break;
//				default : 
//					params.put("lid", "???");
//					params.put("dms_ip", "???");
//					params.put("gms_ip", "???");
//					break;
//			}
//		}
//
//		return params;
//	}

//	@Override
//	public JRDataSource buildJRDataSource(Collection<?> args){
//				
//		List<GermplasmEntry> entries = new ArrayList<>();
//		//this null record is added because in Jasper, the record pointer in the data source is incremented by every element that receives it.
//		//since the datasource used in entry, is previously passed from occ to entry subreport. 
//		entries.add(null);
//		
//		for(MeasurementRow row : (Collection<MeasurementRow>)args){
//			GermplasmEntry entry = new GermplasmEntry();
//			for(MeasurementData dataItem : row.getDataList()){
//				switch(dataItem.getLabel()){
//					case "ENTRY_NO" : entry.setEntryNum(Integer.valueOf(dataItem.getValue()));
//						break;
//					case "CROSS" : entry.setLinea1(dataItem.getValue());
//									 entry.setLinea2(dataItem.getValue());
//						break;
//					case "DESIGNATION" : entry.setLinea3(dataItem.getValue());
//									 entry.setLinea4(dataItem.getValue());
//						break;
//					//TODO: pending mappings
//					default : entry.setS_ent(-99);
//							  entry.setS_tabbr("???");
//							  entry.setSlocycle("???");
//					
//				}
//			}
//			
//			entries.add(entry);
//		}
//		
//		JRDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(new Occurrence(entries)));
//		return dataSource;
//
//	}
	
}

