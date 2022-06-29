package org.generationcp.middleware.service.impl.sampleList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.study.SampleListServiceImpl;
import org.generationcp.middleware.service.impl.study.SamplePlateInfo;
import org.generationcp.middleware.service.impl.study.StudyMeasurements;
import org.generationcp.middleware.util.Util;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class SampleListServiceImplTest {

	public static final String ADMIN = "admin";
	public static final String MAIZE = "maize";
	public static final String PLOT_CODE_PREFIX = "AZDS";
	public static final boolean IS_CROP_LIST = false;
	public static final String PROGRAM_UUID = "3973084-9234894-sasdk-93921";
	public static final int USERID = 1;

	@Mock
	private HibernateSessionProvider session;

	@Mock
	private SampleListDao sampleListDao;

	@Mock
	private SampleDao sampleDao;

	@Mock
	private StudyDataManager studyService;

	@Mock
	private StudyMeasurements studyMeasurements;

	@Mock
	private CropService cropService;

	@Mock
	private Study study;

	@Mock
	private SampleService sampleService;

	@Mock
	private UserService userService;

	private SampleListServiceImpl sampleListService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.sampleListService = new SampleListServiceImpl(this.session);
		this.sampleListService.setStudyMeasurements(this.studyMeasurements);
		this.sampleListService.setCropService(this.cropService);
		this.sampleListService.setSampleService(this.sampleService);
		this.sampleListService.setUserService(this.userService);

		final DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		this.sampleListService.setDaoFactory(daoFactory);
		when(daoFactory.getSampleDao()).thenReturn(this.sampleDao);
		when(daoFactory.getSampleListDao()).thenReturn(this.sampleListDao);

		final WorkbenchUser createdBy = new WorkbenchUser();
		createdBy.setName(ADMIN);
		createdBy.setUserid(USERID);
		when(this.userService.getUserByUsername(ADMIN)).thenReturn(createdBy);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderFolderNull() throws Exception {
		this.sampleListService.createSampleListFolder(null, 1, ADMIN, PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderParentIdNull() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		this.sampleListService.createSampleListFolder("name", null, ADMIN, PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderCreatedByNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", 1, null, PROGRAM_UUID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSampleListFolderFolderNameEmpty() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		createdBy.setName("superadmin");
		this.sampleListService.createSampleListFolder("", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderCreatedByEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("4", 1, null, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderParentListNotExist() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		createdBy.setUserid(org.mockito.Matchers.anyInt());
		when(this.sampleListDao.getById(1)).thenReturn(null);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderFolderNameNotUnique() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		final SampleList notUniqueValue = new SampleList();
		final SampleList parentFolder = new SampleList();
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(notUniqueValue);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderParentListNotAFolder() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.SAMPLE_LIST);
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(null);
		final SampleList sampleFolder = new SampleList();
		sampleFolder.setId(1);
		sampleFolder.setType(SampleListType.FOLDER);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenReturn(sampleFolder);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test
	public void testCreateSampleListFolderOk() throws Exception {
		final WorkbenchUser createdBy = new WorkbenchUser();
		createdBy.setUserid(org.mockito.Matchers.anyInt());
		createdBy.setName("superadmin");
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.FOLDER);
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(null);
		final SampleList sampleFolder = new SampleList();
		sampleFolder.setId(1);
		sampleFolder.setType(SampleListType.FOLDER);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenReturn(sampleFolder);
		final Integer savedObject = this.sampleListService.createSampleListFolder("4", 1, ADMIN, PROGRAM_UUID);
		MatcherAssert.assertThat(sampleFolder.getId(), Matchers.equalTo(savedObject));
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testCreateSampleListFolderDBException() throws Exception {
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.FOLDER);
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(null);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.createSampleListFolder("4", 1, ADMIN, PROGRAM_UUID);
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
		when(this.sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test(expected = Exception.class)
	public void testUpdateSampleListFolderNameFolderIdNotAFolder() throws Exception {
		final Integer folderId = 1;
		final SampleList sampleList = new SampleList();
		sampleList.setId(folderId);
		sampleList.setType(SampleListType.SAMPLE_LIST);
		when(this.sampleListDao.getById(folderId)).thenReturn(sampleList);
		this.sampleListService.updateSampleListFolderName(folderId, "newFolderName");
	}

	@Test(expected = Exception.class)
	public void testUpdateSampleListFolderNameRootFolderNotEditable() throws Exception {
		final Integer folderId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		when(this.sampleListDao.getById(folderId)).thenReturn(rootFolder);
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

		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId(), null))
			.thenReturn(notUniqueFolder);
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

		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId(), null)).thenReturn(null);

		when(this.sampleListDao.saveOrUpdate(folder)).thenThrow(MiddlewareQueryException.class);

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

		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId(), null)).thenReturn(null);

		when(this.sampleListDao.saveOrUpdate(folder)).thenReturn(folder);

		final SampleList savedFolder = this.sampleListService.updateSampleListFolderName(folderId, newFolderName);

		MatcherAssert.assertThat(savedFolder.getListName(), Matchers.equalTo(newFolderName));
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteSampleListFolderNullFolderId() throws Exception {
		this.sampleListService.deleteSampleListFolder(null);
	}

	@Test(expected = Exception.class)
	public void testDeleteSampleListFolderFolderNotExist() throws Exception {
		final Integer folderId = 1;
		when(this.sampleListDao.getById(folderId)).thenReturn(null);
		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = Exception.class)
	public void testDeleteSampleListFolderFolderIsRootFolder() throws Exception {
		final Integer folderId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(folderId);
		rootFolder.setHierarchy(null);
		when(this.sampleListDao.getById(folderId)).thenReturn(rootFolder);
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

		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
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

		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		Mockito.doThrow(new MiddlewareQueryException("")).when(this.sampleListDao).makeTransient(folder);

		this.sampleListService.deleteSampleListFolder(folderId);
	}

	@Test(expected = NullPointerException.class)
	public void testMoveSampleListNullSampleListId() throws Exception {
		this.sampleListService.moveSampleList(null, 1, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testMoveSampleListNullParentFolderId() throws Exception {
		this.sampleListService.moveSampleList(1, null, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMoveSampleListRecursiveRelationship() throws Exception {
		this.sampleListService.moveSampleList(1, 1, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListSampleListIdNotExist() throws Exception {
		final Integer sampleListId = 1;
		when(this.sampleListDao.getById(sampleListId)).thenReturn(null);
		this.sampleListService.moveSampleList(sampleListId, 2, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListSampleListIdIsRootFolder() throws Exception {
		final Integer sampleListId = 1;
		final SampleList rootFolder = new SampleList();
		rootFolder.setId(sampleListId);
		rootFolder.setHierarchy(null);
		when(this.sampleListDao.getById(sampleListId)).thenReturn(rootFolder);
		this.sampleListService.moveSampleList(sampleListId, 2, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListNewParentFolderIdNotExist() throws Exception {
		final Integer sampleListId = 1;
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());

		final Integer folderId = 2;

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(null);

		this.sampleListService.moveSampleList(sampleListId, folderId, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListNewParentFolderIdNotAFolder() throws Exception {
		final Integer sampleListId = 1;
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setType(SampleListType.SAMPLE_LIST);

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);

		this.sampleListService.moveSampleList(sampleListId, folderId, IS_CROP_LIST, PROGRAM_UUID);

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

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(listName, folderId, null)).thenReturn(notUniqueSampleList);

		this.sampleListService.moveSampleList(sampleListId, folderId, IS_CROP_LIST, PROGRAM_UUID);
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

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(listName, folderId, null)).thenReturn(null);

		when(this.sampleListDao.saveOrUpdate(sampleListToMove)).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.moveSampleList(sampleListId, folderId, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testMoveSampleListBetweenRelatives() throws Exception {
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
		folder.setHierarchy(sampleListToMove);

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);
		when(this.sampleListDao.getSampleListByParentAndName(listName, folderId, "")).thenReturn(null);

		when(this.sampleListDao.saveOrUpdate(sampleListToMove)).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.moveSampleList(sampleListId, folderId, IS_CROP_LIST, PROGRAM_UUID);
	}

	@Test()
	public void testMoveSampleListMoveToMoveProgramSampleListToCropListsFolder() throws Exception {
		final Integer sampleListId = 1;
		final String listName = "NAME";
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());
		sampleListToMove.setListName(listName);
		sampleListToMove.setProgramUUID(PROGRAM_UUID);

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setType(SampleListType.FOLDER);

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);

		this.sampleListService.moveSampleList(sampleListId, folderId, true, PROGRAM_UUID);

		Assert.assertNull("The programUUID should be null because the sample list was moved to the crop list",
			sampleListToMove.getProgramUUID());
		Assert.assertEquals(folder, sampleListToMove.getHierarchy());
	}

	@Test()
	public void testMoveSampleListMoveToMoveCropSampleListToProgramListFolder() throws Exception {
		final Integer sampleListId = 1;
		final String listName = "NAME";
		final SampleList sampleListToMove = new SampleList();
		sampleListToMove.setId(sampleListId);
		sampleListToMove.setHierarchy(new SampleList());
		sampleListToMove.setListName(listName);
		sampleListToMove.setProgramUUID(null);

		final Integer folderId = 2;
		final SampleList folder = new SampleList();
		folder.setId(folderId);
		folder.setType(SampleListType.FOLDER);
		folder.setProgramUUID(PROGRAM_UUID);

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleListToMove);
		when(this.sampleListDao.getById(folderId)).thenReturn(folder);

		this.sampleListService.moveSampleList(sampleListId, folderId, false, PROGRAM_UUID);

		Assert.assertEquals("The sample list should inherit the programUUID of its parent folder.", PROGRAM_UUID,
			sampleListToMove.getProgramUUID());
		Assert.assertEquals(folder, sampleListToMove.getHierarchy());
	}

	@Test
	public void testCreateSampleList() {
		final String variableValue = "10";
		final String preferredNameGid = "GID1";

		this.createSampleList(variableValue, preferredNameGid);

		final ArgumentCaptor<SampleList> sampleListArgumentCaptor = ArgumentCaptor.forClass(SampleList.class);
		Mockito.verify(this.sampleListDao).save(sampleListArgumentCaptor.capture());
		Assert.assertEquals(SampleListType.SAMPLE_LIST, sampleListArgumentCaptor.getValue().getType());
		Assert.assertEquals("desc", sampleListArgumentCaptor.getValue().getDescription());
		Assert.assertEquals("notes", sampleListArgumentCaptor.getValue().getNotes());
		Assert.assertEquals(Integer.valueOf(variableValue).longValue(), sampleListArgumentCaptor.getValue().getSamples().size());
	}

	private void createSampleList(final String variableValue, final String preferredNameGid) {
		final int studyId = 1;
		this.study.setId(studyId);

		final CropType cropType = new CropType();
		cropType.setCropName(SampleListServiceImplTest.MAIZE);
		cropType.setPlotCodePrefix(SampleListServiceImplTest.PLOT_CODE_PREFIX);
		final Map<Integer, Integer> mapSampleNumbers = new HashMap<>();
		final Sample sample = new Sample();
		final Integer selectionVariableId = 2;
		final List<Integer> instanceIds = new ArrayList<>();
		instanceIds.add(1);
		final List<ObservationDto> observationDtos = new ArrayList<>();
		final WorkbenchUser user = new WorkbenchUser();
		final SampleList sampleList = new SampleList();

		final List<MeasurementDto> measurementVariableResults = new ArrayList<>();

		final MeasurementDto measurementDto = new MeasurementDto(variableValue);
		measurementVariableResults.add(measurementDto);

		final Integer ndExperimentId = 1;
		final Integer gid = 123;

		final ObservationDto measurement = new ObservationDto(ndExperimentId, preferredNameGid, measurementVariableResults, gid);
		observationDtos.add(measurement);
		final Collection<Integer> experimentIds = CollectionUtils.collect(observationDtos, new Transformer() {

			@Override
			public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getMeasurementId();
			}
		});

		mapSampleNumbers.put(1, 5);

		when(this.studyService.getStudy(studyId)).thenReturn(this.study);
		when(this.studyMeasurements.getSampleObservations(studyId, instanceIds, selectionVariableId)).thenReturn(observationDtos);
		when(this.study.getName()).thenReturn("Maizing_Study");
		when(this.cropService.getCropTypeByName("maize")).thenReturn(cropType);
		when(this.sampleDao.getMaxSampleNumber(experimentIds)).thenReturn(mapSampleNumbers);
		when(this.sampleService
			.buildSample(SampleListServiceImplTest.MAIZE, SampleListServiceImplTest.PLOT_CODE_PREFIX, 1, preferredNameGid,
				Util.getCurrentDate(), ndExperimentId, sampleList, user.getUserid(), Util.getCurrentDate(), user.getUserid(),
				6)).thenReturn(sample);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenReturn(sampleList);
		final SampleList rootSampleList = new SampleList();
		rootSampleList.setType(SampleListType.FOLDER);
		when(this.sampleListDao.getRootSampleList()).thenReturn(rootSampleList);

		this.createSampleListDTO(studyId, selectionVariableId, instanceIds);
	}

	@Test(expected = MiddlewareRequestException.class)
	public void testCreateSampleListLongNameException() throws Exception {

		final String variableValue = "10";
		final String preferredNameGid = RandomStringUtils.randomAlphanumeric(5001);

		this.createSampleList(variableValue, preferredNameGid);
	}

	private SampleListDTO createSampleListDTO(final int datasetId, final Integer selectionVariableId, final List<Integer> instanceIds) {
		final SampleListDTO sampleListDTO = new SampleListDTO();

		sampleListDTO.setCreatedBy(SampleListServiceImplTest.ADMIN);
		sampleListDTO.setCropName("maize");
		sampleListDTO.setListName("SampleListTest");
		sampleListDTO.setDescription("desc");
		sampleListDTO.setInstanceIds(instanceIds);
		sampleListDTO.setNotes("notes");
		sampleListDTO.setSamplingDate(Util.getCurrentDate());

		sampleListDTO.setSelectionVariableId(selectionVariableId);
		sampleListDTO.setDatasetId(datasetId);
		sampleListDTO.setTakenBy(SampleListServiceImplTest.ADMIN);
		sampleListDTO.setProgramUUID("c35c7769-bdad-4c70-a6c4-78c0dbf784e5");
		sampleListDTO.setCreatedDate(Util.getCurrentDate());
		sampleListDTO.setParentId(0);
		this.sampleListService.createSampleList(sampleListDTO);
		return sampleListDTO;
	}

	@Test
	public void testGetSampleLists() {
		final Integer studyId = 1;
		final List<SampleListDTO> list = new ArrayList<>();

		final SampleListDTO sampleListDTO = new SampleListDTO();
		sampleListDTO.setListId(21);
		sampleListDTO.setListName("Test");
		list.add(sampleListDTO);

		when(this.sampleListService.getSampleLists(Arrays.asList(studyId))).thenReturn(list);
		final List<SampleListDTO> result = this.sampleListService.getSampleLists(Arrays.asList(studyId));
		final SampleListDTO dto = result.get(0);
		Assert.assertEquals(1, result.size());
		Assert.assertNotNull(dto);
		Assert.assertEquals(dto.getListId(), sampleListDTO.getListId());
		Assert.assertEquals(dto.getListName(), sampleListDTO.getListName());
	}

	@Test
	public void testGetSampleDetailsDTOs() {

		final Integer userId1 = 1;
		final Integer userId2 = 2;
		final String userFullName1 = "John Doe";
		final String userFullName2 = "Jane Doe";

		final Map<Integer, String> userIDFullNameMap = new HashMap<>();
		userIDFullNameMap.put(userId1, userFullName1);
		userIDFullNameMap.put(userId2, userFullName2);

		final Integer sampleListId = 1;
		final List<SampleDetailsDTO> sampleDetailsDTOS = new ArrayList<>();
		final SampleDetailsDTO sampleDetailsDTO1 = new SampleDetailsDTO();
		sampleDetailsDTO1.setGid(1);
		sampleDetailsDTO1.setEntryNo(1);
		sampleDetailsDTO1.setTakenByUserId(userId1);

		final SampleDetailsDTO sampleDetailsDTO2 = new SampleDetailsDTO();
		sampleDetailsDTO2.setGid(2);
		sampleDetailsDTO2.setEntryNo(2);
		sampleDetailsDTO2.setTakenByUserId(userId2);

		sampleDetailsDTOS.add(sampleDetailsDTO1);
		sampleDetailsDTOS.add(sampleDetailsDTO2);

		when(this.userService.getUserIDFullNameMap(Arrays.asList(userId1, userId2))).thenReturn(userIDFullNameMap);
		when(this.sampleListDao.getSampleDetailsDTO(sampleListId)).thenReturn(sampleDetailsDTOS);

		final List<SampleDetailsDTO> result = this.sampleListService.getSampleDetailsDTOs(sampleListId);

		final SampleDetailsDTO result1 = result.get(0);
		final SampleDetailsDTO result2 = result.get(1);

		Assert.assertEquals(2, result.size());
		Assert.assertNotNull(result1);
		Assert.assertNotNull(result1);
		Assert.assertEquals(result1.getEntryNo(), sampleDetailsDTO1.getEntryNo());
		Assert.assertEquals(result1.getGid(), sampleDetailsDTO1.getGid());
		Assert.assertEquals(userFullName1, result1.getTakenBy());
		Assert.assertEquals(result2.getEntryNo(), sampleDetailsDTO2.getEntryNo());
		Assert.assertEquals(result2.getGid(), sampleDetailsDTO2.getGid());
		Assert.assertEquals("Jane Doe", result2.getTakenBy());
	}

	@Test
	public void testSearchSampleLists() {

		final String searchString = "searchString";
		final String programUUID = "dasdhjashd-djasd-askjdhsa";
		final boolean exactMatch = false;
		final Pageable pageable = null;

		final List<SampleList> expectedResult = new ArrayList<>();
		when(this.sampleListDao.searchSampleLists(searchString, exactMatch, programUUID, pageable)).thenReturn(expectedResult);

		final List<SampleList> result = this.sampleListService.searchSampleLists(searchString, exactMatch, programUUID, pageable);

		Assert.assertSame(expectedResult, result);

	}

	@Test
	public void testCountSamplesByUIDs() {

		final int sampleListId = 1;
		when(this.sampleDao.countBySampleUIDs(Mockito.anySetOf(String.class), Mockito.eq(sampleListId))).thenReturn(1l);

		final long count = this.sampleListService.countSamplesByUIDs(new HashSet<String>(), sampleListId);
		Mockito.verify(this.sampleDao).countBySampleUIDs(Mockito.anySetOf(String.class), Mockito.eq(sampleListId));

		Assert.assertEquals(1l, count);

	}

	@Test
	public void testUpdateSamplePlateInfo() {

		final int sampleListId = 1;
		final Map<String, SamplePlateInfo> samplePlateInfoMap = this.createSamplePlateInfoMap();
		final SampleList sampleList = this.createSampleList();

		for (final Sample sample : sampleList.getSamples()) {
			Assert.assertEquals(null, sample.getPlateId());
			Assert.assertEquals(null, sample.getWell());
		}

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleList);

		this.sampleListService.updateSamplePlateInfo(sampleListId, samplePlateInfoMap);

		final Sample sample1 = sampleList.getSamples().get(0);
		final Sample sample2 = sampleList.getSamples().get(1);

		Assert.assertEquals("PlateId1", sample1.getPlateId());
		Assert.assertEquals("Well1", sample1.getWell());
		Assert.assertEquals("PlateId2", sample2.getPlateId());
		Assert.assertEquals("Well2", sample2.getWell());

		Mockito.verify(this.sampleListDao).saveOrUpdate(sampleList);

	}

	@Test
	public void testUpdateASamplePlateInfoFromSampleList() {

		final int sampleListId = 1;
		final Map<String, SamplePlateInfo> samplePlateInfoMap = new HashMap<>();
		final String plantBusinessKey2 = "BusinessKey2";
		final SamplePlateInfo plateInfo2 = new SamplePlateInfo();
		plateInfo2.setPlateId("PlateId2");
		plateInfo2.setWell("Well2");

		samplePlateInfoMap.put(plantBusinessKey2, plateInfo2);

		final SampleList sampleList = this.createSampleList();

		for (final Sample sample : sampleList.getSamples()) {
			Assert.assertEquals(null, sample.getPlateId());
			Assert.assertEquals(null, sample.getWell());
		}

		when(this.sampleListDao.getById(sampleListId)).thenReturn(sampleList);

		this.sampleListService.updateSamplePlateInfo(sampleListId, samplePlateInfoMap);

		final Sample sample = sampleList.getSamples().get(1);

		Assert.assertEquals("PlateId2", sample.getPlateId());
		Assert.assertEquals("Well2", sample.getWell());

		Mockito.verify(this.sampleListDao).saveOrUpdate(sampleList);

	}

	private SampleList createSampleList() {

		final SampleList sampleList = new SampleList();
		final List<Sample> samples = new ArrayList<>();
		samples.add(this.createSample(sampleList, "BusinessKey1"));
		samples.add(this.createSample(sampleList, "BusinessKey2"));
		sampleList.setSamples(samples);
		return sampleList;
	}

	private Sample createSample(final SampleList sampleList, final String businessKey) {
		final WorkbenchUser createdBy = new WorkbenchUser();
		final Sample sample = SampleTestDataInitializer.createSample(sampleList, createdBy.getUserid());
		sample.setSampleBusinessKey(businessKey);
		return sample;
	}

	private Map<String, SamplePlateInfo> createSamplePlateInfoMap() {
		final Map<String, SamplePlateInfo> samplePlateInfoMap = new HashMap<>();

		final String plantBusinessKey1 = "BusinessKey1";
		final String plantBusinessKey2 = "BusinessKey2";

		final SamplePlateInfo plateInfo1 = new SamplePlateInfo();
		plateInfo1.setPlateId("PlateId1");
		plateInfo1.setWell("Well1");
		final SamplePlateInfo plateInfo2 = new SamplePlateInfo();
		plateInfo2.setPlateId("PlateId2");
		plateInfo2.setWell("Well2");

		samplePlateInfoMap.put(plantBusinessKey1, plateInfo1);
		samplePlateInfoMap.put(plantBusinessKey2, plateInfo2);

		return samplePlateInfoMap;

	}
}
