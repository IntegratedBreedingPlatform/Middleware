
package org.generationcp.middleware.service.impl.study;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.api.study.StudyDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	@Resource
	private StudyDataManager studyDataManager;

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

		final Map<Integer, String> allGermplasmDescriptors = this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<Integer> fixedGermplasmDescriptors =
			Lists.newArrayList(TermId.GID.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.ENTRY_TYPE.getId(),
				TermId.ENTRY_CODE.getId(), TermId.OBS_UNIT_ID.getId(), TermId.CROSS.getId());
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
				TermId.COL.getId(), TermId.FIELDMAP_COLUMN.getId(), TermId.FIELDMAP_RANGE
					.getId());
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

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}


	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
