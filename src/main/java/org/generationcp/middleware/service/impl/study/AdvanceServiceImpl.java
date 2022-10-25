package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.naming.resolver.LocationDataResolver;
import org.generationcp.middleware.ruleengine.naming.resolver.SeasonDataResolver;
import org.generationcp.middleware.ruleengine.naming.resolver.SelectionTraitResolver;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.AdvanceService;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AdvanceServiceImpl implements AdvanceService {

	@Resource
	private DatasetService datasetService;

	@Resource
	private StudyInstanceService studyInstanceService;

	private final DaoFactory daoFactory;
	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitResolver selectionTraitResolver;
	private final LocationDataResolver locationDataResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitResolver = new SelectionTraitResolver();
		this.locationDataResolver = new LocationDataResolver();
	}

	@Override
	public List<Integer> advanceStudy(final Integer studyId, final AdvanceStudyRequest request) {

		final Map<Integer, DatasetDTO> datasetsByType = this.getDatasetsByType(studyId);
		final DatasetDTO plotDataset = datasetsByType.get(DatasetTypeEnum.PLOT_DATA.getId());
		final DatasetDTO environmentDataset = datasetsByType.get(DatasetTypeEnum.SUMMARY_DATA.getId());

		final List<ObservationUnitRow> plotObservations =
			this.getPlotObservations(studyId, plotDataset.getDatasetId(), request.getInstanceIds());
		if (CollectionUtils.isEmpty(plotObservations)) {
			return new ArrayList<>();
		}

		// TODO: check why is returning 0 for study 42756
		final List<ObservationUnitRow> trialObservations =
			this.getTrialObservations(studyId, environmentDataset.getDatasetId(), request.getInstanceIds());

		final List<MeasurementVariable> studyVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId, Arrays.asList(VariableType.STUDY_DETAIL.getId()));
		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyEnvironmentVariables =
			this.getStudyEnvironmentVariables(studyVariables, environmentDataset.getVariables());
		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyVariates = this.getStudyVariates(environmentDataset.getVariables());

		// Getting data related at study level
		final String seasonStudyLevel = this.seasonDataResolver.resolveStudyLevelData(studyEnvironmentVariables);
		final String selectionTraitStudyLevel = this.selectionTraitResolver
			.resolveStudyLevelData(studyId, request.getSelectionTraitRequest(),
				Stream.concat(studyEnvironmentVariables.stream(), studyVariates.stream()).collect(
					Collectors.toList()));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId =
			plotDataset.getVariables().stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));

		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber =
			this.studyInstanceService.getStudyInstances(studyId).stream()
				.collect(Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final List<Method> allMethods = this.daoFactory.getMethodDAO().getAllMethod();
		final Map<String, Method> breedingMethodsByCode =
			allMethods.stream()
				.collect(Collectors.toMap(Method::getMcode, method -> method, (method1, method2) -> method1));
		final Map<Integer, Method> breedingMethodsById =
			allMethods.stream()
				.collect(Collectors.toMap(Method::getMid, method -> method, (method1, method2) -> method1));

		final Map<Integer, Location> locationsByLocationId =
			this.getLocationsByLocationIdFromTrialObservationUnits(trialObservations);

		// TODO: Considering performance issue due to we will have a lot of objects in the memory... Is really needed to have the germplasm entity??? At least according to to the old advance we need only a few of germplasm properties:
		//  https://github.com/IntegratedBreedingPlatform/Fieldbook/blob/f242b4219653d926f20a06214c0c8b1083af148e/src/main/java/com/efficio/fieldbook/web/naming/impl/AdvancingSourceListFactory.java#L245
		//  Besides that part of the code, there are other properties that are required such as mgid, progenitors data, etc. Take a look how many things of germplasm we need in the advance process
		final Map<Integer, Germplasm> originGermplasmsByGid = this.getOriginGermplasmByGid(plotObservations);

		final Set<Integer> advancingSourceOriginGids = new HashSet<>();
		final List<NewAdvancingSource> advancingSources = new ArrayList<>();
		plotObservations.forEach(row -> {
			final Method breedingMethod =
				this.getBreedingMethod(request.getBreedingMethodSelectionRequest(), row.getVariables().values(), breedingMethodsByCode,
					breedingMethodsById);
			if (breedingMethod == null) {
				return;
			}

			final Germplasm germplasm = originGermplasmsByGid.get(row.getGid());
			final Integer plantsSelected =
				this.getPlantSelected(request, row.getVariables().values(), breedingMethodsByCode, breedingMethod.isBulkingMethod());
			if (breedingMethod.isBulkingMethod() == null || germplasm == null || plantsSelected == null) {
				return;
			}

			final NewAdvancingSource advancingSource =
				new NewAdvancingSource(germplasm, breedingMethod, studyId, environmentDataset.getDatasetId(), seasonStudyLevel,
					selectionTraitStudyLevel, plantsSelected);
			advancingSource.setTrialInstanceNumber(String.valueOf(row.getTrialInstance()));

			advancingSourceOriginGids.add(germplasm.getGid());

			// If study is Trial, then setting data if trial instance is not null
			if (advancingSource.getTrialInstanceNumber() != null) {
				this.setTrialIntanceObservations(trialObservations, row, advancingSource, studyInstancesByInstanceNumber);
			}

			this.resolveEnvironmentAndPlotLevelData(environmentDataset.getDatasetId(), plotDataset.getDatasetId(),
				request.getSelectionTraitRequest(), advancingSource, row, locationsByLocationId, plotDataVariablesByTermId);

			// Setting conditions for Breeders Cross ID
			// TODO: we only set conditions for breeders cross id. Find another way to pass conditions to breeders cross id
			advancingSource.setConditions(studyEnvironmentVariables);
			advancingSource
				.setReplicationNumber(this.getObservationValueByVariableId(row.getVariables().values(), TermId.REP_NO.getId()));

			advancingSource
				.setPlotNumber(this.getObservationValueByVariableId(row.getVariables().values(), TermId.PLOT_NO.getId()));

			// TODO: implement get plant selection for sample?
			//			if (advanceInfo.getAdvanceType().equals(AdvanceType.SAMPLE)) {
			//				if (samplesMap.containsKey(row.getExperimentId())) {
			//					plantsSelected = samplesMap.get(row.getExperimentId()).size();
			//					advancingSourceCandidate.setSamples(samplesMap.get(row.getExperimentId()));
			//				} else {
			//					continue;
			//				}
			//			}

			advancingSources.add(advancingSource);
		});

		if (!CollectionUtils.isEmpty(advancingSources)) {
			this.addNamesToAdvancingSource(advancingSources, advancingSourceOriginGids);
		}

		// TODO: create the advanced germplasm and save it

		// TODO: returns the recently created gerplasm::gids
		return null;
	}

	private Map<Integer, DatasetDTO> getDatasetsByType(final Integer studyId) {
		final List<Integer> datasetTypeIds = Arrays.asList(
			DatasetTypeEnum.SUMMARY_DATA.getId(),
			DatasetTypeEnum.PLOT_DATA.getId());
		return this.datasetService.getDatasetsWithVariables(studyId, new HashSet<>(datasetTypeIds)).stream().collect(Collectors.toMap(
			DatasetDTO::getDatasetTypeId, datasetDTO -> datasetDTO));
	}

	private List<ObservationUnitRow> getPlotObservations(final Integer studyId, final Integer plotDatasetId,
		final List<Integer> instancesIds) {

		final ObservationUnitsSearchDTO plotDataObservationsSearchDTO = new ObservationUnitsSearchDTO();
		plotDataObservationsSearchDTO.setInstanceIds(instancesIds);
		return this.datasetService
			.getObservationUnitRows(studyId, plotDatasetId, plotDataObservationsSearchDTO,
				new PageRequest(0, Integer.MAX_VALUE));
	}

	private List<ObservationUnitRow> getTrialObservations(final Integer studyId, final Integer environmentDatasetId,
		final List<Integer> instancesIds) {

		final List<MeasurementVariableDto> environmentConditions = this.getObservationSetVariables(environmentDatasetId,
			Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId()));
		final List<MeasurementVariableDto> environmentDetailsVariables =
			this.getObservationSetVariables(environmentDatasetId, Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));

		final ObservationUnitsSearchDTO trialObservationUnitsSearchDTO = new ObservationUnitsSearchDTO();
		trialObservationUnitsSearchDTO.setInstanceIds(instancesIds);
		trialObservationUnitsSearchDTO.setEnvironmentDatasetId(environmentDatasetId);
		trialObservationUnitsSearchDTO.setEnvironmentConditions(environmentConditions);
		trialObservationUnitsSearchDTO.setEnvironmentDetails(environmentDetailsVariables);
		return this.datasetService
			.getObservationUnitRows(studyId, environmentDatasetId, trialObservationUnitsSearchDTO,
				new PageRequest(0, Integer.MAX_VALUE));
	}

	// TODO: review a better way to obtain this variables
	private List<MeasurementVariable> getStudyEnvironmentVariables(final List<MeasurementVariable> studyVariables,
		final List<MeasurementVariable> environmentVariables) {

		final List<MeasurementVariable> conditions = studyVariables.stream()
			.filter(variable -> variable.getVariableType().getRole() != PhenotypicType.VARIATE)
			.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(environmentVariables)) {
			final List<MeasurementVariable> trialEnvironmentVariables = environmentVariables.stream()
				.filter(variable -> variable.getVariableType().getRole() == PhenotypicType.TRIAL_ENVIRONMENT)
				.collect(Collectors.toList());
			conditions.addAll(trialEnvironmentVariables);
		}
		return conditions;
	}

	// TODO: review a better way to obtain this
	private List<MeasurementVariable> getStudyVariates(final List<MeasurementVariable> studyVariables) {
		return studyVariables.stream()
			.filter(variable -> variable.getVariableType().getRole() == PhenotypicType.VARIATE)
			.collect(Collectors.toList());
	}

	private Optional<ObservationUnitData> getVariableById(final Collection<ObservationUnitData> variables,
		final Integer variableId) {
		return variables.stream()
			.filter(variable -> variableId.equals(variable.getVariableId()))
			.findFirst();
	}

	private String getObservationValueByVariableId(final Collection<ObservationUnitData> variables, final Integer variableId) {
		return this.getVariableById(variables, variableId)
			.map(ObservationUnitData::getValue)
			.orElse(null);
	}

	private List<MeasurementVariableDto> getObservationSetVariables(final Integer datasetId, final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes)
			.stream()
			.map(variable -> new MeasurementVariableDto(variable.getTermId(), variable.getName()))
			.collect(Collectors.toList());
	}

	private void setTrialIntanceObservations(final List<ObservationUnitRow> trialObservations, final ObservationUnitRow plotObservation,
		final NewAdvancingSource advancingSourceCandidate,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber) {
		final Integer trialInstanceNumber = plotObservation.getTrialInstance();
		trialObservations.stream()
			.filter(observationUnitRow -> Integer.valueOf(advancingSourceCandidate.getTrialInstanceNumber())
				.equals(observationUnitRow.getTrialInstance()))
			.findFirst()
			.ifPresent(trialObservation -> {
				if (studyInstancesByInstanceNumber.containsKey(trialInstanceNumber) && trialObservation != null) {
					this.getVariableById(trialObservation.getEnvironmentVariables().values(), TermId.LOCATION_ID.getId())
						.ifPresent(
							observationUnitData -> observationUnitData
								.setValue(String.valueOf(studyInstancesByInstanceNumber.get(trialInstanceNumber).getLocationId()))
						);
				}
				advancingSourceCandidate.setTrialInstanceObservation(trialObservation);
			});
	}

	// TODO: move getBreedingMethod to another class
	private Method getBreedingMethod(final AdvanceStudyRequest.BreedingMethodSelectionRequest request,
		final Collection<ObservationUnitData> observationVariables,
		final Map<String, Method> breedingMethodsByCode,
		final Map<Integer, Method> breedingMethodsById) {
		final Integer breedingMethodId = request.getMethodVariateId() == null ? request.getBreedingMethodId() :
			this.getBreedingMethodId(request.getMethodVariateId(), observationVariables, breedingMethodsByCode);
		if (breedingMethodId == null) {
			return null;
		}
		return breedingMethodsById.get(breedingMethodId);
	}

	// TODO: move getBreedingMethod to another class
	// TODO: we are asking for BREEDING_METHOD_VARIATE and BREEDING_METHOD_VARIATE_TEXT due to backward compatibility
	private Integer getBreedingMethodId(final Integer methodVariateId, final Collection<ObservationUnitData> variables,
		final Map<String, Method> breedingMethodsByCode) {
		// TODO: the user can select BREEDING_METHOD_VARIATE as variate variable ???
		if (TermId.BREEDING_METHOD_VARIATE.getId() == methodVariateId) {
			return this.getIntegerValue(this.getObservationValueByVariableId(variables, methodVariateId));
		} else if (TermId.BREEDING_METHOD_VARIATE_TEXT.getId() == methodVariateId
			|| TermId.BREEDING_METHOD_VARIATE_CODE.getId() == methodVariateId) {
			final String methodName = this.getObservationValueByVariableId(variables, methodVariateId);
			if (StringUtils.isEmpty(methodName)) {
				return null;
			}
			if (NumberUtils.isNumber(methodName)) {
				return Double.valueOf(methodName).intValue();
			}

			// coming from old fb or other sources
			final Method method = breedingMethodsByCode.get(methodName);
			if (method != null && (
				(methodVariateId == TermId.BREEDING_METHOD_VARIATE_TEXT.getId() && methodName.equalsIgnoreCase(method.getMname())) ||
					(methodVariateId == TermId.BREEDING_METHOD_VARIATE_CODE.getId() && methodName
						.equalsIgnoreCase(method.getMcode())))) {
				return method.getMid();
			}
		} else {
			// on load of study, this has been converted to id and not the code.
			return this.getIntegerValue(this.getObservationValueByVariableId(variables, methodVariateId));
		}
		return null;
	}

	private Integer getIntegerValue(final String value) {
		Integer integerValue = null;

		if (NumberUtils.isNumber(value)) {
			integerValue = Double.valueOf(value).intValue();
		}

		return integerValue;
	}

	private Map<Integer, Location> getLocationsByLocationIdFromTrialObservationUnits(
		final List<ObservationUnitRow> trialObservationUnitRows) {
		final Set<Integer> locationIds = this.getVariableValuesFromObservations(trialObservationUnitRows, TermId.LOCATION_ID.getId());
		return this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds)).stream()
			.collect(Collectors.toMap(Location::getLocid, location -> location));
	}

	private Map<Integer, Germplasm> getOriginGermplasmByGid(final List<ObservationUnitRow> plotObservations) {
		final Set<Integer> gids = plotObservations.stream()
			.map(ObservationUnitRow::getGid)
			.collect(Collectors.toSet());
		return this.daoFactory.getGermplasmDao().getByGIDList(new ArrayList<>(gids)).stream()
			.collect(Collectors.toMap(Germplasm::getGid, germplasm -> germplasm));
	}

	private Set<Integer> getVariableValuesFromObservations(final List<ObservationUnitRow> observations, final Integer variableId) {
		return observations.stream()
			.map(row -> row.getEnvironmentVariables().values())
			.flatMap(Collection::stream)
			.filter(observationUnitData -> observationUnitData.getVariableId().equals(variableId))
			.map(observationUnitData -> this.getIntegerValue(observationUnitData.getValue()))
			.collect(Collectors.toSet());
	}

	private void resolveEnvironmentAndPlotLevelData(final Integer environmentDatasetId, final Integer plotDatasetId,
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final NewAdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, Location> locationsByLocationId,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		this.locationDataResolver.resolveEnvironmentLevelData(source, locationsByLocationId);
		this.seasonDataResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);
		this.selectionTraitResolver
			.resolveEnvironmentLevelData(environmentDatasetId, selectionTraitRequest, source, plotDataVariablesByTermId);
		this.selectionTraitResolver
			.resolvePlotLevelData(plotDatasetId, selectionTraitRequest, source, row, plotDataVariablesByTermId);
	}

	// TODO: move plant selection code to another class
	Integer getPlantSelected(final AdvanceStudyRequest request, final Collection<ObservationUnitData> observationVariables,
		final Map<String, Method> breedingMethodsByCode, final boolean isBulkMethod) {

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		if (breedingMethodSelectionRequest.getBreedingMethodId() != null) {
			// User has selected the same Breeding Method for each advance
			return this.getLineSelectedForBreedingMethodVariable(request, isBulkMethod, observationVariables);
		}

		if (breedingMethodSelectionRequest.getMethodVariateId() != null) {
			// User has selected a variate that defines the breeding method for each advance
			final String rowBreedingMethodCode =
				this.getObservationValueByVariableId(observationVariables, breedingMethodSelectionRequest.getMethodVariateId());
			if (!StringUtils.isEmpty(rowBreedingMethodCode) && breedingMethodsByCode.containsKey(rowBreedingMethodCode)) {
				final Method method = breedingMethodsByCode.get(rowBreedingMethodCode);
				return this.getLineSelectedForBreedingMethodVariable(request, method.isBulkingMethod(), observationVariables);
			}

			return null;
		}

		return null;
	}

	// TODO: move plant selection code to another class
	private Integer getLineSelectedForBreedingMethodVariable(final AdvanceStudyRequest request, final Boolean isBulkMethod,
		final Collection<ObservationUnitData> observationVariables) {
		if (isBulkMethod == null) {
			return null;
		}

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
			request.getLineSelectionRequest();
		if (isBulkMethod) {
			if (breedingMethodSelectionRequest.getAllPlotsSelected() == null || !breedingMethodSelectionRequest.getAllPlotsSelected()) {
				// User has selected a variate that defines the number of lines selected from each plot
				final String plotVariateValue =
					this.getObservationValueByVariableId(observationVariables, breedingMethodSelectionRequest.getPlotVariateId());
				return this.getIntegerValue(plotVariateValue);
			} else {
				return 1;
			}
		} else {
			// User has selected the same number of lines for each plot
			if (lineSelectionRequest.getLinesSelected() == null) {
				final String lineVariateValue =
					this.getObservationValueByVariableId(observationVariables, lineSelectionRequest.getLineVariateId());
				return this.getIntegerValue(lineVariateValue);
			} else {
				// User has selected the same number of lines for each plot
				return lineSelectionRequest.getLinesSelected();
			}
		}
	}

	// TODO: at this point we have the names in the origin germplasm. Measure what will perform better of: getting all the names using a map (getNamesByGidsInMap)
	//  or getting the name from the germplasm (this will do a query to obtain the names for each germplasm)
	private void addNamesToAdvancingSource(final List<NewAdvancingSource> advancingSources, final Set<Integer> advancingSourceOriginGids) {
		final Map<Integer, List<Name>> namesByGids =
			this.daoFactory.getNameDao().getNamesByGidsInMap(new ArrayList<>(advancingSourceOriginGids));

		advancingSources.forEach(advancingSource -> {
			final List<Name> names = namesByGids.get(advancingSource.getOriginGermplasm().getGid());
			advancingSource.setNames(names);
		});
	}

}
