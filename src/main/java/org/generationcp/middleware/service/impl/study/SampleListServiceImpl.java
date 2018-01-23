package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
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
@Transactional(propagation = Propagation.REQUIRED)
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

	public void setUserDao(final UserDAO userDao) {
		this.userDao = userDao;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SampleList createSampleList(final SampleListDTO sampleListDTO) {

		Preconditions.checkArgument(sampleListDTO.getInstanceIds() != null, "The Instance List must not be null");
		Preconditions.checkArgument(!sampleListDTO.getInstanceIds().isEmpty(), "The Instance List must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getSelectionVariableId(), "The Selection Variable Id must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getStudyId(), "The Study Id must not be empty");
		Preconditions.checkNotNull(sampleListDTO.getListName(), "The List Name must not be empty");
		Preconditions.checkArgument(StringUtils.isNotBlank(sampleListDTO.getListName()), "The List Name must not be empty");
		Preconditions.checkArgument(sampleListDTO.getListName().length() <= 100, "List Name must not exceed 100 characters");
		Preconditions.checkNotNull(sampleListDTO.getCreatedDate(), "The Created Date must not be empty");
		Preconditions.checkArgument(StringUtils.isBlank(sampleListDTO.getDescription()) || sampleListDTO.getDescription().length() <= 255,
				"List Description must not exceed 255 characters");
		Preconditions.checkArgument(StringUtils.isBlank(sampleListDTO.getNotes()) || sampleListDTO.getNotes().length() <= 65535,
				"Notes must not exceed 65535 characters");

		try {
			final SampleList sampleList = new SampleList();
			User takenBy = null;
			sampleList.setCreatedDate(sampleListDTO.getCreatedDate());
			final User user = this.userDao.getUserByUserName(sampleListDTO.getCreatedBy());
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

			Preconditions.checkArgument(parent.isFolder(), "The parent id must not be a list");

			final SampleList uniqueSampleListName = this.sampleListDao
					.getSampleListByParentAndName(sampleListDTO.getListName(), parent.getId(), sampleListDTO.getProgramUUID());

			Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

			sampleList.setHierarchy(parent);

			final List<ObservationDto> observationDtos = this.studyMeasurements
					.getSampleObservations(sampleListDTO.getStudyId(), sampleListDTO.getInstanceIds(),
							sampleListDTO.getSelectionVariableId());

			Preconditions.checkArgument(!observationDtos.isEmpty(), "The observation list must not be empty");

			if (!sampleListDTO.getTakenBy().isEmpty()) {
				takenBy = this.userDao.getUserByUserName(sampleListDTO.getTakenBy());
			}

			final String cropPrefix = this.workbenchDataManager.getCropTypeByName(sampleListDTO.getCropName()).getPlotCodePrefix();
			final Collection<Integer> experimentIds = this.getExperimentIds(observationDtos);
			final Collection<Integer> gids = this.getGids(observationDtos);
			final Map<Integer, Integer> maxPlantNumbers = this.getMaxPlantNumber(experimentIds);
			final Map<Integer, Integer> maxSequenceNumberByGID = this.getMaxSequenceNumberByGID(gids);
			final List<Sample> samples = new ArrayList<>();

			for (final ObservationDto observationDto : observationDtos) {
				/*
				 * maxSequence is the maximum number among samples in the same GID. If there is no sample for Gid, the sequence starts in 1.
				 */

				final Integer key = observationDto.getGid();
				Integer maxSequence = maxSequenceNumberByGID.get(key);

				if (maxSequence == null) {
					maxSequence = 0;
					maxSequenceNumberByGID.put(key, maxSequence);
				}

				final BigInteger sampleNumber = new BigInteger(observationDto.getVariableMeasurements().get(0).getVariableValue());
				Integer plantNumber = maxPlantNumbers.get(observationDto.getMeasurementId());
				if (plantNumber == null) {
					// counter should be start in 1
					plantNumber = 0;
				}
				for (double i = 0; i < sampleNumber.doubleValue(); i++) {

					plantNumber++;
					maxSequence++;
					final String sampleName = observationDto.getDesignation() + ':' + String.valueOf(maxSequence);

					final Sample sample = this.sampleService
							.buildSample(sampleListDTO.getCropName(), cropPrefix, plantNumber, sampleName, sampleListDTO.getSamplingDate(),
									observationDto.getMeasurementId(), sampleList, user, sampleListDTO.getCreatedDate(), takenBy);
					samples.add(sample);
				}

				maxSequenceNumberByGID.put(key, maxSequence);
			}

			sampleList.setSamples(samples);
			return this.sampleListDao.save(sampleList);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in createSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	private Map<Integer, Integer> getMaxPlantNumber(final Collection<Integer> experimentIds) {
		return this.plantDao.getMaxPlantNumber(experimentIds);
	}

	@SuppressWarnings("unchecked")
	private Collection<Integer> getExperimentIds(final List<ObservationDto> observationDtos) {
		return CollectionUtils.collect(observationDtos, new Transformer() {

			@Override
			public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getMeasurementId();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private Collection<Integer> getGids(final List<ObservationDto> observationDtos) {
		return CollectionUtils.collect(observationDtos, new Transformer() {

			@Override
			public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getGid();
			}
		});
	}

	private Map<Integer, Integer> getMaxSequenceNumberByGID(final Collection<Integer> gids) {
		return this.plantDao.getMaxSequenceNumber(gids);
	}

	/**
	 * Create a sample list folder Sample List folder name must be unique across the elements in the parent folder
	 *
	 * @param folderName
	 * @param parentId
	 * @param createdBy
	 * @param programUUID
	 * @return Sample List
	 * @throws Exception
	 */
	@Override
	public Integer createSampleListFolder(final String folderName, final Integer parentId, final User createdBy, final String programUUID) {
		Preconditions.checkNotNull(folderName);
		Preconditions.checkNotNull(parentId);
		Preconditions.checkNotNull(createdBy, "createdBy can not be empty");
		Preconditions.checkNotNull(programUUID);
		Preconditions.checkArgument(!folderName.isEmpty(), "folderName can not be empty");
		Preconditions.checkArgument(!programUUID.isEmpty(), "programUUID can not be empty");

		final SampleList parentList;
		if (0 == parentId) {
			parentList = this.sampleListDao.getRootSampleList();
		} else {
			parentList = this.sampleListDao.getById(parentId);

		}

		Preconditions.checkArgument(parentList != null, "Parent Folder does not exist");
		Preconditions.checkArgument(parentList.isFolder(), "Specified parentID is not a folder");

		final SampleList uniqueSampleListName =
				this.sampleListDao.getSampleListByParentAndName(folderName, parentList.getId(), programUUID);

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

		try {
			final SampleList sampleFolder = new SampleList();
			sampleFolder.setCreatedDate(new Date());
			sampleFolder.setCreatedBy(createdBy);
			sampleFolder.setDescription(null);
			sampleFolder.setListName(folderName);
			sampleFolder.setNotes(null);
			sampleFolder.setHierarchy(parentList);
			sampleFolder.setType(SampleListType.FOLDER);
			sampleFolder.setProgramUUID(programUUID);
			return this.sampleListDao.save(sampleFolder).getId();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in createSampleListFolder in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	/**
	 * Update sample list folder name New folder name should be unique across the elements in the parent folder
	 *
	 * @param folderId
	 * @param newFolderName
	 * @return SampleList
	 * @throws Exception
	 */
	@Override
	public SampleList updateSampleListFolderName(final Integer folderId, final String newFolderName) {
		Preconditions.checkNotNull(folderId);
		Preconditions.checkNotNull(newFolderName);
		Preconditions.checkArgument(!newFolderName.isEmpty(), "newFolderName can not be empty");

		final SampleList folder = this.sampleListDao.getById(folderId);

		Preconditions.checkArgument(folder != null, "Folder does not exist");
		Preconditions.checkArgument(SampleListType.FOLDER.equals(folder.getType()), "Specified folderID is not a folder");
		Preconditions.checkArgument(folder.getHierarchy() != null, "Root folder name is not editable");

		final SampleList uniqueSampleListName =
				this.sampleListDao.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId(), folder.getProgramUUID());

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

		folder.setListName(newFolderName);
		try {
			return this.sampleListDao.saveOrUpdate(folder);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in updateSampleListFolderName in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	/**
	 * Move a folder to another folder sampleListId must exist (could be a folder or a list), newParentFolderId must exist and must be a
	 * folder newParentFolderId folder must not contain another sample list or folder with the name that the one that needs to be moved
	 *
	 * @param sampleListId
	 * @param newParentFolderId
	 * @param isCropList
	 * @return SampleList
	 * @throws Exception
	 */
	@Override
	public SampleList moveSampleList(final Integer sampleListId, final Integer newParentFolderId, final boolean isCropList) {
		Preconditions.checkNotNull(sampleListId);
		Preconditions.checkNotNull(newParentFolderId);
		Preconditions.checkArgument(!sampleListId.equals(newParentFolderId), "Arguments can not have the same value");

		final SampleList listToMove = this.sampleListDao.getById(sampleListId);

		Preconditions.checkArgument(listToMove != null, "sampleList does not exist");
		Preconditions.checkArgument(listToMove.getHierarchy() != null, "Root folder can not me moved");

		final SampleList newParentFolder;
		if (0 == newParentFolderId) {
			newParentFolder = this.sampleListDao.getRootSampleList();
		} else {
			newParentFolder = this.sampleListDao.getById(newParentFolderId);
		}

		Preconditions.checkArgument(newParentFolder != null, "Specified newParentFolderId does not exist");
		Preconditions.checkArgument(newParentFolder.isFolder(), "Specified newParentFolderId is not a folder");

		// if the list is moved to the crop list, set the program uuid to null so that
		// it will be accessible to all programs of the same crop.
		if (isCropList) {
			listToMove.setProgramUUID(null);
		} else {
			// else, just inherit the program uuid of the parent folder.
			listToMove.setProgramUUID(newParentFolder.getProgramUUID());
		}

		final SampleList uniqueSampleListName =
				this.sampleListDao.getSampleListByParentAndName(listToMove.getListName(), newParentFolderId, listToMove.getProgramUUID());

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");
		Preconditions.checkArgument(!this.isDescendant(listToMove, newParentFolder),
				"You can not move list because are relatives with " + "parent folder");

		listToMove.setHierarchy(newParentFolder);
		try {
			return this.sampleListDao.saveOrUpdate(listToMove);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in moveSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	/**
	 * Delete a folder Folder ID must exist and it can not contain any child
	 *
	 * @param folderId
	 * @throws Exception
	 */
	@Override
	public void deleteSampleListFolder(final Integer folderId) {
		Preconditions.checkNotNull(folderId);
		final SampleList folder = this.sampleListDao.getById(folderId);

		Preconditions.checkArgument(folder != null, "Folder does not exist");
		Preconditions.checkArgument(folder.isFolder(), "Specified folderID is not a folder");
		Preconditions.checkArgument(folder.getHierarchy() != null, "Root folder can not be deleted");
		Preconditions
				.checkArgument(folder.getChildren() == null || folder.getChildren().isEmpty(), "Folder has children and cannot be deleted");

		try {
			this.sampleListDao.makeTransient(folder);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in moveSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
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
	public Map<Integer, GermplasmFolderMetadata> getFolderMetadata(final List<SampleList> sampleLists) {
		final List<Integer> folderIdsToRetrieveFolderCount = this.getFolderIdsFromSampleList(sampleLists);
		return this.getSampleListDao().getSampleFolderMetadata(folderIdsToRetrieveFolderCount);
	}

	private List<Integer> getFolderIdsFromSampleList(final List<SampleList> listIds) {
		final List<Integer> folderIdsToRetrieveFolderCount = new ArrayList<>();
		for (final SampleList parentList : listIds) {
			if (parentList.isFolder()) {
				folderIdsToRetrieveFolderCount.add(parentList.getId());
			}
		}
		return folderIdsToRetrieveFolderCount;
	}

	protected boolean isDescendant(final SampleList list, final SampleList of) {
		if (of.getHierarchy() == null) {
			return false;
		}
		if (of.getHierarchy().equals(list)) {
			return true;
		} else {
			return this.isDescendant(list, of.getHierarchy());
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

	public void setSampleDao(final SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}

	public SampleListDao getSampleListDao() {
		return this.sampleListDao;
	}

	public UserDAO getUserDao() {
		return this.userDao;
	}

	public SampleDao getSampleDao() {
		return this.sampleDao;
	}

	public StudyMeasurements getStudyMeasurements() {
		return this.studyMeasurements;
	}

	public PlantDao getPlantDao() {
		return this.plantDao;
	}

	public SampleService getSampleService() {
		return this.sampleService;
	}

	public StudyDataManager getStudyService() {
		return this.studyService;
	}

	public WorkbenchDataManager getWorkbenchDataManager() {
		return this.workbenchDataManager;
	}
}
