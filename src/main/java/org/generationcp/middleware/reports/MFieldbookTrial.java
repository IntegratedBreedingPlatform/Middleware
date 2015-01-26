package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

public class MFieldbookTrial extends AbstractReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new MFieldbookTrial();
		r.setFileNameExpression("Maize_TRIAL_{trialName}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFb2";
	}

	@Override
	public String getTemplateName() {
		return "MFb2_main.jasper";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		params.put("site", args.get("site"));
		params.put("nursery", args.get("nursery"));
		params.put("season", args.get("season"));
		params.put("seedPrep", args.get("seedPrep"));
		params.put("siteNum", args.get("siteNum"));

		params.put("location", args.get("location"));
		params.put("country", args.get("country"));
		params.put("environment", args.get("environment"));
		params.put("plantingDate", args.get("plantingDate"));
		params.put("trialName", args.get("trialName"));
		params.put("netPlotLength", args.get("netPlotLength"));
		params.put("distanceBetweenStations", args.get("distanceBetweenStations"));
		params.put("distanceBetweenRows", args.get("distanceBetweenRows"));
		params.put("rowsHarvested", args.get("rowsHarvested"));
		params.put("collaborator", args.get("collaborator"));
		params.put("breedingProgram", args.get("breedingProgram"));
		params.put("harvestDate", args.get("harvestDate"));

		return params;
	}

	@Override
	public JRDataSource buildJRDataSource(Collection<?> args){
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(args);
		return dataSource;
	}

	@Override
	public void asOutputStream(OutputStream output) throws BuildReportException {
		if(null != jrPrint){
			try {
		
				JRXlsxExporter ex = createDefaultExcelExporter();
				ex.setExporterInput(new SimpleExporterInput(jrPrint));
				ex.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
				
                ex.exportReport();
                
			} catch (JRException e) {
				e.printStackTrace();
			}
		}		else throw new BuildReportException(getReportCode());
	}

	
}
