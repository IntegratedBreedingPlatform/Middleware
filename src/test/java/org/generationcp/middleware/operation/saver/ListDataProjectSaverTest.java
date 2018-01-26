
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ListDataProjectSaverTest {

	public static final String PROGRAM_UUID = "3872138-813789134-012837";

	@Mock
	private Saver saver;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private GermplasmListDAO germplasmListDAO;

	@Mock
	private ListDataProjectDAO listDataProjectDAO;

	private ListDataProjectSaver listDataProjectSaver;


	@Before
	public void init() {

		this.listDataProjectSaver = new ListDataProjectSaver(saver);

		Mockito.when(this.saver.getStudyDataManager()).thenReturn(studyDataManager);
		Mockito.when(this.saver.getGermplasmListDAO()).thenReturn(germplasmListDAO);
		Mockito.when(this.saver.getListDataProjectDAO()).thenReturn(listDataProjectDAO);

	}


	@Test
	public void testUpdateGermlasmListInfoStudy() throws MiddlewareQueryException {
		final Saver daoFactory = Mockito.mock(Saver.class);
		final ListDataProjectSaver dataSaver = Mockito.spy(new ListDataProjectSaver(daoFactory));
		final GermplasmList crossesList = new GermplasmList();
		final GermplasmListDAO germplasmListDao = Mockito.mock(GermplasmListDAO.class);
		Integer crossesListId = 5;
		Mockito.when(daoFactory.getGermplasmListDAO()).thenReturn(germplasmListDao);

		Mockito.when(germplasmListDao.getById(crossesListId)).thenReturn(crossesList);

		Integer studyId = 15;
		dataSaver.updateGermlasmListInfoStudy(crossesListId, studyId);
		Assert.assertEquals("The study Id in the crosses germplasm should be the same as what we set " + studyId, studyId,
				crossesList.getProjectId());
	}

	@Test
	public void testPerformListDataProjectEntriesDeletion() {
		final Saver daoFactory = Mockito.mock(Saver.class);
		final ListDataProjectSaver dataSaver = Mockito.spy(new ListDataProjectSaver(daoFactory));

		final ListDataProjectDAO listDataProjectDAO = Mockito.mock(ListDataProjectDAO.class);
		List<Integer> germplasmList = new ArrayList<>();
		germplasmList.add(1);
		final Integer listId = 5;

		ListDataProject listDataProject1 = new ListDataProject();
		listDataProject1.setEntryId(1);
		ListDataProject listDataProject2 = new ListDataProject();
		listDataProject2.setEntryId(2);

		List<ListDataProject> listDataProjects = new ArrayList<>();
		listDataProjects.add(listDataProject2);

		Mockito.when(listDataProjectDAO.getByListIdAndGid(listId, 1)).thenReturn(listDataProject1);

		Mockito.when(listDataProjectDAO.getByListId(listId)).thenReturn(listDataProjects);

		Mockito.when(daoFactory.getListDataProjectDAO()).thenReturn(listDataProjectDAO);

		Mockito.when(listDataProjectDAO.saveOrUpdate(listDataProject2)).thenReturn(listDataProject2);

		dataSaver.performListDataProjectEntriesDeletion(germplasmList, listId);

		Assert.assertEquals("The new entry for list listDataProject2 should be 1", (Integer) 1, listDataProject2.getEntryId());

	}

	@Test
	public void testCreateInitialGermplasmList() {

		final int projectId = 1;
		final int listStatus = 101;

		final GermplasmList germplasmList = this.listDataProjectSaver.createInitialGermplasmList(projectId, GermplasmListType.ADVANCED,
				PROGRAM_UUID, listStatus);

		Assert.assertEquals(projectId, germplasmList.getProjectId().intValue());
		Assert.assertEquals(PROGRAM_UUID, germplasmList.getProgramUUID());
		Assert.assertEquals(listStatus, germplasmList.getStatus().intValue());
		Assert.assertNotNull(germplasmList.getDate());
		Assert.assertEquals(GermplasmListType.ADVANCED.name(), germplasmList.getType());
		Assert.assertEquals(germplasmList.getName(), germplasmList.getDescription());

	}

	@Test
	public void testSaveOrUpdateListDataProjectAdvancedList() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int projectId = 1;
		final int userId = 99;
		final int listStatus = 101;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver.saveOrUpdateListDataProject(projectId, GermplasmListType.ADVANCED, originalGermplasmListId, listDataProjectList
				, userId, PROGRAM_UUID, listStatus);

		Mockito.verify(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));
		Mockito.verify(listDataProjectDAO).save(listDataProject);


	}

	@Test
	public void testSaveOrUpdateListDataProjectCrossList() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int projectId = 1;
		final int userId = 99;
		final int listStatus = 101;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver.saveOrUpdateListDataProject(projectId, GermplasmListType.CROSSES, originalGermplasmListId, listDataProjectList
				, userId, PROGRAM_UUID, listStatus);

		Mockito.verify(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));
		Mockito.verify(listDataProjectDAO).save(listDataProject);


	}

	@Test
	public void testSaveOrUpdateListDataProjectListDataProjectIsEmpty() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int projectId = 1;
		final int userId = 99;
		final int listStatus = 101;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver.saveOrUpdateListDataProject(projectId, GermplasmListType.ADVANCED, originalGermplasmListId, listDataProjectList
				, userId, PROGRAM_UUID, listStatus);

		Mockito.verify(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));
		Mockito.verify(listDataProjectDAO, Mockito.never()).save(Mockito.any(ListDataProject.class));


	}

	@Test
	public void testSaveOrUpdateListDataProjectNotAdvanceAndCross() {

		final int originalGermplasmListId = 2;
		final int snapShotListId = 1;
		final int projectId = 1;
		final int userId = 99;
		final int listStatus = 101;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(snapShotListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));


		List<GermplasmList> tempList = new ArrayList<>();
		GermplasmList snapList = new GermplasmList();
		snapList.setId(snapShotListId);
		tempList.add(snapList);

		Mockito.when(germplasmListDAO.getByProjectIdAndType(projectId,  GermplasmListType.LST)).thenReturn(tempList);

		this.listDataProjectSaver.saveOrUpdateListDataProject(projectId, GermplasmListType.LST, originalGermplasmListId, listDataProjectList
				, userId, PROGRAM_UUID, listStatus);

		Mockito.verify(listDataProjectDAO).deleteByListId(snapShotListId);
		Mockito.verify(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));
		Mockito.verify(listDataProjectDAO).save(listDataProject);


	}
}
