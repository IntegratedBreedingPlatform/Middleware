package org.generationcp.middleware.service.impl.study;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
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
	public void createSampleListFolderFolderNull() throws Exception {
		this.sampleListService.createSampleListFolder(null, 1, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void createSampleListFolderParentIdNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", null, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void createSampleListFolderCreatedByNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", 1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSampleListFolderFolderNameEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("", 1, "userName");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSampleListFolderCreatedByEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("4", 1, "");
	}

	@Test(expected = Exception.class)
	public void createSampleListFolderParentListNotExist() throws Exception {
		Mockito.when(sampleListDao.getById(1)).thenReturn(null);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test(expected = Exception.class)
	public void createSampleListFolderFolderNameNotUnique() throws Exception {
		final SampleList notUniqueValue = new SampleList();
		final SampleList parentFolder = new SampleList();
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(notUniqueValue);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	//TODO
	@Test
	public void createSampleListFolderParentListNotAFolder() throws Exception {

	}

	@Test
	public void createSampleListFolderOk() throws Exception {
		final SampleList parentFolder = new SampleList();
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(null);
		Mockito.when(userDAO.getUserByUserName("userName")).thenReturn(new User());
		SampleList sampleFolder = new SampleList();
		sampleFolder.setId(1);
		Mockito.when(sampleListDao.save(Mockito.any(SampleList.class))).thenReturn(sampleFolder);
		final Integer savedObject = this.sampleListService.createSampleListFolder("4", 1, "userName");
		assertThat(sampleFolder.getId(), equalTo(savedObject));
	}

	@Test(expected = MiddlewareQueryException.class)
	public void createSampleListFolderDBException() throws Exception {
		final SampleList parentFolder = new SampleList();
		Mockito.when(sampleListDao.getById(1)).thenReturn(parentFolder);
		Mockito.when(sampleListDao.getSampleListByParentAndName("4", 1)).thenReturn(null);
		Mockito.when(userDAO.getUserByUserName("userName")).thenReturn(new User());
		Mockito.when(sampleListDao.save(Mockito.any(SampleList.class))).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.createSampleListFolder("4", 1, "userName");
	}

	@Test(expected = NullPointerException.class)
	public void updateSampleListFolderNameNullFolderId() throws Exception {
		this.sampleListService.updateSampleListFolderName(null, "newFolderName");
	}

	@Test(expected = NullPointerException.class)
	public void updateSampleListFolderNameNullNewFolderName() throws Exception {
		this.sampleListService.updateSampleListFolderName(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSampleListFolderNameEmptyNewFolderName() throws Exception {
		this.sampleListService.updateSampleListFolderName(1, "");
	}

	@Test(expected = Exception.class)
	public void updateSampleListFolderNameFolderIdNotExist() throws Exception {
		final Integer folderId = 1;
		Mockito.when(sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	//TODO
	@Test
	public void updateSampleListFolderNameFolderIdNotAFolder() throws Exception {

	}

	@Test(expected = Exception.class)
	public void updateSampleListFolderNameRootFolderNotEditable() throws Exception {
		final Integer folderId = 1;
		SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		Mockito.when(sampleListDao.getById(folderId)).thenReturn(rootFolder);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test(expected = Exception.class)
	public void updateSampleListFolderNameNameNotUnique() throws Exception {
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
	public void updateSampleListFolderNameDBException() throws Exception {

		final Integer folderId = 2;
		final Integer parentFolderId = 1;
		final String newFolderName = "NEW_NAME";
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(parentFolderId);
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setHierarchy(parentFolder);

		Mockito.when(sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId())).thenReturn(null);

		Mockito.when(sampleListDao.saveOrUpdate(folder)).thenThrow(MiddlewareQueryException.class);

		this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

	}

	@Test
	public void updateSampleListFolderNameOk() throws Exception {
		final Integer folderId = 2;
		final Integer parentFolderId = 1;
		final String newFolderName = "NEW_NAME";
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(parentFolderId);
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setHierarchy(parentFolder);

		Mockito.when(sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.when(sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId())).thenReturn(null);

		Mockito.when(sampleListDao.saveOrUpdate(folder)).thenReturn(folder);

		final SampleList savedFolder = this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

		assertThat(savedFolder.getListName(), equalTo(newFolderName));
	}

	@Test(expected = NullPointerException.class)
	public void deleteSampleListFolderNullFolderId() throws Exception {
		this.sampleListService.deleteSampleListFolder(null);
	}

	@Test(expected = Exception.class)
	public void deleteSampleListFolderFolderNotExist() throws Exception {
		final Integer folderId = 1;
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = Exception.class)
	public void deleteSampleListFolderFolderIsRootFolder() throws Exception {
		final Integer folderId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(rootFolder);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = Exception.class)
	public void deleteSampleListFolderFolderHasChildren() throws Exception {
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
	public void deleteSampleListFolderDBException() throws Exception {
		final Integer folderId = 1;
		final SampleList folder = new SampleList();
		final SampleList parentFolder = new SampleList();
		parentFolder.setId(2);

		folder.setId(folderId);
		folder.setHierarchy(parentFolder);

		Mockito.when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.doThrow(new MiddlewareQueryException("")).when(this.sampleListDao).makeTransient(folder);

		this.sampleListService.deleteSampleListFolder(folderId);
	}

}
