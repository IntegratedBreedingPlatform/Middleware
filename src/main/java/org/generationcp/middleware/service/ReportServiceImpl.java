package org.generationcp.middleware.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;

import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.MissingReportException;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.reports.ReporterFactory;
import org.generationcp.middleware.service.api.ReportService;

public class ReportServiceImpl extends Service implements ReportService{

	private ReporterFactory factory = ReporterFactory.instance();

	    public ReportServiceImpl(
	            HibernateSessionProvider sessionProviderForLocal,
	            HibernateSessionProvider sessionProviderForCentral, 
	            String localDatabaseName, String centralDatabaseName) {
	        super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
	    }
	    
	@Override
	public JasperPrint getPrintReport(String code, Integer studyId) throws MiddlewareException, MiddlewareQueryException, JRException, IOException{
		
		//TODO define dataBeans content when methods for extract data are known.
		//TODO merge this code (and from getStreamReport) in a method
		Study study = getStudyDataManager().getStudy(studyId);
		List<GermplasmListData> germListData =  getGermplasmListManager().getGermplasmListDataByListId(studyId, 0, 0);
	
		Reporter reporter = factory.createReporter(code);
		
		Map<String,Object> dataBeans = new HashMap<>();
		dataBeans.put("study", study);
		dataBeans.put("datasource",germListData);

		return reporter.buildJRPrint(dataBeans);
		
	}

	public void getStreamReport(String code, Integer studyId, OutputStream output) throws MiddlewareException
			, MiddlewareQueryException, JRException, BuildReportException
	{
		Study study = getStudyDataManager().getStudy(studyId);
		List<GermplasmListData> germListData =  getGermplasmListManager().getGermplasmListDataByListId(studyId, 0, 0);
	
		Reporter reporter = factory.createReporter(code);
		
		Map<String,Object> dataBeans = new HashMap<>();
		dataBeans.put("study", study);
		dataBeans.put("datasource",germListData);
		
		reporter.buildJRPrint(dataBeans);
		reporter.asOutputStream(output);
	}


	@Override
	public Set<String> getReportKeys() {
		return factory.getReportKeys();
	}
	
	
}
