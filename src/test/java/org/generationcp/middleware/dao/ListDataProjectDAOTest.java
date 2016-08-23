package org.generationcp.middleware.dao;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.ListDataProject;
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

	//TODO Remove mock session
	private Session mockHibernateSession;
	private Session realHibernateSession;
	
	private Germplasm parentGermplasm;

	@Before
	public void beforeTest() {
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.mockHibernateSession = Mockito.mock(Session.class);
		this.listDataProjectDAO.setSession(this.mockHibernateSession);
		this.realHibernateSession = this.sessionProvder.getSession();
		
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

		int listId = 100;
		Integer germplasmId = 1;
		Integer MgId = 500;

		List<Germplasm> germplasms = new ArrayList<>();
		List<ListDataProject> listDataProjects = new ArrayList<>();

		Germplasm germplasm = new Germplasm();
		germplasm.setGid(germplasmId);
		germplasm.setMgid(MgId);
		germplasms.add(germplasm);

		ListDataProject listDataProject = new ListDataProject();
		listDataProject.setGermplasmId(germplasmId);
		listDataProjects.add(listDataProject);

		Criteria mockCriteriaGermplasm = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(Germplasm.class)).thenReturn(mockCriteriaGermplasm);

		Criteria mockCriteriaListDataProject = Mockito.mock(Criteria.class);
		when(this.mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject")).thenReturn(mockCriteriaListDataProject);

		Criteria germplasmCriteria = this.mockHibernateSession.createCriteria(Germplasm.class);
		Criteria listDataProjectCriteria = this.mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject");

		when(germplasmCriteria.list()).thenReturn(germplasms);
		when(listDataProjectCriteria.list()).thenReturn(listDataProjects);

		List<ListDataProject> listDataProjectList = this.listDataProjectDAO.getByListId(listId);

		Assert.assertEquals(listDataProjects.size(), listDataProjectList.size());
		Assert.assertEquals(germplasm.getMgid(), listDataProjectList.get(0).getGroupId());

	}
	
	@Test
	public void testGetListDataProjectWithParents() {
		// set to real Hibernate session while mock sessions haven't been removed
		this.listDataProjectDAO.setSession(this.realHibernateSession);
		
		//setup data
		final int studyId = this.createNurseryTestData();
		final int plotNo = 1;
		
		final ListDataProject testListDataProject = this.listDataProjectDAO.getByStudy(studyId, GermplasmListType.NURSERY, plotNo);
		final int listId = testListDataProject.getList().getId();
		
		//get result of method being tested
		final List<ListDataProject> listDataProjects = this.listDataProjectDAO.getListDataProjectWithParents(listId);
		
		// Verify returned result
		Assert.assertNotNull("The list data project should not be null", listDataProjects);
		for (final ListDataProject listDataProject : listDataProjects) {
			final String expectedPreferredName = DataSetupTest.GERMPLSM_PREFIX + listDataProject.getEntryId();
			Assert.assertEquals("The preferred name must be " + expectedPreferredName, expectedPreferredName,
					listDataProject.getDesignation());
			
			//test at least one of the records for the other fields
			if(testListDataProject.getListDataProjectId().equals(listDataProject.getListDataProjectId())) {
				Assert.assertEquals("The entryId must be " + testListDataProject.getEntryId(), testListDataProject.getEntryId(),
						listDataProject.getEntryId());
				Assert.assertEquals("The designation must be " + testListDataProject.getDesignation(), testListDataProject.getDesignation(),
						listDataProject.getDesignation());
				Assert.assertEquals("The groupName must be " + testListDataProject.getGroupName(), testListDataProject.getGroupName(),
						listDataProject.getGroupName());
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
				
				// check parent germplasm values
				String parentPreferredName = this.parentGermplasm.getPreferredName().getNval();
				Assert.assertEquals("The Female Parent Designation must be " + parentPreferredName, parentPreferredName,
						listDataProject.getFemaleParent());
				Assert.assertEquals("The Female Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
						listDataProject.getFgid());
				Assert.assertEquals("The Male Parent Designation must be " + parentPreferredName, parentPreferredName,
						listDataProject.getMaleParent());
				Assert.assertEquals("The Male Parent GID must be " + this.parentGermplasm.getGid(), this.parentGermplasm.getGid(),
						listDataProject.getMgid());
			}
		}
		
	}
	
	/*
	 * Create nursery to create proper listdataproject records.
	 * Would be needing nursery as well for refactoring on ListDataProject.getByStudy method later on
	 */
	private int createNurseryTestData() {
		final String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		this.parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		
		final Integer[] gids =
				this.germplasmTestDataGenerator.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, 
						GERMPLASM_PREFERRED_NAME_PREFIX, this.parentGermplasm);
		
		final int nurseryId = this.dataSetupTest.createNurseryForGermplasm(programUUID, gids);
		
		return nurseryId;
	}

	
}
