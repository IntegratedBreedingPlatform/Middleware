package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ListDataProjectSaverTest {

	public static final String PROJECT_PROGRAM_UUID = "3872138-813789134-012837";
	public static final String ORIGINAL_LIST_PROGRAM_UUID = "7824734-9824750-21378129374";
	public static final int PROJECT_ID = 1;
	public static final int LIST_LOCATION = 99;
	public static final int USER_ID = 123;
	public static final String NOTES = "Notes";
	public static final int S_DATE = 20170101;
	public static final int E_DATE = 20170202;
	public static final String NAME = "GermplasmName";
	public static final String DESCRIPTION = "Description";
	public static final int ORIGINAL_GERMPLASM_LIST_ID = 1;
	public static final int STATUS = 101;

	@Mock
	private Saver saver;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private GermplasmListDAO germplasmListDAO;

	@Mock
	private ListDataProjectDAO listDataProjectDAO;

	private ListDataProjectSaver listDataProjectSaver;

	@Before
	public void init() {

		final DmsProject project = new DmsProject();
		project.setProgramUUID(PROJECT_PROGRAM_UUID);
		project.setProjectId(PROJECT_ID);

		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(ORIGINAL_GERMPLASM_LIST_ID);
		germplasmList.setListLocation(LIST_LOCATION);
		germplasmList.setUserId(USER_ID);
		germplasmList.setNotes(NOTES);
		germplasmList.setsDate(S_DATE);
		germplasmList.seteDate(E_DATE);
		germplasmList.setName(NAME);
		germplasmList.setDescription(DESCRIPTION);
		germplasmList.setProgramUUID(ORIGINAL_LIST_PROGRAM_UUID);
		germplasmList.setStatus(STATUS);

		this.listDataProjectSaver = new ListDataProjectSaver();
		listDataProjectSaver.setSaver(saver);
		listDataProjectSaver.setDaoFactory(daoFactory);

		Mockito.when(this.saver.getStudyDataManager()).thenReturn(studyDataManager);
		Mockito.when(this.daoFactory.getGermplasmListDAO()).thenReturn(germplasmListDAO);
		Mockito.when(this.saver.getListDataProjectDAO()).thenReturn(listDataProjectDAO);
		Mockito.when(this.studyDataManager.getProject(PROJECT_ID)).thenReturn(project);
		Mockito.when(this.germplasmListDAO.getById(ORIGINAL_GERMPLASM_LIST_ID)).thenReturn(germplasmList);

	}

	@Test
	public void testUpdateGermlasmListInfoStudy() throws MiddlewareQueryException {
		final ListDataProjectSaver listDataProjectSaver = new ListDataProjectSaver();
		listDataProjectSaver.setDaoFactory(daoFactory);
		listDataProjectSaver.setSaver(saver);
		final ListDataProjectSaver dataSaver = Mockito.spy(listDataProjectSaver);
		final GermplasmList crossesList = new GermplasmList();
		final GermplasmListDAO germplasmListDao = Mockito.mock(GermplasmListDAO.class);
		final Integer crossesListId = 5;
		Mockito.when(this.daoFactory.getGermplasmListDAO()).thenReturn(germplasmListDao);

		Mockito.when(germplasmListDao.getById(crossesListId)).thenReturn(crossesList);

		final Integer studyId = 15;
		dataSaver.updateGermlasmListInfoStudy(crossesListId, studyId);
		Assert.assertEquals("The study Id in the crosses germplasm should be the same as what we set " + studyId, studyId,
				crossesList.getProjectId());
	}

	@Test
	public void testPerformListDataProjectEntriesDeletion() {
		final ListDataProjectSaver listDataProjectSaver = new ListDataProjectSaver();
		listDataProjectSaver.setDaoFactory(daoFactory);
		listDataProjectSaver.setSaver(saver);
		final ListDataProjectSaver dataSaver = Mockito.spy(listDataProjectSaver);

		final ListDataProjectDAO listDataProjectDAO = Mockito.mock(ListDataProjectDAO.class);
		final List<Integer> germplasmList = new ArrayList<>();
		germplasmList.add(1);
		final Integer listId = 5;

		final ListDataProject listDataProject1 = new ListDataProject();
		listDataProject1.setEntryId(1);
		final ListDataProject listDataProject2 = new ListDataProject();
		listDataProject2.setEntryId(2);

		final List<ListDataProject> listDataProjects = new ArrayList<>();
		listDataProjects.add(listDataProject2);

		Mockito.when(listDataProjectDAO.getByListIdAndGid(listId, 1)).thenReturn(listDataProject1);

		Mockito.when(listDataProjectDAO.getByListId(listId)).thenReturn(listDataProjects);

		Mockito.when(saver.getListDataProjectDAO()).thenReturn(listDataProjectDAO);

		Mockito.when(listDataProjectDAO.saveOrUpdate(listDataProject2)).thenReturn(listDataProject2);

		dataSaver.performListDataProjectEntriesDeletion(germplasmList, listId);

		Assert.assertEquals("The new entry for list listDataProject2 should be 1", (Integer) 1, listDataProject2.getEntryId());

	}

	@Test
	public void testCreateInitialGermplasmList() {

		final GermplasmList germplasmList = this.listDataProjectSaver.createInitialGermplasmList(PROJECT_ID, GermplasmListType.ADVANCED);

		Assert.assertEquals(PROJECT_ID, germplasmList.getProjectId().intValue());
		Assert.assertEquals(PROJECT_PROGRAM_UUID, germplasmList.getProgramUUID());
		Assert.assertEquals(1, germplasmList.getStatus().intValue());
		Assert.assertNotNull(germplasmList.getDate());
		Assert.assertEquals(GermplasmListType.ADVANCED.name(), germplasmList.getType());
		Assert.assertEquals(germplasmList.getName(), germplasmList.getDescription());

	}

	@Test
	public void testSaveOrUpdateListDataProjectAdvancedList() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int userId = 99;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		final ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver
				.saveOrUpdateListDataProject(PROJECT_ID, GermplasmListType.ADVANCED, originalGermplasmListId, listDataProjectList, userId);

		final ArgumentCaptor<GermplasmList> captor = ArgumentCaptor.forClass(GermplasmList.class);
		Mockito.verify(germplasmListDAO).saveOrUpdate(captor.capture());
		Mockito.verify(listDataProjectDAO).save(listDataProject);

		final GermplasmList snapshotGermplasmList = captor.getValue();

		// The snapshot germplasm list should have the same properties as the original germplasm list.
		Assert.assertEquals(LIST_LOCATION, snapshotGermplasmList.getListLocation().intValue());
		Assert.assertEquals(USER_ID, snapshotGermplasmList.getUserId().intValue());
		Assert.assertEquals(NOTES, snapshotGermplasmList.getNotes());
		Assert.assertEquals(S_DATE, snapshotGermplasmList.getsDate().intValue());
		Assert.assertEquals(E_DATE, snapshotGermplasmList.geteDate().intValue());
		Assert.assertEquals(NAME, snapshotGermplasmList.getName());
		Assert.assertEquals(DESCRIPTION, snapshotGermplasmList.getDescription());
		Assert.assertEquals(ORIGINAL_GERMPLASM_LIST_ID, snapshotGermplasmList.getListRef().intValue());
		Assert.assertEquals(ORIGINAL_LIST_PROGRAM_UUID, snapshotGermplasmList.getProgramUUID());
		Assert.assertEquals(STATUS, snapshotGermplasmList.getStatus().intValue());

	}

	@Test
	public void testSaveOrUpdateListDataProjectCrossList() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int userId = 99;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		final ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver
				.saveOrUpdateListDataProject(PROJECT_ID, GermplasmListType.CROSSES, originalGermplasmListId, listDataProjectList, userId);

		final ArgumentCaptor<GermplasmList> captor = ArgumentCaptor.forClass(GermplasmList.class);
		Mockito.verify(germplasmListDAO).saveOrUpdate(captor.capture());
		Mockito.verify(listDataProjectDAO).save(listDataProject);

		final GermplasmList snapshotGermplasmList = captor.getValue();

		// The snapshot germplasm list should have the same properties as the original germplasm list.
		Assert.assertEquals(LIST_LOCATION, snapshotGermplasmList.getListLocation().intValue());
		Assert.assertEquals(USER_ID, snapshotGermplasmList.getUserId().intValue());
		Assert.assertEquals(NOTES, snapshotGermplasmList.getNotes());
		Assert.assertEquals(S_DATE, snapshotGermplasmList.getsDate().intValue());
		Assert.assertEquals(E_DATE, snapshotGermplasmList.geteDate().intValue());
		Assert.assertEquals(NAME, snapshotGermplasmList.getName());
		Assert.assertEquals(DESCRIPTION, snapshotGermplasmList.getDescription());
		Assert.assertEquals(ORIGINAL_GERMPLASM_LIST_ID, snapshotGermplasmList.getListRef().intValue());
		Assert.assertEquals(ORIGINAL_LIST_PROGRAM_UUID, snapshotGermplasmList.getProgramUUID());
		Assert.assertEquals(STATUS, snapshotGermplasmList.getStatus().intValue());

	}

	@Test
	public void testSaveOrUpdateListDataProjectListDataProjectIsEmpty() {

		final int originalGermplasmListId = 1;
		final int newGermplasmListId = 2;
		final int userId = 99;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(newGermplasmListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		this.listDataProjectSaver
				.saveOrUpdateListDataProject(PROJECT_ID, GermplasmListType.ADVANCED, originalGermplasmListId, listDataProjectList, userId);

		final ArgumentCaptor<GermplasmList> captor = ArgumentCaptor.forClass(GermplasmList.class);
		Mockito.verify(germplasmListDAO).saveOrUpdate(captor.capture());
		Mockito.verify(listDataProjectDAO, Mockito.never()).save(Mockito.any(ListDataProject.class));

		final GermplasmList snapshotGermplasmList = captor.getValue();

		// The snapshot germplasm list should have the same properties as the original germplasm list.
		Assert.assertEquals(LIST_LOCATION, snapshotGermplasmList.getListLocation().intValue());
		Assert.assertEquals(USER_ID, snapshotGermplasmList.getUserId().intValue());
		Assert.assertEquals(NOTES, snapshotGermplasmList.getNotes());
		Assert.assertEquals(S_DATE, snapshotGermplasmList.getsDate().intValue());
		Assert.assertEquals(E_DATE, snapshotGermplasmList.geteDate().intValue());
		Assert.assertEquals(NAME, snapshotGermplasmList.getName());
		Assert.assertEquals(DESCRIPTION, snapshotGermplasmList.getDescription());
		Assert.assertEquals(ORIGINAL_GERMPLASM_LIST_ID, snapshotGermplasmList.getListRef().intValue());
		Assert.assertEquals(ORIGINAL_LIST_PROGRAM_UUID, snapshotGermplasmList.getProgramUUID());
		Assert.assertEquals(STATUS, snapshotGermplasmList.getStatus().intValue());

	}

	@Test
	public void testSaveOrUpdateListDataProjectOriginalListDoesNotExist() {

		// Set originalGermplasmListId to 2 which will return null germplasmList
		final int originalGermplasmListId = 2;
		final int snapShotListId = 1;
		final int projectId = 1;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		final ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(snapShotListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		Mockito.when(germplasmListDAO.getByProjectIdAndType(projectId, GermplasmListType.LST)).thenReturn(null);

		this.listDataProjectSaver
				.saveOrUpdateListDataProject(projectId, GermplasmListType.LST, originalGermplasmListId, listDataProjectList, USER_ID);

		final ArgumentCaptor<GermplasmList> captor = ArgumentCaptor.forClass(GermplasmList.class);

		Mockito.verify(germplasmListDAO).saveOrUpdate(captor.capture());
		Mockito.verify(listDataProjectDAO).save(listDataProject);

		final GermplasmList snapshotGermplasmList = captor.getValue();

		Assert.assertNull(snapshotGermplasmList.getListLocation());
		Assert.assertEquals(USER_ID, snapshotGermplasmList.getUserId().intValue());
		Assert.assertNull(snapshotGermplasmList.getNotes());
		Assert.assertNull(snapshotGermplasmList.getsDate());
		Assert.assertNull(snapshotGermplasmList.geteDate());
		Assert.assertNull(snapshotGermplasmList.getListRef());
		Assert.assertEquals(PROJECT_PROGRAM_UUID, snapshotGermplasmList.getProgramUUID());
		Assert.assertEquals(1, snapshotGermplasmList.getStatus().intValue());

	}

	@Test
	public void testSaveOrUpdateListDataProjectNotAdvanceAndCross() {

		final int originalGermplasmListId = 2;
		final int snapShotListId = 1;
		final int projectId = 1;
		final int userId = 99;
		final List<ListDataProject> listDataProjectList = new ArrayList<ListDataProject>();
		final ListDataProject listDataProject = new ListDataProject();
		listDataProjectList.add(listDataProject);

		Mockito.doAnswer(new Answer() {

			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final GermplasmList germplasmList = (GermplasmList) invocationOnMock.getArguments()[0];
				germplasmList.setId(snapShotListId);
				return null;
			}
		}).when(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));

		final List<GermplasmList> tempList = new ArrayList<>();
		final GermplasmList snapList = new GermplasmList();
		snapList.setId(snapShotListId);
		tempList.add(snapList);

		Mockito.when(germplasmListDAO.getByProjectIdAndType(projectId, GermplasmListType.LST)).thenReturn(tempList);

		this.listDataProjectSaver
				.saveOrUpdateListDataProject(projectId, GermplasmListType.LST, originalGermplasmListId, listDataProjectList, userId);

		Mockito.verify(listDataProjectDAO).deleteByListId(snapShotListId);
		Mockito.verify(germplasmListDAO).saveOrUpdate(Mockito.any(GermplasmList.class));
		Mockito.verify(listDataProjectDAO).save(listDataProject);

	}
}
