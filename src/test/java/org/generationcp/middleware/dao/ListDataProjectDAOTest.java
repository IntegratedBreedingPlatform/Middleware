
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class ListDataProjectDAOTest extends IntegrationTestBase {

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private ListDataProjectDAO listDataProjectDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private DataSetupTest dataSetupTest;

	@Autowired
	private DataImportService dataImportService;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private FieldbookService middlewareFieldbookService;

	@Before
	public void beforeTest() {
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.listDataProjectDAO.setSession(this.sessionProvder.getSession());
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.dataSetupTest = new DataSetupTest();
		this.dataSetupTest.setDataImportService(this.dataImportService);
		this.dataSetupTest.setGermplasmListManager(this.germplasmListManager);
		this.dataSetupTest.setMiddlewareFieldbookService(this.middlewareFieldbookService);
	}

	@Test
	public void testGetByListId() throws Exception {

		final Session mockHibernateSession = Mockito.mock(Session.class);
		this.listDataProjectDAO.setSession(mockHibernateSession);

		final int listId = 100;
		final Integer germplasmId = 1;
		final Integer MgId = 500;

		final List<Germplasm> germplasms = new ArrayList<>();
		final List<ListDataProject> listDataProjects = new ArrayList<>();

		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(germplasmId);
		germplasm.setMgid(MgId);
		germplasms.add(germplasm);

		final ListDataProject listDataProject = new ListDataProject();
		listDataProject.setGermplasmId(germplasmId);
		listDataProjects.add(listDataProject);

		final Criteria mockCriteriaGermplasm = Mockito.mock(Criteria.class);
		Mockito.when(mockHibernateSession.createCriteria(Germplasm.class)).thenReturn(mockCriteriaGermplasm);

		final Criteria mockCriteriaListDataProject = Mockito.mock(Criteria.class);
		Mockito.when(mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject")).thenReturn(mockCriteriaListDataProject);

		final Criteria germplasmCriteria = mockHibernateSession.createCriteria(Germplasm.class);
		final Criteria listDataProjectCriteria = mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject");

		Mockito.when(germplasmCriteria.list()).thenReturn(germplasms);
		Mockito.when(listDataProjectCriteria.list()).thenReturn(listDataProjects);

		final List<ListDataProject> listDataProjectList = this.listDataProjectDAO.getByListId(listId);

		Assert.assertEquals(listDataProjects.size(), listDataProjectList.size());
		Assert.assertEquals(germplasm.getMgid(), listDataProjectList.get(0).getGroupId());

	}

	@Test
	public void testGetByStudyListTypeAndPlotNo() {
		final int studyId = this.createNurseryTestData();
		final int plotNo = 1;
		final ListDataProject listDataProject = this.listDataProjectDAO.getByStudyListTypeAndPlotNo(studyId, GermplasmListType.NURSERY, plotNo);
		Assert.assertNotNull("The list data project should not be null", listDataProject);
		final String expectedPreferredName = GERMPLASM_PREFERRED_NAME_PREFIX + plotNo;
		Assert.assertEquals("The preferred name must be " + expectedPreferredName, expectedPreferredName,
				listDataProject.getDesignation());
	}

	private int createNurseryTestData() {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		final Integer[] gids =
				this.germplasmTestDataGenerator.createGermplasmRecords(DataSetupTest.NUMBER_OF_GERMPLASM, GERMPLASM_PREFERRED_NAME_PREFIX);
		final int nurseryId = this.dataSetupTest.createNursery(programUUID, gids);
		this.createNonpreferredNamesOfGermplasms(gids);
		return nurseryId;
	}

	private void createNonpreferredNamesOfGermplasms(final Integer[] gids) {
		final NameDAO nameDAO = new NameDAO();
		nameDAO.setSession(this.sessionProvder.getSession());
		for (final Integer gid : gids) {
			final Name otherName = GermplasmTestDataInitializer.createGermplasmName(gid, "Other Name ");
			otherName.setNstat(0);
			nameDAO.save(otherName);
		}
	}
	
	@Test
	public void testGetListDataProjectWithParents() {
		//setup data
		final int studyId = this.createNurseryTestData();
		final int plotNo = 1;
		final ListDataProject testListDataProject = this.listDataProjectDAO.getByStudyListTypeAndPlotNo(studyId, GermplasmListType.NURSERY, plotNo);
		final int listId = testListDataProject.getList().getId();
		//get result of method being tested
		final List<ListDataProject> listDataProjects = this.listDataProjectDAO.getListDataProjectWithParents(listId);
		//verify result
		Assert.assertNotNull("The list should not be null", listDataProjects);
		for (final ListDataProject listDataProject : listDataProjects) {
			final String expectedPreferredName = "GP-VARIETY-" + listDataProject.getEntryId();
			Assert.assertEquals("The preferred name must be " + expectedPreferredName, expectedPreferredName,
					listDataProject.getDesignation());
			//test at least one of the records for the other fields
			if(listDataProject.getListDataProjectId() == testListDataProject.getListDataProjectId()) {
				Assert.assertEquals("The entryId must be " + testListDataProject.getEntryId(), testListDataProject.getEntryId(),
						listDataProject.getEntryId());
				Assert.assertEquals("The designation must be " + testListDataProject.getDesignation(), testListDataProject.getDesignation(),
						listDataProject.getDesignation());
				Assert.assertEquals("The groupName must be " + testListDataProject.getGroupName(), testListDataProject.getGroupName(),
						listDataProject.getGroupName());
				Assert.assertEquals("The femaleParent must be " + testListDataProject.getFemaleParent(), testListDataProject.getFemaleParent(),
						listDataProject.getFemaleParent());
				Assert.assertEquals("The fgid must be " + testListDataProject.getFgid(), testListDataProject.getFgid(),
						listDataProject.getFgid());
				Assert.assertEquals("The maleParent must be " + testListDataProject.getMaleParent(), testListDataProject.getMaleParent(),
						listDataProject.getMaleParent());
				Assert.assertEquals("The mgid must be " + testListDataProject.getMgid(), testListDataProject.getMgid(),
						listDataProject.getMgid());
				Assert.assertEquals("The groupId must be " + testListDataProject.getGroupId(), testListDataProject.getGroupId(),
						listDataProject.getGroupId());
				Assert.assertEquals("The germplasmId must be " + testListDataProject.getGermplasmId(), testListDataProject.getGermplasmId(),
						listDataProject.getGermplasmId());
				Assert.assertEquals("The seedSource must be " + testListDataProject.getSeedSource(), testListDataProject.getSeedSource(),
						listDataProject.getSeedSource());
				Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(), testListDataProject.getDuplicate(),
						listDataProject.getDuplicate());
				Assert.assertEquals("The duplicate must be " + testListDataProject.getDuplicate(), testListDataProject.getDuplicate(),
						listDataProject.getDuplicate());
				Assert.assertEquals("The checkType must be " + testListDataProject.getCheckType(), testListDataProject.getCheckType(),
						listDataProject.getCheckType());
				Assert.assertEquals("The entryCode must be " + testListDataProject.getEntryCode(), testListDataProject.getEntryCode(),
						listDataProject.getEntryCode());
			}
		}
		
	}

}
