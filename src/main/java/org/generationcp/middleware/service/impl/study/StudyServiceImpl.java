
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
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.ObservationLevel;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private static final Logger LOG = LoggerFactory.getLogger(StudyServiceImpl.class);
	public static final String ENVIRONMENT = "-ENVIRONMENT";
	public static final String PLOT = "-PLOTDATA";

	private StudyMeasurements studyMeasurements;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	private static LoadingCache<StudyKey, String> studyIdToProgramIdCache;

	private DaoFactory daoFactory;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.studyMeasurements = new StudyMeasurements(sessionProvider.getSession());
		this.studyDataManager = new StudyDataManagerImpl(sessionProvider);

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
						entry.add(null);
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
				List<Integer> variableIds = environmentConditions.stream().map(MeasurementVariable::getTermId)
					.collect(Collectors.toList());
				if (!variableIds.isEmpty()) {
					environmentParameters.addAll(
						this.studyDataManager.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(instanceId, variableIds));
				}
				final List<MeasurementVariable> environmentDetails = this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(environmentDataset.getProjectId(),
						Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));
				variableIds = environmentDetails.stream().map(MeasurementVariable::getTermId)
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
				variableIds = environmentVariables.stream().map(MeasurementVariable::getTermId)
					.collect(Collectors.toList());
				properties.put("studyObjective", studyMetadata.getStudyObjective());
				properties.putAll(this.studyDataManager.getGeolocationPropsAndValuesByGeolocation(instanceId, variableIds));
				final Map<Integer, Map<String, String>> projectPropMap = this.daoFactory.getProjectPropertyDAO().getProjectPropsAndValuesByStudyIds(
					Collections.singletonList(studyMetadata.getNurseryOrTrialId()));
				if(projectPropMap.containsKey(studyMetadata.getNurseryOrTrialId())) {
					properties.putAll(projectPropMap.get(studyMetadata.getNurseryOrTrialId()));
				}
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
		final List<Integer> variableIds = measurementVariables.stream().map(MeasurementVariable::getTermId)
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
				final String value = geolocation.getGeodeticDatum() == null ? "" : geolocation.getGeodeticDatum();
				variableMap.get(TermId.GEODETIC_DATUM.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.GEODETIC_DATUM.getId()));
			}

		}
		return geolocationVariables;
	}

	@Override
	public boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId) {
		return this.daoFactory.getPhenotypeDAO().hasMeasurementDataEntered(ids, studyId);
	}

	@Override
	public List<PhenotypeSearchDTO> searchPhenotypes(final Integer pageSize, final Integer pageNumber,
		final PhenotypeSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().searchPhenotypes(pageSize, pageNumber, requestDTO);
	}

	@Override
	public long countPhenotypes(final PhenotypeSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypes(requestDTO);
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
			if(!CollectionUtils.isEmpty(studyInstanceDtos)) {
				final List<Integer> studyIds = new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getTrialDbId()))
					.collect(Collectors.toSet()));
				final Map<Integer, List<ObservationLevel>> observationLevelsMap = this.daoFactory.getDmsProjectDAO()
					.getObservationLevelsMap(studyIds);
				final Map<Integer, Integer> studyEnvironmentDatasetIdMap = this.daoFactory.getDmsProjectDAO()
					.getStudyIdEnvironmentDatasetIdMap(studyIds);

				final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap = new HashMap<>();
				final Map<Integer, Map<String, String>> studyAdditionalInfoMap = this.daoFactory.getProjectPropertyDAO()
					.getProjectPropsAndValuesByStudyIds(studyIds);

				for(final StudyInstanceDto studyInstanceDto: studyInstanceDtos) {
					final Integer trialDbId = Integer.valueOf(studyInstanceDto.getTrialDbId());
					final Integer studyDbId = Integer.valueOf(studyInstanceDto.getStudyDbId());
					final List<MeasurementVariable> environmentVariables =
						this.getEnvironmentVariables(studyEnvironmentVariablesMap, studyEnvironmentDatasetIdMap.get(trialDbId));

					final List<MeasurementVariable> environmentParameterVariables = new ArrayList<>();
					final List<Integer> variableIds = environmentVariables.stream().map(MeasurementVariable::getTermId)
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
					if(studyAdditionalInfoMap.containsKey(trialDbId)) {
						studyInstanceDto.getAdditionalInfo().putAll(studyAdditionalInfoMap.get(trialDbId));
					}

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

	List<MeasurementVariable> getEnvironmentVariables(
		final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap, final Integer environmentDatasetId) {
		final List<MeasurementVariable> environmentVariables;
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
		if (!CollectionUtils.isEmpty(studies)) {
			final List<Integer> studyIds = studies.stream().map(StudySummary::getTrialDbId).collect(Collectors.toList());
			final Map<Integer, List<ProjectProperty>> propsMap = this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(studyIds);
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getStudyExternalReferenceDAO().getExternalReferences(studyIds).stream().collect(groupingBy(
					ExternalReferenceDTO::getEntityId));
				final List<Integer> locationIds = studySearchFilter.getLocationDbId() != null ?
					Collections.singletonList(Integer.parseInt(studySearchFilter.getLocationDbId())) :
					Collections.emptyList();
			final Map<Integer, List<InstanceMetadata>> trialInstancesMap =
				this.daoFactory.getGeolocationDao().getInstanceMetadata(studyIds, locationIds).stream().collect(groupingBy(
					InstanceMetadata::getTrialDbId));

			final Map<Integer, Optional<DataType>> variableDataTypeMap = Maps.newHashMap();
			final Map<Integer, String> categoricalValueMap = Maps.newHashMap();
			for (final StudySummary studySummary : studies) {
				// Add study settings in optionalInfo map
				final Integer studyId = studySummary.getTrialDbId();
				this.setStudySettings(propsMap, variableDataTypeMap, categoricalValueMap, studySummary, studyId);
				System.out.println("studyId " + studyId + " , external references: " + externalReferencesMap.get(studyId));
				studySummary.setExternalReferences(externalReferencesMap.get(studyId.toString()));
				studySummary
					.setInstanceMetaData(trialInstancesMap.get(studyId));

			}
		}
		return studies;
	}

	private void setStudySettings(final Map<Integer, List<ProjectProperty>> propsMap,
		final Map<Integer, Optional<DataType>> variableDataTypeMap, final Map<Integer, String> categoricalValueMap,
		final StudySummary studySummary, final Integer studyId) {
		final Map<String, String> additionalProps = Maps.newHashMap();
		if (!CollectionUtils.isEmpty(propsMap.get(studyId))) {
			propsMap.get(studyId).stream().forEach(prop -> {
				final Integer variableId = prop.getVariableId();
				variableDataTypeMap.putIfAbsent(variableId, this.ontologyVariableDataManager.getDataType(prop.getVariableId()));
				final Optional<DataType> variableDataType = variableDataTypeMap.get(variableId);
				String value = prop.getValue();
				if (variableDataType.isPresent() && DataType.CATEGORICAL_VARIABLE.getId().equals(variableDataType.get().getId())
					&& StringUtils.isNotBlank(value) && NumberUtils.isDigits(value)) {
					final Integer categoricalId = Integer.parseInt(value);
					categoricalValueMap.putIfAbsent(categoricalId, this.ontologyVariableDataManager
						.retrieveVariableCategoricalValue(studySummary.getProgramDbId(), prop.getVariableId(), categoricalId));
					final String categoricalValue = categoricalValueMap.get(categoricalId);
					if (!StringUtils.isEmpty(categoricalValue)) {
						value = categoricalValue;
					}
				}
				if (!StringUtils.isEmpty(value)) {
					additionalProps.put(prop.getAlias(), value);
				}
			});
			studySummary.setOptionalInfo(additionalProps);
		}
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

	@Override
	public List<GermplasmStudyDto> getGermplasmStudies(final Integer gid) {
		return this.daoFactory.getStockDao().getGermplasmStudyDtos(gid);
	}

	@Override
	public List<StudySummary> saveStudies(final String cropName, final List<TrialImportRequestDTO> trialImportRequestDtoList, final Integer userId) {
		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropName);
		final List<String> studyIds = new ArrayList<>();
		for (final TrialImportRequestDTO trialImportRequestDto : trialImportRequestDtoList){
			final DmsProject study = new DmsProject();
			final String trialDescription = trialImportRequestDto.getTrialDescription();
			study.setDescription(trialDescription);
			final String trialName = trialImportRequestDto.getTrialName();
			study.setName(trialName);
			final String programUUID = trialImportRequestDto.getProgramDbId();
			study.setProgramUUID(programUUID);
			final String startDate = Util.tryConvertDate(trialImportRequestDto.getStartDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
			if (startDate != null) {
				study.setStartDate(startDate);
			}
			final String endDate = Util.tryConvertDate(trialImportRequestDto.getEndDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
			if (endDate != null) {
				study.setEndDate(endDate);
			}
			// TODO verify if study_update should be filled up upon creation
			study.setStudyUpdate(Util.getCurrentDateAsStringValue());
			study.setCreatedBy(String.valueOf(userId));
			study.setStudyType(this.daoFactory.getStudyTypeDao().getStudyTypeByName(StudyTypeDto.TRIAL_NAME));
			study.setParent(new DmsProject(DmsProject.SYSTEM_FOLDER_ID));
			study.setDeleted(false);
			// Save study settings
			if (!CollectionUtils.isEmpty(trialImportRequestDto.getAdditionalInfo())) {
				final List<ProjectProperty> properties = new ArrayList<>();
				final Map<String, Map<Integer, VariableType>> nameOrSynonymsMap = this.daoFactory.getCvTermDao()
					.getTermIdsWithTypeByNameOrSynonyms(new ArrayList<>(trialImportRequestDto.getAdditionalInfo().keySet()),
						CvId.VARIABLES.getId());
				trialImportRequestDto.getAdditionalInfo().entrySet().forEach(entry -> {
					final Map<Integer, VariableType> variableMap = nameOrSynonymsMap.get(entry.getKey().toUpperCase());
					// TODO check if VariableType is study detail
					if (!CollectionUtils.isEmpty(variableMap)) {
						final Integer rank = properties.size() + 1;
						properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(), entry.getValue(), rank, variableMap.keySet().iterator().next(),
							"TRIAL_INSTANCE"));
					}
				});
				study.setProperties(properties);
			}
			// Save external references
			if (trialImportRequestDto.getExternalReferences() != null) {
				final List<StudyExternalReference> references = new ArrayList<>();
				trialImportRequestDto.getExternalReferences().forEach(reference -> {
					final StudyExternalReference externalReference =
						new StudyExternalReference(study, reference.getReferenceID(), reference.getReferenceSource());
					references.add(externalReference);
				});
				study.setExternalReferences(references);
			}
			final DmsProjectDao dmsProjectDAO = this.daoFactory.getDmsProjectDAO();
			dmsProjectDAO.save(study);

			// Save environment and plot datasets
			final DmsProject envDataset = new DmsProject();
			final String envDatasetname = trialName + StudyServiceImpl.ENVIRONMENT;
			envDataset.setName(envDatasetname);
			final String envDatasetDescription = trialDescription + StudyServiceImpl.ENVIRONMENT;
			envDataset.setDescription(envDatasetDescription);
			envDataset.setProgramUUID(programUUID);
			envDataset.setStudy(study);
			envDataset.setDatasetType(this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.SUMMARY_DATA.getId()));
			envDataset.setParent(study);
			this.addDatasetVariables(envDataset, true);
			dmsProjectDAO.save(envDataset);

			final DmsProject plotDataset = new DmsProject();
			final String plotDatasetname = trialName + StudyServiceImpl.PLOT;
			plotDataset.setName(plotDatasetname);
			final String plotDatasetDescription = trialDescription + StudyServiceImpl.PLOT;
			plotDataset.setDescription(plotDatasetDescription);
			plotDataset.setProgramUUID(programUUID);
			plotDataset.setStudy(study);
			plotDataset.setDatasetType(this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.PLOT_DATA.getId()));
			plotDataset.setParent(study);
			this.addDatasetVariables(plotDataset,false);
			dmsProjectDAO.save(plotDataset);

			this.saveTrialInstance(study, envDataset, cropType);
			studyIds.add(study.getProjectId().toString());
		}
		// Unless the session is flushed, the latest changes are not reflected in DTOs returned by method
		this.sessionProvider.getSession().flush();
		final StudySearchFilter filter = new StudySearchFilter();
		filter.setTrialDbIds(studyIds);
		return this.getStudies(filter, null);
	}

	private void addDatasetVariables(final DmsProject dataset, final boolean isEnvironmentDataset) {
		final ProjectProperty datasetNameProp =
			new ProjectProperty(dataset, VariableType.STUDY_DETAIL.getId(), null, 1, TermId.DATASET_NAME.getId(),
				TermId.DATASET_NAME.name());
		final ProjectProperty datasetTitleProp =
			new ProjectProperty(dataset, VariableType.STUDY_DETAIL.getId(), null, 2, TermId.DATASET_TITLE.getId(),
				TermId.DATASET_TITLE.name());
		final ProjectProperty trialInstanceProp =
			new ProjectProperty(dataset, VariableType.ENVIRONMENT_DETAIL.getId(), null, 3, TermId.TRIAL_INSTANCE_FACTOR.getId(),
				"TRIAL_INSTANCE");
		final List<ProjectProperty> properties = new ArrayList<>();
		properties.addAll(Arrays.asList(datasetNameProp, datasetTitleProp, trialInstanceProp));
		if (isEnvironmentDataset) {
			final ProjectProperty locationNameProp =
				new ProjectProperty(dataset, VariableType.ENVIRONMENT_DETAIL.getId(), null, 4, TermId.LOCATION_ID.getId(),
					"LOCATION_NAME");
			properties.add(locationNameProp);
		}
		dataset.setProperties(properties);
	}

	private void saveTrialInstance(final DmsProject study, final DmsProject environmentDataset, final CropType crop) {
		// The default value of an instance's location name is "Unspecified Location"
		final java.util.Optional<Location> location = this.daoFactory.getLocationDAO().getUnspecifiedLocation();
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription("1");
		location.ifPresent(loc -> geolocation.setProperties(Collections
			.singletonList(new GeolocationProperty(geolocation, String.valueOf(loc.getLocid()), 1, TermId.LOCATION_ID.getId()))));
		this.daoFactory.getGeolocationDao().save(geolocation);

		// Study Experiment
		final ExperimentModel studyExperiment =
			this.experimentModelGenerator.generate(crop, study.getProjectId(), java.util.Optional.of(geolocation), ExperimentType.STUDY_INFORMATION);
		this.daoFactory.getExperimentDao().save(studyExperiment);

		// Environment Experiment
		final ExperimentModel envExperiment =
			this.experimentModelGenerator.generate(crop, environmentDataset.getProjectId(), java.util.Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);
		this.daoFactory.getExperimentDao().save(envExperiment);
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
