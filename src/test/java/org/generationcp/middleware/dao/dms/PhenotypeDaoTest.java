/*******************************************************************************

 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.ServiceIntegraionTest;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.DataImportService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PhenotypeDaoTest extends ServiceIntegraionTest {

    private static PhenotypeDao dao;
    private static DataImportService dataImportService;
    private static StudyDataManager studyDataManager;
    private static final String FIELDBOOK_FILE_IBD_VALID = "Trial457-3-1_Valid_IBD.xls";
    private static final String FIELDBOOK_FILE_CATVARIATES_ONLY = "FieldbookFile_CategoricalVariatesOnly.xls";
    private static Map<Integer,Map<String,Object>> studies = new HashMap<Integer, Map<String,Object>>();
    
    private static final String DATASETS = "datasets";
    private static final String WORKBOOK = "workbook";
	
    @BeforeClass
    public static void setUp() throws Exception {
        dao = new PhenotypeDao();
        dao.setSession(localSessionUtil.getCurrentSession());
        
        HibernateSessionProvider sessionProviderForLocal = new HibernateSessionPerThreadProvider(
				localSessionUtil.getSessionFactory());
		HibernateSessionProvider sessionProviderForCentral = new HibernateSessionPerThreadProvider(
				centralSessionUtil.getSessionFactory());
		
		ManagerFactory managerFactory = new ManagerFactory();
		managerFactory.setSessionProviderForCentral(sessionProviderForCentral);
		managerFactory.setSessionProviderForLocal(sessionProviderForLocal);
		managerFactory.setLocalDatabaseName(localConnectionParameters.getDbName());
		managerFactory.setCentralDatabaseName(centralConnectionParams.getDbName());
		
        dataImportService = serviceFactory.getDataImportService();
        studyDataManager = managerFactory.getNewStudyDataManager();
        
        importFieldbookFile(FIELDBOOK_FILE_IBD_VALID);
        importFieldbookFile(FIELDBOOK_FILE_CATVARIATES_ONLY);
    }


    private static void importFieldbookFile(String fieldbookFileIbdValid) throws Exception {
    	String fileLocation = PhenotypeDaoTest.class.getClassLoader().getResource(fieldbookFileIbdValid).getFile();
        File file = new File(fileLocation);
        Workbook workbook = dataImportService.parseWorkbook(file);
        workbook.print(INDENT);
        
    	int studyId = dataImportService.saveDataset(workbook);
    	
    	List<DatasetReference> datasetRefences = studyDataManager.getDatasetReferences(studyId);
    	
    	Map<String,Object> studyDetails = new HashMap<String,Object>();
    	studyDetails.put(DATASETS, datasetRefences);
    	studyDetails.put(WORKBOOK, workbook);
    	studies.put(studyId,studyDetails);
	}


	@SuppressWarnings("unchecked")
	@Test
    public void testContainsAtLeast2CommonEntriesWithValues() throws Exception {
		int locationId = 0;
		int trialId = 0;
    	int plotId = 0;
    	
		for (Integer studyId : studies.keySet()) {
    		
    		Map<String,Object> studyDetails = studies.get(studyId);
    		Workbook workbook = (Workbook) studyDetails.get(WORKBOOK);
    		List<DatasetReference> datasetRefences = (List<DatasetReference>) studyDetails.get(DATASETS);
    		
    		locationId = (int)workbook.getObservations().get(0).getLocationId();
        	
    		for (DatasetReference datasetReference : datasetRefences) {
        		if(datasetReference.getName().endsWith("PLOTDATA")) {
        			plotId = datasetReference.getId();
        		} else if(datasetReference.getName().endsWith("ENVIRONMENT")){
        			trialId = datasetReference.getId();
        		}
        	}
    		assertTrue("The plot dataset should have at least 2 common entries for analysis",dao.
            		containsAtLeast2CommonEntriesWithValues(plotId, locationId));
            assertFalse("The trial dataset does not contain entries for analysis",dao.
            		containsAtLeast2CommonEntriesWithValues(trialId, locationId));
		}
    }
    
    @SuppressWarnings("unchecked")
	@AfterClass
    public static void tearDown() throws Exception {
    	for (Integer studyId : studies.keySet()) {
    		List<DatasetReference> datasetRefences = 
				(List<DatasetReference>) studies.get(studyId).get(DATASETS);
    		for (DatasetReference datasetReference : datasetRefences) {
    			int datasetId = datasetReference.getId();
    			studyDataManager.deleteDataSet(datasetId);
        	}
    		studyDataManager.deleteDataSet(studyId);
		}
    	dao.setSession(null);
        dao = null;
    }

}
