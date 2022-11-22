package org.generationcp.middleware.service.impl.study.advance;

import com.google.common.collect.Lists;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.generator.SeedSourceGenerator;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.ruleengine.naming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.advance.AdvanceService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceInput;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.service.impl.study.advance.resolver.BreedingMethodResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.PlantSelectedResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.TrialInstanceObservationsResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.LocationDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.SeasonDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.SelectionTraitDataResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
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

	@Resource
	private CropService cropService;

	@Resource
	private GermplasmGroupingService germplasmGroupingService;

	@Resource
	private GermplasmStudySourceService germplasmStudySourceService;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource(name = "getCropDatabaseSessionProvider")
	private HibernateSessionProvider sessionProvider;

	private final DaoFactory daoFactory;
	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitDataResolver selectionTraitDataResolver;
	private final LocationDataResolver locationDataResolver;
	private final BreedingMethodResolver breedingMethodResolver;
	private final PlantSelectedResolver plantSelectedResolver;
	private final TrialInstanceObservationsResolver trialInstanceObservationsResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitDataResolver = new SelectionTraitDataResolver();
		this.locationDataResolver = new LocationDataResolver();
		this.breedingMethodResolver = new BreedingMethodResolver();
		this.plantSelectedResolver = new PlantSelectedResolver();
		this.trialInstanceObservationsResolver = new TrialInstanceObservationsResolver();
	}

	@Override
	public List<Integer> advanceStudy(final Integer studyId, final AdvanceStudyRequest request) {

		final DatasetDTO plotDataset =
			this.datasetService.getDatasetsWithVariables(studyId, Collections.singleton(DatasetTypeEnum.PLOT_DATA.getId())).get(0);
		final DatasetDTO environmentDataset =
			this.datasetService.getDatasetsWithVariables(studyId, Collections.singleton(DatasetTypeEnum.SUMMARY_DATA.getId())).get(0);

		final List<ObservationUnitRow> plotObservations =
			this.getPlotObservations(studyId, plotDataset.getDatasetId(), request.getInstanceIds(), request.getSelectedReplications());
		if (CollectionUtils.isEmpty(plotObservations)) {
			return new ArrayList<>();
		}

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
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId =
			environmentDataset.getVariables().stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));

		final List<StudyInstance> studyInstances = this.studyInstanceService.getStudyInstances(studyId);
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber = studyInstances.stream()
			.collect(Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final Map<String, String> locationsNamesByIds = this.studyInstanceService.getStudyInstances(studyId).stream()
			.collect(Collectors.toMap(studyInstance -> String.valueOf(studyInstance.getLocationId()),
				StudyInstance::getLocationName));

		final Map<Integer, Variable> variablesByTermIds = this.getVariablesByTermIds();

		// Setting stuff to context.
		AdvanceContext.setStudyEnvironmentVariables(studyEnvironmentVariables);
		AdvanceContext.setStudyInstancesByInstanceNumber(studyInstancesByInstanceNumber);
		AdvanceContext.setLocationsNamesByIds(locationsNamesByIds);
		AdvanceContext.setEnvironmentVariablesByTermId(environmentVariablesByTermId);
		AdvanceContext.setVariablesByTermId(variablesByTermIds);

		final Map<String, Method> breedingMethodsByCode = new HashMap<>();
		final Map<Integer, Method> breedingMethodsById = new HashMap<>();
		this.daoFactory.getMethodDAO().getAllMethod().forEach(method -> {
			breedingMethodsByCode.put(method.getMcode(), method);
			breedingMethodsById.put(method.getMid(), method);
		});

		final List<ObservationUnitRow> trialObservations =
			this.getTrialObservations(studyId, environmentDataset.getDatasetId(), request.getInstanceIds());

		final List<Location> locations = this.getLocationsFromTrialObservationUnits(trialObservations);
		final Map<Integer, Location> locationsByLocationId =
			locations.stream().collect(Collectors.toMap(Location::getLocid, location -> location));

		final Set<Integer> gids = plotObservations.stream()
			.map(ObservationUnitRow::getGid)
			.collect(Collectors.toSet());

		final Map<Integer, List<BasicNameDTO>> namesByGids = this.getNamesByGids(gids);

		final Map<Integer, BasicGermplasmDTO> originGermplasmsByGid = this.getGermplasmByGids(gids);
		final CropType cropType = this.cropService.getCropTypeByName(ContextHolder.getCurrentCrop());
		final List<AdvancingSource> advancingSources = new ArrayList<>();
		final Set<Integer> originGermplasmParentGids = new HashSet<>();

		plotObservations.forEach(row -> {
			final BasicGermplasmDTO originGermplasm = originGermplasmsByGid.get(row.getGid());
			// Get the selected breeding method
			final Method breedingMethod =
				this.breedingMethodResolver.resolveBreedingMethod(request.getBreedingMethodSelectionRequest(), row, breedingMethodsByCode,
					breedingMethodsById);
			if (originGermplasm == null || breedingMethod == null || breedingMethod.isBulkingMethod() == null) {
				return;
			}

			// Calculates the plant selection number.
			final Integer plantsSelected =
				this.plantSelectedResolver.resolvePlantSelected(request, row, breedingMethodsByCode, breedingMethod.isBulkingMethod());
			if (plantsSelected == null || plantsSelected <= 0) {
				return;
			}

			// If study is Trial, then setting data if trial instance is not null
			final Integer trialInstanceNumber = row.getTrialInstance();
			final ObservationUnitRow trialInstanceObservation =
				this.getTrialInstanceObservations(trialInstanceNumber,
					() -> this.trialInstanceObservationsResolver
						.getTrialInstanceObservations(trialInstanceNumber, trialObservations, studyInstancesByInstanceNumber));

			// To prevent a NPE in the future, filter the observations variables that don't have a variableId assigned, like PARENT_OBS_UNIT_ID
			row.setVariables(this.filterNullObservations(row.getVariables()));

			// Creates the advancing source. This object will be useful for expressions used later when the names are being generated
			final AdvancingSource advancingSource =
				new AdvancingSource(originGermplasm, namesByGids.get(row.getGid()), row, trialInstanceObservation,
					breedingMethod, breedingMethodsById.get(originGermplasm.getMethodId()),
					seasonStudyLevel, selectionTraitStudyLevel, plantsSelected);

			// Resolves data related to season, selection trait and location for environment and plot
			this.resolveEnvironmentAndPlotLevelData(environmentDataset.getDatasetId(), plotDataset.getDatasetId(),
				request.getSelectionTraitRequest(), advancingSource, row, locationsByLocationId, plotDataVariablesByTermId);

			// TODO: implement get plant selection for samples
			//			if (advanceInfo.getAdvanceType().equals(AdvanceType.SAMPLE)) {
			//				if (samplesMap.containsKey(row.getExperimentId())) {
			//					plantsSelected = samplesMap.get(row.getExperimentId()).size();
			//					advancingSourceCandidate.setSamples(samplesMap.get(row.getExperimentId()));
			//				} else {
			//					continue;
			//				}
			//			}

			// Creates the lines that are advanced
			this.createAdvancedGermplasm(cropType, advancingSource);

			// Getting the gids that later will be used to obtain the 'selection history at fixation' and code names from the parents
			if (originGermplasm.getMgid() > 0 && originGermplasm.getGpid2() > 0) {
				originGermplasmParentGids.add(originGermplasm.getGpid2());
			}

			advancingSources.add(advancingSource);
		});

		final List<Integer> advancedGermplasmGids = new ArrayList<>();
		if (!CollectionUtils.isEmpty(advancingSources)) {
			try {
				// Clearing the hibernate session to prevent performance issue when generating names
				this.sessionProvider.getSession().clear();

				// Generating the advanced names
				this.namingConventionService.generateAdvanceListName(advancingSources);
			} catch (final RuleException e) {
				throw new MiddlewareException("Error trying to generate advancing names.");
			}

			final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
			final Map<String, String> locationNameByIds =
				locations.stream().collect(Collectors.toMap(location -> String.valueOf(location.getLocid()), Location::getLname));

			final Map<Integer, List<BasicNameDTO>> parentOriginGermplasmNamesByGids =
				org.apache.commons.collections.CollectionUtils.isEmpty(originGermplasmParentGids) ? new HashMap<>() :
					this.getNamesByGids(originGermplasmParentGids);

			final Integer plotCodeVariableId = this.germplasmService.getPlotCodeField().getId();
			final Integer plotNumberVariableId = this.getVariableId(PLOT_NUMBER_VARIABLE_NAME);
			final Integer repNumberVariableId = this.getVariableId(REP_NUMBER_VARIABLE_NAME);
			final Integer trialInstanceVariableId = this.getVariableId(TRIAL_INSTANCE_VARIABLE_NAME);
			final Integer plantNumberVariableId = this.getVariableId(PLANT_NUMBER_VARIABLE_NAME);
			advancingSources.forEach(advancingSource -> {
				final AtomicInteger selectionNumber = new AtomicInteger(1);
				advancingSource.getAdvancedGermplasms().forEach(germplasm -> {

					// inherit 'selection history at fixation' and code names of parent if parent is part of a group (= has mgid)
					if (germplasm.getMgid() > 0) {
						final List<BasicNameDTO> parentNames = parentOriginGermplasmNamesByGids.get(germplasm.getGpid2());
						this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(germplasm, germplasm.getGpid2(), parentNames);
						this.germplasmGroupingService.copyCodedNames(germplasm, parentNames);
					}

					// Finally, persisting the new advanced line with its derivative name. Also, it has the selection history at fixation and
					// code name of the parent if it corresponds.
					this.daoFactory.getGermplasmDao().save(germplasm);
					advancedGermplasmGids.add(germplasm.getGid());

					final Boolean allPlotsSelected =
						request.getBulkingRequest() == null ? null : request.getBulkingRequest().getAllPlotsSelected();
					// Adding attributes to the advanced germplasm
					this.createGermplasmAttributes(study.getName(), advancingSource,
						allPlotsSelected, germplasm.getGid(),
						selectionNumber.get(), germplasm.getLocationId(), germplasm.getGdate(),
						plotCodeVariableId, plotNumberVariableId, repNumberVariableId, trialInstanceVariableId,
						plantNumberVariableId, studyEnvironmentVariables, environmentDataset.getVariables(),
						locationNameByIds, studyInstancesByInstanceNumber);

					final GermplasmStudySourceInput germplasmStudySourceInput = new GermplasmStudySourceInput(germplasm.getGid(), studyId,
						advancingSource.getPlotObservation().getObservationUnitId(),
						GermplasmStudySourceType.ADVANCE);
					this.germplasmStudySourceService.saveGermplasmStudySources(Arrays.asList(germplasmStudySourceInput));

					selectionNumber.incrementAndGet();
				});
			});
		}

		return advancedGermplasmGids;
	}

	private List<ObservationUnitRow> getPlotObservations(final Integer studyId, final Integer plotDatasetId,
		final List<Integer> instancesIds, final List<String> selectedReplications) {

		final ObservationUnitsSearchDTO plotDataObservationsSearchDTO = new ObservationUnitsSearchDTO();
		plotDataObservationsSearchDTO.setInstanceIds(instancesIds);

		if (!CollectionUtils.isEmpty(selectedReplications)) {
			final Map<String, List<String>> filteredValues = new HashMap<>();
			filteredValues.put(String.valueOf(TermId.REP_NO.getId()), selectedReplications);
			final ObservationUnitsSearchDTO.Filter filter = plotDataObservationsSearchDTO.new Filter();
			filter.setFilteredValues(filteredValues);

			final StandardVariable replicationNumberVariable = this.ontologyDataManager.getStandardVariable(TermId.REP_NO.getId(), null);
			final Map<String, String> variableTypeMap = new HashMap<>();
			variableTypeMap
				.put(String.valueOf(TermId.REP_NO.getId()), replicationNumberVariable.getVariableTypes().iterator().next().name());
			filter.setVariableTypeMap(variableTypeMap);
			plotDataObservationsSearchDTO.setFilter(filter);
		}

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

	private List<MeasurementVariableDto> getObservationSetVariables(final Integer datasetId, final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes)
			.stream()
			.map(variable -> new MeasurementVariableDto(variable.getTermId(), variable.getName()))
			.collect(Collectors.toList());
	}

	private Map<Integer, Variable> getVariablesByTermIds() {
		final HashMap<Integer, Variable> variablesByTermId = new HashMap<>();
		variablesByTermId.put(TermId.HABITAT_DESIGNATION.getId(), this.getVariableByTermId(TermId.HABITAT_DESIGNATION.getId()));
		variablesByTermId.put(TermId.PROJECT_PREFIX.getId(), this.getVariableByTermId(TermId.PROJECT_PREFIX.getId()));
		variablesByTermId.put(TermId.SEASON_VAR.getId(), this.getVariableByTermId(TermId.SEASON_VAR.getId()));
		return variablesByTermId;
	}

	private Variable getVariableByTermId(final Integer termId) {
		return this.ontologyVariableDataManager.getVariable(ContextHolder.getCurrentProgramOptional().get(), termId, true);
	}

	private List<Location> getLocationsFromTrialObservationUnits(
		final List<ObservationUnitRow> trialObservationUnitRows) {
		final Set<Integer> locationIds = this.getVariableValuesFromObservations(trialObservationUnitRows, TermId.LOCATION_ID.getId());
		return this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds));
	}

	private Map<Integer, List<BasicNameDTO>> getNamesByGids(final Set<Integer> gids) {
		return this.daoFactory.getNameDao().getBasicNamesByGids(gids)
			.stream()
			.collect(Collectors.groupingBy(BasicNameDTO::getGid, Collectors.toList()));
	}

	private Map<Integer, BasicGermplasmDTO> getGermplasmByGids(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getBasicGermplasmByGids(gids)
			.stream()
			.collect(Collectors.toMap(BasicGermplasmDTO::getGid, basicGermplasmDTO -> basicGermplasmDTO));
	}

	private Set<Integer> getVariableValuesFromObservations(final List<ObservationUnitRow> observations, final Integer variableId) {
		return observations.stream()
			.map(row -> row.getEnvironmentVariables().values())
			.flatMap(Collection::stream)
			.filter(observationUnitData -> observationUnitData.getVariableId().equals(variableId))
			.map(observationUnitData -> AdvanceUtils.getIntegerValue(observationUnitData.getValue()))
			.collect(Collectors.toSet());
	}

	private void resolveEnvironmentAndPlotLevelData(final Integer environmentDatasetId, final Integer plotDatasetId,
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final AdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, Location> locationsByLocationId,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		this.locationDataResolver.resolveEnvironmentLevelData(source, locationsByLocationId);
		this.seasonDataResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);
		this.selectionTraitDataResolver
			.resolveEnvironmentLevelData(environmentDatasetId, selectionTraitRequest, source, plotDataVariablesByTermId);
		this.selectionTraitDataResolver
			.resolvePlotLevelData(plotDatasetId, selectionTraitRequest, source, row, plotDataVariablesByTermId);
	}

	private ObservationUnitRow getTrialInstanceObservations(final Integer trialInstanceNumber,
		final Supplier<ObservationUnitRow> trialObservationsSupplier) {
		if (trialInstanceNumber == null) {
			return null;
		}

		final ObservationUnitRow trialObservations = trialObservationsSupplier.get();
		// To prevent a NPE in the future, filter the observations variables that don't have a variableId assigned, like PARENT_OBS_UNIT_ID
		trialObservations.setVariables(this.filterNullObservations(trialObservations.getVariables()));
		return trialObservations;
	}

	private Map<String, ObservationUnitData> filterNullObservations(final Map<String, ObservationUnitData> observations) {
		return observations.entrySet().stream()
			.filter(entry -> Objects.nonNull(entry.getValue()) && Objects.nonNull(entry.getValue().getVariableId()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private void createAdvancedGermplasm(final CropType cropType, final AdvancingSource advancingSource) {

		for (int i = 0; i < advancingSource.getPlantsSelected(); i++) {
			final BasicGermplasmDTO originGermplasm = advancingSource.getOriginGermplasm();
			final Germplasm advancedGermplasm = new Germplasm();
			if (originGermplasm.getGpid1() == 0 || (advancingSource.getSourceMethod() != null && advancingSource.getSourceMethod()
				.isDerivative())) {
				advancedGermplasm.setGpid1(originGermplasm.getGid());
			} else {
				advancedGermplasm.setGpid1(originGermplasm.getGpid1());
			}

			advancedGermplasm.setGpid2(-1);
			advancedGermplasm.setGnpgs(-1);

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

			advancedGermplasm.setMethod(advancingSource.getBreedingMethod());

			GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(advancedGermplasm));

			advancingSource.addAdvancedGermplasm(advancedGermplasm);
		}
	}

	private Integer getVariableId(final String name) {
		final Term term = this.ontologyDataManager.findTermByName(name, CvId.VARIABLES.getId());
		if (term == null) {
			return null;
		}
		return term.getId();
	}

	private void createGermplasmAttributes(final String studyName,
		final AdvancingSource advancingSource, final Boolean allPlotsSelected,
		final Integer advancedGermplasmGid, final Integer selectionNumber,
		final Integer locationId, final Integer date, final Integer plotCodeVariableId, final Integer plotNumberVariableId,
		final Integer repNumberVariableId,
		final Integer trialInstanceVariableId, final Integer plantNumberVariableId,
		final List<MeasurementVariable> studyEnvironmentVariables, final List<MeasurementVariable> environmentVariables,
		final Map<String, String> locationNameByIds,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber) {

		// TODO: implement it for samples
		final String plantNumber = null;
		//				final Iterator<SampleDTO> sampleIterator = row.getSamples().iterator();
		//				if (sampleIterator.hasNext()) {
		//					plantNumber = String.valueOf(sampleIterator.next().getSampleNumber());
		//				}

		//				final ObservationUnitRow plotObservation = advancingSource.getPlotObservation();
		//				final String plotNumber = plotObservation.getVariableValueByVariableId(TermId.PLOT_NO.getId());
		//				final String seedSource = this.generateSeedSource(studyName, selectionNumber.get(), plotNumber,
		//					breedingMethod.isBulkingMethod(), breedingMethodSelectionRequest.getAllPlotsSelected(),
		//					advancingSource.getPlantsSelected(), plotObservation,
		//					studyEnvironmentVariables, locationNameByIds, studyInstancesByInstanceNumber, environmentVariables);

		final ObservationUnitRow plotObservation = advancingSource.getPlotObservation();
		final String plotNumber = plotObservation.getVariableValueByVariableId(TermId.PLOT_NO.getId());
		final String seedSource = this.generateSeedSource(studyName, selectionNumber, plotNumber,
			advancingSource.getBreedingMethod().isBulkingMethod(), allPlotsSelected,
			advancingSource.getPlantsSelected(), plotObservation,
			studyEnvironmentVariables, locationNameByIds, studyInstancesByInstanceNumber, environmentVariables);

		final Attribute plotCodeAttribute =
			this.createGermplasmAttribute(advancedGermplasmGid, seedSource, plotCodeVariableId, locationId, date);
		this.daoFactory.getAttributeDAO().save(plotCodeAttribute);

		if (plotNumberVariableId != null) {
			final Attribute plotNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, plotNumber, plotNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plotNumberAttribute);
		}

		final String replicationNumber = plotObservation.getVariableValueByVariableId(TermId.REP_NO.getId());
		if (repNumberVariableId != null && !StringUtils.isEmpty(replicationNumber)) {
			final Attribute replicationNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, replicationNumber, repNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(replicationNumberAttribute);
		}

		if (!StringUtils.isEmpty(plantNumber) && plantNumberVariableId != null) {
			final Attribute plantNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, plantNumber, plantNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plantNumberAttribute);
		}

		if (trialInstanceVariableId != null) {
			final Attribute trialInstanceNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, plotObservation.getTrialInstance().toString(), trialInstanceVariableId,
					locationId, date);
			this.daoFactory.getAttributeDAO().save(trialInstanceNumberAttribute);
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
			.generateSeedSource(row.getVariables().values(), studyEnvironmentVariables, seedSourceSelectionNumber, plotNumber,
				studyName, sampleNo, locationNameByIds, studyInstancesByInstanceNumber,
				environmentVariables);
	}

	private Attribute createGermplasmAttribute(final Integer germplasmId, final String value, final Integer typeId,
		final Integer locationId, final Integer date) {
		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasmId);
		attribute.setAval(value);
		attribute.setTypeId(typeId);
		attribute.setAdate(date);
		attribute.setLocationId(locationId);
		return attribute;
	}

}
