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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.DataImportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class PhenotypeDaoTest extends IntegrationTestBase {

	private PhenotypeDao dao;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private StudyDataManager studyDataManager;

	private static final String FIELDBOOK_FILE_IBD_VALID = "Trial457-3-1_Valid_IBD.xls";
	private static final String FIELDBOOK_FILE_CATVARIATES_ONLY = "FieldbookFile_CategoricalVariatesOnly.xls";
	private Map<Integer, Map<String, Object>> studies = new HashMap<Integer, Map<String, Object>>();
	private boolean testDataLoaded = false;

	private static final String DATASETS = "datasets";
	private static final String WORKBOOK = "workbook";
	private final String cropPrefix = "ABCD";
	
	@Before
	public void setUp() throws Exception {

		if (this.dao == null) {
			this.dao = new PhenotypeDao();
			this.dao.setSession(this.sessionProvder.getSession());
		}

		
		this.importFieldbookFile(PhenotypeDaoTest.FIELDBOOK_FILE_IBD_VALID, cropPrefix);
		this.importFieldbookFile(PhenotypeDaoTest.FIELDBOOK_FILE_CATVARIATES_ONLY, cropPrefix);
	}

	private void importFieldbookFile(final String fieldbookFileIbdValid, final String cropPrefix) throws Exception {

		if (!this.testDataLoaded) {
			String fileLocation = PhenotypeDaoTest.class.getClassLoader().getResource(fieldbookFileIbdValid).getFile();
			File file = new File(fileLocation);
			Workbook workbook = this.dataImportService.parseWorkbook(file);
			workbook.print(IntegrationTestBase.INDENT);

			int studyId = this.dataImportService.saveDataset(workbook, null, cropPrefix);

			List<DatasetReference> datasetRefences = this.studyDataManager.getDatasetReferences(studyId);

			Map<String, Object> studyDetails = new HashMap<String, Object>();
			studyDetails.put(PhenotypeDaoTest.DATASETS, datasetRefences);
			studyDetails.put(PhenotypeDaoTest.WORKBOOK, workbook);
			this.studies.put(studyId, studyDetails);
			this.testDataLoaded = true;
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() throws Exception {
		int locationId = 0;
		int trialId = 0;
		int plotId = 0;

		for (Integer studyId : this.studies.keySet()) {

			Map<String, Object> studyDetails = this.studies.get(studyId);
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
					this.dao.containsAtLeast2CommonEntriesWithValues(plotId, locationId, TermId.ENTRY_NO.getId()));
			Assert.assertFalse("The trial dataset does not contain entries for analysis",
					this.dao.containsAtLeast2CommonEntriesWithValues(trialId, locationId, TermId.ENTRY_NO.getId()));
		}
	}
}
