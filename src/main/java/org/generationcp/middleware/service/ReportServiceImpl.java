package org.generationcp.middleware.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.reports.ReporterFactory;
import org.generationcp.middleware.service.api.ReportService;
import org.hibernate.Session;
import org.hibernate.Query;

public class ReportServiceImpl extends Service implements ReportService{

	private ReporterFactory factory = ReporterFactory.instance();
	private PedigreeDataManager pediMgr;

	    public ReportServiceImpl(
	            HibernateSessionProvider sessionProviderForLocal,
	            HibernateSessionProvider sessionProviderForCentral, 
	            String localDatabaseName, String centralDatabaseName) {
	        super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
	        
	        pediMgr = new PedigreeDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
	    }
	    
	@Override
	public JasperPrint getPrintReport(String code, Integer studyId) throws MiddlewareException, MiddlewareQueryException, 
	JRException, IOException, BuildReportException{
		
		Reporter reporter = factory.createReporter(code);
		Map<String,Object> dataBeans = extractFieldbookData(studyId, reporter.isParentsInfoRequired());

		return reporter.buildJRPrint(dataBeans);
		
	}

	public Reporter getStreamReport(String code, Integer studyId, OutputStream output)throws MiddlewareException, MiddlewareQueryException,
		JRException, IOException, BuildReportException
	{ 
		
		Reporter reporter = factory.createReporter(code);
		Map<String,Object> dataBeans = extractFieldbookData(studyId, reporter.isParentsInfoRequired());

		reporter.buildJRPrint(dataBeans);
		reporter.asOutputStream(output);
		
		return reporter;
	}
	
	/**
	 * Creates a Map containing all information needed to generate a report.
	 * @param studyId The study Id to which extract information from.
	 * @return a Map containing information of the study.
	 */
	private Map<String,Object> extractFieldbookData(Integer studyId, boolean parentsInfoRequireed)throws MiddlewareQueryException{

		Workbook wb = getWorkbookBuilder().create(studyId);
		List<MeasurementRow> observations =  wb.getObservations();
		
		if(parentsInfoRequireed){
			appendParentsInformation(studyId, observations);
		}

		Map<String,Object> dataBeans = new HashMap<>();
		dataBeans.put("studyConditions", wb.getConditions());  //List<MeasurementVariable>
		dataBeans.put("dataSource", observations); //list<measurementRow>
		dataBeans.put("studyObservations", wb.getTrialObservations());//list<measurementRow>
		dataBeans.put("studyId", studyId);//list<measurementRow>
		
		return dataBeans;
	}


	@Override
	public Set<String> getReportKeys() {
		return factory.getReportKeys();
	}
	
	/**
	 * Local method to add information about male and female parents. The information is appended in the form of new {@link MeasurementData} elements for each {@link MeasurementRow} provided
	 * @param studyId the id for the study which the observations belong to.
	 * @param observations List of rows representing entries in a study, in which parent information will be appended
	 */
	private void appendParentsInformation(Integer studyId, List<MeasurementRow> observations) throws MiddlewareQueryException{
		// put germNodes extraction
		Map<Integer, GermplasmPedigreeTreeNode> germNodes = getGermplasmDataManager().getDirectParentsForStudy(studyId);
		
		for(MeasurementRow row : observations){
			int gid = Integer.valueOf(row.getMeasurementDataValue("GID"));
			GermplasmPedigreeTreeNode germNode = germNodes.get(gid);
			Germplasm germplasm = germNode.getGermplasm();
						
			if(germplasm.getGrplce() >=0 & germNode.getLinkedNodes().size() == 2){ // is geneative and has parents
				Germplasm female = germNode.getLinkedNodes().get(0).getGermplasm();
				Germplasm male = germNode.getLinkedNodes().get(1).getGermplasm();
				
				//TODO: pending values for origin of the entries
				row.getDataList().add(new MeasurementData("f_selHist", female.getSelectionHistory()));
				row.getDataList().add(new MeasurementData("f_cross_name", female.getSelectionHistory()));
				row.getDataList().add(new MeasurementData("f_tabbr", "NA")); // put source trial abbreviation
				row.getDataList().add(new MeasurementData("f_locycle", "NA")); //put source trial cycle 
				row.getDataList().add(new MeasurementData("f_ent", "-99")); // put source trial entry
				row.getDataList().add(new MeasurementData("f_lid", "-99")); // put source location id


				row.getDataList().add(new MeasurementData("m_selHist", male.getSelectionHistory()));
				row.getDataList().add(new MeasurementData("m_cross_name", male.getSelectionHistory()));
				row.getDataList().add(new MeasurementData("m_tabbr", "NA")); // put source trial abbreviation
				row.getDataList().add(new MeasurementData("m_locycle", "NA")); //put source trial cycle 
				row.getDataList().add(new MeasurementData("m_ent", "-99")); // put source trial entry
				row.getDataList().add(new MeasurementData("m_lid", "-99")); // put source location id
			}
		}
		
		
	}
	
	
}
