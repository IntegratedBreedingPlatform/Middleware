package org.generationcp.middleware.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.reports.ReporterFactory;
import org.generationcp.middleware.service.api.ReportService;
import org.hibernate.Session;

public class ReportServiceImpl extends Service implements ReportService{

	private ReporterFactory factory = ReporterFactory.instance();

	    public ReportServiceImpl(
	            HibernateSessionProvider sessionProviderForLocal,
	            HibernateSessionProvider sessionProviderForCentral, 
	            String localDatabaseName, String centralDatabaseName) {
	        super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
	    }
	    
	@Override
	public JasperPrint getPrintReport(String code, Integer studyId) throws MiddlewareException, MiddlewareQueryException, 
	JRException, IOException, BuildReportException{
		
		Reporter reporter = factory.createReporter(code);
		Map<String,Object> dataBeans = extractFieldbookData(studyId);

		return reporter.buildJRPrint(dataBeans);
		
	}

	public Reporter getStreamReport(String code, Integer studyId, OutputStream output)throws MiddlewareException, MiddlewareQueryException,
		JRException, IOException, BuildReportException
	{ 
		
		Reporter reporter = factory.createReporter(code);
		Map<String,Object> dataBeans = extractFieldbookData(studyId);

		reporter.buildJRPrint(dataBeans);
		reporter.asOutputStream(output);
		
		return reporter;
	}
	
	/**
	 * Creates a Map containing all information needed to generate a report.
	 * @param studyId The study Id to which extract information from.
	 * @return a Map containing information of the study.
	 */
	private Map<String,Object> extractFieldbookData(Integer studyId)throws MiddlewareQueryException{

		Workbook wb = getWorkbookBuilder().create(studyId);
		
		Map<String,Object> dataBeans = new HashMap<>();
		dataBeans.put("studyConditions", wb.getConditions());  //List<MeasurementVariable>
		dataBeans.put("dataSource",wb.getObservations()); //list<measurementRow>
		dataBeans.put("studyObservations", wb.getTrialObservations());//list<measurementRow>
		dataBeans.put("studyId", studyId);//list<measurementRow>
		
		return dataBeans;
	}


	@Override
	public Set<String> getReportKeys() {
		return factory.getReportKeys();
	}
	
	
}
