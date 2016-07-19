package org.generationcp.middleware.dao;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.DataImportServiceImpl;
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
		dataSetupTest = new DataSetupTest();
		dataSetupTest.setDataImportService(dataImportService);
		dataSetupTest.setGermplasmListManager(germplasmListManager);
		dataSetupTest.setMiddlewareFieldbookService(middlewareFieldbookService);
	}

	@Test
	public void testGetByListId() throws Exception {
		
		final Session mockHibernateSession = Mockito.mock(Session.class);
		this.listDataProjectDAO.setSession(mockHibernateSession);

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
		when(mockHibernateSession.createCriteria(Germplasm.class)).thenReturn(mockCriteriaGermplasm);

		Criteria mockCriteriaListDataProject = Mockito.mock(Criteria.class);
		when(mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject")).thenReturn(mockCriteriaListDataProject);

		Criteria germplasmCriteria = mockHibernateSession.createCriteria(Germplasm.class);
		Criteria listDataProjectCriteria = mockHibernateSession.createCriteria(ListDataProject.class, "listDataProject");

		when(germplasmCriteria.list()).thenReturn(germplasms);
		when(listDataProjectCriteria.list()).thenReturn(listDataProjects);

		List<ListDataProject> listDataProjectList = this.listDataProjectDAO.getByListId(listId);

		Assert.assertEquals(listDataProjects.size(), listDataProjectList.size());
		Assert.assertEquals(germplasm.getMgid(), listDataProjectList.get(0).getGroupId());

	}
	
	@Test
	public void testGetByStudy() {
		final int studyId = this.createNurseryTestData();
		final int plotNo = 1;
		final ListDataProject listDataProject = this.listDataProjectDAO.getByStudy(
				studyId, GermplasmListType.NURSERY, plotNo);
		Assert.assertNotNull("The list data project should not be null",listDataProject);
		String expectedPreferredName = "GP-VARIETY-1";
		Assert.assertEquals("The preferred name must be the preferred name" + expectedPreferredName, 
				expectedPreferredName, listDataProject.getDesignation());
	}
	
	private int createNurseryTestData() {
		String programUUID = "884fefcc-1cbd-4e0f-9186-ceeef3aa3b78";
		final Integer[] gids =
				this.germplasmTestDataGenerator.createGermplasmRecords(DataSetupTest.NUMBER_OF_GERMPLASM, DataSetupTest.GERMPLSM_PREFIX);
		int nurseryId = dataSetupTest.createNursery(programUUID, gids);
		this.createNonpreferredNamesOfGermplasms(gids);
		return nurseryId;
	}
	
	private void createNonpreferredNamesOfGermplasms(final Integer[] gids) {
		final NameDAO nameDAO = new NameDAO();
		nameDAO.setSession(this.sessionProvder.getSession());
		for (final Integer gid : gids) {
			Name otherName = GermplasmTestDataInitializer.createGermplasmName(gid, "Other Name ");
			otherName.setNstat(0);
			nameDAO.save(otherName);
		}
	}
	
}
