
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional (propagation = Propagation.REQUIRED)
public class SampleListServiceImpl implements SampleListService {

	private SampleListDao sampleListDao;

	private UserDAO userDao;

	private SampleDao sampleDao;

	private StudyMeasurements studyMeasurements;

	private PlantDao plantDao;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private StudyDataManager studyService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public SampleListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(sessionProvider.getSession());
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());
		this.userDao = new UserDAO();
		this.userDao.setSession(sessionProvider.getSession());
		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());
		this.studyMeasurements = new StudyMeasurements(sessionProvider.getSession());
		this.sampleService = new SampleServiceImpl(sessionProvider);
		this.studyService = new StudyDataManagerImpl(sessionProvider);
		this.workbenchDataManager = new WorkbenchDataManagerImpl(sessionProvider);
	}

	public void setSampleListDao(final SampleListDao sampleListDao) {
		this.sampleListDao = sampleListDao;
	}

	public void setUserDao (final UserDAO userDao) {
		this.userDao = userDao;
	}

	@Override
	public SampleList createSampleList(final SampleListDTO sampleListDTO) {

		Preconditions.checkArgument(sampleListDTO.getInstanceIds() != null, "The Instance List must not be null");
		Preconditions.checkArgument(!sampleListDTO.getInstanceIds().isEmpty(), "The Instance List must not be empty");
		Preconditions.checkArgument(sampleListDTO.getProgramUUID() != null, "The programUUID must not be null");

		Preconditions.checkNotNull(sampleListDTO.getSelectionVariableId(), "The Selection Variable Id must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getStudyId(), "The Study Id must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getListName(), "The List Name must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getCreatedDate(), "The Created Date must not be empty");



		try {
			final SampleList sampleList = new SampleList();
			User takenBy = null;
			sampleList.setCreatedDate(sampleListDTO.getCreatedDate());
			User user = this.userDao.getUserByUserName(sampleListDTO.getCreatedBy());
			sampleList.setProgramUUID(sampleListDTO.getProgramUUID());
			sampleList.setCreatedBy(user);
			sampleList.setDescription(sampleListDTO.getDescription());
			sampleList.setListName(sampleListDTO.getListName());
			sampleList.setNotes(sampleListDTO.getNotes());
			sampleList.setType(SampleListType.SAMPLE_LIST);
			final SampleList parent;

			if (sampleListDTO.getParentId() == null || sampleListDTO.getParentId().equals(0)) {
				parent = this.sampleListDao.getRootSampleList();
			} else {
				parent = this.sampleListDao.getParentSampleFolder(sampleListDTO.getParentId());
			}

			Preconditions.checkState(parent.isFolder(), "The parent id must not be a list");

			if (this.sampleListDao.getSampleListByParentAndName(sampleListDTO.getListName(), parent.getId()) != null) {
				throw new MiddlewareQueryException("List name should be unique within the same directory");
			}

			sampleList.setHierarchy(parent);

			final List<ObservationDto> observationDtos = this.studyMeasurements
				.getSampleObservations(sampleListDTO.getStudyId(), sampleListDTO.getInstanceIds(), sampleListDTO.getSelectionVariableId());

			Preconditions.checkArgument(!observationDtos.isEmpty(), "The observation list must not be empty");

			if (!sampleListDTO.getTakenBy().isEmpty()) {
				takenBy = this.userDao.getUserByUserName(sampleListDTO.getTakenBy());
			}

			final String cropPrefix = this.workbenchDataManager.getCropTypeByName(sampleListDTO.getCropName()).getPlotCodePrefix();

			final Map<Integer, Integer> maxPlantNumbers = this.getMaxPlantNumber(observationDtos);
			final List<Sample> samples = new ArrayList<>();

			for (final ObservationDto observationDto : observationDtos) {

				final BigInteger sampleNumber = new BigInteger(observationDto.getVariableMeasurements().get(0).getVariableValue());
				Integer count = maxPlantNumbers.get(observationDto.getMeasurementId());
				if (count == null) {
					// counter should be start in 1
					count = 0;
				}
				for (double i = 0; i < sampleNumber.doubleValue(); i++) {
					count++;
					final Sample sample = this.sampleService
						.buildSample(sampleListDTO.getCropName(), cropPrefix, count, observationDto.getDesignation(),
							sampleListDTO.getSamplingDate(), observationDto.getMeasurementId(), sampleList, user, sampleListDTO.getCreatedDate(), takenBy);
					samples.add(sample);
				}
			}

			sampleList.setSamples(samples);
			return this.sampleListDao.save(sampleList);
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in createSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	private Map<Integer, Integer> getMaxPlantNumber(final List<ObservationDto> observationDtos) {

		@SuppressWarnings("unchecked")
		final Collection<Integer> experimentIds = CollectionUtils.collect(observationDtos, new Transformer() {

			@Override
			public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getMeasurementId();
			}
		});

		return this.plantDao.getMaxPlantNumber(experimentIds);
	}

	/**
	 * Create a sample list folder
	 * Sample List folder name must be unique across the elements in the parent folder
	 *
	 * @param folderName
	 * @param parentId
	 * @param createdBy
	 * @param programUUID
	 * @return Sample List
	 * @throws Exception
	 */
	@Override
	public Integer createSampleListFolder(final String folderName, final Integer parentId, final String createdBy,  final String programUUID) throws Exception {
		Preconditions.checkNotNull(folderName);
		Preconditions.checkNotNull(parentId);
		Preconditions.checkNotNull(createdBy);
		Preconditions.checkNotNull(programUUID);
		Preconditions.checkArgument(!folderName.isEmpty(), "folderName can not be empty");
		Preconditions.checkArgument(!createdBy.isEmpty(), "createdBy can not be empty");
		Preconditions.checkArgument(!programUUID.isEmpty(), "programUUID can not be empty");

		final SampleList parentList;
		if (0 == parentId) {
			parentList = this.sampleListDao.getRootSampleList();
		} else {
			parentList = this.sampleListDao.getById(parentId);

		}
		if (parentList == null) {
			throw new Exception("Parent Folder does not exist");
		}

		if (!parentList.isFolder()) {
			throw new Exception("Specified parentID is not a folder");
		}

		if (this.sampleListDao.getSampleListByParentAndName(folderName, parentList.getId()) != null) {
			throw new Exception("Folder name should be unique within the same directory");
		}
		final SampleList sampleFolder = new SampleList();
		sampleFolder.setCreatedDate(new Date());
		sampleFolder.setCreatedBy(this.userDao.getUserByUserName(createdBy));
		sampleFolder.setDescription(null);
		sampleFolder.setListName(folderName);
		sampleFolder.setNotes(null);
		sampleFolder.setHierarchy(parentList);
		sampleFolder.setType(SampleListType.FOLDER);
		sampleFolder.setProgramUUID(programUUID);
		return this.sampleListDao.save(sampleFolder).getId();
	}

	/**
	 * Update sample list folder name
	 * New folder name should be unique across the elements in the parent folder
	 *
	 * @param folderId
	 * @param newFolderName
	 * @return SampleList
	 * @throws Exception
	 */
	@Override
	public SampleList updateSampleListFolderName(final Integer folderId, final String newFolderName) throws Exception {
		Preconditions.checkNotNull(folderId);
		Preconditions.checkNotNull(newFolderName);
		Preconditions.checkArgument(!newFolderName.isEmpty(), "newFolderName can not be empty");

		final SampleList folder = this.sampleListDao.getById(folderId);

		if (folder == null) {
			throw new Exception("Folder does not exist");
		}

		if (!SampleListType.FOLDER.equals(folder.getType())) {
			throw new Exception("Specified folderID is not a folder");
		}

		if (folder.getHierarchy() == null) {
			throw new Exception("Root folder name is not editable");
		}

		if (this.sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId()) != null) {
			throw new Exception("Folder name should be unique within the same directory");
		}

		folder.setListName(newFolderName);

		return this.sampleListDao.saveOrUpdate(folder);
	}

	/**
	 * Move a folder to another folder
	 * sampleListId must exist (could be a folder or a list), newParentFolderId must exist and must be a folder
	 * newParentFolderId folder must not contain another sample list or folder with the name that the one that needs to be moved
	 *
	 * @param sampleListId
	 * @param newParentFolderId
	 * @return SampleList
	 * @throws Exception
	 */
	public SampleList moveSampleList(final Integer sampleListId, final Integer newParentFolderId) throws Exception {
		Preconditions.checkNotNull(sampleListId);
		Preconditions.checkNotNull(newParentFolderId);
		Preconditions.checkArgument(!sampleListId.equals(newParentFolderId), "Arguments can not have the same value");

		final SampleList listToMove = this.sampleListDao.getById(sampleListId);

		if (listToMove == null) {
			throw new Exception("sampleList does not exist");
		}

		if (listToMove.getHierarchy() == null) {
			throw new Exception("Root folder can not me moved");
		}

		final SampleList newParentFolder;
		if (0 == newParentFolderId) {
			newParentFolder = this.sampleListDao.getRootSampleList();
		} else {
			newParentFolder = this.sampleListDao.getById(newParentFolderId);

		}

		if (newParentFolder == null) {
			throw new Exception("Specified newParentFolderId does not exist");
		}

		if (!newParentFolder.isFolder()) {
			throw new Exception("Specified newParentFolderId is not a folder");
		}

		final SampleList uniqueSampleListName =
				this.sampleListDao.getSampleListByParentAndName(listToMove.getListName(), newParentFolderId);

		if (uniqueSampleListName != null) {
			throw new Exception("Folder name should be unique within the same directory");
		}

		if (isDescendant(listToMove, newParentFolder)) {
			throw new Exception("You can not move list because are relatives with parent folder");
		}

		listToMove.setHierarchy(newParentFolder);

		return this.sampleListDao.saveOrUpdate(listToMove);
	}

	/**
	 * Delete a folder
	 * Folder ID must exist and it can not contain any child
	 *
	 * @param folderId
	 * @throws Exception
	 */
	@Override
	public void deleteSampleListFolder(final Integer folderId) throws Exception {
		Preconditions.checkNotNull(folderId);
		final SampleList folder = this.sampleListDao.getById(folderId);
		if (folder == null) {
			throw new Exception("Folder does not exist");
		}
		if (!folder.isFolder()) {
			throw new Exception("Specified folderID is not a folder");
		}

		if (folder.getHierarchy() == null) {
			throw new Exception("Root folder can not be deleted");
		}
		if (folder.getChildren() != null && !folder.getChildren().isEmpty()) {
			throw new Exception("Folder has children and cannot be deleted");
		}
		this.sampleListDao.makeTransient(folder);
	}

	@Override
	public List<SampleList> getAllTopLevelLists(final String programUUID) {
		return this.getSampleListDao().getAllTopLevelLists(programUUID);
	}

	@Override
	public SampleList getSampleListByListId(final Integer listId) {
		return this.getSampleListDao().getById(listId);
	}

	@Override
	public SampleList getLastSavedSampleListByUserId(final Integer userId, final String programUuid) {
		return this.getSampleListDao().getLastCreatedByUserID(userId, programUuid);
	}

	@Override
	public Map<Integer, GermplasmFolderMetadata> getFolderMetadata(List<SampleList> sampleLists) {
		final List<Integer> folderIdsToRetrieveFolderCount = getFolderIdsFromSampleList(sampleLists);
		return this.getSampleListDao().getSampleFolderMetadata(folderIdsToRetrieveFolderCount);
	}

	private List<Integer> getFolderIdsFromSampleList(List<SampleList> listIds) {
		final List<Integer> folderIdsToRetrieveFolderCount = new ArrayList<>();
		for (final SampleList parentList : listIds) {
			if(parentList.isFolder()) {
				folderIdsToRetrieveFolderCount.add(parentList.getId());
			}
		}
		return folderIdsToRetrieveFolderCount;
	}

	protected boolean isDescendant(SampleList list, SampleList of) {
		if (of.getHierarchy() == null) {
			return false;
		}
		if (of.getHierarchy().equals(list)) {
			return true;
		} else {
			return isDescendant(list, of.getHierarchy());
		}
	}

	@Override
	public List<SampleListDTO> getSampleLists(final Integer trialId) {
		return this.sampleListDao.getSampleLists(trialId);
	}

	@Override
	public SampleList getSampleList(final Integer sampleListId) {
		return this.sampleListDao.getById(sampleListId);
	}

	@Override
	public List<SampleDetailsDTO> getSampleDetailsDTOs(final Integer sampleListId) {
		return this.sampleListDao.getSampleDetailsDTO(sampleListId);
	}

	@Override
	public List<SampleList> getAllSampleTopLevelLists(final String programUUID) {
		return this.sampleListDao.getAllTopLevelLists(programUUID);
	}

	@Override
	public List<SampleList> getSampleListByParentFolderIdBatched(final Integer parentId, final String programUUID, final int batchSize) {
		return this.getSampleListDao().getByParentFolderId(parentId, programUUID);
	}

	public void setStudyMeasurements(final StudyMeasurements studyMeasurements) {
		this.studyMeasurements = studyMeasurements;
	}

	public void setStudyService(final StudyDataManager studyService) {
		this.studyService = studyService;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setPlantDao(final PlantDao plantDAO) {
		this.plantDao = plantDAO;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public  void setSampleDao(final SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}

	public SampleListDao getSampleListDao() {
		return sampleListDao;
	}

	public UserDAO getUserDao() {
		return userDao;
	}

	public SampleDao getSampleDao() {
		return sampleDao;
	}

	public StudyMeasurements getStudyMeasurements() {
		return studyMeasurements;
	}

	public PlantDao getPlantDao() {
		return plantDao;
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public StudyDataManager getStudyService() {
		return studyService;
	}

	public WorkbenchDataManager getWorkbenchDataManager() {
		return workbenchDataManager;
	}
}
