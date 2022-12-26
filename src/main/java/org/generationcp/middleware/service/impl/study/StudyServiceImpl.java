
package org.generationcp.middleware.service.impl.study;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.api.study.StudyDTO;
import org.generationcp.middleware.api.study.StudyDetailsDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.api.study.StudySearchResponse;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private DatasetService datasetService;

	private static LoadingCache<StudyKey, String> studyIdToProgramIdCache;

	private DaoFactory daoFactory;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		final CacheLoader<StudyKey, String> studyKeyCacheBuilder = new CacheLoader<StudyKey, String>() {

			@Override
			public String load(final StudyKey key) {
				return StudyServiceImpl.this.studyDataManager.getProject(key.getStudyId()).getProgramUUID();
			}
		};
		StudyServiceImpl.studyIdToProgramIdCache =
			CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MINUTES).build(studyKeyCacheBuilder);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public boolean hasCrossesOrSelections(final int studyId) {
		final GermplasmStudySourceSearchRequest searchParameters = new GermplasmStudySourceSearchRequest();
		searchParameters.setStudyId(studyId);
		return this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters) > 0;
	}

	@Override
	public Map<Integer, String> getGenericGermplasmDescriptors(final int studyIdentifier) {

		final Map<Integer, String> allGermplasmDescriptors =
			this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<Integer> fixedGermplasmDescriptors =
			Lists.newArrayList(TermId.GID.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.ENTRY_TYPE.getId(),
				TermId.ENTRY_CODE.getId(), TermId.CROSS.getId());
		final Map<Integer, String> genericGermplasmDescriptors = Maps.newHashMap();

		for (final Map.Entry<Integer, String> gpDescriptor : allGermplasmDescriptors.entrySet()) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor.getKey())) {
				genericGermplasmDescriptors.put(gpDescriptor.getKey(), gpDescriptor.getValue());
			}
		}
		return genericGermplasmDescriptors;
	}

	@Override
	public Map<Integer, String> getAdditionalDesignFactors(final int studyIdentifier) {

		final Map<Integer, String> allDesignFactors = this.daoFactory.getProjectPropertyDAO().getDesignFactors(studyIdentifier);
		/**
		 * Fixed design factors are already being retrieved individually in Measurements query. We are only interested in additional
		 * EXPERIMENTAL_DESIGN and TREATMENT FACTOR variables
		 */
		final List<Integer> fixedDesignFactors =
			Lists.newArrayList(TermId.REP_NO.getId(), TermId.PLOT_NO.getId(), TermId.BLOCK_NO.getId(), TermId.ROW.getId(),
				TermId.COL.getId(), TermId.FIELDMAP_COLUMN.getId(), TermId.FIELDMAP_RANGE.getId(), TermId.OBS_UNIT_ID.getId());
		final Map<Integer, String> additionalDesignFactors = Maps.newHashMap();

		for (final Map.Entry<Integer, String> designFactor : allDesignFactors.entrySet()) {
			if (!fixedDesignFactors.contains(designFactor.getKey())) {
				additionalDesignFactors.put(designFactor.getKey(), designFactor.getValue());
			}
		}
		return additionalDesignFactors;
	}

	@Override
	public Integer getPlotDatasetId(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0)
			.getProjectId();
	}

	@Override
	public Integer getEnvironmentDatasetId(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
			.getProjectId();
	}

	@Override
	public String getProgramUUID(final Integer studyIdentifier) {
		try {
			return StudyServiceImpl.studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop()));
		} catch (final ExecutionException e) {
			throw new MiddlewareQueryException(
				"Unexpected error updating observations. Please contact support for " + "further assistence.", e);
		}
	}

	@Override
	public boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId) {
		return this.daoFactory.getPhenotypeDAO().hasMeasurementDataEntered(ids, studyId);
	}

	@Override
	public boolean studyHasGivenDatasetType(final Integer studyId, final Integer datasetTypeId) {
		final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(studyId, datasetTypeId);
		return (!org.springframework.util.CollectionUtils.isEmpty(datasets));
	}

	@Override
	public List<GermplasmStudyDto> getGermplasmStudies(final Integer gid) {
		return this.daoFactory.getStockDao().getGermplasmStudyDtos(gid);
	}

	@Override
	public List<StudyDTO> getFilteredStudies(final String programUUID, final StudySearchRequest studySearchRequest,
		final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().filterStudies(programUUID, studySearchRequest, pageable);
	}

	@Override
	public long countFilteredStudies(final String programUUID, final StudySearchRequest studySearchRequest) {
		return this.daoFactory.getDmsProjectDAO().countFilteredStudies(programUUID, studySearchRequest);
	}

	@Override
	public void deleteProgramStudies(final String programUUID) {
		final List<Integer> studyAndFolderIds = this.daoFactory.getDmsProjectDAO().getAllProgramStudiesAndFolders(programUUID);
		this.daoFactory.getDmsProjectDAO().markProjectsAndChildrenAsDeleted(studyAndFolderIds);
	}

	@Override
	public void deleteStudy(final int studyId) {
		this.daoFactory.getDmsProjectDAO().markProjectsAndChildrenAsDeleted(Arrays.asList(studyId));
	}

	@Override
	public long countStudiesByGids(final List<Integer> gids) {
		return this.daoFactory.getStockDao().countStudiesByGids(gids);
	}

	@Override
	public long countPlotsByGids(final List<Integer> gids) {
		return this.daoFactory.getStockDao().countPlotsByGids(gids);
	}

	@Override
	public boolean isLocationUsedInStudy(final Integer locationId) {
		return this.daoFactory.getGeolocationPropertyDao()
			.getGeolocationIdsByPropertyTypeAndValue(TermId.LOCATION_ID.getId(), locationId.toString()).size() > 0;
	}

	@Override
	public void deleteNameTypeFromStudies(final Integer nameTypeId) {
		this.daoFactory.getProjectPropertyDAO().deleteNameTypeFromStudies(nameTypeId);
	}

	@Override
	public List<StudySearchResponse> searchStudies(final String programUUID, final StudySearchRequest studySearchRequest,
		final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().searchStudies(programUUID, studySearchRequest, pageable);
	}

	@Override
	public long countSearchStudies(final String programUUID, final StudySearchRequest studySearchRequest) {
		return this.daoFactory.getDmsProjectDAO().countSearchStudies(programUUID, studySearchRequest);
	}

	@Override
	public Optional<FolderReference> getFolderByParentAndName(final Integer parentId, final String folderName, final String programUUID) {
		return this.daoFactory.getDmsProjectDAO().getFolderByParentAndName(parentId, folderName, programUUID);
	}

	@Override
	public StudyDetailsDTO getStudyDetails(final String programUUID, final Integer studyId) {
		final StudySearchRequest studySearchRequest = new StudySearchRequest();
		studySearchRequest.setStudyIds(Arrays.asList(studyId));
		final List<StudySearchResponse> searchResponse = this.searchStudies(programUUID, studySearchRequest, new PageRequest(0, 1));
		final StudySearchResponse studyData = searchResponse.get(0);

		final StudyDetailsDTO studyDetailsDTO = new StudyDetailsDTO();
		studyDetailsDTO.setId(studyData.getStudyId());
		studyDetailsDTO.setName(studyData.getStudyName());
		studyDetailsDTO.setDescription(studyData.getDescription());
		studyDetailsDTO.setObjective(studyData.getObjective());
		studyDetailsDTO.setCreatedByName(studyData.getOwnerName());
		studyDetailsDTO.setStartDate(studyData.getStartDate());
		studyDetailsDTO.setEndDate(studyData.getEndDate());
		studyDetailsDTO.setLastUpdateDate(studyData.getUpdateDate());

		// TODO: I added a method to retrieve a dataset with its variables in New Advance process implementation: https://github.com/IntegratedBreedingPlatform/Middleware/blob/0bbe6823d1c72f1cd4b3b14cd62f4e3c4e55fa8f/src/main/java/org/generationcp/middleware/service/api/dataset/DatasetService.java#L149
		final DatasetDTO plotDataset =
			this.datasetService.getDatasets(studyId, Collections.singleton(DatasetTypeEnum.PLOT_DATA.getId())).get(0);
		// TODO: Also added a variableTypeResolver: https://github.com/IntegratedBreedingPlatform/Middleware/blob/0bbe6823d1c72f1cd4b3b14cd62f4e3c4e55fa8f/src/main/java/org/generationcp/middleware/service/impl/dataset/DatasetServiceImpl.java#L686
		final List<Integer> observationDatasetVariableTypes = DatasetServiceImpl.OBSERVATION_DATASET_VARIABLE_TYPES;
		plotDataset.setVariables(this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(plotDataset.getDatasetId(), observationDatasetVariableTypes));

		// TODO: same here as above
		final DatasetDTO environmentDataset =
			this.datasetService.getDatasets(studyId, Collections.singleton(DatasetTypeEnum.SUMMARY_DATA.getId())).get(0);
		final List<Integer> environmentDatasetVariableTypes = DatasetServiceImpl.ENVIRONMENT_DATASET_VARIABLE_TYPES;
		environmentDataset.setVariables(this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(plotDataset.getDatasetId(), environmentDatasetVariableTypes));

		final long numberOfEntries = this.daoFactory.getExperimentDao().countStocksByDatasetId(plotDataset.getDatasetId());
		studyDetailsDTO.setNumberOfEntries((int) numberOfEntries);

		final long numberOfPlots = this.daoFactory.getExperimentDao().count(plotDataset.getDatasetId());
		studyDetailsDTO.setNumberOfPlots((int) numberOfPlots);

		final boolean hasFieldLayout = this.daoFactory.getExperimentDao().hasFieldLayout(plotDataset.getDatasetId());
		studyDetailsDTO.setHasFieldLayout(hasFieldLayout);

		final List<Integer> variableIds = plotDataset.getVariables().stream().map(MeasurementVariable::getTermId).collect(Collectors.toList());
		final int numberOfVariablesWithData = this.studyDataManager.countVariatesWithData(plotDataset.getDatasetId(), variableIds);
		studyDetailsDTO.setNumberOfVariablesWithData(numberOfVariablesWithData);
		studyDetailsDTO.setTotalVariablesWithData(variableIds.size());

		return studyDetailsDTO;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}


	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}
