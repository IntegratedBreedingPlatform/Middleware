package org.generationcp.middleware.service.impl.study;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class SampleListServiceImplTest {

	@Mock
	private HibernateSessionProvider session;

	@Mock
	private SampleListDao sampleListDao;

	@Mock
	private UserDAO userDAO;

	private SampleListServiceImpl sampleListService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		sampleListService = new SampleListServiceImpl(session);
		sampleListService.setSampleListDao(sampleListDao);
		sampleListService.setUserDao(userDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderFolderNull() throws Exception {
		this.sampleListService.createSampleListFolder(null, 1, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderParentIdNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", null, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderCreatedByNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", 1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSampleListFolderFolderNameEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("", 1, "userName");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSampleListFolderCreatedByEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("4", 1, "");
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderParentListNotExist() throws Exception {
		Mockito.when(sampleListDao.getById(1)).thenReturn(null);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderFolderNameNotUnique() throws Exception {
		final SampleList notUniqueValue = new SampleList();
		final SampleList parentFolder = new SampleList();
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(notUniqueValue);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test (expected = Exception.class)
	public void testCreateSampleListFolderParentListNotAFolder() throws Exception {
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.SAMPLE_LIST);
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(null);
		Mockito.when(userDAO.getUserByUserName("userName")).thenReturn(new User());
		SampleList sampleFolder = new SampleList();
		sampleFolder.setId(1);
		sampleFolder.setType(SampleListType.FOLDER);
		Mockito.when(sampleListDao.save(Mockito.any(SampleList.class))).thenReturn(sampleFolder);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test
	public void testCreateSampleListFolderOk() throws Exception {
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.FOLDER);
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(null);
		Mockito.when(userDAO.getUserByUserName("userName")).thenReturn(new User());
		SampleList sampleFolder = new SampleList();
		sampleFolder.setId(1);
		sampleFolder.setType(SampleListType.FOLDER);
		Mockito.when(sampleListDao.save(Mockito.any(SampleList.class))).thenReturn(sampleFolder);
		final Integer savedObject = this.sampleListService.createSampleListFolder("4", 1, "userName");
		assertThat(sampleFolder.getId(), equalTo(savedObject));
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testCreateSampleListFolderDBException() throws Exception {
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.FOLDER);
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(null);
		Mockito.when(userDAO.getUserByUserName("userName")).thenReturn(new User());
		Mockito.when(sampleListDao.save(Mockito.any(SampleList.class))).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void testUpdateSampleListFolderNameNullFolderId() throws Exception {
		this.sampleListService.updateSampleListFolderName(null, "newFolderName");
	}

	@Test(expected = NullPointerException.class)
	public void testUpdateSampleListFolderNameNullNewFolderName() throws Exception {
		this.sampleListService.updateSampleListFolderName(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateSampleListFolderNameEmptyNewFolderName() throws Exception {
		this.sampleListService.updateSampleListFolderName(1, "");
	}

	@Test(expected = Exception.class)
	public void testUpdateSampleListFolderNameFolderIdNotExist() throws Exception {
		final Integer folderId = 1;
		Mockito.when(sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test (expected = Exception.class)
	public void testUpdateSampleListFolderNameFolderIdNotAFolder() throws Exception {
		final Integer folderId = 1;
		final SampleList sampleList = new SampleList();
		sampleList.setId(folderId);
		sampleList.setType(SampleListType.SAMPLE_LIST);
		Mockito.when(sampleListDao.getById(folderId)).thenReturn(sampleList);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test(expected = Exception.class)
	public void testUpdateSampleListFolderNameRootFolderNotEditable() throws Exception {
		final Integer folderId = 1;
		SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		Mockito.when(sampleListDao.getById(folderId)).thenReturn(rootFolder);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test(expected = Exception.class)
	public void testUpdateSampleListFolderNameNameNotUnique() throws Exception {
		final Integer folderId = 2;
		final Integer parentFolderId = 1;
		final String newFolderName = "NEW_NAME";
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(parentFolderId);
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setHierarchy(parentFolder);
		final SampleList notUniqueFolder = new SampleList();

		Mockito.when(sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId())).thenReturn(notUniqueFolder);
		this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

	}

	@Test(expected = MiddlewareQueryException.class)
	public void testUpdateSampleListFolderNameDBException() throws Exception {

		final Integer folderId = 2;
		final Integer parentFolderId = 1;
		final String newFolderName = "NEW_NAME";
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(parentFolderId);
		parentFolder.setType(SampleListType.FOLDER);
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setHierarchy(parentFolder);
		folder.setType(SampleListType.FOLDER);

		Mockito.when(sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId())).thenReturn(null);

		Mockito.when(sampleListDao.saveOrUpdate(folder)).thenThrow(MiddlewareQueryException.class);

		this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

	}

	@Test
	public void testUpdateSampleListFolderNameOk() throws Exception {
		final Integer folderId = 2;
		final Integer parentFolderId = 1;
		final String newFolderName = "NEW_NAME";
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(parentFolderId);
		parentFolder.setType(SampleListType.FOLDER);
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setHierarchy(parentFolder);
		folder.setType(SampleListType.FOLDER);

		Mockito.when(sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId())).thenReturn(null);

		Mockito.when(sampleListDao.saveOrUpdate(folder)).thenReturn(folder);

		final SampleList savedFolder = this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

		assertThat(savedFolder.getListName(), equalTo(newFolderName));
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteSampleListFolderNullFolderId() throws Exception {
		this.sampleListService.deleteSampleListFolder(null);
	}

	@Test(expected = Exception.class)
	public void testDeleteSampleListFolderFolderNotExist() throws Exception {
		final Integer folderId = 1;
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = Exception.class)
	public void testDeleteSampleListFolderFolderIsRootFolder() throws Exception {
		final Integer folderId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(rootFolder);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = Exception.class)
	public void testDeleteSampleListFolderFolderHasChildren() throws Exception {
		final Integer folderId = 1;
		final SampleList folder = new SampleList();
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(2);

		folder.setId(folderId);
		folder.setHierarchy(parentFolder);

		final SampleList child = new SampleList();
		child.setId(3);
		final List<SampleList> children = new ArrayList<>();
		children.add(child);

		folder.setChildren(children);

		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testDeleteSampleListFolderDBException() throws Exception {
		final Integer folderId = 1;
		final SampleList folder = new SampleList();
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(2);
		parentFolder.setType(SampleListType.FOLDER);

		folder.setId(folderId);
		folder.setHierarchy(parentFolder);
		folder.setType(SampleListType.FOLDER);

		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.doThrow(new MiddlewareQueryException("")).when(this.sampleListDao).makeTransient(folder);

		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = NullPointerException.class)
	public void testMoveSampleListNullSampleListId() throws Exception {
		this.sampleListService.moveSampleList(null, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testMoveSampleListNewParentFolderId() throws Exception {
		this.sampleListService.moveSampleList(1, null);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListSampleListIdNotExist() throws Exception {
		final Integer sampleListId = 1;
		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(null);
		this.sampleListService.moveSampleList(sampleListId, 2);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListSampleListIdIsRootFolder() throws Exception {
		final Integer sampleListId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(sampleListId);
		rootFolder.setHierarchy(null);
		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(rootFolder);
		this.sampleListService.moveSampleList(sampleListId, 2);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListNewParentFolderIdNotExist() throws Exception {
		final Integer sampleListId = 1;
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());

		final Integer folderId = 2;

		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(null);

		this.sampleListService.moveSampleList(sampleListId, folderId);
	}

	@Test (expected = Exception.class)
	public void testMoveSampleListNewParentFolderIdNotAFolder() throws Exception {
		final Integer sampleListId = 1;
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setType(SampleListType.SAMPLE_LIST);

		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);

		this.sampleListService.moveSampleList(sampleListId, folderId);

	}

	@Test(expected = Exception.class)
	public void testMoveSampleListSampleListNameNotUnique() throws Exception {
		final Integer sampleListId = 1;
		final String listName = "NAME";
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());
		sampleListToMove.setListName(listName);

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);

		final SampleList notUniqueSampleList = new SampleList();
		notUniqueSampleList.setListName(listName);
		notUniqueSampleList.setHierarchy(folder);

		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(this.sampleListDao.getSampleListByParentAndName(listName, folderId)).thenReturn(notUniqueSampleList);

		this.sampleListService.moveSampleList(sampleListId, folderId);
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testMoveSampleListDBException() throws Exception {
		final Integer sampleListId = 1;
		final String listName = "NAME";
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());
		sampleListToMove.setListName(listName);

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setType(SampleListType.FOLDER);

		Mockito.when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(this.sampleListDao.getSampleListByParentAndName(listName, folderId)).thenReturn(null);

		Mockito.when(this.sampleListDao.saveOrUpdate(sampleListToMove)).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.moveSampleList(sampleListId, folderId);
	}
}

