package org.generationcp.middleware.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.reports.BuildReportException;
import org.generationcp.middleware.reports.Reporter;
import org.generationcp.middleware.reports.ReporterFactory;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.service.api.ReportService;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import net.sf.jasperreports.engine.JRException;

@RunWith(JUnit4.class)
public class ReportServiceImplTest extends DataManagerIntegrationTest {

    private static ReportService reportService;
    private static DataImportService dataImportService;
    
    private static StudyDataManager studyMgr = managerFactory.getStudyDataManager();
    private static StudyDataManager studyMgrNew = managerFactory.getNewStudyDataManager();
    
    private static FieldbookService fbService = managerFactory.getFieldbookMiddlewareService();
    private static OntologyService ontologyService = managerFactory.getOntologyService();
    
    
    private static final int PROJECT_ID = -2; // local nursery;
    private static final String MAIZE_FIELDBOOK_NURSERY_KEY = "MFbNur";
    private static final String MAIZE_NURSERY_FOR_PROJ_ID = "Maize_NUR_El Batan Station.xlsx";
    
    @BeforeClass
    public static void setUp() throws Exception {
    	reportService = managerFactory.getReportService();
    					
        dataImportService = managerFactory.getDataImportService();
    }

    
    @Test
    public void testGetReportKeys(){
    	//reportFactory is working well and registering Reporter's impl-classes?
    	assertTrue(reportService.getReportKeys().size() > 0);
    }

    @Test
    public void testGetStreamReport_MaizeNursery() throws MiddlewareQueryException,  JRException
    ,FileNotFoundException,MiddlewareException, BuildReportException, IOException{
    	
    	boolean hasMaizeNurseryFb = reportService.getReportKeys().contains(MAIZE_FIELDBOOK_NURSERY_KEY);

    	if(hasMaizeNurseryFb){
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();

    		Reporter rep = reportService.getStreamReport(MAIZE_FIELDBOOK_NURSERY_KEY, PROJECT_ID, baos);

    		assertTrue(baos.size() > 0);
    		assertEquals("xlsx", rep.getFileExtension());
    		assertEquals(rep.getFileName(), MAIZE_NURSERY_FOR_PROJ_ID);
    		
    	}
    	
    }    

    @Test
    public void testGenerateFile() throws MiddlewareQueryException,  JRException
    ,FileNotFoundException,MiddlewareException, BuildReportException, IOException{
    	
    	boolean hasMaizeNurseryFb = reportService.getReportKeys().contains(MAIZE_FIELDBOOK_NURSERY_KEY);

    	if(hasMaizeNurseryFb){
    		
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();

    		Reporter rep = reportService.getStreamReport(MAIZE_FIELDBOOK_NURSERY_KEY, PROJECT_ID, baos);


    		File xlsx = new File(rep.getFileName());
    		
    		baos.writeTo(new FileOutputStream(xlsx));
    		
    	}
    	
    }    

}
