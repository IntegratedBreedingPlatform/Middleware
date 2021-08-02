package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Maps;
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
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
public class TrialServiceBrapiImpl extends Service implements TrialServiceBrapi {

	public static final String ENVIRONMENT = "-ENVIRONMENT";
	public static final String PLOT = "-PLOTDATA";

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

	private final DaoFactory daoFactory;

	public TrialServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudySummary> getStudies(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final List<StudySummary> studies = this.daoFactory.getDmsProjectDAO().getStudies(studySearchFilter, pageable);
		if (!CollectionUtils.isEmpty(studies)) {
			final List<Integer> studyIds = studies.stream().map(StudySummary::getTrialDbId).collect(Collectors.toList());
			final Map<Integer, List<ProjectProperty>> propsMap = this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(studyIds);
			final Map<Integer, List<ValueReference>> categoricalValuesMap = this.getCategoricalValuesMap(propsMap);
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getStudyExternalReferenceDAO().getExternalReferences(studyIds).stream().collect(groupingBy(
					ExternalReferenceDTO::getEntityId));
			final List<Integer> locationIds = studySearchFilter.getLocationDbId() != null ?
				Collections.singletonList(Integer.parseInt(studySearchFilter.getLocationDbId())) :
				Collections.emptyList();
			final Map<Integer, List<InstanceMetadata>> trialInstancesMap =
				this.daoFactory.getGeolocationDao().getInstanceMetadata(studyIds, locationIds).stream().collect(groupingBy(
					InstanceMetadata::getTrialDbId));

			for (final StudySummary studySummary : studies) {
				final Integer studyId = studySummary.getTrialDbId();
				this.retrieveStudySettings(propsMap, categoricalValuesMap, studySummary, studyId);
				studySummary.setExternalReferences(externalReferencesMap.get(studyId.toString()));
				studySummary
					.setInstanceMetaData(trialInstancesMap.get(studyId));

			}
		}
		return studies;
	}

	@Override
	public long countStudies(final StudySearchFilter studySearchFilter) {
		return this.daoFactory.getDmsProjectDAO().countStudies(studySearchFilter);
	}

	@Override
	public List<StudySummary> saveStudies(final String cropName, final List<TrialImportRequestDTO> trialImportRequestDtoList,
		final Integer userId) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(cropName);
		final List<String> studyIds = new ArrayList<>();
		final StudyType studyTypeByName = this.daoFactory.getStudyTypeDao().getStudyTypeByName(StudyTypeDto.TRIAL_NAME);
		final DatasetType envDatasetType = this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.SUMMARY_DATA.getId());
		final DatasetType plotDatasetType = this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.PLOT_DATA.getId());
		final List<String> studyDetailVariableNames = new ArrayList<>();
		trialImportRequestDtoList.stream().forEach(dto ->
				studyDetailVariableNames
					.addAll(dto.getAdditionalInfo().keySet().stream().map(String::toUpperCase).collect(Collectors.toList()))
		);
		final Map<String, MeasurementVariable> variableNamesMap =
			this.daoFactory.getCvTermDao().getVariablesByNamesAndVariableType(studyDetailVariableNames, VariableType.STUDY_DETAIL);
		final Map<String, MeasurementVariable> variableSynonymsMap =
			this.daoFactory.getCvTermDao().getVariablesBySynonymsAndVariableType(studyDetailVariableNames, VariableType.STUDY_DETAIL);
		final List<Integer> categoricalVariableIds = new ArrayList<>();
		categoricalVariableIds.addAll(
			variableNamesMap.values().stream().filter(var -> DataType.CATEGORICAL_VARIABLE.getId().equals(var.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
				Collectors.toList()));
		categoricalVariableIds.addAll(
			variableSynonymsMap.values().stream().filter(var -> DataType.CATEGORICAL_VARIABLE.getId().equals(var.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
				Collectors.toList()));
		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);

		for (final TrialImportRequestDTO trialImportRequestDto : trialImportRequestDtoList) {
			final DmsProject study = this.createStudy(userId, studyTypeByName, trialImportRequestDto);
			this.setStudySettings(trialImportRequestDto, study, variableNamesMap, variableSynonymsMap, categoricalVariablesMap);
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
		final StudySearchFilter filter = new StudySearchFilter();
		filter.setTrialDbIds(studyIds);
		return this.getStudies(filter, null);
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

	private void setStudySettings(final TrialImportRequestDTO trialImportRequestDto, final DmsProject study,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap,
		final Map<Integer, List<ValueReference>> categoricalValuesMap) {
		if (!CollectionUtils.isEmpty(trialImportRequestDto.getAdditionalInfo())) {
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
					if (!dataValidator.isPresent() || dataValidator.get().isValid(measurementVariable)) {
						final Integer rank = properties.size() + 1;
						properties.add(new ProjectProperty(study, VariableType.STUDY_DETAIL.getId(),
							measurementVariable.getValue(), rank, measurementVariable.getTermId(), entry.getKey()));
					}
				}
			});
			study.setProperties(properties);
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
				new ProjectProperty(dataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, 4, TermId.ENTRY_TYPE.getId(),
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
				new ProjectProperty(dataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, 7, TermId.ENTRY_NO.getId(),
					"ENTRY_NO");
			properties.add(entryNoProp);

			final ProjectProperty obsUnitIdProp =
				new ProjectProperty(dataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), null, 8, TermId.OBS_UNIT_ID.getId(),
					"OBS_UNIT_ID");
			properties.add(obsUnitIdProp);

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

	private void retrieveStudySettings(final Map<Integer, List<ProjectProperty>> propsMap,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final StudySummary studySummary, final Integer studyId) {
		final Map<String, String> additionalProps = Maps.newHashMap();
		if (!CollectionUtils.isEmpty(propsMap.get(studyId))) {
			propsMap.get(studyId).stream().forEach(prop -> {
				final Integer variableId = prop.getVariableId();
				String value = prop.getValue();
				if (categoricalVariablesMap.containsKey(variableId) && StringUtils.isNotBlank(value) && NumberUtils.isDigits(value)) {
					final Integer categoricalId = Integer.parseInt(value);
					final Map<Integer, ValueReference> categoricalValues = categoricalVariablesMap.get(variableId).stream()
						.collect(Collectors.toMap(ValueReference::getId, Function.identity()));
					if (categoricalValues.containsKey(categoricalId)) {
						value = categoricalValues.get(categoricalId).getDescription();
					}
				}
				if (!StringUtils.isEmpty(value)) {
					additionalProps.put(prop.getAlias(), value);
				}
			});
			studySummary.setAdditionalInfo(additionalProps);
		}
	}


}
