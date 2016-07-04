/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.StudyTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyDataManagerImplTest extends IntegrationTestBase {

	private static final int START_DATE = 20140627;

	private static final String BASIC_NURSERY_TEMPLATE = "Basic nursery template";

	private StudyDataManagerImpl manager;

	@Autowired
	private OntologyDataManager ontologyManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataDM;
	
	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private static CrossExpansionProperties crossExpansionProperties;
	private StudyReference studyReference;
	private StudyTestDataInitializer studyTDI;
	
	@Before
	public void setUp() throws Exception {
		this.manager = new StudyDataManagerImpl(this.sessionProvder);
		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		StudyDataManagerImplTest.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		StudyDataManagerImplTest.crossExpansionProperties.setDefaultLevel(1);
		this.studyTDI = new StudyTestDataInitializer(this.manager, this.ontologyManager, this.commonTestProject, this.germplasmDataDM);
		this.studyReference = this.studyTDI.addTestStudy();
	}

	@Test
	public void testGetStudy() throws Exception {
		Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME, study.getName());
		Assert.assertEquals("The study description should be " + StudyTestDataInitializer.STUDY_DESCRIPTION, StudyTestDataInitializer.STUDY_DESCRIPTION, study.getTitle());
	}
	
	@Test
	public void testGetStudyConditions() throws Exception {
		Study study = this.manager.getStudy(this.studyReference.getId());
		Assert.assertNotNull(study);
		VariableList vList = study.getConditions();
		Assert.assertNotNull("The Variable list should not be null", vList);
	}

	@Test
	public void testGetAllStudyFactor() throws Exception {
		this.studyTDI.addTestDataset(this.studyReference.getId());
		VariableTypeList factors = this.manager.getAllStudyFactors(this.studyReference.getId());
		Assert.assertNotNull(factors);
		//compare the size to the minimum possible value of variable type's size
		Assert.assertTrue("The size should be greater than 0", factors.getVariableTypes().size() > 0);
	}
	
	@Test
	public void testGetAllStudyVariates() throws Exception {
		VariableTypeList variates = this.manager.getAllStudyVariates(this.studyReference.getId());
		Assert.assertNotNull(variates);
		DMSVariableType studyName = variates.findById(TermId.STUDY_NAME.getId());
		Assert.assertEquals("The study name should be " + StudyTestDataInitializer.STUDY_NAME, StudyTestDataInitializer.STUDY_NAME, studyName.getLocalName());
	}

	@Test
	public void testGetStudiesByFolder() throws Exception {
		StudyResultSet resultSet = this.manager.searchStudies(new ParentFolderStudyQueryFilter(1), 5);
		//We are sure that the result set will return at least one study, the study that we added in the setup
		Assert.assertTrue("The size should be greater than 0.", resultSet.size()>0);
	}

	@Test
	public void testSearchStudiesForName() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();

		filter.setName("FooFoo"); 
		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		Assert.assertTrue("The size should be zero since the name is invalid", resultSet.size() == 0);

		filter.setName(BASIC_NURSERY_TEMPLATE);
		resultSet = this.manager.searchStudies(filter, 10);
		
		//We are sure that the result set will contain at least one study
		Assert.assertTrue("The size should be greater than zero", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesForStartDate() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		//start date of basic nursery template
		filter.setStartDate(START_DATE);

		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	
	@Test
	public void testSearchStudiesForAll() throws Exception {
		BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
		filter.setStartDate(START_DATE);
		filter.setName(BASIC_NURSERY_TEMPLATE);

		StudyResultSet resultSet = this.manager.searchStudies(filter, 10);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}

	@Test
	public void testSearchStudiesByGid() throws Exception {
		GidStudyQueryFilter filter = new GidStudyQueryFilter(this.studyTDI.getGid());
		StudyResultSet resultSet = this.manager.searchStudies(filter, 50);
		//We are sure that the result set will contain the test study we added in the set up
		Assert.assertTrue("The size should be greater than 0", resultSet.size() > 0);
	}
	
	@Test
	public void testGetRootFolders() throws Exception {
		List<Reference> rootFolders = this.manager.getRootFolders(this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(rootFolders);
		Assert.assertFalse("Root folders should not be empty because it contains the templates for Nursery and Trial.", rootFolders.isEmpty());
	}

	@Test
	public void testGetChildrenOfFolder() throws Exception {
		
		String uniqueId = this.commonTestProject.getUniqueID();
		DmsProject mainFolder = this.studyTDI.createFolderTestData(uniqueId);
		int subFolderID =  this.manager.addSubFolder(mainFolder.getProjectId(), "Sub folder", "Sub Folder",
				uniqueId);

		List<Reference> childrenNodes = this.manager.getChildrenOfFolder(mainFolder.getProjectId(), this.commonTestProject.getUniqueID(), StudyType.nurseriesAndTrials());
		Assert.assertNotNull(childrenNodes);
		Assert.assertTrue("The size should be one.", childrenNodes.size() == 1);
		Assert.assertTrue("The id of the subFolder should be " + subFolderID,  subFolderID == childrenNodes.get(0).getId());
	}

	@Test
	public void testGetAllFolders() {

		this.studyTDI.createFolderTestData(this.commonTestProject.getUniqueID());
		this.studyTDI.createFolderTestData(null);

		final List<FolderReference> allFolders = this.manager.getAllFolders();
		// We only assert that there are minimum two folders that we added in test.
		// The test database might already have some pre-init and developer created folders too which we dont want the test to depend on
		// because we do not reset the test database for each test run yet.
		Assert.assertTrue(allFolders.size() >= 2);
	}
}
