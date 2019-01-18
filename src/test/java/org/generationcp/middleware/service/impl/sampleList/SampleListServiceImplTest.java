package org.generationcp.middleware.service.impl.sampleList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.data.initializer.SampleTestDataInitializer;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.MeasurementDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
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

	@Mock
	private HibernateSessionProvider session;

	@Mock
	private SampleListDao sampleListDao;

	@Mock
	private SampleDao sampleDao;

	@Mock
	private UserDAO userDAO;

	@Mock
	private StudyDataManager studyService;

	@Mock
	private StudyMeasurements studyMeasurements;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private PlantDao plantDao;

	@Mock
	private Study study;

	@Mock
	private SampleService sampleService;

	private SampleListServiceImpl sampleListService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.sampleListService = new SampleListServiceImpl(this.session);
		this.sampleListService.setStudyMeasurements(this.studyMeasurements);
		this.sampleListService.setWorkbenchDataManager(this.workbenchDataManager);
		this.sampleListService.setSampleService(this.sampleService);

		final DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		this.sampleListService.setDaoFactory(daoFactory);
		when(daoFactory.getUserDao()).thenReturn(this.userDAO);
		when(daoFactory.getSampleDao()).thenReturn(this.sampleDao);
		when(daoFactory.getSampleListDao()).thenReturn(this.sampleListDao);
		when(daoFactory.getPlantDao()).thenReturn(this.plantDao);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderFolderNull() throws Exception {
		final User createdBy = new User();
		this.sampleListService.createSampleListFolder(null, 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderParentIdNull() throws Exception {
		final User createdBy = new User();
		this.sampleListService.createSampleListFolder("name", null, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderCreatedByNull() throws Exception {
		this.sampleListService.createSampleListFolder("name", 1, null, PROGRAM_UUID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSampleListFolderFolderNameEmpty() throws Exception {
		final User createdBy = new User();
		createdBy.setName("superadmin");
		this.sampleListService.createSampleListFolder("", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSampleListFolderCreatedByEmpty() throws Exception {
		this.sampleListService.createSampleListFolder("4", 1, null, PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderParentListNotExist() throws Exception {
		final User createdBy = new User();
		createdBy.setUserid(org.mockito.Matchers.anyInt());
		when(this.sampleListDao.getById(1)).thenReturn(null);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderFolderNameNotUnique() throws Exception {
		final User createdBy = new User();
		final SampleList notUniqueValue = new SampleList();
		final SampleList parentFolder = new SampleList();
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(notUniqueValue);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
	}

	@Test(expected = Exception.class)
	public void testCreateSampleListFolderParentListNotAFolder() throws Exception {
		final User createdBy = new User();
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
		final User createdBy = new User();
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
		final Integer savedObject = this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
		MatcherAssert.assertThat(sampleFolder.getId(), Matchers.equalTo(savedObject));
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testCreateSampleListFolderDBException() throws Exception {
		final User createdBy = new User();
		createdBy.setUserid(org.mockito.Matchers.anyInt());
		createdBy.setName("superadmin");
		final SampleList parentFolder = new SampleList();
		parentFolder.setType(SampleListType.FOLDER);
		when(this.sampleListDao.getById(1)).thenReturn(parentFolder);
		when(this.sampleListDao.getSampleListByParentAndName("4", 1, null)).thenReturn(null);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenThrow(MiddlewareQueryException.class);
		this.sampleListService.createSampleListFolder("4", 1, createdBy.getName(), PROGRAM_UUID);
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
		final int studyId = 1;
		this.study.setId(studyId);
		final CropType cropType = new CropType();
		cropType.setCropName(SampleListServiceImplTest.MAIZE);
		cropType.setPlotCodePrefix(SampleListServiceImplTest.PLOT_CODE_PREFIX);
		final Map<Integer, Integer> mapPlantNumbers = new HashMap<>();
		final Sample sample = new Sample();
		final Integer selectionVariableId = 2;
		final List<Integer> instanceIds = new ArrayList<>();
		instanceIds.add(1);
		final List<ObservationDto> observationDtos = new ArrayList<>();
		final User user = new User();
		final SampleList sampleList = new SampleList();

		final List<MeasurementDto> measurementVariableResults = new ArrayList<>();

		final String variableValue = "10";
		final MeasurementDto measurementDto = new MeasurementDto(variableValue);
		measurementVariableResults.add(measurementDto);

		final String preferredNameGid = "GID1";
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

		mapPlantNumbers.put(1, 5);

		when(this.studyService.getStudy(studyId)).thenReturn(this.study);
		when(this.studyMeasurements.getSampleObservations(studyId, instanceIds, selectionVariableId)).thenReturn(observationDtos);
		when(this.study.getName()).thenReturn("Maizing_Study");
		when(this.workbenchDataManager.getCropTypeByName("maize")).thenReturn(cropType);
		when(this.plantDao.getMaxPlantNumber(experimentIds)).thenReturn(mapPlantNumbers);
		when(this.sampleService
				.buildSample(SampleListServiceImplTest.MAIZE, SampleListServiceImplTest.PLOT_CODE_PREFIX, 1, 1, preferredNameGid,
						Util.getCurrentDate(), ndExperimentId, sampleList, user, Util.getCurrentDate(), user)).thenReturn(sample);
		when(this.sampleListDao.save(org.mockito.Matchers.any(SampleList.class))).thenReturn(sampleList);
		final SampleList rootSampleList = new SampleList();
		rootSampleList.setType(SampleListType.FOLDER);
		when(this.sampleListDao.getRootSampleList()).thenReturn(rootSampleList);

		this.createSampleListDTO(studyId, selectionVariableId, instanceIds);

		final ArgumentCaptor<SampleList> sampleListArgumentCaptor = ArgumentCaptor.forClass(SampleList.class);
		Mockito.verify(this.sampleListDao).save(sampleListArgumentCaptor.capture());
		Assert.assertEquals(SampleListType.SAMPLE_LIST, sampleListArgumentCaptor.getValue().getType());
		Assert.assertEquals("desc", sampleListArgumentCaptor.getValue().getDescription());
		Assert.assertEquals("notes", sampleListArgumentCaptor.getValue().getNotes());
		Assert.assertEquals(Integer.valueOf(variableValue).longValue(), sampleListArgumentCaptor.getValue().getSamples().size());
	}

	private SampleListDTO createSampleListDTO(final int studyId, final Integer selectionVariableId, final List<Integer> instanceIds) {
		final SampleListDTO sampleListDTO = new SampleListDTO();

		sampleListDTO.setCreatedBy(SampleListServiceImplTest.ADMIN);
		sampleListDTO.setCropName("maize");
		sampleListDTO.setListName("SampleListTest");
		sampleListDTO.setDescription("desc");
		sampleListDTO.setInstanceIds(instanceIds);
		sampleListDTO.setNotes("notes");
		sampleListDTO.setSamplingDate(Util.getCurrentDate());

		sampleListDTO.setSelectionVariableId(selectionVariableId);
		sampleListDTO.setStudyId(studyId);
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

		when(this.sampleListService.getSampleLists(studyId)).thenReturn(list);
		final List<SampleListDTO> result = this.sampleListService.getSampleLists(studyId);
		final SampleListDTO dto = result.get(0);
		Assert.assertEquals(1, result.size());
		Assert.assertNotNull(dto);
		Assert.assertEquals(dto.getListId(), sampleListDTO.getListId());
		Assert.assertEquals(dto.getListName(), sampleListDTO.getListName());
	}

	@Test
	public void testGetSampleDetailsDTOs() {
		final Integer sampleListId = 1;
		final List<SampleDetailsDTO> list = new ArrayList<>();
		final SampleDetailsDTO dto0 = new SampleDetailsDTO();
		dto0.setGid(1);
		dto0.setEntryNo(1);

		final SampleDetailsDTO dto1 = new SampleDetailsDTO();
		dto1.setGid(2);
		dto1.setEntryNo(2);

		list.add(dto0);
		list.add(dto1);
		when(this.sampleListService.getSampleDetailsDTOs(sampleListId)).thenReturn(list);

		final List<SampleDetailsDTO> result = this.sampleListService.getSampleDetailsDTOs(sampleListId);

		final SampleDetailsDTO result0 = result.get(0);
		final SampleDetailsDTO result1 = result.get(1);

		Assert.assertEquals(2, result.size());
		Assert.assertNotNull(result0);
		Assert.assertNotNull(result1);
		Assert.assertEquals(result0.getEntryNo(), dto0.getEntryNo());
		Assert.assertEquals(result0.getGid(), dto0.getGid());
		Assert.assertEquals(result1.getEntryNo(), dto1.getEntryNo());
		Assert.assertEquals(result1.getGid(), dto1.getGid());
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
		when(sampleDao.countBySampleUIDs(Mockito.anySetOf(String.class), Mockito.eq(sampleListId))).thenReturn(1l);

		final long count = this.sampleListService.countSamplesByUIDs(new HashSet<String>(), sampleListId);
		Mockito.verify(sampleDao).countBySampleUIDs(Mockito.anySetOf(String.class), Mockito.eq(sampleListId));

		Assert.assertEquals(1l, count);

	}

	@Test
	public void testUpdateSamplePlateInfo() {

		final int sampleListId = 1;
		final Map<String, SamplePlateInfo> samplePlateInfoMap = this.createSamplePlateInfoMap();
		final SampleList sampleList = this.createSampleList();

		for(Sample sample: sampleList.getSamples()){
			Assert.assertEquals(null, sample.getPlateId());
			Assert.assertEquals(null, sample.getWell());
		}
		
		when(sampleListDao.getById(sampleListId)).thenReturn(sampleList);

		this.sampleListService.updateSamplePlateInfo(sampleListId, samplePlateInfoMap);

		final Sample sample1 = sampleList.getSamples().get(0);
		final Sample sample2 = sampleList.getSamples().get(1);

		Assert.assertEquals("PlateId1", sample1.getPlateId());
		Assert.assertEquals("Well1", sample1.getWell());
		Assert.assertEquals("PlateId2", sample2.getPlateId());
		Assert.assertEquals("Well2", sample2.getWell());

		Mockito.verify(sampleListDao).saveOrUpdate(sampleList);

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

		for(Sample sample: sampleList.getSamples()){
			Assert.assertEquals(null, sample.getPlateId());
			Assert.assertEquals(null, sample.getWell());
		}

		when(sampleListDao.getById(sampleListId)).thenReturn(sampleList);

		this.sampleListService.updateSamplePlateInfo(sampleListId, samplePlateInfoMap);

		final Sample sample = sampleList.getSamples().get(1);

		Assert.assertEquals("PlateId2", sample.getPlateId());
		Assert.assertEquals("Well2", sample.getWell());

		Mockito.verify(sampleListDao).saveOrUpdate(sampleList);

	}
	

	private SampleList createSampleList() {

		final SampleList sampleList = new SampleList();
		final List<Sample> samples = new ArrayList<>();
		samples.add(createSample(sampleList, "BusinessKey1"));
		samples.add(createSample(sampleList, "BusinessKey2"));
		sampleList.setSamples(samples);
		return sampleList;
	}

	private Sample createSample(final SampleList sampleList, final String businessKey) {
		final User createdBy = new User();
		final Plant plant = new Plant();
		plant.setPlantBusinessKey(businessKey);
		final Sample sample = SampleTestDataInitializer.createSample(sampleList, plant, createdBy);
		sample.setSampleBusinessKey(businessKey);
		return sample;
	}


	private Map<String,SamplePlateInfo> createSamplePlateInfoMap() {
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
