
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.ObservationLevel;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private static final Logger LOG = LoggerFactory.getLogger(StudyServiceImpl.class);

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_SELECT = "select count(*) as totalObservationUnits from "
		+ "nd_experiment nde \n"
		+ "    inner join project proj on proj.project_id = nde.project_id \n"
		+ "    inner join nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n";

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_WHERE = " where \n"
		+ "	proj.study_id = :studyIdentifier AND proj.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n"
		+ "    and gl.nd_geolocation_id = :instanceId ";

	public static final String SQL_FOR_HAS_MEASUREMENT_DATA_ENTERED =
		"SELECT nde.nd_experiment_id,cvterm_variable.cvterm_id,cvterm_variable.name, count(ph.value) \n" + " FROM \n" + " project p \n"
			+ "        INNER JOIN nd_experiment nde ON nde.project_id = p.project_id \n"
			+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
			+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
			+ "        LEFT JOIN phenotype ph ON ph.nd_experiment_id = nde.nd_experiment_id \n"
			+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n"
			+ " WHERE p.study_id = :studyId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n"
			+ " AND cvterm_variable.cvterm_id IN (:cvtermIds) AND ph.value IS NOT NULL\n" + " GROUP BY  cvterm_variable.name";

	public static final String SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_NO_NULL_VALUES =
		StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_SELECT
			+ "		LEFT JOIN phenotype ph ON ph.nd_experiment_id = nde.nd_experiment_id \n"
			+ StudyServiceImpl.SQL_FOR_COUNT_TOTAL_OBSERVATION_UNITS_WHERE + " and ph.value is not null ";

	private StudyMeasurements studyMeasurements;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private StudyDataManager studyDataManager;

	private static LoadingCache<StudyKey, String> studyIdToProgramIdCache;

	private DaoFactory daoFactory;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		final Session currentSession = this.getCurrentSession();
		this.studyMeasurements = new StudyMeasurements(currentSession);
		this.studyDataManager = new StudyDataManagerImpl(sessionProvider);

		final CacheLoader<StudyKey, String> studyKeyCacheBuilder = new CacheLoader<StudyKey, String>() {

			@Override
			public String load(final StudyKey key) throws Exception {
				return StudyServiceImpl.this.studyDataManager.getProject(key.getStudyId()).getProgramUUID();
			}
		};
		StudyServiceImpl.studyIdToProgramIdCache =
			CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MINUTES).build(studyKeyCacheBuilder);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	/**
	 * Only used for tests.
	 *
	 * @param trialMeasurements
	 */
	StudyServiceImpl(final StudyMeasurements trialMeasurements) {
		this.studyMeasurements = trialMeasurements;
		this.daoFactory = new DaoFactory(this.sessionProvider);
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
	public boolean hasCrossesOrSelections(final int studyId) {
		final GermplasmStudySourceSearchRequest searchParameters = new GermplasmStudySourceSearchRequest();
		searchParameters.setStudyId(studyId);
		return this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters) > 0;
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

		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.daoFactory.getProjectPropertyDAO().getVariables(studyIdentifier,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		return this.studyMeasurements.getAllMeasurements(studyIdentifier, selectionMethodsAndTraits,
			this.getGenericGermplasmDescriptors(studyIdentifier), this.getAdditionalDesignFactors(studyIdentifier), instanceId,
			pageNumber, pageSize, sortBy, sortOrder);
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
	public List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier) {
		final List<MeasurementVariableDto> traits =
			this.daoFactory.getProjectPropertyDAO().getVariables(studyIdentifier, VariableType.TRAIT.getId());
		return this.studyMeasurements.getMeasurement(studyIdentifier, traits, this.getGenericGermplasmDescriptors(studyIdentifier),
			this.getAdditionalDesignFactors(studyIdentifier), measurementIdentifier);
	}

	@Override
	public ObservationDto updateObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement) {

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
	public String getProgramUUID(final Integer studyIdentifier) {
		try {
			return StudyServiceImpl.studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop()));
		} catch (final ExecutionException e) {
			throw new MiddlewareQueryException(
				"Unexpected error updating observations. Please contact support for " + "further assistence.", e);
		}
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier) {
		return this.getTrialObservationTable(studyIdentifier, null);
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier, final Integer instanceDbId) {
		final List<MeasurementVariableDto> traits =
			this.daoFactory.getProjectPropertyDAO().getVariables(studyIdentifier, VariableType.TRAIT.getId());

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

		for (final Iterator<MeasurementVariableDto> iterator = measurementVariables.iterator(); iterator.hasNext(); ) {
			final MeasurementVariableDto measurementVariableDto = iterator.next();
			observationVariableDbIds.add(measurementVariableDto.getId());
			observationVariableNames.add(measurementVariableDto.getName());
		}

		final List<List<String>> data = Lists.newArrayList();

		final String year = this.getYearFromStudy(studyIdentifier);

		if (!CollectionUtils.isEmpty(results)) {

			for (final Object[] row : results) {
				final List<String> entry = Lists.newArrayList();

				entry.add(year);

				final int lastFixedColumn = 19;

				// studyDbId = nd_geolocation_id
				entry.add(String.valueOf(row[17]));

				final String locationName = (String) row[13];
				final String locationAbbreviation = (String) row[14];

				// studyName
				final String studyName = row[lastFixedColumn] + " Environment Number " + row[1];
				entry.add(studyName);

				// locationDbId
				entry.add(String.valueOf(row[18]));

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

				// obsUnitId
				entry.add(String.valueOf(row[12]));

				// phenotypic values
				int columnOffset = 1;
				for (int i = 0; i < traits.size(); i++) {
					final Object rowValue = row[lastFixedColumn + columnOffset];

					if (rowValue != null) {
						entry.add(String.valueOf(rowValue));
					} else {
						entry.add((String) null);
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
			"Y", "obsUnitId"));

		return dto;
	}

	@Override
	public StudyDetailsDto getStudyDetailsByInstance(final Integer instanceId) {
		try {
			final StudyMetadata studyMetadata = this.studyDataManager.getStudyMetadataForInstance(instanceId);
			if (studyMetadata != null) {
				final StudyDetailsDto studyDetailsDto = new StudyDetailsDto();
				studyDetailsDto.setMetadata(studyMetadata);

				final List<UserDto> users = new ArrayList<>();
				users.addAll(this.studyDataManager.getUsersForEnvironment(studyMetadata.getStudyDbId()));
				users.addAll(this.studyDataManager.getUsersAssociatedToStudy(studyMetadata.getNurseryOrTrialId()));
				studyDetailsDto.setContacts(users);

				final DmsProject environmentDataset =
					this.daoFactory.getDmsProjectDAO()
						.getDatasetsByTypeForStudy(studyMetadata.getTrialDbId(), DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
				final List<MeasurementVariable> environmentConditions = this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(environmentDataset.getProjectId(),
						Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId()));
				final List<MeasurementVariable> environmentParameters = new ArrayList<>();
				List<Integer> variableIds = environmentConditions.stream().map(measurementVariable -> measurementVariable.getTermId())
					.collect(Collectors.toList());
				if (!variableIds.isEmpty()) {
					environmentParameters.addAll(
						this.studyDataManager.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(instanceId, variableIds));
				}
				final List<MeasurementVariable> environmentDetails = this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(environmentDataset.getProjectId(),
						Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));
				variableIds = environmentDetails.stream().map(measurementVariable -> measurementVariable.getTermId())
					.collect(Collectors.toList());
				if (!variableIds.isEmpty()) {
					environmentParameters.addAll(
						this.studyDataManager.getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(instanceId, variableIds));
				}

				final List<MeasurementVariable> environmentVariables = new ArrayList<>(environmentConditions);
				environmentVariables.addAll(environmentDetails);
				environmentParameters.addAll(this.createGeolocationVariables(environmentVariables, instanceId));
				studyDetailsDto.setEnvironmentParameters(environmentParameters);

				final Map<String, String> properties = new HashMap<>();
				variableIds = environmentVariables.stream().map(measurementVariable -> measurementVariable.getTermId())
					.collect(Collectors.toList());
				properties.put("studyObjective", studyMetadata.getStudyObjective());
				properties.putAll(this.studyDataManager.getGeolocationPropsAndValuesByGeolocation(instanceId, variableIds));
				properties.putAll(this.studyDataManager.getProjectPropsAndValuesByStudy(studyMetadata.getNurseryOrTrialId(), variableIds));
				studyDetailsDto.setAdditionalInfo(properties);
				return studyDetailsDto;
			}
			return null;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getStudyDetailsForGeolocation() query with instanceId: " + instanceId;
			StudyServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private List<MeasurementVariable> createGeolocationVariables(final List<MeasurementVariable> measurementVariables,
		final Integer geolocationId) {
		final List<MeasurementVariable> geolocationVariables = new ArrayList<>();
		final List<Integer> variableIds = measurementVariables.stream().map(measurementVariable -> measurementVariable.getTermId())
			.collect(Collectors.toList());
		if (variableIds.contains(TermId.ALTITUDE.getId()) || variableIds.contains(TermId.LATITUDE.getId())
			|| variableIds.contains(TermId.LONGITUDE.getId()) || variableIds.contains(TermId.GEODETIC_DATUM.getId())) {
			final Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(geolocationId);
			final Map<Integer, MeasurementVariable> variableMap = new HashMap<>();
			for (final MeasurementVariable mvar : measurementVariables) {
				variableMap.put(mvar.getTermId(), mvar);
			}
			if (variableIds.contains(TermId.ALTITUDE.getId())) {
				final String value = geolocation.getAltitude() == null ? "" : geolocation.getAltitude().toString();
				variableMap.get(TermId.ALTITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.ALTITUDE.getId()));
			}
			if (variableIds.contains(TermId.LATITUDE.getId())) {
				final String value = geolocation.getLatitude() == null ? "" : geolocation.getLatitude().toString();
				variableMap.get(TermId.LATITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.LATITUDE.getId()));
			}
			if (variableIds.contains(TermId.LONGITUDE.getId())) {
				final String value = geolocation.getLongitude() == null ? "" : geolocation.getLongitude().toString();
				variableMap.get(TermId.LONGITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.LONGITUDE.getId()));
			}
			if (variableIds.contains(TermId.GEODETIC_DATUM.getId())) {
				variableMap.get(TermId.GEODETIC_DATUM.getId()).setValue(geolocation.getGeodeticDatum());
				geolocationVariables.add(variableMap.get(TermId.GEODETIC_DATUM.getId()));
			}

		}
		return geolocationVariables;
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

	@Override
	public List<PhenotypeSearchDTO> searchPhenotypes(final Integer pageSize, final Integer pageNumber,
		final PhenotypeSearchRequestDTO requestDTO) {
		return this.getPhenotypeDao().searchPhenotypes(pageSize, pageNumber, requestDTO);
	}

	@Override
	public long countPhenotypes(final PhenotypeSearchRequestDTO requestDTO) {
		return this.getPhenotypeDao().countPhenotypes(requestDTO);
	}

	@Override
	public List<StudyInstanceDto> getStudyInstances(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().getStudyInstances(studySearchFilter, pageable);
	}
	@Override
	public List<StudyInstanceDto> getStudyInstancesWithMetadata(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		try {
			final List<StudyInstanceDto> studyInstanceDtos = this.daoFactory.getDmsProjectDAO()
				.getStudyInstances(studySearchFilter, pageable);
			if(studyInstanceDtos != null) {
				final List<Integer> studyIds = new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getTrialDbId()))
					.collect(Collectors.toSet()));
				final Map<Integer, List<ObservationLevel>> observationLevelsMap = this.daoFactory.getDmsProjectDAO()
					.getObservationLevelsMap(studyIds);
				final Map<Integer, Integer> studyEnvironmentDatasetIdMap = this.daoFactory.getDmsProjectDAO()
					.getStudyIdEnvironmentDatasetIdMap(studyIds);

				final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap = new HashMap<>();
				final Map<Integer, Map<String, String>> studyAdditionalInfoMap = new HashMap<>();

				for(final StudyInstanceDto studyInstanceDto: studyInstanceDtos) {
					final Integer trialDbId = Integer.valueOf(studyInstanceDto.getTrialDbId());
					final Integer studyDbId = Integer.valueOf(studyInstanceDto.getStudyDbId());
					final List<MeasurementVariable> environmentVariables =
						this.getEnvironmentVariables(studyEnvironmentVariablesMap, studyEnvironmentDatasetIdMap.get(trialDbId));

					final List<MeasurementVariable> environmentParameterVariables = new ArrayList<>();
					final List<Integer> variableIds = environmentVariables.stream().map(measurementVariable -> measurementVariable.getTermId())
						.collect(Collectors.toList());
					if (!variableIds.isEmpty()) {
						environmentParameterVariables.addAll(
							this.studyDataManager
								.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(studyDbId, variableIds));
						environmentParameterVariables.addAll(
							this.studyDataManager
								.getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(studyDbId, variableIds));
					}

					environmentParameterVariables.addAll(this.createGeolocationVariables(environmentVariables, studyDbId));

					final List<EnvironmentParameter> environmentParameters = environmentParameterVariables.stream()
						.map(variable -> new EnvironmentParameter(variable)).collect(Collectors.toList());
					studyInstanceDto.setEnvironmentParameters(environmentParameters);

					studyInstanceDto.getAdditionalInfo()
						.putAll(this.studyDataManager.getGeolocationPropsAndValuesByGeolocation(studyDbId, variableIds));
					studyInstanceDto.getAdditionalInfo()
						.putAll(this.getStudyAdditionalInfo(trialDbId, studyAdditionalInfoMap, variableIds));

					studyInstanceDto.setObservationLevels(observationLevelsMap.get(trialDbId));
				}
			}
			return studyInstanceDtos;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getStudyInstances()";
			StudyServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	Map<String, String> getStudyAdditionalInfo(final Integer trialDbId, final Map<Integer, Map<String, String>> studyAdditonalInfoMap,
		final List<Integer> variableIds) {
		Map<String, String> studyAdditionalInfo;
		if(studyAdditonalInfoMap.get(trialDbId) == null) {
			studyAdditionalInfo = this.studyDataManager.getProjectPropsAndValuesByStudy(trialDbId, variableIds);
			studyAdditonalInfoMap.put(trialDbId, studyAdditionalInfo);
		} else {
			studyAdditionalInfo = studyAdditonalInfoMap.get(trialDbId);
		}
		return studyAdditionalInfo;
	}

	List<MeasurementVariable> getEnvironmentVariables(
		final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap, final Integer environmentDatasetId) {
		List<MeasurementVariable> environmentVariables;
		if(studyEnvironmentVariablesMap.get(environmentDatasetId) == null) {
			environmentVariables = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(
					environmentDatasetId,
					Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId(), VariableType.ENVIRONMENT_DETAIL.getId()));
			studyEnvironmentVariablesMap.put(environmentDatasetId, environmentVariables);
		} else {
			environmentVariables = studyEnvironmentVariablesMap.get(environmentDatasetId);
		}
		return environmentVariables;
	}

	@Override
	public long countStudyInstances(final StudySearchFilter studySearchFilter) {
		return this.daoFactory.getDmsProjectDAO().countStudyInstances(studySearchFilter);
	}

	@Override
	public List<StudySummary> getStudies(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final List<StudySummary> studies = this.daoFactory.getDmsProjectDAO().getStudies(studySearchFilter, pageable);
		final Map<Integer, StudySummary> studiesMap = studies.stream().collect(Collectors.toMap(StudySummary::getStudyDbid, Function.identity()));
		final Set<Integer> studyIds = studiesMap.keySet();
		if (!CollectionUtils.isEmpty(studyIds)) {
			final List<DmsProject> projects = this.daoFactory.getDmsProjectDAO().getByIds(studyIds);
			final Map<Integer, Optional<DataType>> variableDataTypeMap = Maps.newHashMap();
			// Add study settings in optionalInfo map
			for (final DmsProject dmsProject : projects) {
				final StudySummary studySummary = studiesMap.get(dmsProject.getProjectId());
				final Map<String, String> additionalProps = Maps.newHashMap();
				for (final ProjectProperty prop : dmsProject.getProperties()) {
					final Integer variableId = prop.getVariableId();
					if (!variableDataTypeMap.containsKey(variableId)) {
						final Optional<DataType> dataType = this.ontologyVariableDataManager.getDataType(prop.getVariableId());
						variableDataTypeMap.put(variableId, dataType);
					}
					final Optional<DataType> variableDataType = variableDataTypeMap.get(variableId);
					String value = prop.getValue();
					if (variableDataType.isPresent() && DataType.CATEGORICAL_VARIABLE.getId().equals(variableDataType.get().getId())
							&& StringUtils.isNotBlank(value) && NumberUtils.isDigits(value)) {
						final Integer categoricalId = Integer.parseInt(value);
						final String categoricalValue = this.ontologyVariableDataManager
							.retrieveVariableCategoricalValue(dmsProject.getProgramUUID(), prop.getVariableId(), categoricalId);
						if (!StringUtils.isEmpty(categoricalValue)) {
							value = categoricalValue;
						}
					}

					if (variableId.equals(TermId.SEASON_VAR_TEXT.getId())) {
						studySummary.addSeason(value);
					} else if (variableId.equals(TermId.LOCATION_ID.getId())) {
						studySummary.setLocationId(!StringUtils.isEmpty(value) ? value : null);
					} else {
						additionalProps.put(prop.getAlias(), value);
					}
				}

				studySummary.setOptionalInfo(additionalProps).setName(dmsProject.getName()).setProgramDbId(dmsProject.getProgramUUID())
					.setStudyDbid(dmsProject.getProjectId());
				final List<Integer> locationIds = studySearchFilter.getLocationDbId() != null ?
					Collections.singletonList(Integer.parseInt(studySearchFilter.getLocationDbId())) :
					Collections.emptyList();
				studySummary
					.setInstanceMetaData(this.daoFactory.getGeolocationDao().getInstanceMetadata(dmsProject.getProjectId(), locationIds));
			}

		}
		return studies;
	}

	@Override
	public long countStudies(final StudySearchFilter studySearchFilter) {
		return this.daoFactory.getDmsProjectDAO().countStudies(studySearchFilter);
	}

	@Override
	public boolean studyHasGivenDatasetType(final Integer studyId, final Integer datasetTypeId) {
		final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.MEANS_DATA.getId());
		return (!org.springframework.util.CollectionUtils.isEmpty(datasets));
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	String getYearFromStudy(final int studyIdentifier) {
		final String startDate = this.studyDataManager.getProjectStartDateByProjectId(studyIdentifier);
		if (startDate != null) {
			return startDate.substring(0, 4);
		}
		return startDate;
	}

	public void setStudyMeasurements(final StudyMeasurements studyMeasurements) {
		this.studyMeasurements = studyMeasurements;
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
