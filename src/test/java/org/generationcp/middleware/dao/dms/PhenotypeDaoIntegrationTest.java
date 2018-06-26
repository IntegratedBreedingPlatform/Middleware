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
public class PhenotypeDaoIntegrationTest extends IntegrationTestBase {

	private PhenotypeDao dao;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private StudyDataManager studyDataManager;

	private static final String FIELDBOOK_FILE_IBD_VALID = "Study457-3-1_Valid_IBD.xls";
	private static final String FIELDBOOK_FILE_CATVARIATES_ONLY = "FieldbookFile_CategoricalVariatesOnly.xls";
	private static final Integer CREATED_BY = 1;
	private final Map<Integer, Map<String, Object>> studies = new HashMap<Integer, Map<String, Object>>();
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

		
		this.importFieldbookFile(PhenotypeDaoIntegrationTest.FIELDBOOK_FILE_IBD_VALID, cropPrefix);
		this.importFieldbookFile(PhenotypeDaoIntegrationTest.FIELDBOOK_FILE_CATVARIATES_ONLY, cropPrefix);
	}

	private void importFieldbookFile(final String fieldbookFileIbdValid, final String cropPrefix) throws Exception {

		if (!this.testDataLoaded) {
			final String fileLocation = PhenotypeDaoIntegrationTest.class.getClassLoader().getResource(fieldbookFileIbdValid).getFile();
			final File file = new File(fileLocation);
			final Workbook workbook = this.dataImportService.parseWorkbook(file, PhenotypeDaoIntegrationTest.CREATED_BY);
			workbook.print(IntegrationTestBase.INDENT);

			final int studyId = this.dataImportService.saveDataset(workbook, null, cropPrefix);

			final List<DatasetReference> datasetRefences = this.studyDataManager.getDatasetReferences(studyId);

			final Map<String, Object> studyDetails = new HashMap<String, Object>();
			studyDetails.put(PhenotypeDaoIntegrationTest.DATASETS, datasetRefences);
			studyDetails.put(PhenotypeDaoIntegrationTest.WORKBOOK, workbook);
			this.studies.put(studyId, studyDetails);
			this.testDataLoaded = true;
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testContainsAtLeast2CommonEntriesWithValues() throws Exception {
		int locationId = 0;
		int plotId = 0;

		for (Integer studyId : this.studies.keySet()) {

			final Map<String, Object> studyDetails = this.studies.get(studyId);
			final Workbook workbook = (Workbook) studyDetails.get(PhenotypeDaoIntegrationTest.WORKBOOK);
			final List<DatasetReference> datasetRefences = (List<DatasetReference>) studyDetails.get(PhenotypeDaoIntegrationTest.DATASETS);

			locationId = (int) workbook.getObservations().get(0).getLocationId();

			for (final DatasetReference datasetReference : datasetRefences) {
				if (datasetReference.getName().endsWith("PLOTDATA")) {
					plotId = datasetReference.getId();
				} else if (datasetReference.getName().endsWith("ENVIRONMENT")) {
					studyId = datasetReference.getId();
				}
			}
			Assert.assertTrue("The plot dataset should have at least 2 common entries for analysis",
					this.dao.containsAtLeast2CommonEntriesWithValues(plotId, locationId, TermId.ENTRY_NO.getId()));
			Assert.assertFalse("The trial dataset does not contain entries for analysis",
					this.dao.containsAtLeast2CommonEntriesWithValues(studyId, locationId, TermId.ENTRY_NO.getId()));
		}
	}
}
