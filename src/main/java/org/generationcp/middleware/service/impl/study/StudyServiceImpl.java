
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyScaleDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyDetailDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Ordering;

import javax.annotation.Nullable;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private TraitService trialTraits;

	private StudyMeasurements studyMeasurements;

	private StudyGermplasmListService studyGermplasmListService;

	private OntologyVariableDataManager ontologyVariableDataManager;

	private StudyDataManager studyDataManager;

	private static LoadingCache<StudyKey, String> studyIdToProgramIdCache;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		final Session currentSession = this.getCurrentSession();
		this.trialTraits = new TraitServiceImpl(currentSession);
		this.studyMeasurements = new StudyMeasurements(this.getCurrentSession());
		this.studyGermplasmListService = new StudyGermplasmListServiceImpl(this.getCurrentSession());
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(new OntologyMethodDataManagerImpl(sessionProvider),
				new OntologyPropertyDataManagerImpl(sessionProvider),
				new OntologyScaleDataManagerImpl(sessionProvider), sessionProvider);
		this.studyDataManager = new StudyDataManagerImpl(sessionProvider);

		final CacheLoader<StudyKey, String> studyKeyCacheBuilder = new CacheLoader<StudyKey, String>() {
			public String load(StudyKey key) throws Exception {
				return studyDataManager.getProject(key.getStudyId()).getProgramUUID();
			}
		};
		studyIdToProgramIdCache = CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MINUTES).build(studyKeyCacheBuilder);
	}

	/**
	 * Only used for tests.
	 *
	 * @param trialTraits
	 * @param trialMeasurements
	 */
	StudyServiceImpl(final TraitService trialTraits, final StudyMeasurements trialMeasurements,
			final StudyGermplasmListService studyGermplasmListServiceImpl) {
		this.trialTraits = trialTraits;
		this.studyMeasurements = trialMeasurements;
		this.studyGermplasmListService = studyGermplasmListServiceImpl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StudySummary> search(final StudySearchParameters serchParameters) {

		final List<StudySummary> studySummaries = new ArrayList<>();

		StringBuffer sql = new StringBuffer()
		.append("SELECT p.project_id AS id, p.name AS name, p.description AS title, ")
		.append("	p.program_uuid AS programUUID, ppType.value AS studyTypeId, ppObjective.value AS objective, ")
		.append("	ppStartDate.value AS startDate, ppEndDate.value AS endDate, ppPI.value AS piName, ppLocation.value AS location, ppSeason.value AS season ")
		.append(" FROM project p ")
		.append("  INNER JOIN projectprop ppType ON p.project_id = ppType.project_id AND ppType.type_id = ").append(TermId.STUDY_TYPE.getId())
		.append("  LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id AND ppObjective.type_id = ").append(TermId.STUDY_OBJECTIVE.getId())
		.append("  LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.type_id = ").append(TermId.START_DATE.getId())
		.append("  LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.type_id = ").append(TermId.END_DATE.getId())
		.append("  LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id AND ppPI.type_id = ").append(TermId.PI_NAME.getId())
		.append("  LEFT JOIN projectprop ppLocation ON p.project_id = ppLocation.project_id AND ppLocation.type_id = ").append(TermId.TRIAL_LOCATION.getId())
		.append("  LEFT JOIN projectprop ppSeason ON p.project_id = ppSeason.project_id AND ppSeason.type_id = ").append(TermId.SEASON_VAR_TEXT.getId())
		.append(" WHERE NOT EXISTS ")
		.append("  (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id = ").append(TermId.STUDY_STATUS.getId())
		.append("    AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(")");

		if (!StringUtils.isEmpty(serchParameters.getProgramUniqueId())) {
			sql.append(" AND p.program_uuid = '").append(serchParameters.getProgramUniqueId().trim()).append("'");
		}
		if (!StringUtils.isEmpty(serchParameters.getPrincipalInvestigator())) {
			sql.append(" AND ppPI.value LIKE '%").append(serchParameters.getPrincipalInvestigator().trim()).append("%'");
		}
		if (!StringUtils.isEmpty(serchParameters.getLocation())) {
			sql.append(" AND ppLocation.value LIKE '%").append(serchParameters.getLocation().trim()).append("%'");
		}
		if (!StringUtils.isEmpty(serchParameters.getSeason())) {
			sql.append(" AND ppSeason.value LIKE '%").append(serchParameters.getSeason().trim()).append("%'");
		}

		List<Object[]> list = null;
		try {
			final Query query =
					this.getCurrentSession().createSQLQuery(sql.toString()).addScalar("id").addScalar("name").addScalar("title")
					.addScalar("programUUID").addScalar("studyTypeId").addScalar("objective").addScalar("startDate")
					.addScalar("endDate").addScalar("piName").addScalar("location").addScalar("season");
			list = query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in listAllStudies() query in StudyServiceImpl: " + e.getMessage(), e);
		}

		if (list != null && !list.isEmpty()) {
			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String name = (String) row[1];
				final String title = (String) row[2];
				final String programUUID = (String) row[3];
				final String studyTypeId = (String) row[4];
				final String objective = (String) row[5];
				final String startDate = (String) row[6];
				final String endDate = (String) row[7];
				final String pi = (String) row[8];
				final String location = (String) row[9];
				final String season = (String) row[10];

				final StudySummary studySummary =
						new StudySummary(id, name, title, objective, StudyType.getStudyTypeById(Integer.valueOf(studyTypeId)), startDate,
								endDate, programUUID, pi, location, season);
				studySummaries.add(studySummary);
			}
		}
		return studySummaries;
	}

	@Override
	public List<ObservationDto> getObservations(final int studyIdentifier) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		return this.studyMeasurements.getAllMeasurements(studyIdentifier, traits);
	}

	@Override
	public List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		return this.studyMeasurements.getMeasurement(studyIdentifier, traits, measurementIdentifier);

	}

	@Override
	public ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement) {

		final Session currentSession = this.getCurrentSession();
		final Observations observations = new Observations(currentSession, ontologyVariableDataManager);
		try {
			final ObservationDto updatedMeasurement =
					observations.updataObsevationTraits(middlewareMeasurement,
							studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop())));
			return updatedMeasurement;
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Unexpected error updating observations. Please contact support for "
					+ "further assistence.", e); // or
		}
	}

	@Override
	public List<StudyGermplasmDto> getStudyGermplasmList(final Integer studyIdentifer) {
		return this.studyGermplasmListService.getGermplasmList(studyIdentifer);
	}

	@Override
	public String getProgramUUID(final Integer studyIdentifier) {
		try {
			return studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop()));
		} catch (ExecutionException e) {
			throw new MiddlewareQueryException("Unexpected error updating observations. Please contact support for "
					+ "further assistence.", e);
		}
	}
	
	@Override
	public StudyDetailDto getStudyDetails(final int studyIdentifier) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		final List<TraitDto> sortedTraits = Ordering.from(new Comparator<TraitDto>() {

			@Override
			public int compare(final TraitDto o1, final TraitDto o2) {
				return o1.getTraitId() - o2.getTraitId();
			}
		}).immutableSortedCopy(traits);

		final List<Object[]> results = this.studyMeasurements.getAllStudyDetailsAsTable(studyIdentifier, sortedTraits);

		final List<Integer> observationVariableDbId = new ArrayList<Integer>();

		final List<String> observationVariableName = new ArrayList<String>();

		for (final Iterator<TraitDto> iterator = sortedTraits.iterator(); iterator.hasNext();) {
			final TraitDto traitDto = iterator.next();
			observationVariableDbId.add(traitDto.getTraitId());
			observationVariableName.add(traitDto.getTraitName());
		}

		List<List<String>> data = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(results)) {
			data = Lists.transform(results, new Function<Object[], List<String>>() {

				@Nullable
				@Override
				public List<String> apply(final Object[] row) {
					final List<String> entry = Lists.newArrayList();
					// plotId
					entry.add((String) row[8]);

					// block
					entry.add((String) row[9]);

					// rep
					entry.add((String) row[7]);

					// gid
					entry.add(String.valueOf(row[3]));

					// phenotypic values
					int counterTwo = 1;
					for (int i = 0; i < traits.size(); i++) {
						final Object rowValue = row[9 + counterTwo];

						if (rowValue != null) {
							entry.add(String.valueOf(rowValue));
						} else {
							entry.add((String) rowValue);
						}

						counterTwo += 2;
					}
					return entry;
				}
			});
		}

		final StudyDetailDto dto = new StudyDetailDto().setStudyDbId(studyIdentifier).setObservationVariableDbId(observationVariableDbId)
				.setObservationVariableName(observationVariableName).setData(data);
		return dto;
	}
}

