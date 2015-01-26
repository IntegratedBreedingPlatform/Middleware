package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

public class MFieldbookNur extends AbstractReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new MFieldbookNur();
		r.setFileNameExpression("Maize_NUR_{site}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFb1";
	}

	@Override
	public String getTemplateName() {
		return "MFb1_main.jasper";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		params.put("site", args.get("site"));
		params.put("nursery", args.get("nursery"));
		params.put("season", args.get("season"));
		params.put("seedPrep", args.get("seedPrep"));
		params.put("siteNum", args.get("siteNum"));
		
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
