package org.generationcp.middleware.reports;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.report.SiteEntry;

public class MShipList extends AbstractDynamicReporter{

	@Override
	public Reporter createReporter() {
		Reporter r = new MShipList();
		r.setFileNameExpression("Maize_SHIPM_{shipId}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFb";
	}

	@Override
	public String getTemplateName() {
		return "MFb3_main2.jasper";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String,Object> args){
		Map<String, Object> params = super.buildJRParams(args);
		
		params.put("recLastName", args.get("recLastName"));
		params.put("recFirstName", args.get("recFirstName"));
		params.put("institution", args.get("institution"));
		params.put("shippingAddress", args.get("shippingAddress"));
		params.put("country", args.get("country"));
		params.put("contactNumber", args.get("contactNumber"));
		params.put("phytoInstr", args.get("phytoInstr"));
		params.put("shippingInstr", args.get("shippingInstr"));
		params.put("shipFrom", args.get("shipFrom"));
		params.put("shipId", args.get("shipId"));
		params.put("prepDate", args.get("prepDate"));
		params.put("carrier", args.get("carrier"));
		params.put("airwayBill", args.get("airwayBill"));
		params.put("dateSent", args.get("dateSent"));
		params.put("quantity", args.get("quantity"));
		params.put("commments", args.get("commments"));

		return params;
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

	@Override
	public List<List<String>> convertBeanCollectionToStringLists( Collection<?> dataRecords) {
		List<List<String>> dataSource = new ArrayList<>();
		for(Object e : dataRecords){
			SiteEntry entry = (SiteEntry)e;
			List<String> record = new ArrayList<>();
			record.add(entry.getAnthesisDate());
			record.add(entry.getFloweringF());
			record.add(entry.getFloweringM());
			record.add(entry.getHeightPlant());
			record.add(entry.getHeightEar());
			record.add(entry.getHeightEar());
			record.add(entry.getHeightEar());
			record.add(entry.getHeightEar());
			record.add(entry.getHeightEar());
			dataSource.add(record);
		}
		
		return dataSource;
	}

	
}
