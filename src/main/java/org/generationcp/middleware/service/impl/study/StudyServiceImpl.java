
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
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.user.UserDto;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
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
	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS = "select count(*) as totalObservationUnits from nd_experiment nde \n"
		+ "    inner join nd_experiment_project ndep on ndep.nd_experiment_id = nde.nd_experiment_id \n"
		+ "    inner join project proj on proj.project_id = ndep.project_id \n"
		+ "    inner join nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
		+ "		LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n"
		+ "		LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n" + " where \n"
		+ "	proj.project_id = (select  p.project_id from project_relationship pr inner join project p ON p.project_id = pr.subject_project_id where (pr.object_project_id = :studyIdentifier and name like '%PLOTDATA')) \n"
		+ "    and gl.nd_geolocation_id = :instanceId ";

	public static final String SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED = "SELECT nde.nd_experiment_id,cvterm_variable.cvterm_id,cvterm_variable.name, count(ph.value) \n"
		+ " FROM \n" + " project p \n" + " INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
		+ "        INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id \n"
		+ "        INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id \n"
		+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
		+ "        INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id \n"
		+ "        INNER JOIN stock s ON s.stock_id = es.stock_id \n"
		+ "        LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n"
		+ "        LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n"
		+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n"
		+ " WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr "
		+ "							INNER JOIN project p ON p.project_id = pr.subject_project_id "
		+ "  WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n"
		+ " AND cvterm_variable.cvterm_id IN (:cvtermIds) AND ph.value IS NOT NULL\n" + " GROUP BY  cvterm_variable.name";

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES = SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS + " and ph.value is not null ";

	private final String TRIAL_TYPE = "T";

	private TraitService trialTraits;

	private GermplasmDescriptors germplasmDescriptors;

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
		this.germplasmDescriptors = new GermplasmDescriptors(currentSession);
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
			final StudyGermplasmListService studyGermplasmListServiceImpl, GermplasmDescriptors germplasmDescriptors) {
		this.trialTraits = trialTraits;
		this.studyMeasurements = trialMeasurements;
		this.studyGermplasmListService = studyGermplasmListServiceImpl;
		this.germplasmDescriptors = germplasmDescriptors;
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
	public boolean hasMeasurementDataOnEnvironment(final int studyIdentifier, final int instanceId) {
		try {

			final SQLQuery query = this.getCurrentSession().createSQLQuery(SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES);
			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("studyIdentifier", studyIdentifier);
			query.setParameter("instanceId", instanceId);
			return ((int) query.uniqueResult()) > 0;
		} catch (HibernateException he) {
			throw new MiddlewareQueryException(String
				.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ", studyIdentifier,
					instanceId) + he.getMessage(), he);
		}
	}

	@Override
	public int countTotalObservationUnits(final int studyIdentifier, final int instanceId) {
		try {
			String sql = SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS;

			final SQLQuery query = this.getCurrentSession().createSQLQuery(sql);
			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("studyIdentifier", studyIdentifier);
			query.setParameter("instanceId", instanceId);
			return (int) query.uniqueResult();
		} catch (HibernateException he) {
			throw new MiddlewareQueryException(String
				.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ", studyIdentifier,
					instanceId) + he.getMessage(), he);
		}
	}

	@Override
	public List<ObservationDto> getObservations(final int studyIdentifier, final int instanceId, final int pageNumber,
			final int pageSize, final String sortBy, final String sortOrder) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);
		final List<TraitDto> selectionMethods = this.trialTraits.getSelectionMethods(studyIdentifier);
		final List<TraitDto> selectionMethodsAndTraits = new ArrayList<>();
		selectionMethodsAndTraits.addAll(traits);
		selectionMethodsAndTraits.addAll(selectionMethods);

		return this.studyMeasurements.getAllMeasurements(studyIdentifier, selectionMethodsAndTraits, findGenericGermplasmDescriptors(studyIdentifier),
				instanceId, pageNumber, pageSize,
				sortBy, sortOrder);
	}

	private List<String> findGenericGermplasmDescriptors(final int studyIdentifier) {

		final List<String> allGermplasmDescriptors = this.germplasmDescriptors.find(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<String> fixedGermplasmDescriptors =
				Lists.newArrayList("GID", "DESIGNATION", "ENTRY_NO", "ENTRY_TYPE", "ENTRY_CODE", "PLOT_ID");
		final List<String> genericGermplasmDescriptors = Lists.newArrayList();

		for (String gpDescriptor : allGermplasmDescriptors) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor)) {
				genericGermplasmDescriptors.add(gpDescriptor);
			}
		}
		return genericGermplasmDescriptors;
	}

	@Override
	public List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier) {
		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);
		return this.studyMeasurements.getMeasurement(studyIdentifier, traits, findGenericGermplasmDescriptors(studyIdentifier),
				measurementIdentifier);
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {

		try {
			final String sql = "select \n" + 
					"	geoloc.nd_geolocation_id as INSTANCE_DBID, \n" + 
					"	max(if(geoprop.type_id = 8180, geoprop.value, null)) as LOCATION_NAME, \n" + // 8180 = cvterm for LOCATION_NAME
					"	max(if(geoprop.type_id = 8189, geoprop.value, null)) as LOCATION_ABBR, \n" + // 8189 = cvterm for LOCATION_ABBR
					"   geoloc.description as INSTANCE_NUMBER \n" +
					" from \n" + 
					"	nd_geolocation geoloc \n" + 
					"    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n" + 
					"    inner join nd_experiment_project ndep on ndep.nd_experiment_id = nde.nd_experiment_id \n" + 
					"    inner join project proj on proj.project_id = ndep.project_id \n" + 
					"    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n" + 
					" where \n" + 
					"    proj.project_id = (select  p.project_id from project_relationship pr inner join project p ON p.project_id = pr.subject_project_id " + 
					"    		where (pr.object_project_id = :studyId and name like '%ENVIRONMENT')) \n" +
					"    group by geoloc.nd_geolocation_id \n" + 
					"    order by (1 * geoloc.description) asc ";

			final SQLQuery query = this.getCurrentSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			query.addScalar("INSTANCE_DBID", new IntegerType());
			query.addScalar("LOCATION_NAME", new StringType());
			query.addScalar("LOCATION_ABBR", new StringType());
			query.addScalar("INSTANCE_NUMBER", new IntegerType());
			
			final List queryResults = query.list();
			final List<StudyInstance> instances = new ArrayList<>();
			for (final Object result : queryResults) {				
				Object[] row = (Object[]) result;
				instances.add(new StudyInstance((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3]));
			}
			return instances;
		} catch (HibernateException he) {
			throw new MiddlewareQueryException(
					"Unexpected error in executing getAllStudyInstanceNumbers(studyId = " + studyId + ") query: " + he.getMessage(), he);
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

	public boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId) {
		final List queryResults;
		try {
			final SQLQuery query = this.getCurrentSession().createSQLQuery(SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED);
			query.setParameter("studyId", studyId);
			query.setParameterList("cvtermIds", ids);
			queryResults = query.list();

		} catch (HibernateException he) {
			throw new MiddlewareQueryException(
				"Unexpected error in executing hasMeasurementDataEntered(studyId = " + studyId + ") query: " + he.getMessage(), he);
		}

		if (queryResults.isEmpty()) {
			return false;
		}
		return true;
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


