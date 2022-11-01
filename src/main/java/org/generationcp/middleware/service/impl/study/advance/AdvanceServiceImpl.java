package org.generationcp.middleware.service.impl.study.advance;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.generator.SeedSourceGenerator;
import org.generationcp.middleware.ruleengine.naming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.advance.AdvanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.service.impl.study.advance.resolver.BreedingMethodResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.LocationDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.SeasonDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.SelectionTraitDataResolver;
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
import java.util.Collections;
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

	@Resource
	private CropService cropService;

	private final DaoFactory daoFactory;
	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitDataResolver selectionTraitDataResolver;
	private final LocationDataResolver locationDataResolver;
	private final BreedingMethodResolver breedingMethodResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitDataResolver = new SelectionTraitDataResolver();
		this.locationDataResolver = new LocationDataResolver();
		this.breedingMethodResolver = new BreedingMethodResolver();
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

		final Set<Integer> gids = plotObservations.stream()
			.map(ObservationUnitRow::getGid)
			.collect(Collectors.toSet());

		final Map<Integer, List<Name>> namesByGids =
			this.daoFactory.getNameDao().getNamesByGidsInMap(new ArrayList<>(gids));

		// TODO: Considering performance issue due to we will have a lot of objects in the memory... Is really needed to have the germplasm entity??? At least according to to the old advance we need only a few of germplasm properties:
		//  https://github.com/IntegratedBreedingPlatform/Fieldbook/blob/f242b4219653d926f20a06214c0c8b1083af148e/src/main/java/com/efficio/fieldbook/web/naming/impl/AdvancingSourceListFactory.java#L245
		//  Besides that part of the code, there are other properties that are required such as mgid, progenitors data, etc. Take a look how many things of germplasm we need in the advance process
		final Map<Integer, Germplasm> originGermplasmsByGid = this.daoFactory.getGermplasmDao().getByGIDList(new ArrayList<>(gids)).stream()
			.collect(Collectors.toMap(Germplasm::getGid, germplasm -> germplasm));
		final CropType cropType = this.cropService.getCropTypeByName(ContextHolder.getCurrentCrop());
		final List<NewAdvancingSource> advancingSources = new ArrayList<>();
		plotObservations.forEach(row -> {
			final Germplasm originGermplasm = originGermplasmsByGid.get(row.getGid());
			final Method breedingMethod =
				this.breedingMethodResolver.resolveBreedingMethod(request.getBreedingMethodSelectionRequest(), row, breedingMethodsByCode,
					breedingMethodsById);
			if (originGermplasm == null || breedingMethod == null || breedingMethod.isBulkingMethod() == null) {
				return;
			}

			final Integer plantsSelected =
				this.getPlantSelected(request, row, breedingMethodsByCode, breedingMethod.isBulkingMethod());
			if (plantsSelected == null || plantsSelected <= 0) {
				return;
			}

			// If study is Trial, then setting data if trial instance is not null
			final Integer trialInstanceNumber = row.getTrialInstance();
			final ObservationUnitRow trialInstanceObservation = (trialInstanceNumber != null) ?
				this.setTrialInstanceObservations(trialInstanceNumber, trialObservations, studyInstancesByInstanceNumber) : null;

			final NewAdvancingSource advancingSource =
				new NewAdvancingSource(originGermplasm, namesByGids.get(row.getGid()), row, trialInstanceObservation,
					studyEnvironmentVariables,
					breedingMethod, studyId,
					environmentDataset.getDatasetId(), seasonStudyLevel, selectionTraitStudyLevel, plantsSelected);

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

			this.createAdvancedGermplasm(cropType, advancingSource);

			advancingSources.add(advancingSource);
		});

		final List<Integer> advancedGermplasmGids = new ArrayList<>();
		if (!CollectionUtils.isEmpty(advancingSources)) {
			try {
				this.namingConventionService.generateAdvanceListName(advancingSources);
			} catch (final RuleException e) {
				throw new MiddlewareException("Error trying to generate advancing names.");
			}

			final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
			final Map<String, String> locationNameByIds =
				locations.stream().collect(Collectors.toMap(location -> String.valueOf(location.getLocid()), Location::getLname));

			final Integer plotCodeVariableId = this.germplasmService.getPlotCodeField().getId();
			final Integer plotNumberVariableId = this.getVariableId(PLOT_NUMBER_VARIABLE_NAME);
			final Integer repNumberVariableId = this.getVariableId(REP_NUMBER_VARIABLE_NAME);
			final Integer trialInstanceVariableId = this.getVariableId(TRIAL_INSTANCE_VARIABLE_NAME);
			final Integer plantNumberVariableId = this.getVariableId(PLANT_NUMBER_VARIABLE_NAME);
			advancingSources.forEach(advancingSource -> {
				final AtomicInteger selectionNumber = new AtomicInteger(1);
				advancingSource.getAdvancedGermplasms().forEach(germplasm -> {

					// TODO: add selfixhistory names from parents
					// TODO: Check If another property of germplasm is being updated
					this.daoFactory.getGermplasmDao().save(germplasm);
					advancedGermplasmGids.add(germplasm.getGid());

					this.createGermplasmAttributes(study.getName(), request.getBreedingMethodSelectionRequest(), advancingSource,
						selectionNumber.get(), germplasm.getLocationId(), germplasm.getGdate(), plotCodeVariableId,
						plotNumberVariableId, repNumberVariableId, trialInstanceVariableId, plantNumberVariableId,
						studyEnvironmentVariables, environmentDataset.getVariables(), locationNameByIds,
						studyInstancesByInstanceNumber);
					selectionNumber.incrementAndGet();
				});

				// TODO: create study source
			});
		}

		return advancedGermplasmGids;
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
				trialObservation.getVariableById(trialObservation.getEnvironmentVariables().values(), TermId.LOCATION_ID.getId())
					.ifPresent(
						observationUnitData -> observationUnitData
							.setValue(String.valueOf(studyInstancesByInstanceNumber.get(trialInstanceNumber).getLocationId()))
					);
			}
			return trialObservation;
		}
		return null;
	}

	private List<Location> getLocationsFromTrialObservationUnits(
		final List<ObservationUnitRow> trialObservationUnitRows) {
		final Set<Integer> locationIds = this.getVariableValuesFromObservations(trialObservationUnitRows, TermId.LOCATION_ID.getId());
		return this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds));
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
	Integer getPlantSelected(final AdvanceStudyRequest request, final ObservationUnitRow plotObservation,
		final Map<String, Method> breedingMethodsByCode, final boolean isBulkMethod) {

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		if (breedingMethodSelectionRequest.getBreedingMethodId() != null) {
			// User has selected the same Breeding Method for each advance
			return this.getLineSelectedForBreedingMethodVariable(request, isBulkMethod, plotObservation);
		}

		if (breedingMethodSelectionRequest.getMethodVariateId() != null) {
			// User has selected a variate that defines the breeding method for each advance
			final String rowBreedingMethodCode =
				plotObservation.getVariableValueByVariableId(breedingMethodSelectionRequest.getMethodVariateId());
			if (!StringUtils.isEmpty(rowBreedingMethodCode) && breedingMethodsByCode.containsKey(rowBreedingMethodCode)) {
				final Method method = breedingMethodsByCode.get(rowBreedingMethodCode);
				return this.getLineSelectedForBreedingMethodVariable(request, method.isBulkingMethod(), plotObservation);
			}

			return null;
		}

		return null;
	}

	// TODO: move plant selection code to another class
	private Integer getLineSelectedForBreedingMethodVariable(final AdvanceStudyRequest request, final Boolean isBulkMethod,
		final ObservationUnitRow plotObservation) {
		if (isBulkMethod == null) {
			return null;
		}

		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest =
			request.getBreedingMethodSelectionRequest();
		final AdvanceStudyRequest.LineSelectionRequest lineSelectionRequest =
			request.getLineSelectionRequest();
		if (isBulkMethod) {
			if (breedingMethodSelectionRequest.getAllPlotsSelected() == null || !breedingMethodSelectionRequest.getAllPlotsSelected()) {
				// User has selected a variable that defines the number of lines selected from each plot. However, this is tricky because
				// the variable works as a boolean. It return 1 if there is a value present, otherwise it returns zero.
				final String plotVariateValue =
					plotObservation.getVariableValueByVariableId(breedingMethodSelectionRequest.getPlotVariateId());
				return StringUtils.isEmpty(plotVariateValue) ? 0 : 1;
			} else {
				return 1;
			}
		} else {
			// User has selected the same number of lines for each plot
			if (lineSelectionRequest.getLinesSelected() == null) {
				final String lineVariateValue =
					plotObservation.getVariableValueByVariableId(lineSelectionRequest.getLineVariateId());
				return AdvanceUtils.getIntegerValue(lineVariateValue);
			} else {
				// User has selected the same number of lines for each plot
				return lineSelectionRequest.getLinesSelected();
			}
		}
	}

	private void createAdvancedGermplasm(final CropType cropType, final NewAdvancingSource advancingSource) {

		for (int i = 0; i < advancingSource.getPlantsSelected(); i++) {
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
		final AdvanceStudyRequest.BreedingMethodSelectionRequest breedingMethodSelectionRequest, final NewAdvancingSource advancingSource,
		final Integer selectionNumber,
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
			advancingSource.getBreedingMethod().isBulkingMethod(), breedingMethodSelectionRequest.getAllPlotsSelected(),
			advancingSource.getPlantsSelected(), plotObservation,
			studyEnvironmentVariables, locationNameByIds, studyInstancesByInstanceNumber, environmentVariables);

		final Attribute plotCodeAttribute = this.createGermplasmAttribute(seedSource, plotCodeVariableId, locationId, date);
		this.daoFactory.getAttributeDAO().save(plotCodeAttribute);

		if (plotNumberVariableId != null) {
			final Attribute plotNumberAttribute = this.createGermplasmAttribute(plotNumber, plotNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plotNumberAttribute);
		}

		final String replicationNumber = plotObservation.getVariableValueByVariableId(TermId.REP_NO.getId());
		if (repNumberVariableId != null) {
			final Attribute replicationNumberAttribute =
				this.createGermplasmAttribute(replicationNumber, repNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(replicationNumberAttribute);
		}

		if (!StringUtils.isEmpty(plantNumber) && plantNumberVariableId != null) {
			final Attribute plantNumberAttribute =
				this.createGermplasmAttribute(plantNumber, plantNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plantNumberAttribute);
		}

		if (trialInstanceVariableId != null) {
			final Attribute trialInstanceNumberAttribute =
				this.createGermplasmAttribute(plotObservation.getTrialInstance().toString(), trialInstanceVariableId, locationId,
					date);
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

}
