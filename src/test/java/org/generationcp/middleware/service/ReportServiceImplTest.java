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
    private static final String KEY_MAIZE_FIELDBOOK_NURSERY = "MFbNur";
    private static final String KEY_MAIZE_FIELDBOOK_TRIAL = "MFbTrial";
    private static final String KEY_MAIZE_FIELDBOOK_SHIPM = "MFbShipList";
    
    private static final int PROJECT_WHEAT_ID = -2; // local trial;
    private static final int PROJECT_WHEAT_CROSSES_ID = -8; // local nursery;
    private static final String KEY_WHEAT_FIELDBOOK_23 = "WFb23";
    private static final String KEY_WHEAT_FIELDBOOK_24 = "WFb24";
    private static final String KEY_WHEAT_FIELDBOOK_25 = "WFb25";
    private static final String KEY_WHEAT_FIELDBOOK_26 = "WFb26";
    private static final String KEY_WHEAT_FIELDBOOK_28 = "WFb28";
    private static final String KEY_WHEAT_FIELDBOOK_29 = "WFb29";
    private static final String KEY_WHEAT_FIELDBOOK_41 = "WFb41";
    private static final String KEY_WHEAT_FIELDBOOK_42 = "WFb42";
    private static final String KEY_WHEAT_FIELDBOOK_43 = "WFb43";
    private static final String KEY_WHEAT_FIELDBOOK_47 = "WFb47";
    private static final String KEY_WHEAT_FIELDBOOK_60 = "WFb60";
    private static final String KEY_WHEAT_FIELDBOOK_61 = "WFb61";
    private static final String KEY_WHEAT_TAGS_04 = "WTAG04";
    private static final String KEY_WHEAT_TAGS_22 = "WTAG22";
    private static final String KEY_WHEAT_LABELS_05 = "WLBL05";
    private static final String KEY_WHEAT_LABELS_21 = "WLBL21";
    
    @BeforeClass
    public static void setUp() throws Exception {
    	reportService = managerFactory.getReportService();
    					
        dataImportService = managerFactory.getDataImportService();
    }

    //TODO create separate class for testing keys
    //TODO create separate class for testing total number of parameters by report type
    
    
    @Test
    public void testGetReportKeys(){
    	assertTrue(reportService.getReportKeys().size() > 0);
    }

    @Test
    public void testGetStreamReport_MaizeNursery(){
    	 assertReportGenerated(PROJECT_ID, KEY_MAIZE_FIELDBOOK_NURSERY);
    }    
  

    @Test
    public void testGetStreamReport_MaizeTrial(){    	
  	  assertReportGenerated(PROJECT_ID, KEY_MAIZE_FIELDBOOK_TRIAL);
    	
    }   

  @Test
  public void testGetStreamReport_ShipList(){
	  assertReportGenerated(PROJECT_ID, KEY_MAIZE_FIELDBOOK_SHIPM);

  }    

    @Test
    public void testGetStreamReport_WheatFb23(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_FIELDBOOK_23);
    }

    @Test
    public void testGetStreamReport_WheatFb24(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_24);
    }

    @Test
    public void testGetStreamReport_WheatFb25(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_25);
    }

    @Test
    public void testGetStreamReport_WheatFb26(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_26);
    }

    @Test
    public void testGetStreamReport_WheatFb28(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_28);
    }

    @Test
    public void testGetStreamReport_WheatFb29(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_29);
    }

    @Test
    public void testGetStreamReport_WheatFb41(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_41);
    }

    @Test
    public void testGetStreamReport_WheatFb42(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_42);
    }

    @Test
    public void testGetStreamReport_WheatFb43(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_43);
    }

    @Test
    public void testGetStreamReport_WheatFb47(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_FIELDBOOK_47);
    }

    @Test
    public void testGetStreamReport_WheatFb60(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_60);
    }

    @Test
    public void testGetStreamReport_WheatFb61(){
  	  assertReportGenerated(PROJECT_WHEAT_ID, KEY_WHEAT_FIELDBOOK_61);
    }

    @Test
    public void testGetStreamReport_WheatTag04(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_TAGS_04);
    }

    @Test
    public void testGetStreamReport_WheatTag22(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_TAGS_22);
    }

    @Test
    public void testGetStreamReport_WheatLabel05(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_LABELS_05);
    }


    @Test
    public void testGetStreamReport_WheatLabel21(){
  	  assertReportGenerated(PROJECT_WHEAT_CROSSES_ID, KEY_WHEAT_LABELS_21);
    }

   /**
    * Tests that a particular report is indeed created, given a studyId. 
    * @param studyId id of the test study
    * @param reportCode specific report code to generate.
    */
    private void assertReportGenerated(Integer studyId, String reportCode){
    	
    	boolean hasReportKey = reportService.getReportKeys().contains(reportCode);

    	if(hasReportKey){
    		try{
	    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	    		Reporter rep = reportService.getStreamReport(reportCode, studyId, baos);
	
	    		assertTrue("Failed test - empty report for code ["+reportCode+"].", baos.size() > 0);

	    		//additionally creates the file in 'target' folder, for human validation ;) 
	    		File xlsx = new File("target",rep.getFileName());
	    		baos.writeTo(new FileOutputStream(xlsx));
	    		
    		}catch(Exception e){
    			e.printStackTrace();
    			assertTrue("Failed test - generate report with code ["+reportCode+"].", false);
    		}
    	}
    }

}
