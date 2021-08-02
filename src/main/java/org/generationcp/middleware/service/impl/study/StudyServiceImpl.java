
package org.generationcp.middleware.service.impl.study;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

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
	public List<String> getGenericGermplasmDescriptors(final int studyIdentifier) {

		final List<String> allGermplasmDescriptors = this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<String> fixedGermplasmDescriptors =
			Lists.newArrayList("GID", "DESIGNATION", "ENTRY_NO", "ENTRY_TYPE", "ENTRY_CODE", "OBS_UNIT_ID");
		final List<String> genericGermplasmDescriptors = Lists.newArrayList();

		for (final String gpDescriptor : allGermplasmDescriptors) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor)) {
				genericGermplasmDescriptors.add(gpDescriptor);
			}
		}
		return genericGermplasmDescriptors;
	}

	@Override
	public List<String> getAdditionalDesignFactors(final int studyIdentifier) {

		final List<String> allDesignFactors = this.daoFactory.getProjectPropertyDAO().getDesignFactors(studyIdentifier);
		/**
		 * Fixed design factors are already being retrieved individually in Measurements query. We are only interested in additional
		 * EXPERIMENTAL_DESIGN and TREATMENT FACTOR variables
		 */
		final List<String> fixedDesignFactors =
			Lists.newArrayList("REP_NO", "PLOT_NO", "BLOCK_NO", "ROW", "COL", "FIELDMAP COLUMN", "FIELDMAP RANGE");
		final List<String> additionalDesignFactors = Lists.newArrayList();

		for (final String designFactor : allDesignFactors) {
			if (!fixedDesignFactors.contains(designFactor)) {
				additionalDesignFactors.add(designFactor);
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
			.getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.MEANS_DATA.getId());
		return (!org.springframework.util.CollectionUtils.isEmpty(datasets));
	}

	@Override
	public List<GermplasmStudyDto> getGermplasmStudies(final Integer gid) {
		return this.daoFactory.getStockDao().getGermplasmStudyDtos(gid);
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}


	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
