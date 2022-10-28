package org.generationcp.middleware.service.impl.study.advance;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.generator.SeedSourceGenerator;
import org.generationcp.middleware.ruleengine.naming.resolver.LocationDataResolver;
import org.generationcp.middleware.ruleengine.naming.resolver.SeasonDataResolver;
import org.generationcp.middleware.ruleengine.naming.resolver.SelectionTraitDataResolver;
import org.generationcp.middleware.ruleengine.naming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.advance.AdvanceService;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AdvanceServiceImpl implements AdvanceService {

	// TODO: move to common constants
	private static final String DATE_TIME_FORMAT = "yyyyMMdd";
	private static final String PLOT_NUMBER_VARIABLE_NAME = "PLOT_NUMBER_AP_text";
	private static final String TRIAL_INSTANCE_VARIABLE_NAME = "INSTANCE_NUMBER_AP_text";
	private static final String REP_NUMBER_VARIABLE_NAME = "REP_NUMBER_AP_text";
	private static final String PLANT_NUMBER_VARIABLE_NAME = "PLANT_NUMBER_AP_text";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	@Resource
	private DatasetService datasetService;

	@Resource
	private StudyInstanceService studyInstanceService;

	@Resource
	private SeedSourceGenerator seedSourceGenerator;

	@Resource
	private GermplasmService germplasmService;

	@Resource
	private OntologyDataManager ontologyDataManager;

	@Resource
	private NamingConventionService namingConventionService;

	private final DaoFactory daoFactory;
	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitDataResolver selectionTraitDataResolver;
	private final LocationDataResolver locationDataResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitDataResolver = new SelectionTraitDataResolver();
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
		final String selectionTraitStudyLevel = this.selectionTraitDataResolver
			.resolveStudyLevelData(studyId, request.getSelectionTraitRequest(),
				Stream.concat(studyEnvironmentVariables.stream(), studyVariates.stream()).collect(
					Collectors.toList()));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId =
			plotDataset.getVariables().stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));

		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber =
			this.studyInstanceService.getStudyInstances(studyId).stream()
				.collect(Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		final Map<Integer, Method> breedingMethodsById = new HashMap<>();
		this.daoFactory.getMethodDAO().getAllMethod().forEach(method -> {
			breedingMethodsByCode.put(method.getMcode(), method);
			breedingMethodsById.put(method.getMid(), method);
		});

		final List<Location> locations = this.getLocationsFromTrialObservationUnits(trialObservations);
		final Map<Integer, Location> locationsByLocationId =
			locations.stream().collect(Collectors.toMap(Location::getLocid, location -> location));
		final Map<String, String> locationNameByIds =
			locations.stream().collect(Collectors.toMap(location -> String.valueOf(location.getLocid()), Location::getLname));

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
			if (breedingMethod == null || breedingMethod.isBulkingMethod() == null) {
				return;
			}

			final Germplasm originGermplasm = originGermplasmsByGid.get(row.getGid());
			final Integer plantsSelected =
				this.getPlantSelected(request, row.getVariables().values(), breedingMethodsByCode, breedingMethod.isBulkingMethod());
			if (originGermplasm == null || plantsSelected == null || plantsSelected <= 0) {
				return;
			}

			final NewAdvancingSource advancingSource =
				new NewAdvancingSource(row, originGermplasm, breedingMethod, studyId, environmentDataset.getDatasetId(), seasonStudyLevel,
					selectionTraitStudyLevel, plantsSelected);

			advancingSourceOriginGids.add(originGermplasm.getGid());

			// TODO: add the following properties in the constructor
			// If study is Trial, then setting data if trial instance is not null
			final Integer trialInstanceNumber = row.getTrialInstance();
			if (trialInstanceNumber != null) {
				final ObservationUnitRow observationUnitRow =
					this.setTrialInstanceObservations(trialInstanceNumber, trialObservations, studyInstancesByInstanceNumber);
				advancingSource.setTrialInstanceObservation(observationUnitRow);
			}

			this.resolveEnvironmentAndPlotLevelData(environmentDataset.getDatasetId(), plotDataset.getDatasetId(),
				request.getSelectionTraitRequest(), advancingSource, row, locationsByLocationId, plotDataVariablesByTermId);

			// Setting conditions for Breeders Cross ID
			// TODO: we only set conditions for breeders cross id. Find another way to pass conditions to breeders cross id
			advancingSource.setConditions(studyEnvironmentVariables);

			// TODO: implement get plant selection for samples
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
			// From a performance point of view, it's better to get all the names at once instead of getting the name from the germplasm
			// for each line doing germplasm::getNames
			final Map<Integer, List<Name>> namesByGids =
				this.daoFactory.getNameDao().getNamesByGidsInMap(new ArrayList<>(advancingSourceOriginGids));
			final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);

			this.createAdvancedGermplasm(study.getName(), advancingSources, request.getBreedingMethodSelectionRequest(),
				studyEnvironmentVariables,
				environmentDataset.getVariables(), locationNameByIds, studyInstancesByInstanceNumber, namesByGids);

			// TODO: persists germplasm
		}

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

	private ObservationUnitRow setTrialInstanceObservations(final Integer trialInstanceNumber,
		final List<ObservationUnitRow> trialObservations,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber) {

		final Optional<ObservationUnitRow> optionalTrialObservation = trialObservations.stream()
			.filter(observationUnitRow -> trialInstanceNumber.equals(observationUnitRow.getTrialInstance()))
			.findFirst();
		if (optionalTrialObservation.isPresent()) {
			final ObservationUnitRow trialObservation = optionalTrialObservation.get();
			if (studyInstancesByInstanceNumber.containsKey(trialInstanceNumber)) {
				this.getVariableById(trialObservation.getEnvironmentVariables().values(), TermId.LOCATION_ID.getId())
					.ifPresent(
						observationUnitData -> observationUnitData
							.setValue(String.valueOf(studyInstancesByInstanceNumber.get(trialInstanceNumber).getLocationId()))
					);
			}
			return trialObservation;
		}
		return null;
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

	private List<Location> getLocationsFromTrialObservationUnits(
		final List<ObservationUnitRow> trialObservationUnitRows) {
		final Set<Integer> locationIds = this.getVariableValuesFromObservations(trialObservationUnitRows, TermId.LOCATION_ID.getId());
		return this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds));
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
		this.selectionTraitDataResolver
			.resolveEnvironmentLevelData(environmentDatasetId, selectionTraitRequest, source, plotDataVariablesByTermId);
		this.selectionTraitDataResolver
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

	private void createAdvancedGermplasm(final String studyName, final List<NewAdvancingSource> advancingSources,
		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest,
		final List<MeasurementVariable> studyEnvironmentVariables, final List<MeasurementVariable> environmentVariables,
		final Map<String, String> locationNameByIds,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber,
		final Map<Integer, List<Name>> namesByGids) {

		final Integer plotCodeVariableId = this.germplasmService.getPlotCodeField().getId();
		final Integer plotNumberVariableId = this.getVariableId(PLOT_NUMBER_VARIABLE_NAME);
		final Integer repNumberVariableId = this.getVariableId(REP_NUMBER_VARIABLE_NAME);
		final Integer trialInstanceVariableId = this.getVariableId(TRIAL_INSTANCE_VARIABLE_NAME);
		final Integer plantNumberVariableId = this.getVariableId(PLANT_NUMBER_VARIABLE_NAME);

		Map<String, Integer> keySequenceMap = new HashMap<>();

		for (final NewAdvancingSource advancingSource : advancingSources) {
			final Method breedingMethod = advancingSource.getBreedingMethod();
			final AtomicInteger selectionNumber = new AtomicInteger(1);
			final int iterationCount = Boolean.TRUE.equals(breedingMethod.isBulkingMethod()) ? 1 : advancingSource.getPlantsSelected();
			for (int i = 0; i < iterationCount; i++) {

				// TODO: implement it for samples
				final String plantNumber = null;
				//				final Iterator<SampleDTO> sampleIterator = row.getSamples().iterator();
				//				if (sampleIterator.hasNext()) {
				//					plantNumber = String.valueOf(sampleIterator.next().getSampleNumber());
				//				}

				final ObservationUnitRow plotObservation = advancingSource.getPlotObservation();
				final String plotNumber =
					this.getObservationValueByVariableId(plotObservation.getVariables().values(), TermId.PLOT_NO.getId());
				final String seedSource = this.generateSeedSource(studyName, selectionNumber.get(), plotNumber,
					breedingMethod.isBulkingMethod(), breedingMethodSelectionRequest.getAllPlotsSelected(),
					advancingSource.getPlantsSelected(), plotObservation,
					studyEnvironmentVariables, locationNameByIds, studyInstancesByInstanceNumber, environmentVariables);

				final Germplasm originGermplasm = advancingSource.getOriginGermplasm();
				final Germplasm advancedGermplasm = new Germplasm();
				if (originGermplasm.getGpid1() == 0 || originGermplasm.getMethod().isDerivative()) {
					advancedGermplasm.setGpid1(originGermplasm.getGid());
				} else {
					advancedGermplasm.setGpid1(originGermplasm.getGpid1());
				}

				advancedGermplasm.setGpid2(-1);
				advancedGermplasm.setGnpgs(-1);
				advancedGermplasm.setLgid(0);

				final Integer locationId = advancingSource.getHarvestLocationId();
				advancedGermplasm.setLocationId(locationId);

				final Integer date = Integer.valueOf(LocalDate.now().format(DATE_TIME_FORMATTER));
				advancedGermplasm.setGdate(date);

				advancedGermplasm.setReferenceId(0);
				advancedGermplasm.setGrplce(0);

				// check to see if a group ID (MGID) exists in the parent for this Germplasm, and set newly created germplasm if part of a
				// group ( > 0 )
				if (originGermplasm.getMgid() != null && originGermplasm.getMgid() > 0) {
					advancedGermplasm.setMgid(originGermplasm.getMgid());
				} else {
					advancedGermplasm.setMgid(0);
				}

				// TODO: add names???
//				final List<Name> names = namesByGids.get(advancingSource.getOriginGermplasm().getGid());
//				advancedGermplasm.setNames(names);
//				// TODO: add names
//				names.forEach(name -> {
//					name.setLocationId(locationId);
//					name.setNdate(date);
//					name.setReferenceId(0);
//				});

				advancedGermplasm.setMethod(breedingMethod);

				// TODO: add attributes
				final List<Attribute> attributes = new ArrayList<>();
				final Attribute plotCodeAttribute = this.createGermplasmAttribute(seedSource, plotCodeVariableId, locationId, date);
				attributes.add(plotCodeAttribute);

				if (plotNumberVariableId != null) {
					final Attribute plotNumberAttribute = this.createGermplasmAttribute(plotNumber, plotNumberVariableId, locationId, date);
					attributes.add(plotNumberAttribute);
				}

				final String replicationNumber = this.getObservationValueByVariableId(plotObservation.getVariables().values(), TermId.REP_NO
					.getId());
				if (repNumberVariableId != null) {
					final Attribute replicationNumberAttribute =
						this.createGermplasmAttribute(replicationNumber, repNumberVariableId, locationId, date);
					attributes.add(replicationNumberAttribute);
				}

				if (!StringUtils.isEmpty(plantNumber) && plantNumberVariableId != null) {
					final Attribute plantNumberAttribute =
						this.createGermplasmAttribute(plantNumber, plantNumberVariableId, locationId, date);
					attributes.add(plantNumberAttribute);
				}

				if (trialInstanceVariableId != null) {
					final Attribute trialInstanceNumberAttribute =
						this.createGermplasmAttribute(plotObservation.getTrialInstance().toString(), trialInstanceVariableId, locationId,
							date);
					attributes.add(trialInstanceNumberAttribute);
				}

				// TODO: handle exception properly
				try {
					advancingSource.setKeySequenceMap(keySequenceMap);
					final String generatedName = this.namingConventionService.generateAdvanceListName(advancingSource);

					final Name derivativeName = this.createDerivativeName(generatedName);
					advancedGermplasm.setNames(Arrays.asList(derivativeName));

					// Pass the key sequence map to the next line to process
					keySequenceMap = advancingSource.getKeySequenceMap();

				} catch (final RuleException e) {
					e.printStackTrace();
				}

				selectionNumber.incrementAndGet();
			}
		}
	}

	private String generateSeedSource(final String studyName, final int selectionNumber, final String plotNumber,
		final Boolean isBulkingMethod,
		final Boolean allPlotsSelected,
		final Integer plantsSelected,
		final ObservationUnitRow row,
		final List<MeasurementVariable> studyEnvironmentVariables, final Map<String, String> locationNameByIds,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber, final List<MeasurementVariable> environmentVariables) {
		// TODO: implement this code for samples
		final String sampleNo = null;
		//				final Iterator<SampleDTO> sampleIterator = row.getSamples().iterator();
		//				if (sampleIterator.hasNext()) {
		//					sampleNo = String.valueOf(sampleIterator.next().getSampleNumber());
		//				}

		final String seedSourceSelectionNumber;
		if (Boolean.TRUE.equals(isBulkingMethod)) {
			if (Boolean.TRUE.equals(allPlotsSelected)) {
				seedSourceSelectionNumber = null;
			} else {
				seedSourceSelectionNumber = String.valueOf(plantsSelected);
			}
		} else {
			seedSourceSelectionNumber = String.valueOf(selectionNumber);
		}

		return this.seedSourceGenerator
			.generateSeedSource(row, studyEnvironmentVariables, seedSourceSelectionNumber, plotNumber,
				studyName, sampleNo, locationNameByIds, studyInstancesByInstanceNumber,
				environmentVariables);
	}

	private Attribute createGermplasmAttribute(final String value, final Integer typeId,
		final Integer locationId, final Integer date) {
		final Attribute attribute = new Attribute();
		attribute.setAval(value);
		attribute.setTypeId(typeId);
		attribute.setAdate(date);
		attribute.setLocationId(locationId);
		return attribute;
	}

	private Integer getVariableId(final String name) {
		final Term term = this.ontologyDataManager.findTermByName(name, CvId.VARIABLES.getId());
		if (term == null) {
			return null;
		}
		return term.getId();
	}

	protected Name createDerivativeName(final String designation) {
		final Name name = new Name();
		name.setTypeId(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID());
		name.setNval(designation);
		name.setNstat(1);
		return name;
	}

}
