package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class SampleListServiceImpl implements SampleListService {

	public static final int MAX_SAMPLE_NAME_SIZE = 5000;

	private StudyMeasurements studyMeasurements;

	private DaoFactory daoFactory;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private UserService userService;

	@Autowired
	private CropService cropService;

	public SampleListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.studyMeasurements = new StudyMeasurements(sessionProvider.getSession());
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SampleList createSampleList(final SampleListDTO sampleListDTO) {

		try {
			final SampleList sampleList = new SampleList();
			sampleList.setCreatedDate(sampleListDTO.getCreatedDate());
			final WorkbenchUser workbenchUser = this.userService.getUserByUsername(sampleListDTO.getCreatedBy());
			sampleList.setProgramUUID(sampleListDTO.getProgramUUID());
			sampleList.setCreatedByUserId(workbenchUser.getUserid());
			sampleList.setDescription(sampleListDTO.getDescription());
			sampleList.setListName(sampleListDTO.getListName());
			sampleList.setNotes(sampleListDTO.getNotes());
			sampleList.setType(SampleListType.SAMPLE_LIST);
			final SampleList parent;

			if (sampleListDTO.getParentId() == null || sampleListDTO.getParentId().equals(0)) {
				parent = this.daoFactory.getSampleListDao().getRootSampleList();
			} else {
				parent = this.daoFactory.getSampleListDao().getParentSampleFolder(sampleListDTO.getParentId());
			}

			Preconditions.checkArgument(parent.isFolder(), "The parent id must not be a list");

			final SampleList uniqueSampleListName = this.daoFactory.getSampleListDao()
				.getSampleListByParentAndName(sampleListDTO.getListName(), parent.getId(), sampleListDTO.getProgramUUID());

			Preconditions.checkArgument(uniqueSampleListName == null, "List name should be unique within the same directory");

			sampleList.setHierarchy(parent);

			final List<Sample> samples;
			if (!sampleListDTO.getEntries().isEmpty()) {
				/*
				 * TODO IBP-4375
				 *  - create sample_list_data
				 *  - change hibernate mapping / adapt createSamplesFromStudy
				 *  - set new entry_no
				 *  - set list id
				 */
				throw new NotImplementedException();
			} else {
				samples = this.createSamplesFromStudy(sampleListDTO, sampleList, workbenchUser);
			}

			sampleList.setSamples(samples);
			return this.daoFactory.getSampleListDao().save(sampleList);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in createSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	private List<Sample> createSamplesFromStudy(final SampleListDTO sampleListDTO, final SampleList sampleList,
		final WorkbenchUser workbenchUser) {

		final List<ObservationDto> observationDtos = this.studyMeasurements
			.getSampleObservations(sampleListDTO.getDatasetId(), sampleListDTO.getInstanceIds(),
				sampleListDTO.getSelectionVariableId());

		Preconditions.checkArgument(!observationDtos.isEmpty(), "The observation list must not be empty");

		Integer takenBy = null;
		if (!sampleListDTO.getTakenBy().isEmpty()) {
			takenBy = this.userService.getUserByUsername(sampleListDTO.getTakenBy()).getUserid();
		}

		final String cropPrefix = this.cropService.getCropTypeByName(sampleListDTO.getCropName()).getPlotCodePrefix();
		final Collection<Integer> gids = this.getGids(observationDtos);
		final Collection<Integer> experimentIds = this.getExperimentIds(observationDtos);
		final Map<Integer, Integer> maxSampleNumbers = this.getMaxSampleNumber(experimentIds);
		final Map<Integer, Integer> maxSequenceNumberByGID = this.getMaxSequenceNumberByGID(gids);
		final List<Sample> samples = new ArrayList<>();
		int entryNumber = 0;

		for (final ObservationDto observationDto : observationDtos) {
			/*
			 * maxSequence is the maximum number among samples in the same GID. If there is no sample for Gid, the sequence starts in 1.
			 */

			final Integer gid = observationDto.getGid();
			Integer maxSequence = maxSequenceNumberByGID.get(gid);

			if (maxSequence == null) {
				maxSequence = 0;
				maxSequenceNumberByGID.put(gid, maxSequence);
			}

			final int selectionVariateValue =
				(Double.valueOf(observationDto.getVariableMeasurements().get(0).getVariableValue())).intValue();

			Integer sampleNumber = maxSampleNumbers.get(observationDto.getMeasurementId());
			if (sampleNumber == null) {
				sampleNumber = 0;
			}

			for (int i = 0; i < selectionVariateValue; i++) {

				sampleNumber++;
				maxSequence++;
				entryNumber++;

				final String sampleName = observationDto.getDesignation() + ':' + maxSequence;

				if (sampleName.length() > MAX_SAMPLE_NAME_SIZE) {
					throw new MiddlewareRequestException("", "error.save.resulting.name.exceeds.limit");
				}

				final Sample sample = this.sampleService
					.buildSample(sampleListDTO.getCropName(), cropPrefix, entryNumber, sampleName,
						sampleListDTO.getSamplingDate(), observationDto.getMeasurementId(), sampleList, workbenchUser.getUserid(),
						sampleListDTO.getCreatedDate(), takenBy, sampleNumber);
				samples.add(sample);
			}

			maxSequenceNumberByGID.put(gid, maxSequence);
		}
		return samples;
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

	private Map<Integer, Integer> getMaxSampleNumber(final Collection<Integer> experimentIds) {
		return this.daoFactory.getSampleDao().getMaxSampleNumber(experimentIds);
	}

	private Map<Integer, Integer> getMaxSequenceNumberByGID(final Collection<Integer> gids) {
		return this.daoFactory.getSampleDao().getMaxSequenceNumber(gids);
	}

	/**
	 * Create a sample list folder Sample List folder name must be unique across the elements in the parent folder
	 *
	 * @param folderName
	 * @param parentId
	 * @param username
	 * @param programUUID
	 * @return Sample List
	 * @throws Exception
	 */
	@Override
	public Integer createSampleListFolder(final String folderName, final Integer parentId, final String username,
		final String programUUID) {
		Preconditions.checkNotNull(folderName);
		Preconditions.checkNotNull(parentId);
		Preconditions.checkNotNull(username, "username can not be empty");
		Preconditions.checkNotNull(programUUID);
		Preconditions.checkArgument(!folderName.isEmpty(), "folderName can not be empty");
		Preconditions.checkArgument(!programUUID.isEmpty(), "programUUID can not be empty");

		final SampleList parentList;
		if (0 == parentId) {
			parentList = this.daoFactory.getSampleListDao().getRootSampleList();
		} else {
			parentList = this.daoFactory.getSampleListDao().getById(parentId);

		}

		Preconditions.checkArgument(parentList != null, "Parent Folder does not exist");
		Preconditions.checkArgument(parentList.isFolder(), "Specified parentID is not a folder");

		final SampleList uniqueSampleListName =
			this.daoFactory.getSampleListDao().getSampleListByParentAndName(folderName, parentList.getId(), programUUID);

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

		try {

			final WorkbenchUser workbenchUser = this.userService.getUserByUsername(username);
			final SampleList sampleFolder = new SampleList();
			sampleFolder.setCreatedDate(new Date());
			sampleFolder.setCreatedByUserId(workbenchUser.getUserid());
			sampleFolder.setDescription(null);
			sampleFolder.setListName(folderName);
			sampleFolder.setNotes(null);
			sampleFolder.setHierarchy(parentList);
			sampleFolder.setType(SampleListType.FOLDER);
			sampleFolder.setProgramUUID(programUUID);
			return this.daoFactory.getSampleListDao().save(sampleFolder).getId();

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

		final SampleList folder = this.daoFactory.getSampleListDao().getById(folderId);

		Preconditions.checkArgument(folder != null, "Folder does not exist");
		Preconditions.checkArgument(SampleListType.FOLDER.equals(folder.getType()), "Specified folderID is not a folder");
		Preconditions.checkArgument(folder.getHierarchy() != null, "Root folder name is not editable");

		final SampleList uniqueSampleListName = this.daoFactory.getSampleListDao()
			.getSampleListByParentAndName(newFolderName, folder.getHierarchy().getId(), folder.getProgramUUID());

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

		folder.setListName(newFolderName);
		try {
			return this.daoFactory.getSampleListDao().saveOrUpdate(folder);
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
	 * @param programUUID
	 * @return SampleList
	 * @throws Exception
	 */
	@Override
	public SampleList moveSampleList(final Integer sampleListId, final Integer newParentFolderId, final boolean isCropList,
		final String programUUID) {
		Preconditions.checkNotNull(sampleListId);
		Preconditions.checkNotNull(newParentFolderId);
		Preconditions.checkArgument(!sampleListId.equals(newParentFolderId), "Arguments can not have the same value");

		final SampleList listToMove = this.daoFactory.getSampleListDao().getById(sampleListId);

		Preconditions.checkArgument(listToMove != null, "sampleList does not exist");
		Preconditions.checkArgument(listToMove.getHierarchy() != null, "Root folder can not me moved");

		final SampleList newParentFolder;
		if (0 == newParentFolderId) {
			newParentFolder = this.daoFactory.getSampleListDao().getRootSampleList();
		} else {
			newParentFolder = this.daoFactory.getSampleListDao().getById(newParentFolderId);
		}

		Preconditions.checkArgument(newParentFolder != null, "Specified newParentFolderId does not exist");
		Preconditions.checkArgument(newParentFolder.isFolder(), "Moving of a list to another list is not allowed.");

		// if the list is moved to the crop list, set the program uuid to null so that
		// it will be accessible to all programs of the same crop.
		if (isCropList) {
			listToMove.setProgramUUID(null);
		} else {
			// else, just set the current programUUID
			listToMove.setProgramUUID(programUUID);
		}

		final SampleList uniqueSampleListName = this.daoFactory.getSampleListDao()
			.getSampleListByParentAndName(listToMove.getListName(), newParentFolderId, listToMove.getProgramUUID());

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");
		Preconditions.checkArgument(!this.isDescendant(listToMove, newParentFolder),
			"You can not move list because are relatives with " + "parent folder");

		listToMove.setHierarchy(newParentFolder);
		try {
			return this.daoFactory.getSampleListDao().saveOrUpdate(listToMove);
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
		final SampleList folder = this.daoFactory.getSampleListDao().getById(folderId);

		Preconditions.checkArgument(folder != null, "Folder does not exist");
		Preconditions.checkArgument(folder.isFolder(), "Specified folderID is not a folder");
		Preconditions.checkArgument(folder.getHierarchy() != null, "Root folder can not be deleted");
		Preconditions
			.checkArgument(folder.getChildren() == null || folder.getChildren().isEmpty(), "Folder has children and cannot be deleted");

		try {
			this.daoFactory.getSampleListDao().makeTransient(folder);
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in moveSampleList in SampleListServiceImpl: " + e.getMessage(), e);
		}
	}

	@Override
	public SampleList getLastSavedSampleListByUserId(final Integer userId, final String programUuid) {
		return this.daoFactory.getSampleListDao().getLastCreatedByUserID(userId, programUuid);
	}

	@Override
	public Map<Integer, ListMetadata> getListMetadata(final List<SampleList> sampleLists) {
		final List<Integer> listIds = Lists.transform(sampleLists, new Function<SampleList, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final SampleList sampleList) {
				return sampleList.getId();
			}
		});
		return this.daoFactory.getSampleListDao().getSampleListMetadata(listIds);
	}

	@Override
	public long countSamplesByUIDs(final Set<String> sampleUIDs, final Integer listId) {
		return this.daoFactory.getSampleDao().countBySampleUIDs(sampleUIDs, listId);
	}

	@Override
	public long countSamplesByDatasetId(final Integer datasetId) {
		return this.daoFactory.getSampleDao().countByDatasetId(datasetId);
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
	public List<SampleListDTO> getSampleLists(final List<Integer> datasetIds) {
		return this.daoFactory.getSampleListDao().getSampleLists(datasetIds);
	}

	@Override
	public List<SampleListDTO> getSampleListsByStudy(final Integer studyId, final boolean withGenotypesOnly) {
		return this.daoFactory.getSampleListDao().getSampleListsByStudy(studyId, withGenotypesOnly);
	}

	@Override
	public SampleList getSampleList(final Integer sampleListId) {
		return this.daoFactory.getSampleListDao().getById(sampleListId);
	}

	@Override
	public List<SampleDetailsDTO> getSampleDetailsDTOs(final Integer sampleListId) {
		final List<SampleDetailsDTO> sampleDetailsDTOS = this.daoFactory.getSampleListDao().getSampleDetailsDTO(sampleListId);

		// Populate takenBy with full name of user from workbench database.
		final List<Integer> userIds = Lists.transform(sampleDetailsDTOS, new Function<SampleDetailsDTO, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final SampleDetailsDTO input) {
				return input.getTakenByUserId();
			}
		});
		final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
		for (final SampleDetailsDTO sampleDetailsDTO : sampleDetailsDTOS) {
			sampleDetailsDTO.setTakenBy(userIDFullNameMap.get(sampleDetailsDTO.getTakenByUserId()));
		}
		return sampleDetailsDTOS;
	}

	@Override
	public List<SampleList> getAllSampleTopLevelLists(final String programUUID) {
		final List<SampleList> sampleLists = this.daoFactory.getSampleListDao().getAllTopLevelLists(programUUID);
		// Populate createdBy with full name of user from workbench database.
		this.populateSampleListCreatedByName(sampleLists);
		return sampleLists;
	}

	@Override
	public List<SampleList> searchSampleLists(final String searchString, final boolean exactMatch, final String programUUID,
		final Pageable pageable) {
		final List<SampleList> sampleLists =
			this.daoFactory.getSampleListDao().searchSampleLists(searchString, exactMatch, programUUID, pageable);
		// Populate createdBy with full name of user from workbench database.
		this.populateSampleListCreatedByName(sampleLists);
		return sampleLists;
	}

	@Override
	public List<SampleList> getSampleListByParentFolderIdBatched(final Integer parentId, final String programUUID, final int batchSize) {
		final List<SampleList> sampleLists = this.daoFactory.getSampleListDao().getByParentFolderId(parentId, programUUID);
		// Populate createdBy with full name of user from workbench database.
		this.populateSampleListCreatedByName(sampleLists);
		return sampleLists;
	}

	@Override
	public void updateSamplePlateInfo(final Integer sampleListId, final Map<String, SamplePlateInfo> plateInfoMap) {
		final SampleList sampleList = this.daoFactory.getSampleListDao().getById(sampleListId);
		for (final Sample sample : sampleList.getSamples()) {
			if (plateInfoMap.containsKey(sample.getSampleBusinessKey())) {
				sample.setPlateId(plateInfoMap.get(sample.getSampleBusinessKey()).getPlateId());
				sample.setWell(plateInfoMap.get(sample.getSampleBusinessKey()).getWell());
			}
		}
		this.daoFactory.getSampleListDao().saveOrUpdate(sampleList);
	}

	@Override
	public String getObservationVariableName(final Integer sampleListId) {
		final DatasetDTO datasetDTO = this.daoFactory.getDmsProjectDAO().getDatasetOfSampleList(sampleListId);
		return this.daoFactory.getObservationUnitsSearchDAO().getObservationVariableName(datasetDTO.getDatasetId());
	}

	@Override
	public List<SampleDTO> getSampleListEntries(final List<Integer> sampleIds) {
		return this.daoFactory.getSampleDao().getSamples(sampleIds);
	}

	@Override
	public void deleteSamples(final Integer sampleListId, final List<Integer> sampleIds) {
		this.daoFactory.getGenotypeDao().deleteSampleGenotypes(sampleIds);
		this.daoFactory.getSampleDao().deleteBySampleIds(sampleListId, sampleIds);
		this.daoFactory.getSampleDao().reOrderEntries(sampleListId);
	}

	public void setStudyMeasurements(final StudyMeasurements studyMeasurements) {
		this.studyMeasurements = studyMeasurements;
	}

	public void setCropService(final CropService cropService) {
		this.cropService = cropService;
	}

	public void setSampleService(final SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	void populateSampleListCreatedByName(final List<SampleList> sampleLists) {
		final List<Integer> userIds = Lists.transform(sampleLists, new Function<SampleList, Integer>() {

			@Nullable
			@Override
			public Integer apply(@Nullable final SampleList input) {
				return input.getCreatedByUserId();
			}
		});
		final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
		for (final SampleList sampleList : sampleLists) {
			sampleList.setCreatedBy(userIDFullNameMap.get(sampleList.getCreatedByUserId()));
		}
	}
}
