
package org.generationcp.middleware.service.impl.study;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
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
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchObservationDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private static final Logger LOG = LoggerFactory.getLogger(StudyServiceImpl.class);

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_SELECT = "select count(*) as totalObservationUnits from "
			+ "nd_experiment nde \n" + "    inner join nd_experiment_project ndep on ndep.nd_experiment_id = nde.nd_experiment_id \n"
			+ "    inner join project proj on proj.project_id = ndep.project_id \n"
			+ "    inner join nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n";

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_WHERE = " where \n"
			+ "	proj.project_id = (select  p.project_id from project_relationship pr inner join project p ON p.project_id = pr.subject_project_id where (pr.object_project_id = :studyIdentifier and name like '%PLOTDATA')) \n"
			+ "    and gl.nd_geolocation_id = :instanceId ";

	public static final String SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED =
			"SELECT nde.nd_experiment_id,cvterm_variable.cvterm_id,cvterm_variable.name, count(ph.value) \n" + " FROM \n" + " project p \n"
					+ " INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
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

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES =
			StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_SELECT
					+ "		LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n"
					+ "		LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n"
					+ StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_WHERE + " and ph.value is not null ";


	public static final String TRIAL_TYPE = StudyType.T.getName();

	private MeasurementVariableService measurementVariableService;

	private GermplasmDescriptors germplasmDescriptors;

	private DesignFactors designFactors;

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
		this.germplasmDescriptors = new GermplasmDescriptors(currentSession);
		this.designFactors = new DesignFactors(currentSession);
		this.studyMeasurements = new StudyMeasurements(currentSession);
		this.studyGermplasmListService = new StudyGermplasmListServiceImpl(currentSession);
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(new OntologyMethodDataManagerImpl(sessionProvider),
				new OntologyPropertyDataManagerImpl(sessionProvider), new OntologyScaleDataManagerImpl(sessionProvider), sessionProvider);
		this.studyDataManager = new StudyDataManagerImpl(sessionProvider);
		this.measurementVariableService = new MeasurementVariableServiceImpl(currentSession);

		this.userDataManager = new UserDataManagerImpl(sessionProvider);

		final CacheLoader<StudyKey, String> studyKeyCacheBuilder = new CacheLoader<StudyKey, String>() {

			@Override
			public String load(final StudyKey key) throws Exception {
				return StudyServiceImpl.this.studyDataManager.getProject(key.getStudyId()).getProgramUUID();
			}
		};
		StudyServiceImpl.studyIdToProgramIdCache =
				CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MINUTES).build(studyKeyCacheBuilder);
	}

	/**
	 * Only used for tests.
	 *
	 * @param measurementVariableService
	 * @param trialMeasurements
	 */
	StudyServiceImpl(final MeasurementVariableService measurementVariableService, final StudyMeasurements trialMeasurements,
			final StudyGermplasmListService studyGermplasmListServiceImpl, final GermplasmDescriptors germplasmDescriptors) {
		this.measurementVariableService = measurementVariableService;
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
		.append("	p.program_uuid AS programUUID, p.study_type AS studyType, ppObjective.value AS objective, ")
		.append("	ppStartDate.value AS startDate, ppEndDate.value AS endDate, ppPI.value AS piName, ppLocation.value AS location, ppSeason.value AS season ")
		.append(" FROM project p ")
		.append("  LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id AND ppObjective.type_id = ").append(TermId.STUDY_OBJECTIVE.getId())
		.append("  LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.type_id = ").append(TermId.START_DATE.getId())
		.append("  LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.type_id = ").append(TermId.END_DATE.getId())
		.append("  LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id AND ppPI.type_id = ").append(TermId.PI_NAME.getId())
		.append("  LEFT JOIN projectprop ppLocation ON p.project_id = ppLocation.project_id AND ppLocation.type_id = ").append(TermId.TRIAL_LOCATION.getId())
		.append("  LEFT JOIN projectprop ppSeason ON p.project_id = ppSeason.project_id AND ppSeason.type_id = ").append(TermId.SEASON_VAR_TEXT.getId())
		.append(" WHERE p.deleted = 0");

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
					.addScalar("programUUID").addScalar("studyType").addScalar("objective").addScalar("startDate")
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
				final String studyType = (String) row[4];
				final String objective = (String) row[5];
				final String startDate = (String) row[6];
				final String endDate = (String) row[7];
				final String pi = (String) row[8];
				final String location = (String) row[9];
				final String season = (String) row[10];

				final StudySummary studySummary =
						new StudySummary(id, name, title, objective, StudyType.getStudyTypeByName(studyType), startDate,
								endDate, programUUID, pi, location, season);

				studySummaries.add(studySummary);
			}
		}
		return studySummaries;
	}

	@Override
	public boolean hasMeasurementDataOnEnvironment(final int studyIdentifier, final int instanceId) {
		try {

			final SQLQuery query =
					this.getCurrentSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES);
			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("studyIdentifier", studyIdentifier);
			query.setParameter("instanceId", instanceId);
			return (int) query.uniqueResult() > 0;
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
					String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
							studyIdentifier, instanceId) + he.getMessage(),
					he);
		}
	}

	@Override
	public int countTotalObservationUnits(final int studyIdentifier, final int instanceId) {
		try {
			final SQLQuery query = this.getCurrentSession().createSQLQuery(StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_SELECT
					+ StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_WHERE);
			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("studyIdentifier", studyIdentifier);
			query.setParameter("instanceId", instanceId);
			return (int) query.uniqueResult();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
					String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
							studyIdentifier, instanceId) + he.getMessage(),
					he);
		}
	}

	@Override
	public List<ObservationDto> getObservations(final int studyIdentifier, final int instanceId, final int pageNumber, final int pageSize,
			final String sortBy, final String sortOrder) {

		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariables(studyIdentifier,
				VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		return this.studyMeasurements.getAllMeasurements(studyIdentifier, selectionMethodsAndTraits,
				this.findGenericGermplasmDescriptors(studyIdentifier), this.findAdditionalDesignFactors(studyIdentifier), instanceId,
				pageNumber, pageSize, sortBy, sortOrder);
	}

	List<String> findGenericGermplasmDescriptors(final int studyIdentifier) {

		final List<String> allGermplasmDescriptors = this.germplasmDescriptors.find(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<String> fixedGermplasmDescriptors =
				Lists.newArrayList("GID", "DESIGNATION", "ENTRY_NO", "ENTRY_TYPE", "ENTRY_CODE", "PLOT_ID");
		final List<String> genericGermplasmDescriptors = Lists.newArrayList();

		for (final String gpDescriptor : allGermplasmDescriptors) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor)) {
				genericGermplasmDescriptors.add(gpDescriptor);
			}
		}
		return genericGermplasmDescriptors;
	}

	List<String> findAdditionalDesignFactors(final int studyIdentifier) {

		final List<String> allDesignFactors = this.designFactors.find(studyIdentifier);
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
	public List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier) {
		final List<MeasurementVariableDto> traits =
				this.measurementVariableService.getVariables(studyIdentifier, VariableType.TRAIT.getId());
		return this.studyMeasurements.getMeasurement(studyIdentifier, traits, this.findGenericGermplasmDescriptors(studyIdentifier),
				this.findAdditionalDesignFactors(studyIdentifier), measurementIdentifier);
	}

	@Override
	public ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement) {

		final Session currentSession = this.getCurrentSession();
		final Observations observations = new Observations(currentSession, this.ontologyVariableDataManager);
		try {
			return observations.updataObsevationTraits(middlewareMeasurement,
					StudyServiceImpl.studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop())));
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Unexpected error updating observations. Please contact support for " + "further assistence.", e); // or
		}
	}

	@Override
	public List<StudyGermplasmDto> getStudyGermplasmList(final Integer studyIdentifer) {
		return this.studyGermplasmListService.getGermplasmList(studyIdentifer);
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

	@SuppressWarnings("rawtypes")
	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {

		try {
			final String sql = "select \n" + "	geoloc.nd_geolocation_id as INSTANCE_DBID, \n"
					+ "	max(if(geoprop.type_id = 8180, geoprop.value, null)) as LOCATION_NAME, \n" + // 8180 = cvterm for LOCATION_NAME
					"	max(if(geoprop.type_id = 8189, geoprop.value, null)) as LOCATION_ABBR, \n" + // 8189 = cvterm for LOCATION_ABBR
					"   geoloc.description as INSTANCE_NUMBER \n" + " from \n" + "	nd_geolocation geoloc \n"
					+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
					+ "    inner join nd_experiment_project ndep on ndep.nd_experiment_id = nde.nd_experiment_id \n"
					+ "    inner join project proj on proj.project_id = ndep.project_id \n"
					+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
					+ " where \n"
					+ "    proj.project_id = (select  p.project_id from project_relationship pr inner join project p ON p.project_id = pr.subject_project_id "
					+ "    		where (pr.object_project_id = :studyId and name like '%ENVIRONMENT')) \n"
					+ "    group by geoloc.nd_geolocation_id \n" + "    order by (1 * geoloc.description) asc ";

			final SQLQuery query = this.getCurrentSession().createSQLQuery(sql);
			query.setParameter("studyId", studyId);
			query.addScalar("INSTANCE_DBID", new IntegerType());
			query.addScalar("LOCATION_NAME", new StringType());
			query.addScalar("LOCATION_ABBR", new StringType());
			query.addScalar("INSTANCE_NUMBER", new IntegerType());

			final List queryResults = query.list();
			final List<StudyInstance> instances = new ArrayList<>();
			for (final Object result : queryResults) {
				final Object[] row = (Object[]) result;
				instances.add(new StudyInstance((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3]));
			}
			return instances;
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
					"Unexpected error in executing getAllStudyInstanceNumbers(studyId = " + studyId + ") query: " + he.getMessage(), he);
		}
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier) {
		return this.getTrialObservationTable(studyIdentifier, null);
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier, final Integer instanceDbId) {
		final List<MeasurementVariableDto> traits =
				this.measurementVariableService.getVariables(studyIdentifier, VariableType.TRAIT.getId());

		final List<MeasurementVariableDto> measurementVariables = Ordering.from(new Comparator<MeasurementVariableDto>() {

			@Override
			public int compare(final MeasurementVariableDto o1, final MeasurementVariableDto o2) {
				return o1.getId() - o2.getId();
			}
		}).immutableSortedCopy(traits);

		final List<Object[]> results =
				this.studyMeasurements.getAllStudyDetailsAsTable(studyIdentifier, measurementVariables, instanceDbId);

		final List<Integer> observationVariableDbIds = new ArrayList<>();

		final List<String> observationVariableNames = new ArrayList<>();

		for (final Iterator<MeasurementVariableDto> iterator = measurementVariables.iterator(); iterator.hasNext();) {
			final MeasurementVariableDto measurementVariableDto = iterator.next();
			observationVariableDbIds.add(measurementVariableDto.getId());
			observationVariableNames.add(measurementVariableDto.getName());
		}

		final List<List<String>> data = Lists.newArrayList();

		final String year = this.getYearFromStudy(studyIdentifier);

		if (!CollectionUtils.isEmpty(results)) {

			for (final Object[] row : results) {
				final List<String> entry = Lists.newArrayList();

				if (year != null) {
					entry.add(year);
				}

				final int lastFixedColumn = 18;

				// studyDbId = nd_geolocation_id
				entry.add(String.valueOf(row[17]));

				final String locationName = (String) row[13];
				final String locationAbbreviation = (String) row[14];

				// studyName
				final String studyName = "Study-" + row[1];
				entry.add(studyName);

				// locationDbId
				entry.add(String.valueOf(row[lastFixedColumn]));

				// locationName
				if (StringUtils.isNotBlank(locationAbbreviation)) {
					entry.add(locationAbbreviation);
				} else if (StringUtils.isNotBlank(locationName)) {
					entry.add(locationName);
				} else {
					entry.add(studyName);
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

				/**
				 *
				 * x (Col) \\\\\\\\\\\\\\\\\\\\ \...|....|....|....\ \...|....|....|....\ \------------------\ y (Row) \...|....|....|....\
				 * \...|....|....|....\ \------------------\ \...|....|....|....\ \...|....|....|....\ \\\\\\\\\\\\\\\\\\\\
				 *
				 *
				 */
				Object x = row[11]; // COL
				Object y = row[10]; // ROW

				// If there is no row and col design,
				// get fieldmap row and col
				if (x == null || y == null) {
					x = row[15];
					y = row[16];
				}

				// X = col
				entry.add(String.valueOf(x));

				// Y = row
				entry.add(String.valueOf(y));

				// plotId
				entry.add(String.valueOf(row[12]));

				// phenotypic values
				int columnOffset = 1;
				for (int i = 0; i < traits.size(); i++) {
					final Object rowValue = row[lastFixedColumn + columnOffset];

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

		final TrialObservationTable dto = new TrialObservationTable().setStudyDbId(instanceDbId != null ? instanceDbId : studyIdentifier)
				.setObservationVariableDbIds(observationVariableDbIds).setObservationVariableNames(observationVariableNames).setData(data);

		dto.setHeaderRow(Lists.newArrayList("year", "studyDbId", "studyName", "locationDbId", "locationName", "germplasmDbId",
				"germplasmName", "observationUnitDbId", "plotNumber", "replicate", "blockNumber", "observationTimestamp", "entryType", "X",
				"Y", "plotId"));

		return dto;
	}

	@Override
	public StudyDetailsDto getStudyDetails(final Integer studyId) {
		try {
			final StudyMetadata studyMetadata = this.studyDataManager.getStudyMetadata(studyId);
			if (studyMetadata != null) {
				final StudyDetailsDto studyDetailsDto = new StudyDetailsDto();
				studyDetailsDto.setMetadata(studyMetadata);
				final List<UserDto> users = new ArrayList<>();
				final Map<String, String> properties = new HashMap<>();
				if (studyMetadata.getStudyType().equalsIgnoreCase(StudyServiceImpl.TRIAL_TYPE)) {
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
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getStudyDetails() query from study: " + studyId;
			StudyServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@Override
	public boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId) {
		final List queryResults;
		try {
			final SQLQuery query = this.getCurrentSession().createSQLQuery(StudyServiceImpl.SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED);
			query.setParameter("studyId", studyId);
			query.setParameterList("cvtermIds", ids);
			queryResults = query.list();

		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
					"Unexpected error in executing hasMeasurementDataEntered(studyId = " + studyId + ") query: " + he.getMessage(), he);
		}

		return !queryResults.isEmpty();
	}

	// TODO Move to DAO
	@Override
	public List<PhenotypeSearchDTO> searchPhenotypes(Integer pageSize, Integer pageNumber, PhenotypeSearchRequestDTO requestDTO) {

		StringBuilder queryString = new StringBuilder(PhenotypeQuery.PHENOTYPE_SEARCH);

		List<Integer> cvTermIds = requestDTO.getCvTermIds();

		if (requestDTO.getStudyDbId() != null) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER);
		}

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATION_FILTER);
		}

		final SQLQuery sqlQuery = this.getCurrentSession().createSQLQuery(queryString.toString());

		if (pageNumber != null && pageSize != null) {
			sqlQuery.setFirstResult(pageSize * (pageNumber - 1));
			sqlQuery.setMaxResults(pageSize);
		}

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			sqlQuery.setParameterList("cvTermIds", cvTermIds);
		}

		if (requestDTO.getStudyDbId() != null) {
			sqlQuery.setParameter("studyDbId", requestDTO.getStudyDbId());
		}

		sqlQuery.addScalar("nd_experiment_id").addScalar("observationUnitDbId", new StringType()).addScalar("observationUnitName")
			.addScalar("observationLevel").addScalar("plantNumber", new IntegerType()).addScalar("germplasmDbId", new StringType())
			.addScalar("germplasmName").addScalar("studyDbId", new StringType()).addScalar("studyName").addScalar("programName")
			.addScalar("FieldMapRow").addScalar("FieldMapCol").addScalar("plotNumber", new StringType())
			.addScalar("blockNumber", new StringType()).addScalar("replicate", new StringType()).addScalar("COL").addScalar("ROW")
			.addScalar("studyLocationDbId", new StringType()).addScalar("studyLocation", new StringType()).addScalar("entryType")
			.addScalar("entryNumber", new StringType());

		List<Object[]> results = sqlQuery.list();

		final Map<Integer, PhenotypeSearchDTO> observationUnitsByNdExpId = new LinkedHashMap<>();

		if (results != null && !results.isEmpty()) {

			// Process ObservationUnits (Measurement row)
			for (final Object[] row : results) {
				final PhenotypeSearchDTO observationUnit = new PhenotypeSearchDTO();

				Integer ndExperimentId = (Integer) row[0];
				observationUnit.setObservationUnitDbId((String) row[1]); // plot_id
				observationUnit.setObservationUnitName((String) row[2]);
				observationUnit.setObservationLevel((String) row[3]);
				observationUnit.setPlantNumber((String) row[4]);
				observationUnit.setGermplasmDbId((String) row[5]);
				observationUnit.setGermplasmName((String) row[6]);
				observationUnit.setStudyDbId((String) row[7]);
				observationUnit.setStudyName((String) row[8]);
				observationUnit.setProgramName((String) row[9]);
				final String x = (String) row[15]; // ROW
				final String y = (String) row[16]; // COL
				if (StringUtils.isBlank(x) || StringUtils.isBlank(y)) {
					observationUnit.setX((String) row[10]); // fieldMapRow
					observationUnit.setY((String) row[11]); // fieldMapCol
				}
				observationUnit.setX(x);
				observationUnit.setY(y);
				observationUnit.setPlotNumber((String) row[12]);
				observationUnit.setBlockNumber((String) row[13]);
				observationUnit.setReplicate((String) row[14]);
				observationUnit.setStudyLocationDbId((String) row[17]);
				observationUnit.setStudyLocation((String) row[18]);
				observationUnit.setEntryType((String) row[19]);
				observationUnit.setEntryNumber((String) row[20]);

				observationUnitsByNdExpId.put(ndExperimentId, observationUnit);
			}

			// Get observations (Traits)
			final SQLQuery observationsQuery = this.getCurrentSession().createSQLQuery(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATIONS);
			observationsQuery.setParameterList("ndExperimentIds", observationUnitsByNdExpId.keySet());
			List<Object[]> observationResults = observationsQuery.list();

			for (final Object[] result : observationResults) {
				Integer ndExperimentId = (Integer) result[0];

				PhenotypeSearchObservationDTO observation = new PhenotypeSearchObservationDTO();
				observation.setObservationDbId((Integer) result[1]);
				observation.setValue((String) result[4]);

				PhenotypeSearchDTO observationUnit = observationUnitsByNdExpId.get(ndExperimentId);
				// TODO solve duplicate nd_experiment_phenotype_id
				observationUnit.getObservations().add(observation);
			}
		}

		return new ArrayList<>(observationUnitsByNdExpId.values());
	}

	// TODO Move to DAO
	@Override
	public long countPhenotypes(PhenotypeSearchRequestDTO requestDTO) {
		StringBuilder queryString = new StringBuilder(PhenotypeQuery.PHENOTYPE_SEARCH);

		List<Integer> cvTermIds = requestDTO.getCvTermIds();
		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATION_FILTER);
		}

		final SQLQuery query = this.getCurrentSession().createSQLQuery("SELECT COUNT(1) FROM (" + queryString + ") T");

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			query.setParameterList("cvTermIds", cvTermIds);
		}

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public StudyServiceImpl setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
		return this;
	}

	public StudyServiceImpl setUserDataManager(final UserDataManager userDataManager) {
		this.userDataManager = userDataManager;
		return this;
	}

	private String getYearFromStudy(final int studyIdentifier) {
		final DmsProject project = new DmsProject();
		project.setProjectId(studyIdentifier);
		final ProjectProperty projectProperty = this.studyDataManager.getByVariableIdAndProjectID(project, TermId.START_DATE.getId());
		if (projectProperty != null) {
			return projectProperty.getValue().substring(0, 4);
		}
		return "";
	}

	public void setGermplasmDescriptors(final GermplasmDescriptors germplasmDescriptors) {
		this.germplasmDescriptors = germplasmDescriptors;
	}

	public void setDesignFactors(final DesignFactors designFactors) {
		this.designFactors = designFactors;
	}
}
