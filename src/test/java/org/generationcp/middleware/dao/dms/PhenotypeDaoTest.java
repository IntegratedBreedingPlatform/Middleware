/*******************************************************************************
 * 
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.DataImportService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PhenotypeDaoTest extends DataManagerIntegrationTest {

	private static PhenotypeDao dao;
	private static DataImportService dataImportService;
	private static StudyDataManager studyDataManager;
	private static final String FIELDBOOK_FILE_IBD_VALID = "Trial457-3-1_Valid_IBD.xls";
	private static final String FIELDBOOK_FILE_CATVARIATES_ONLY = "FieldbookFile_CategoricalVariatesOnly.xls";
	private static Map<Integer, Map<String, Object>> studies = new HashMap<Integer, Map<String, Object>>();

	private static final String DATASETS = "datasets";
	private static final String WORKBOOK = "workbook";

	@BeforeClass
	public static void setUp() throws Exception {
		PhenotypeDaoTest.dao = new PhenotypeDao();
		PhenotypeDaoTest.dao.setSession(MiddlewareIntegrationTest.sessionUtil.getCurrentSession());

		PhenotypeDaoTest.dataImportService = DataManagerIntegrationTest.managerFactory.getDataImportService();
		PhenotypeDaoTest.studyDataManager = DataManagerIntegrationTest.managerFactory.getNewStudyDataManager();

		PhenotypeDaoTest.importFieldbookFile(PhenotypeDaoTest.FIELDBOOK_FILE_IBD_VALID);
		PhenotypeDaoTest.importFieldbookFile(PhenotypeDaoTest.FIELDBOOK_FILE_CATVARIATES_ONLY);
	}

	private static void importFieldbookFile(String fieldbookFileIbdValid) throws Exception {
		String fileLocation = PhenotypeDaoTest.class.getClassLoader().getResource(fieldbookFileIbdValid).getFile();
		File file = new File(fileLocation);
		Workbook workbook = PhenotypeDaoTest.dataImportService.parseWorkbook(file);
		workbook.print(MiddlewareIntegrationTest.INDENT);

		int studyId = PhenotypeDaoTest.dataImportService.saveDataset(workbook, null);

		List<DatasetReference> datasetRefences = PhenotypeDaoTest.studyDataManager.getDatasetReferences(studyId);

		Map<String, Object> studyDetails = new HashMap<String, Object>();
		studyDetails.put(PhenotypeDaoTest.DATASETS, datasetRefences);
		studyDetails.put(PhenotypeDaoTest.WORKBOOK, workbook);
		PhenotypeDaoTest.studies.put(studyId, studyDetails);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() throws Exception {
		int locationId = 0;
		int trialId = 0;
		int plotId = 0;

		for (Integer studyId : PhenotypeDaoTest.studies.keySet()) {

			Map<String, Object> studyDetails = PhenotypeDaoTest.studies.get(studyId);
			Workbook workbook = (Workbook) studyDetails.get(PhenotypeDaoTest.WORKBOOK);
			List<DatasetReference> datasetRefences = (List<DatasetReference>) studyDetails.get(PhenotypeDaoTest.DATASETS);

			locationId = (int) workbook.getObservations().get(0).getLocationId();

			for (DatasetReference datasetReference : datasetRefences) {
				if (datasetReference.getName().endsWith("PLOTDATA")) {
					plotId = datasetReference.getId();
				} else if (datasetReference.getName().endsWith("ENVIRONMENT")) {
					trialId = datasetReference.getId();
				}
			}
			Assert.assertTrue("The plot dataset should have at least 2 common entries for analysis",
					PhenotypeDaoTest.dao.containsAtLeast2CommonEntriesWithValues(plotId, locationId));
			Assert.assertFalse("The trial dataset does not contain entries for analysis",
					PhenotypeDaoTest.dao.containsAtLeast2CommonEntriesWithValues(trialId, locationId));
		}
	}

	@SuppressWarnings("unchecked")
	@AfterClass
	public static void tearDown() throws Exception {
		for (Integer studyId : PhenotypeDaoTest.studies.keySet()) {
			List<DatasetReference> datasetRefences =
					(List<DatasetReference>) PhenotypeDaoTest.studies.get(studyId).get(PhenotypeDaoTest.DATASETS);
			for (DatasetReference datasetReference : datasetRefences) {
				int datasetId = datasetReference.getId();
				PhenotypeDaoTest.studyDataManager.deleteDataSet(datasetId);
			}
			PhenotypeDaoTest.studyDataManager.deleteDataSet(studyId);
		}
		PhenotypeDaoTest.dao.setSession(null);
		PhenotypeDaoTest.dao = null;
	}

}
