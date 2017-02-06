
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.UserDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyScaleDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.generationcp.middleware.service.api.user.UserDto;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private static final Logger LOG = LoggerFactory.getLogger(StudyServiceImpl.class);

	private final String TRIAL_TYPE = "T";

	private TraitService trialTraits;

	private StudyMeasurements studyMeasurements;

	private StudyGermplasmListService studyGermplasmListService;

	private OntologyVariableDataManager ontologyVariableDataManager;

	private StudyDataManager studyDataManager;

	private UserDataManager userDataManager;

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
		this.userDataManager = new UserDataManagerImpl(sessionProvider);

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
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier) {
		return this.getTrialObservationTable(studyIdentifier, null);
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier, Integer instanceDbId) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		final List<TraitDto> sortedTraits = Ordering.from(new Comparator<TraitDto>() {

			@Override
			public int compare(final TraitDto o1, final TraitDto o2) {
				return o1.getTraitId() - o2.getTraitId();
			}
		}).immutableSortedCopy(traits);

		final List<Object[]> results = this.studyMeasurements.getAllStudyDetailsAsTable(studyIdentifier, sortedTraits, instanceDbId);

		final List<Integer> observationVariableDbIds = new ArrayList<Integer>();

		final List<String> observationVariableNames = new ArrayList<String>();

		for (final Iterator<TraitDto> iterator = sortedTraits.iterator(); iterator.hasNext();) {
			final TraitDto traitDto = iterator.next();
			observationVariableDbIds.add(traitDto.getTraitId());
			observationVariableNames.add(traitDto.getTraitName());
		}

		List<List<String>> data = Lists.newArrayList();


		if (!CollectionUtils.isEmpty(results)) {

			for (Object[] row : results) {
				final List<String> entry = Lists.newArrayList();

				// locationDbId = trial instance number
				// In brapi this will equate to studyDbId
				// TODO Update query and use nd_geolocation_id instead. For now instance number will be ok.
				entry.add((String) row[1]);

				String locationName = (String) row[12];
				String locationAbbreviation = (String) row[13];

				if (StringUtils.isNotBlank(locationAbbreviation)) {
					entry.add(locationAbbreviation);
				} else  if (StringUtils.isNotBlank(locationName)) {
					entry.add(locationName);
				} else {
					entry.add("Study-" + (String) row[1]);
				}

				// gid
				entry.add(String.valueOf(row[3]));

				// germplasm Name/designation
				entry.add(String.valueOf(row[4]));

				// observation Db Id = nd_experiment_id
				entry.add(String.valueOf(row[0]));

				// PlotNumber
				entry.add((String) row[8]);

				// replication number
				entry.add((String) row[7]);

				// blockNumber
				entry.add((String) row[9]);

				// Timestamp
				entry.add("UnknownTimestamp");

				// entry type
				entry.add(String.valueOf(row[2]));

				Object x = row[11];
				Object y = row[10];

				// If there is no row and col design,
				// get fieldmap row and col
				if (x == null || y == null) {
					x = row[14];
					y = row[15];
				}

				// X = col
				entry.add(String.valueOf(x));

				// Y = row
				entry.add(String.valueOf(y));

				// phenotypic values
				int columnOffset = 1;
				for (int i = 0; i < traits.size(); i++) {
					final Object rowValue = row[15 + columnOffset];

					if (rowValue != null) {
						entry.add(String.valueOf(rowValue));
					} else {
						entry.add((String) rowValue);
					}

					// get every other column skipping over PhenotypeId column
					columnOffset += 2;
				}
				data.add(entry);
			}
		}

		final TrialObservationTable
			dto = new TrialObservationTable().setStudyDbId(instanceDbId != null ? instanceDbId : studyIdentifier).setObservationVariableDbIds(observationVariableDbIds)
			.setObservationVariableNames(observationVariableNames).setData(data);

		dto.setHeaderRow(Lists.newArrayList("locationDbId", "locationName", "germplasmDbId", "germplasmName", "observationUnitDbId",
				"plotNumber", "replicate", "blockNumber", "observationTimestamp", "entryType", "X", "Y"));

		return dto;
	}

	@Override
	public StudyDetailsDto getStudyDetails(final Integer studyId) throws MiddlewareQueryException {
		try {
			final StudyMetadata studyMetadata = this.studyDataManager.getStudyMetadata(studyId);
			if (studyMetadata != null) {
				StudyDetailsDto studyDetailsDto = new StudyDetailsDto();
				studyDetailsDto.setMetadata(studyMetadata);
				List<UserDto> users = new ArrayList<>();
				Map<String, String> properties = new HashMap<>();
				if (studyMetadata.getStudyType().equalsIgnoreCase(TRIAL_TYPE)) {
					users.addAll(this.userDataManager.getUsersForEnvironment(studyMetadata.getStudyDbId()));
					users.addAll(this.userDataManager.getUsersAssociatedToStudy(studyMetadata.getNurseryOrTrialId()));
					properties.putAll(this.studyDataManager.getGeolocationPropsAndValuesByStudy(studyId));
					properties.putAll(this.studyDataManager.getProjectPropsAndValuesByStudy(studyMetadata.getNurseryOrTrialId()));
				} else {
					users.addAll(this.userDataManager.getUsersAssociatedToStudy(studyMetadata.getNurseryOrTrialId()));
					properties.putAll(this.studyDataManager.getProjectPropsAndValuesByStudy(studyMetadata.getNurseryOrTrialId()));
				}
				studyDetailsDto.setContacts(users);
				studyDetailsDto.setAdditionalInfo(properties);
				return studyDetailsDto;
			}
			return null;
		} catch (MiddlewareQueryException e) {
			final String message = "Error with getStudyDetails() query from study: " + studyId;
			StudyServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public StudyServiceImpl setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
		return this;
	}

	public StudyServiceImpl setUserDataManager(final UserDataManager userDataManager) {
		this.userDataManager = userDataManager;
		return this;
	}
}


