package org.generationcp.middleware.service.impl.study;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
		final Integer savedId = this.sampleListService.createSampleListFolder("4", 1, "userName");
		assertThat(sampleFolder.getId(), equalTo(savedId));
	}

}
