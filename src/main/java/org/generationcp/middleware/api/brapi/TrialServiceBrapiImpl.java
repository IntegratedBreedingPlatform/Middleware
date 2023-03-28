package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.TrialSearchRequestDTO;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.generationcp.middleware.service.api.user.ContactDto;
import org.generationcp.middleware.service.api.user.ContactVariable;
import org.generationcp.middleware.service.impl.study.StudyMeasurements;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class TrialServiceBrapiImpl implements TrialServiceBrapi {

	public static final String ENVIRONMENT = "-ENVIRONMENT";
	public static final String PLOT = "-PLOTDATA";
	private static final List<Integer> CONTACT_VARIABLE_IDS = ContactVariable.getIds();
	private static final List<String> CONTACT_VARIABLE_NAMES = ContactVariable.getNames();

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

	@Resource
	private StudyDataManager studyDataManager;

	private HibernateSessionProvider sessionProvider;
	private DaoFactory daoFactory;
	private WorkbenchDaoFactory workbenchDaoFactory;

	public TrialServiceBrapiImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public TrialServiceBrapiImpl(final HibernateSessionProvider sessionProvider, final HibernateSessionProvider workbenchSessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.workbenchDaoFactory = new WorkbenchDaoFactory(workbenchSessionProvider);
		this.sessionProvider = sessionProvider;
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier) {
		return this.getTrialObservationTable(studyIdentifier, null);
	}

	@Override
	public TrialObservationTable getTrialObservationTable(final int studyIdentifier, final Integer instanceDbId) {

		final StudyMeasurements studyMeasurements = new StudyMeasurements(this.sessionProvider.getSession());

		final List<MeasurementVariableDto> traits =
			this.daoFactory.getProjectPropertyDAO().getVariables(studyIdentifier, VariableType.TRAIT.getId());

		final List<MeasurementVariableDto> measurementVariables = Ordering.from((MeasurementVariableDto o1, MeasurementVariableDto o2) ->
			o1.getId() - o2.getId()).immutableSortedCopy(traits);

		final List<Object[]> results =
			studyMeasurements.getAllStudyDetailsAsTable(studyIdentifier, measurementVariables, instanceDbId);

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
	public List<StudySummary> searchTrials(final TrialSearchRequestDTO trialSearchRequestDTO, final Pageable pageable) {
		final List<StudySummary> studies = this.daoFactory.getTrialSearchDao().searchTrials(trialSearchRequestDTO, pageable);
		if (!CollectionUtils.isEmpty(studies)) {
			final List<Integer> studyIds = studies.stream().map(StudySummary::getTrialDbId).collect(Collectors.toList());
			final Map<Integer, List<ProjectProperty>> propsMap = this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(studyIds);
			final Map<Integer, List<ValueReference>> categoricalValuesMap = this.getCategoricalValuesMap(propsMap);
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getStudyExternalReferenceDAO().getExternalReferences(studyIds).stream().collect(groupingBy(
					ExternalReferenceDTO::getEntityId));
			final Map<Integer, List<InstanceMetadata>> trialInstancesMap = this.retrieveTrialInstancesMap(trialSearchRequestDTO, studyIds);
			final Map<Integer, MeasurementVariable> studySettingsVariablesMap = this.getVariablesMap(propsMap);
			for (final StudySummary studySummary : studies) {
				final Integer studyId = studySummary.getTrialDbId();
				this.retrieveStudySettings(propsMap, studySettingsVariablesMap, categoricalValuesMap, studySummary, studyId);
				studySummary.setExternalReferences(externalReferencesMap.get(studyId.toString()));
				studySummary
					.setInstanceMetaData(trialInstancesMap.get(studyId));

			}
		}
		return studies;
	}

	private Map<Integer, List<InstanceMetadata>> retrieveTrialInstancesMap(final TrialSearchRequestDTO trialSearchRequestDTO,
		final List<Integer> studyIds) {
		final List<Integer> locationIds = !CollectionUtils.isEmpty(trialSearchRequestDTO.getLocationDbIds()) ?
			trialSearchRequestDTO.getLocationDbIds().stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList()) :
			Collections.emptyList();
		return
			this.daoFactory.getGeolocationDao().getInstanceMetadata(studyIds, locationIds).stream().collect(groupingBy(
				InstanceMetadata::getTrialDbId));
	}

	@Override
	public long countSearchTrials(final TrialSearchRequestDTO trialSearchRequestDTO) {
		return this.daoFactory.getTrialSearchDao().countSearchTrials(trialSearchRequestDTO);
	}

	@Override
	public List<StudySummary> saveStudies(final String cropName, final List<TrialImportRequestDTO> trialImportRequestDtoList,
		final Integer userId) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(cropName);
		final List<String> studyIds = new ArrayList<>();
		final StudyType studyTypeByName = this.daoFactory.getStudyTypeDao().getStudyTypeByName(StudyTypeDto.TRIAL_NAME);
		final DatasetType envDatasetType = this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.SUMMARY_DATA.getId());
		final DatasetType plotDatasetType = this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.PLOT_DATA.getId());
		final List<String> studyDetailVariableNames = this.collectVariableNames(trialImportRequestDtoList);
		final Map<String, MeasurementVariable> variableNamesMap =
			this.daoFactory.getCvTermDao().getVariablesByNamesAndVariableType(studyDetailVariableNames, VariableType.STUDY_DETAIL);
		final Map<String, MeasurementVariable> variableSynonymsMap =
			this.daoFactory.getCvTermDao().getVariablesBySynonymsAndVariableType(studyDetailVariableNames, VariableType.STUDY_DETAIL);
		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.collectCategoricalVariables(variableNamesMap, variableSynonymsMap);

		for (final TrialImportRequestDTO trialImportRequestDto : trialImportRequestDtoList) {
			final DmsProject study = this.createStudy(userId, studyTypeByName, trialImportRequestDto);
			this.setStudySettings(cropName, trialImportRequestDto, study, variableNamesMap, variableSynonymsMap, categoricalVariablesMap);
			this.setStudyExternalReferences(trialImportRequestDto, study);
			this.daoFactory.getDmsProjectDAO().save(study);

			// Save environment and plot datasets
			this.saveDataset(study, TrialServiceBrapiImpl.ENVIRONMENT, envDatasetType, true);
			this.saveDataset(study, TrialServiceBrapiImpl.PLOT, plotDatasetType, false);

			this.saveTrialInstance(study, cropType);
			studyIds.add(study.getProjectId().toString());
		}
		// Unless the session is flushed, the latest changes are not reflected in DTOs returned by method
		this.sessionProvider.getSession().flush();
		final TrialSearchRequestDTO trialSearchRequestDTO = new TrialSearchRequestDTO();
		trialSearchRequestDTO.setTrialDbIds(studyIds);
		return this.searchTrials(trialSearchRequestDTO, null);
	}

	private Map<Integer, List<ValueReference>> collectCategoricalVariables(final Map<String, MeasurementVariable> variableNamesMap,
		final Map<String, MeasurementVariable> variableSynonymsMap) {
		final List<Integer> categoricalVariableIds = new ArrayList<>();
		categoricalVariableIds.addAll(
			variableNamesMap.values().stream()
				.filter(measurementVariable -> DataType.CATEGORICAL_VARIABLE.getId().equals(measurementVariable.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
					Collectors.toList()));
		categoricalVariableIds.addAll(
			variableSynonymsMap.values().stream()
				.filter(measurementVariable -> DataType.CATEGORICAL_VARIABLE.getId().equals(measurementVariable.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
					Collectors.toList()));
		return this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);
	}

	private List<String> collectVariableNames(final List<TrialImportRequestDTO> trialImportRequestDtoList) {
		final List<String> studyDetailVariableNames = new ArrayList<>();
		trialImportRequestDtoList.stream().forEach(dto ->
			studyDetailVariableNames
				.addAll(dto.getAdditionalInfo().keySet().stream().map(String::toUpperCase).collect(Collectors.toList()))
		);
		studyDetailVariableNames.addAll(CONTACT_VARIABLE_NAMES);
		return studyDetailVariableNames;
	}

	private DmsProject createStudy(final Integer userId, final StudyType studyTypeByName,
		final TrialImportRequestDTO trialImportRequestDto) {
		final DmsProject study = new DmsProject();
		final String trialDescription = trialImportRequestDto.getTrialDescription();
		study.setDescription(trialDescription);
		final String trialName = trialImportRequestDto.getTrialName();
		study.setName(trialName);
		final String programUUID = trialImportRequestDto.getProgramDbId();
		study.setProgramUUID(programUUID);
		final String startDate =
			Util.tryConvertDate(trialImportRequestDto.getStartDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
		if (startDate != null) {
			study.setStartDate(startDate);
		}
		final String endDate =
			Util.tryConvertDate(trialImportRequestDto.getEndDate(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
		if (endDate != null) {
			study.setEndDate(endDate);
		}
		study.setStudyUpdate(Util.getCurrentDateAsStringValue());
		study.setCreatedBy(String.valueOf(userId));
		study.setStudyType(studyTypeByName);
		study.setParent(new DmsProject(DmsProject.SYSTEM_FOLDER_ID));
		study.setDeleted(false);
		return study;
	}

	private void setStudyExternalReferences(final TrialImportRequestDTO trialImportRequestDto, final DmsProject study) {
		if (trialImportRequestDto.getExternalReferences() != null) {
			final List<StudyExternalReference> references = new ArrayList<>();
			trialImportRequestDto.getExternalReferences().forEach(reference -> {
				final StudyExternalReference externalReference =
					new StudyExternalReference(study, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			study.setExternalReferences(references);
		}
	}

	private DmsProject saveDataset(final DmsProject study, final String suffix,
		final DatasetType datasetType, final boolean isEnvironmentDataset) {
		final DmsProject dataset = new DmsProject();
		final String envDatasetname = study.getName() + suffix;
		dataset.setName(envDatasetname);
		final String envDatasetDescription = study.getDescription() + suffix;
		dataset.setDescription(envDatasetDescription);
		dataset.setProgramUUID(study.getProgramUUID());
		dataset.setStudy(study);
		dataset.setDatasetType(datasetType);
		dataset.setParent(study);
		this.addDatasetVariables(dataset, isEnvironmentDataset);
		this.daoFactory.getDmsProjectDAO().save(dataset);
		return dataset;
	}

	private void setStudySettings(final String cropName, final TrialImportRequestDTO trialImportRequestDto, final DmsProject study,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap,
		final Map<Integer, List<ValueReference>> categoricalValuesMap) {
		// Set contacts to study settings map
		this.setContactsAsStudySettings(trialImportRequestDto);
		final List<ProjectProperty> properties = new ArrayList<>();
		trialImportRequestDto.getAdditionalInfo().entrySet().forEach(entry -> {
			final String variableName = entry.getKey().toUpperCase();
			// Lookup variable by name first, then synonym
			final MeasurementVariable measurementVariable =
				variableNamesMap.containsKey(variableName) ? variableNamesMap.get(variableName) : variableSynonymsMap.get(variableName);
			if (measurementVariable != null) {
				measurementVariable.setValue(entry.getValue());
				final DataType dataType = DataType.getById(measurementVariable.getDataTypeId());
				final java.util.Optional<VariableValueValidator> dataValidator =
					this.variableDataValidatorFactory.getValidator(dataType);

				if (categoricalValuesMap.containsKey(measurementVariable.getTermId())) {
					measurementVariable.setPossibleValues(categoricalValuesMap.get(measurementVariable.getTermId()));
				}

				Integer rank = properties.size() + 1;

				// Skip COOPERATOR_ID and PI_ID
				if (TermId.COOPERATOOR_ID.getId() == measurementVariable.getTermId()
					|| TermId.PI_ID.getId() == measurementVariable.getTermId())
					return;

				if (TermId.COOPERATOR.getId() == measurementVariable.getTermId()) {
					// Special handling for COOPERATOR. When COOPERATOR variable is added to the study,
					// The system should automatically add its corresponding pair ID variable (i.e. COOPERATOR_ID).
					final String personFullName = entry.getValue();
					// Find the person by full name
					final Optional<Person> personOptional =
						this.getPersonByFullName(cropName, personFullName);
					// Store the name of the person as COOPERATOR variable in projectprop
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						personOptional.isPresent() ? personFullName : StringUtils.EMPTY, rank, TermId.COOPERATOR.getId(),
						entry.getKey()));
					// Store the id of the person as COOPERATOR_ID variable in projectprop
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						personOptional.isPresent() ? personOptional.get().getId().toString() : StringUtils.EMPTY, ++rank,
						TermId.COOPERATOOR_ID.getId(), entry.getKey()));
				} else if (TermId.PI_NAME.getId() == measurementVariable.getTermId()) {
					// Special handling for PI_NAME variable. When PI_NAME variables is added to the study,
					// The system should automatically add its corresponding pair ID variable (i.e. PI_NAME_ID).
					final String personFullName = entry.getValue();
					// Find the person by full name
					final Optional<Person> personOptional =
						this.getPersonByFullName(cropName, personFullName);
					// Store the name of the person as PI_NAME variable in projectprop
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						personOptional.isPresent() ? personFullName : StringUtils.EMPTY, rank, TermId.PI_NAME.getId(), entry.getKey()));
					// Store the name of the person as PI_ID variable in projectprop
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						personOptional.isPresent() ? personOptional.get().getId().toString() : StringUtils.EMPTY, ++rank,
						TermId.PI_ID.getId(), entry.getKey()));
				} else if (!dataValidator.isPresent() || dataValidator.get().isValid(measurementVariable)) {
					// Add the study setting with value if the value provided is valid.
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						measurementVariable.getValue(), rank, measurementVariable.getTermId(), entry.getKey()));
				} else {
					// Add the study setting with an empty value if the value provided is not valid.
					properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
						"", rank, measurementVariable.getTermId(), entry.getKey()));
				}
			}
		});
		study.setProperties(properties);

	}

	private void setContactsAsStudySettings(final TrialImportRequestDTO trialImportRequestDTO) {
		if (!CollectionUtils.isEmpty(trialImportRequestDTO.getContacts())) {
			// Limitation of BMS ontology, is we can only store add variable once as study setting so we can only save one set of contact variables
			final Optional<ContactDto>
				contactDto = trialImportRequestDTO.getContacts().stream().filter(c -> StringUtils.isNotEmpty(c.getName())).findFirst();
			if (contactDto.isPresent()) {
				trialImportRequestDTO.getAdditionalInfo().put(ContactVariable.CONTACT_NAME.getName(), contactDto.get().getName());
				trialImportRequestDTO.getAdditionalInfo().put(ContactVariable.CONTACT_EMAIL.getName(), contactDto.get().getEmail());
				trialImportRequestDTO.getAdditionalInfo().put(ContactVariable.CONTACT_ORG.getName(), contactDto.get().getInstituteName());
				trialImportRequestDTO.getAdditionalInfo().put(ContactVariable.CONTACT_TYPE.getName(), contactDto.get().getType());
			}
		}
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
		} else {
			final ProjectProperty entryTypeProp =
				new ProjectProperty(dataset, VariableType.ENTRY_DETAIL.getId(), null, 4, TermId.ENTRY_TYPE.getId(),
					"ENTRY_TYPE");
			properties.add(entryTypeProp);

			final ProjectProperty gidProp =
				new ProjectProperty(dataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, 5, TermId.GID.getId(),
					"GID");
			properties.add(gidProp);

			final ProjectProperty designationProp =
				new ProjectProperty(dataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, 6, TermId.DESIG.getId(),
					"DESIGNATION");
			properties.add(designationProp);

			final ProjectProperty entryNoProp =
				new ProjectProperty(dataset, VariableType.ENTRY_DETAIL.getId(), null, 7, TermId.ENTRY_NO.getId(),
					"ENTRY_NO");
			properties.add(entryNoProp);

		}
		dataset.setProperties(properties);
	}

	private void saveTrialInstance(final DmsProject study, final CropType crop) {
		// The default value of an instance's location name is "Unspecified Location"
		final java.util.Optional<Location> location = this.daoFactory.getLocationDAO().getUnspecifiedLocation();
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription("1");
		location.ifPresent(loc -> geolocation.setProperties(Collections
			.singletonList(new GeolocationProperty(geolocation, String.valueOf(loc.getLocid()), 1, TermId.LOCATION_ID.getId()))));
		this.daoFactory.getGeolocationDao().save(geolocation);

		// Study Experiment
		final ExperimentModel studyExperiment =
			this.experimentModelGenerator
				.generate(crop, study.getProjectId(), java.util.Optional.of(geolocation), ExperimentType.STUDY_INFORMATION);
		this.daoFactory.getExperimentDao().save(studyExperiment);
	}

	private Map<Integer, List<ValueReference>> getCategoricalValuesMap(final Map<Integer, List<ProjectProperty>> propsMap) {
		final List<Integer> studySettingVariableIds = new ArrayList<>();
		propsMap.values().stream().forEach(propList ->
			studySettingVariableIds.addAll(propList.stream().map(ProjectProperty::getVariableId).collect(Collectors.toList()))
		);
		return this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(studySettingVariableIds);
	}

	private Map<Integer, MeasurementVariable> getVariablesMap(final Map<Integer, List<ProjectProperty>> propsMap) {
		final List<String> studySettingVariableIds = new ArrayList<>();
		propsMap.values().stream().forEach(propList ->
			studySettingVariableIds.addAll(propList.stream().map(p -> String.valueOf(p.getVariableId())).collect(Collectors.toList()))
		);
		return this.daoFactory.getCvTermDao()
			.getVariablesByIdsAndVariableTypes(studySettingVariableIds, Collections.singletonList(VariableType.STUDY_DETAIL.getName()));
	}

	private void retrieveStudySettings(final Map<Integer, List<ProjectProperty>> propsMap,
		final Map<Integer, MeasurementVariable> studySettingsVariablesMap,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final StudySummary studySummary, final Integer studyId) {
		final Map<String, String> additionalProps = Maps.newHashMap();
		if (!CollectionUtils.isEmpty(propsMap.get(studyId))) {
			final ContactDto contactDto = new ContactDto();
			propsMap.get(studyId).stream().forEach(prop -> {
				final Integer variableId = prop.getVariableId();
				final String value = prop.getValue();
				if (CONTACT_VARIABLE_IDS.contains(variableId)) {
					contactDto.setFieldFromVariable(variableId, value);
				} else {
					this.processStudySetting(studySettingsVariablesMap, categoricalVariablesMap, additionalProps, variableId, value);
				}
			});
			if (contactDto.atLeastOneContactDetailProvided()) {
				studySummary.setContacts(Collections.singletonList(contactDto));
			}
			studySummary.setAdditionalInfo(additionalProps);
		}
	}

	private void processStudySetting(final Map<Integer, MeasurementVariable> studySettingsVariablesMap,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final Map<String, String> additionalProps, final Integer variableId, String value) {
		if (categoricalVariablesMap.containsKey(variableId) && StringUtils.isNotBlank(value) && NumberUtils.isDigits(value)) {
			final Integer categoricalId = Integer.parseInt(value);
			final Map<Integer, ValueReference> categoricalValues = categoricalVariablesMap.get(variableId).stream()
				.collect(Collectors.toMap(ValueReference::getId, Function.identity()));
			if (categoricalValues.containsKey(categoricalId)) {
				value = categoricalValues.get(categoricalId).getDescription();
			}
		}

		if (studySettingsVariablesMap.containsKey(variableId) && !StringUtils.isEmpty(value)) {
			additionalProps.put(studySettingsVariablesMap.get(variableId).getName(), value);
		}
	}

	String getYearFromStudy(final int studyIdentifier) {
		final String startDate = this.studyDataManager.getProjectStartDateByProjectId(studyIdentifier);
		if (startDate != null) {
			return startDate.substring(0, 4);
		}
		return startDate;
	}

	Optional<Person> getPersonByFullName(final String cropName, final String fullName) {
		final long count = this.workbenchDaoFactory.getWorkbenchUserDAO().countUsersByFullName(fullName);
		// Find the exact person by full name, if there are multiple matches, just return empty result.
		if (count == 1) {
			final Optional<WorkbenchUser> result = this.workbenchDaoFactory.getWorkbenchUserDAO().getUserByFullName(fullName);
			if (result.isPresent()) {
				// Check if the user has access to the crop
				final CropPerson cropPerson =
					this.workbenchDaoFactory.getCropPersonDAO().getByCropNameAndPersonId(cropName, result.get().getPerson().getId());
				if (cropPerson != null) {
					return Optional.of(result.get().getPerson());
				}
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	// Setters used for tests only
	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setWorkbenchDaoFactory(final WorkbenchDaoFactory workbenchDaoFactory) {
		this.workbenchDaoFactory = workbenchDaoFactory;
	}

	public void setVariableDataValidatorFactory(
		final VariableDataValidatorFactory variableDataValidatorFactory) {
		this.variableDataValidatorFactory = variableDataValidatorFactory;
	}

	public void setExperimentModelGenerator(
		final ExperimentModelGenerator experimentModelGenerator) {
		this.experimentModelGenerator = experimentModelGenerator;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}
}
