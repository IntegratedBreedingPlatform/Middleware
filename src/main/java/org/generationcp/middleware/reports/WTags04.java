package org.generationcp.middleware.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.report.GermplasmEntry;
import org.generationcp.middleware.pojos.report.Occurrence;
import org.generationcp.middleware.util.StringUtil;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;

public class WTags04 extends AbstractReporter{

	protected List<List<String>> dataSource = new ArrayList<>();
	protected Map<String,String> studyMeta = new HashMap<>();
	
	protected WTags04(){}
	
	@Override
	public Reporter createReporter() {
		Reporter r = new WTags04();
		r.setFileNameExpression("TAGS04_{trial_name}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "WTAG04";
	}

	@Override
	public String getTemplateName() {
		return null;
	}
	
	public String getFileExtension(){
		return "txt";
	}


	public JasperPrint buildJRPrint(Map<String, Object> args) throws JRException{

		Map<String, Object> jrParams = null;

		if(null != args){
			jrParams = buildJRParams(args);
			setFileName( super.buildOutputFileName(jrParams) );
			
		}
		
		MeasurementRow[] entries = {};
		entries = ((Collection<MeasurementRow>)args.get("dataSource")).toArray(entries);
		
		dataSource.clear();
		studyMeta.clear();
		
		for(MeasurementVariable var : ((List<MeasurementVariable>)args.get("studyConditions"))){
			studyMeta.put(var.getName(), var.getValue());
		}
		
		//add headers in first row of dataSource
		List<String> row = new ArrayList<>();
		for(MeasurementData data : entries[0].getDataList()){
			row.add(data.getLabel());
		}
		dataSource.add(row);
		
		for(MeasurementRow measurementRow : entries){
			row = new ArrayList<>();
			for(MeasurementData data : measurementRow.getDataList()){
				row.add(data.getValue());
			}
			dataSource.add(row);
		}

	 	return null;

	}
	
	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		List<MeasurementVariable> studyConditions = (List<MeasurementVariable>)args.get("studyConditions");

		for(MeasurementVariable var : studyConditions){
			String trialName = null;
			switch(var.getName()){
				case "STUDY_TITLE" : trialName = var.getValue();
					break;
			}
			
			if(null != trialName){
				params.put("trial_name", trialName);
				break;
			}
		}

		return params;
	}
	
	@Override
	public JRDataSource buildJRDataSource(Collection<?> args){
		return null;
	}

	public void asOutputStream(OutputStream output) throws BuildReportException {
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(buildPrintTestRecord());
			sb.append(buildPrintTestRecord());

			for(int i = 1; i<dataSource.size(); i++){
				sb.append(
						buildRecord(dataSource.get(i), dataSource.get(0)));
			}
			output.write(sb.toString().getBytes());
		}catch(IOException e){
			e.printStackTrace();
			throw new BuildReportException(this.getReportCode());
		}
	}

	
	protected String buildRecord(List<String> row, List<String> headers){
		String study
		,occ
		,subProg
		,type
		,season
		,entry = null
		,pedigreeA = null
		,pedigreeB = null
		,selHistA = null
		,selHistB = null;
		
		study = studyMeta.get("STUDY_NAME");
		occ = studyMeta.get("TRIAL_INSTANCE");
		subProg = studyMeta.get("BreedingProgram");
		type = studyMeta.get("STUDY_TYPE"); //a type for nal,int, etc
		season = studyMeta.get("CROP_SEASON");
		
		for(int i = 0; i < headers.size(); i++){
			switch(headers.get(i)){
				case "ENTRY_NO": entry = row.get(i);
					break;
				case "CROSS": pedigreeA = row.get(i);
							  pedigreeB = pedigreeA;
							  pedigreeA = pedigreeA.length() > 40 ? pedigreeA.substring(0, pedigreeA.substring(0,40).lastIndexOf("/")+1) : pedigreeA;
							  pedigreeB = pedigreeB.length() > 40 ? pedigreeB.substring(pedigreeB.lastIndexOf("/", 40)+1,pedigreeB.length()) : "";
					break;
				case "DESIGNATION": selHistA = row.get(i);
									selHistB = selHistA;
									selHistA = selHistA.length() > 36 ? selHistA.substring(0, selHistA.substring(0,36).lastIndexOf("-")+1) : selHistA;
									selHistB = selHistB.length() > 36 ? selHistB.substring(selHistB.lastIndexOf("-", 36)+1,selHistB.length()) : "";
					break;
			}
		}
		
		//now format
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format(study, 30, true)).append(" OCC: ")
		  .append(StringUtil.format(occ, 4, true))
		  .append("\r\n").append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format(subProg, 3, true)).append(" ")
		  .append(StringUtil.format(type, 5, true)).append(" ")
		  .append(StringUtil.format(season, 13, true))
		  .append(StringUtil.format("ENTRY", 7, false)).append(" ")
		  .append(StringUtil.format(entry, 6, true))
		  .append("\r\n").append(StringUtil.stringOf(" ", 25))
		  .append(StringUtil.format("CIMMYT", 6, false))
		  .append("\r\n").append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format(pedigreeA, 40, true))
		  .append("\r\n").append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format(pedigreeB, 40, true))
		  .append("\r\n").append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format("", 4, true))
		  .append(StringUtil.format(selHistA, 36, true))
		  .append("\r\n").append(StringUtil.stringOf(" ", 40))
		  .append(StringUtil.format("", 4, true))
		  .append(StringUtil.format(selHistB, 36, true)).append("\r\n\r\n");
		
		return sb.toString();
	}
	
	private String buildPrintTestRecord(){
		return new StringBuilder()
			.append(StringUtil.stringOf(" ",40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n").append(StringUtil.stringOf(" ", 40))
			.append(StringUtil.stringOf("X",40))
			.append("\r\n\r\n")
			.toString();
	}

}
