package org.generationcp.middleware.service.impl.study.advance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.germplasm.GermplasmAttributeService;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.search.GermplasmAttributeSearchRequest;
import org.generationcp.middleware.api.study.AdvanceRequest;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.ruleengine.RuleException;
import org.generationcp.middleware.ruleengine.generator.SeedSourceGenerator;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.ruleengine.naming.service.NamingConventionService;
import org.generationcp.middleware.ruleengine.pojo.AdvanceGermplasmPreview;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.PedigreeService;
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
import org.generationcp.middleware.service.impl.study.advance.resolver.TrialInstanceObservationsResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.LocationDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.SeasonDataResolver;
import org.generationcp.middleware.service.impl.study.advance.resolver.level.SelectionTraitDataResolver;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetAllPlotsSelectedVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetBreedingMethodVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetDatasetVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetExperimentSamplesVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetObservationsVisibleColumnsVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetPlantSelectedVisitor;
import org.generationcp.middleware.service.impl.study.advance.visitor.GetSampleNumbersVisitor;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	public static final String PLOT_NUMBER_VARIABLE_NAME = "PLOT_NUMBER_AP_text";
	public static final String TRIAL_INSTANCE_VARIABLE_NAME = "INSTANCE_NUMBER_AP_text";
	public static final String REP_NUMBER_VARIABLE_NAME = "REP_NUMBER_AP_text";
	public static final String PLANT_NUMBER_VARIABLE_NAME = "PLANT_NUMBER_AP_text";

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

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	@Resource
	private PedigreeService pedigreeService;

	@Resource
	private GermplasmAttributeService germplasmAttributeService;

	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitDataResolver selectionTraitDataResolver;
	private final LocationDataResolver locationDataResolver;
	private final TrialInstanceObservationsResolver trialInstanceObservationsResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitDataResolver = new SelectionTraitDataResolver();
		this.locationDataResolver = new LocationDataResolver();
		this.trialInstanceObservationsResolver = new TrialInstanceObservationsResolver();
	}

	@Override
	public List<Integer> advanceStudy(final Integer studyId, final AdvanceStudyRequest request) {
		return this.advance(studyId, request, false, null);
	}

	@Override
	public List<AdvanceGermplasmPreview> advanceStudyPreview(final Integer studyId, final AdvanceStudyRequest request) {
		final List<AdvanceGermplasmPreview> advanceGermplasmPreviewList = new ArrayList<>();
		this.advance(studyId, request, true, advanceGermplasmPreviewList);
		Collections.sort(advanceGermplasmPreviewList, Comparator.comparing(AdvanceGermplasmPreview::getEntryNumberValue));

		return advanceGermplasmPreviewList;
	}

	@Override
	public List<Integer> advanceSamples(final Integer studyId, final AdvanceSamplesRequest request) {
		return this.advance(studyId, request, false, null);
	}

	@Override
	public List<AdvanceGermplasmPreview> advanceSamplesPreview(final Integer studyId, final AdvanceSamplesRequest request) {
		final List<AdvanceGermplasmPreview> advanceGermplasmPreviewList = new ArrayList<>();
		this.advance(studyId, request, true, advanceGermplasmPreviewList);
		Collections.sort(advanceGermplasmPreviewList, Comparator.comparing(AdvanceGermplasmPreview::getEntryNumberValue));

		return advanceGermplasmPreviewList;
	}

	private List<Integer> advance(final Integer studyId, final AdvanceRequest request,
		final boolean isPreview, final List<AdvanceGermplasmPreview> advanceGermplasmPreviewList) {

		final DatasetDTO dataset = request.accept(new GetDatasetVisitor(studyId, this.datasetService));
		final DatasetDTO environmentDataset =
			this.datasetService.getDatasetsWithVariables(studyId, Collections.singleton(DatasetTypeEnum.SUMMARY_DATA.getId())).get(0);

		final MeasurementVariable observationUnitVariable = this.getObservationUnitVariable(dataset);
		final Set<String> observationVisibleColumns =
			request.accept(
				new GetObservationsVisibleColumnsVisitor(dataset.getDatasetId(), observationUnitVariable, dataset.getVariables()));
		final List<ObservationUnitRow> observations =
			this.getObservations(studyId, dataset.getDatasetId(), request.getInstanceIds(), request.getSelectedReplications(),
				observationVisibleColumns);

		if (CollectionUtils.isEmpty(observations)) {
			return new ArrayList<>();
		}

		final long numberOfPlots = observations.size();

		final List<MeasurementVariable> studyVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId, Arrays.asList(VariableType.STUDY_DETAIL.getId()));
		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyEnvironmentVariables =
			this.getStudyEnvironmentVariables(studyVariables, environmentDataset.getVariables());
		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyVariates = this.getStudyVariates(environmentDataset.getVariables());

		final Map<Integer, MeasurementVariable> datasetVariablesByTermId =
			dataset.getVariables().stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId =
			environmentDataset.getVariables().stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));

		final List<StudyInstance> studyInstances = this.studyInstanceService.getStudyInstances(studyId);
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber = studyInstances.stream()
			.collect(Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final Map<String, String> locationsNamesByIds = studyInstances.stream()
			.collect(Collectors.toMap(studyInstance -> String.valueOf(studyInstance.getLocationId()),
				StudyInstance::getLocationName, (locationName1, locationName2) -> locationName1));

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

		final Set<Integer> gids = observations.stream()
			.map(ObservationUnitRow::getGid)
			.collect(Collectors.toSet());

		final Map<Integer, List<BasicNameDTO>> namesByGids = this.getNamesByGids(gids);

		final Map<Integer, BasicGermplasmDTO> originGermplasmsByGid = this.getGermplasmByGids(gids);
		final CropType cropType = this.cropService.getCropTypeByName(ContextHolder.getCurrentCrop());
		final List<AdvancingSource> advancingSources = new ArrayList<>();

		// Retrieve the germplasm attributes and passport descriptors of the germplasm being advanced.
		// We will copy these attributes to the advanced germplasm later
		// These maps will only be populated if the user opts to propagate these attributes.
		final Map<Integer, List<GermplasmAttributeDto>> germplasmDescriptorsMap = isPreview || !request.isPropagateDescriptors() ?
			new HashMap<>() : this.getAttributesDtoMap(gids);

		// Getting data related at study level
		final String seasonStudyLevel = this.seasonDataResolver.resolveStudyLevelData(studyEnvironmentVariables);
		final String selectionTraitStudyLevel = this.selectionTraitDataResolver
			.resolveStudyLevelData(studyId, request.getSelectionTraitRequest(),
				Stream.concat(studyEnvironmentVariables.stream(), studyVariates.stream()).collect(
					Collectors.toList()));

		// Get experiment samples
		final Map<Integer, List<SampleDTO>> samplesByExperimentId =
			request.accept(new GetExperimentSamplesVisitor(studyId, this.studyDataManager));

		observations.forEach(row -> {
			final BasicGermplasmDTO originGermplasm = originGermplasmsByGid.get(row.getGid());

			// Get the selected breeding method
			final Method breedingMethod = request.accept(new GetBreedingMethodVisitor(row, breedingMethodsById, breedingMethodsByCode));

			if (breedingMethod == null || breedingMethod.isBulkingMethod() == null) {
				return;
			}

			// Get the sample numbers
			final List<Integer> sampleNumbers =
				request.accept(new GetSampleNumbersVisitor(row.getObservationUnitId(), samplesByExperimentId));

			// Get the number of selected plants
			final Integer plantsSelected =
				request.accept(new GetPlantSelectedVisitor(row, breedingMethodsByCode, breedingMethod, sampleNumbers));

			if (originGermplasm == null || plantsSelected == null || plantsSelected <= 0) {
				return;
			}

			// If study is Trial, then setting data if trial instance is not null
			final Integer trialInstanceNumber = row.getTrialInstance();
			final ObservationUnitRow trialInstanceObservation =
				this.getTrialInstanceObservations(trialInstanceNumber,
					() -> this.trialInstanceObservationsResolver
						.getTrialInstanceObservations(trialInstanceNumber, trialObservations, studyInstancesByInstanceNumber));

			// To prevent a NPE in the future, filter the observations variables that don't have a variableId assigned, like PARENT_OBS_UNIT_ID
			row.setVariables(this.filterNullObservations(row.getVariables(), observationUnitVariable));

			// Creates the advancing source. This object will be useful for expressions used later when the names are being generated
			final AdvancingSource advancingSource =
				new AdvancingSource(originGermplasm, namesByGids.get(row.getGid()), row, trialInstanceObservation,
					breedingMethod, breedingMethodsById.get(originGermplasm.getMethodId()),
					seasonStudyLevel, selectionTraitStudyLevel, plantsSelected, sampleNumbers);
			advancingSource.setPreview(isPreview);

			// Resolves data related to season, selection trait and location for environment and plot
			this.resolveEnvironmentAndPlotAndSubObservationLevelData(environmentDataset.getDatasetId(), dataset.getDatasetId(),
				request.getSelectionTraitRequest(), advancingSource, row, locationsByLocationId, datasetVariablesByTermId,
				environmentVariablesByTermId);

			// Creates the lines that are advanced
			this.createAdvancedGermplasm(cropType, advancingSource);

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
				locations.stream().collect(Collectors.toMap(location -> String.valueOf(location.getLocid()), Location::getLname,
					(locationName1, locationName2) -> locationName1));

			final Map<Integer, String> pedigreeStringMap = new HashMap<>();

			if (isPreview) {
				pedigreeStringMap.putAll(this.pedigreeService.getCrossExpansions(new HashSet<>(gids), null, this.crossExpansionProperties));
			}

			final Integer plotCodeVariableId = this.germplasmService.getPlotCodeField().getId();
			final Integer plotNumberVariableId = this.getVariableId(PLOT_NUMBER_VARIABLE_NAME);
			final Integer repNumberVariableId = this.getVariableId(REP_NUMBER_VARIABLE_NAME);
			final Integer trialInstanceVariableId = this.getVariableId(TRIAL_INSTANCE_VARIABLE_NAME);
			final Integer plantNumberVariableId = this.getVariableId(PLANT_NUMBER_VARIABLE_NAME);

			// Default passport descriptors variables should not be inherited from the source (orginal germplasm)
			final List<Integer> defaultPassportDescriptorsVariableIds =
				Arrays.asList(plotCodeVariableId, plotNumberVariableId, repNumberVariableId,
					trialInstanceVariableId, plantNumberVariableId);

			advancingSources.forEach(advancingSource -> {
				final AtomicInteger selectionNumber = new AtomicInteger(1);

				final ObservationUnitRow observation = advancingSource.getObservation();
				final Integer trialInstance = observation.getTrialInstance();
				final String plotNumber = observation.getVariableValueByVariableId(TermId.PLOT_NO.getId());
				final String plantNumber = observation.getVariableValueByVariableId(TermId.PLANT_NO.getId());
				final String entryNumber = observation.getVariableValueByVariableId(TermId.ENTRY_NO.getId());
				final List<BasicNameDTO> originGermplasmNames = advancingSource.getNames();
				final String originGermplasmName = originGermplasmNames.isEmpty() ? "" :
					originGermplasmNames.stream()
						.filter(name -> name.getNstat().equals(1))
						.findFirst().get().getNval(); //retrieve origin germplasm preferred name
				final String pedigreeString = pedigreeStringMap.get(observation.getGid());

				advancingSource.getAdvancedGermplasm().forEach(germplasm -> {
					final Iterator<Integer> sampleNumberIterator = advancingSource.getSampleNumbers().iterator();
					final String sampleNumber = (sampleNumberIterator.hasNext()) ? String.valueOf(sampleNumberIterator.next()) : null;

					// inherit 'selection history at fixation' and code names of parent if parent is part of a group (= has mgid)
					if (germplasm.getMgid() > 0) {
						final List<BasicNameDTO> parentNames = namesByGids.get(germplasm.getGpid2());
						this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(germplasm, germplasm.getGpid2(), parentNames);
						this.germplasmGroupingService.copyCodedNames(germplasm, parentNames);
					}

					if (isPreview) {
						final List<Name> currentGermplasmNames = germplasm.getNames();

						advanceGermplasmPreviewList.add(
							new AdvanceGermplasmPreview(
								trialInstance == null ? "" : trialInstance.toString(),
								locationsByLocationId.get(germplasm.getLocationId()).getLname(),
								entryNumber, plotNumber, plantNumber, pedigreeString,
								originGermplasmName, germplasm.getMethod().getMcode(),
								currentGermplasmNames.isEmpty() ? "" : currentGermplasmNames.get(0).getNval()));
					} else {
						// Finally, persisting the new advanced line with its derivative name. Also, it has the selection history at fixation and
						// code name of the parent if it corresponds.
						this.daoFactory.getGermplasmDao().save(germplasm);
						advancedGermplasmGids.add(germplasm.getGid());

						// Get all plots selection
						final Boolean allPlotsSelected = request.accept(new GetAllPlotsSelectedVisitor());

						// Adding attributes to the advanced germplasm
						this.createGermplasmAttributes(study.getName(), advancingSource,
							allPlotsSelected, germplasm.getGid(),
							selectionNumber.get(), sampleNumber, germplasm.getLocationId(),
							germplasm.getGdate(), plotCodeVariableId, plotNumberVariableId, repNumberVariableId,
							trialInstanceVariableId, plantNumberVariableId, studyEnvironmentVariables,
							environmentDataset.getVariables(), locationNameByIds, studyInstancesByInstanceNumber,
							dataset.getDatasetTypeId(),
							observationUnitVariable);

						// Propagate the Germplasm Passport from the original germplasm to the advanced germplasm
						// Default passport descriptors variables should not be inherited from the source
						this.propagateAttributesFromOriginalGermplasmToAdvancedGermplasm(advancingSource.getOriginGermplasm().getGid(),
							germplasm.getGid(), germplasmDescriptorsMap, defaultPassportDescriptorsVariableIds, request, germplasm.getLocationId());

						final GermplasmStudySourceInput germplasmStudySourceInput =
							new GermplasmStudySourceInput(germplasm.getGid(), studyId,
								advancingSource.getObservation().getObservationUnitId(),
								GermplasmStudySourceType.ADVANCE);
						this.germplasmStudySourceService.saveGermplasmStudySources(Arrays.asList(germplasmStudySourceInput));
					}

					selectionNumber.incrementAndGet();
				});
			});
		}

		return advancedGermplasmGids;
	}

	private Map<Integer, List<GermplasmAttributeDto>> getAttributesDtoMap(final Set<Integer> gids) {
		final GermplasmAttributeSearchRequest germplasmAttributeSearchRequest = new GermplasmAttributeSearchRequest();
		germplasmAttributeSearchRequest.setGids(gids);
		germplasmAttributeSearchRequest.setVariableTypeIds(Arrays.asList(VariableType.GERMPLASM_PASSPORT.getId(), VariableType.GERMPLASM_ATTRIBUTE.getId()));
		return this.germplasmAttributeService.getGermplasmAttributeDtos(germplasmAttributeSearchRequest).stream()
			.collect(Collectors.groupingBy(GermplasmAttributeDto::getGid));
	}

	private List<ObservationUnitRow> getObservations(final Integer studyId, final Integer plotDatasetId,
		final List<Integer> instancesIds, final List<Integer> selectedReplications, final Set<String> observationVisibleColumns) {

		final ObservationUnitsSearchDTO plotDataObservationsSearchDTO = new ObservationUnitsSearchDTO();
		plotDataObservationsSearchDTO.setInstanceIds(instancesIds);

		if (!CollectionUtils.isEmpty(selectedReplications)) {
			final Map<String, List<String>> filteredValues = new HashMap<>();
			filteredValues.put(String.valueOf(TermId.REP_NO.getId()), selectedReplications.stream().map(Objects::toString).collect(
				Collectors.toList()));
			final ObservationUnitsSearchDTO.Filter filter = plotDataObservationsSearchDTO.new Filter();
			filter.setFilteredValues(filteredValues);

			final StandardVariable replicationNumberVariable = this.ontologyDataManager.getStandardVariable(TermId.REP_NO.getId(), null);
			final Map<String, String> variableTypeMap = new HashMap<>();
			variableTypeMap
				.put(String.valueOf(TermId.REP_NO.getId()), replicationNumberVariable.getVariableTypes().iterator().next().name());
			filter.setVariableTypeMap(variableTypeMap);
			plotDataObservationsSearchDTO.setFilter(filter);
		}

		final Sort sort = new Sort(
			new Sort.Order(Sort.Direction.ASC, "TRIAL_INSTANCE"),
			new Sort.Order(Sort.Direction.ASC, "PLOT_NO"),
			new Sort.Order(Sort.Direction.ASC, "REP_NO"));
		final PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, sort);

		// Add the required observation table columns necessary for advancing to the visible columns, so that
		// entry details, attributes, passports and names will be excluded in the observations query.
		observationVisibleColumns.addAll(Sets.newHashSet("TRIAL_INSTANCE", "PLOT_NO", "REP_NO"));

		plotDataObservationsSearchDTO.setVisibleColumns(observationVisibleColumns);

		return this.datasetService
			.getObservationUnitRows(studyId, plotDatasetId, plotDataObservationsSearchDTO, pageRequest);
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

	private void resolveEnvironmentAndPlotAndSubObservationLevelData(final Integer environmentDatasetId, final Integer plotDatasetId,
		final AdvanceStudyRequest.SelectionTraitRequest selectionTraitRequest,
		final AdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, Location> locationsByLocationId,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId,
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {

		this.locationDataResolver.resolveEnvironmentLevelData(source, locationsByLocationId);
		this.seasonDataResolver.resolveEnvironmentLevelData(source, environmentVariablesByTermId);
		this.selectionTraitDataResolver
			.resolveEnvironmentLevelData(environmentDatasetId, selectionTraitRequest, source, environmentVariablesByTermId);
		this.selectionTraitDataResolver
			.resolvePlotAndSubObservationLevelData(plotDatasetId, selectionTraitRequest, source, row, plotDataVariablesByTermId);
	}

	private ObservationUnitRow getTrialInstanceObservations(final Integer trialInstanceNumber,
		final Supplier<ObservationUnitRow> trialObservationsSupplier) {
		if (trialInstanceNumber == null) {
			return null;
		}

		final ObservationUnitRow trialObservations = trialObservationsSupplier.get();
		// To prevent a NPE in the future, filter the observations variables that don't have a variableId assigned, like PARENT_OBS_UNIT_ID
		trialObservations.setVariables(this.filterNullObservations(trialObservations.getVariables(), null));
		return trialObservations;
	}

	private Map<String, ObservationUnitData> filterNullObservations(final Map<String, ObservationUnitData> observations,
		final MeasurementVariable observationNameVariable) {
		if (observationNameVariable != null) {
			observations.computeIfPresent(observationNameVariable.getName(), (s, observationUnitData) -> {
				observationUnitData.setVariableId(observationNameVariable.getTermId());
				return observationUnitData;
			});
		}

		return observations.entrySet().stream()
			.filter(entry -> Objects.nonNull(entry.getValue()) && Objects.nonNull(entry.getValue().getVariableId()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private void createAdvancedGermplasm(final CropType cropType, final AdvancingSource advancingSource) {

		final Integer advancedGermplasmCount = advancingSource.isBulkingMethod() ? 1 : advancingSource.getPlantsSelected();
		for (int i = 0; i < advancedGermplasmCount; i++) {
			final BasicGermplasmDTO originGermplasm = advancingSource.getOriginGermplasm();
			final Germplasm advancedGermplasm = new Germplasm();
			if (originGermplasm.getGpid1() == 0 || (advancingSource.getSourceMethod() != null && advancingSource.getSourceMethod()
				.isGenerative())) {
				advancedGermplasm.setGpid1(originGermplasm.getGid());
			} else {
				advancedGermplasm.setGpid1(originGermplasm.getGpid1());
			}

			advancedGermplasm.setGpid2(originGermplasm.getGid());
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

	private void propagateAttributesFromOriginalGermplasmToAdvancedGermplasm(final Integer originalGermplasmGid,
		final Integer advancedGermplasmGid,	final Map<Integer, List<GermplasmAttributeDto>> attributesMap,
		final List<Integer> excludedVariableIdsForPropagation, final AdvanceRequest request, final Integer locationId) {
		if (attributesMap.containsKey(originalGermplasmGid)) {
			final Integer date = Integer.valueOf(LocalDate.now().format(DATE_TIME_FORMATTER));

			attributesMap.get(originalGermplasmGid).forEach((attributeDto -> {
				// Default passport descriptors variables should not be inherited from the source
				if (!excludedVariableIdsForPropagation.contains(attributeDto.getVariableId()) && request.getDescriptorIds().contains(attributeDto.getVariableId())) {
					final Integer attributeLocationId = request.isOverrideDescriptorsLocation() ? request.getLocationOverrideId() : locationId;
					this.daoFactory.getAttributeDAO()
						.save(this.createGermplasmAttribute(advancedGermplasmGid, attributeDto.getValue(), attributeDto.getcValueId(),
							attributeDto.getVariableId(), attributeLocationId, date));
				}
			}));
		}
	}

	private void createGermplasmAttributes(final String studyName,
		final AdvancingSource advancingSource, final Boolean allPlotsSelected,
		final Integer advancedGermplasmGid, final Integer selectionNumber,
		final String sampleNumber, final Integer locationId, final Integer date, final Integer plotCodeVariableId,
		final Integer plotNumberVariableId,
		final Integer repNumberVariableId,
		final Integer trialInstanceVariableId, final Integer plantNumberVariableId,
		final List<MeasurementVariable> studyEnvironmentVariables, final List<MeasurementVariable> environmentVariables,
		final Map<String, String> locationNameByIds,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber, final Integer datasetTypeId,
		final MeasurementVariable observationUnitVariable) {

		final ObservationUnitRow observation = advancingSource.getObservation();
		final String plotNumber = observation.getVariableValueByVariableId(TermId.PLOT_NO.getId());
		final String seedSource = this.generateSeedSource(studyName, selectionNumber, sampleNumber,
			plotNumber, advancingSource.getBreedingMethod().isBulkingMethod(),
			allPlotsSelected, advancingSource.getPlantsSelected(),
			observation, studyEnvironmentVariables, locationNameByIds, studyInstancesByInstanceNumber, environmentVariables);

		final Attribute plotCodeAttribute =
			this.createGermplasmAttribute(advancedGermplasmGid, seedSource, null, plotCodeVariableId, locationId, date);
		this.daoFactory.getAttributeDAO().save(plotCodeAttribute);

		if (plotNumberVariableId != null) {
			final Attribute plotNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, plotNumber, null, plotNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plotNumberAttribute);
		}

		final String replicationNumber = observation.getVariableValueByVariableId(TermId.REP_NO.getId());
		if (repNumberVariableId != null && !StringUtils.isEmpty(replicationNumber)) {
			final Attribute replicationNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, replicationNumber, null, repNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(replicationNumberAttribute);
		}

		this.addPlantNumberAttribute(advancedGermplasmGid, plantNumberVariableId, sampleNumber, locationId, date, datasetTypeId,
			observation, observationUnitVariable);

		if (trialInstanceVariableId != null) {
			final Attribute trialInstanceNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, observation.getTrialInstance().toString(), null,
					trialInstanceVariableId,
					locationId, date);
			this.daoFactory.getAttributeDAO().save(trialInstanceNumberAttribute);
		}
	}

	private String generateSeedSource(final String studyName, final int selectionNumber, final String sampleNumber,
		final String plotNumber,
		final Boolean isBulkingMethod,
		final Boolean allPlotsSelected,
		final Integer plantsSelected,
		final ObservationUnitRow row,
		final List<MeasurementVariable> studyEnvironmentVariables, final Map<String, String> locationNameByIds,
		final Map<Integer, StudyInstance> studyInstancesByInstanceNumber, final List<MeasurementVariable> environmentVariables) {

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
				studyName, sampleNumber, locationNameByIds, studyInstancesByInstanceNumber,
				environmentVariables);
	}

	private void addPlantNumberAttribute(final Integer advancedGermplasmGid, final Integer plantNumberVariableId, final String sampleNumber,
		final Integer locationId, final Integer date, final Integer datasetTypeId, final ObservationUnitRow observation,
		final MeasurementVariable observationUnitVariable) {

		if (plantNumberVariableId == null) {
			return;
		}

		// Adding attribute for samples advancement
		if (sampleNumber != null) {
			final Attribute plantNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, sampleNumber, null, plantNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plantNumberAttribute);
			return;
		}

		// Adding attribute for plants sub-observations advancement
		if (observationUnitVariable == null) {
			return;
		}

		final String plantNumber = observation.getVariableValueByVariableId(observationUnitVariable.getTermId());
		if (DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId() == datasetTypeId && plantNumber != null) {
			final Attribute plantNumberAttribute =
				this.createGermplasmAttribute(advancedGermplasmGid, plantNumber, null, plantNumberVariableId, locationId, date);
			this.daoFactory.getAttributeDAO().save(plantNumberAttribute);
		}
	}

	private Attribute createGermplasmAttribute(final Integer germplasmId, final String value, final Integer cValueId, final Integer typeId,
		final Integer locationId, final Integer date) {
		return new Attribute(null, germplasmId, typeId, value, cValueId, locationId, null, date);
	}

	private MeasurementVariable getObservationUnitVariable(final DatasetDTO datasetDTO) {
		if (DatasetTypeEnum.PLOT_DATA.getId() != datasetDTO.getDatasetTypeId()) {
			return datasetDTO.getVariables().stream()
				.filter(variable -> variable.getVariableType() == VariableType.OBSERVATION_UNIT)
				.findFirst()
				.orElse(null);
		}
		return null;
	}
}
