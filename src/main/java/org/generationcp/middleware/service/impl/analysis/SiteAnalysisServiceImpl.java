package org.generationcp.middleware.service.impl.analysis;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SiteAnalysisServiceImpl implements SiteAnalysisService {

	public static final Map<Integer, VariableType> MEANS_DATASET_DMSPROJECT_PROPERTIES = ImmutableMap.<Integer, VariableType>builder()
		.put(TermId.DATASET_NAME.getId(), VariableType.STUDY_DETAIL)
		.put(TermId.DATASET_TITLE.getId(), VariableType.STUDY_DETAIL)
		.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), VariableType.ENVIRONMENT_DETAIL).build();

	public static final Map<Integer, VariableType> SUMMARY_STATISTICS_DATASET_DMSPROJECT_PROPERTIES =
		ImmutableMap.<Integer, VariableType>builder()
			.put(TermId.DATASET_NAME.getId(), VariableType.STUDY_DETAIL)
			.put(TermId.DATASET_TITLE.getId(), VariableType.STUDY_DETAIL)
			.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), VariableType.ENVIRONMENT_DETAIL)
			.put(TermId.LOCATION_ID.getId(), VariableType.ENVIRONMENT_DETAIL).build();
	public static final String LOCATION_NAME = "LOCATION_NAME";
	public static final String DELIMITER = ", ";

	private final DaoFactory daoFactory;

	public SiteAnalysisServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createMeansDataset(final String crop, final Integer studyId, final MeansImportRequest meansImportRequest) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final Set<String> analysisVariableNames =
			meansImportRequest.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream).collect(Collectors.toSet());
		final Map<String, CVTerm> analysisVariablesMap =
			new CaseInsensitiveMap(
				this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisVariableNames, CvId.VARIABLES, true).stream()
					.collect(Collectors.toMap(
						CVTerm::getName, Function.identity())));

		// Create means dataset
		final DmsProject meansDataset = this.createDataset(study, DatasetTypeEnum.MEANS_DATA, "-MEANS");
		// Add necessary dataset project properties
		this.addMeansDatasetVariables(meansDataset, analysisVariablesMap);
		// Save means experiment and means values
		this.saveMeansExperimentAndValues(crop, studyId, meansDataset, analysisVariablesMap, meansImportRequest);

		return meansDataset.getProjectId();
	}

	@Override
	public Integer createSummaryStatisticsDataset(final String crop, final Integer studyId,
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final Set<String> analysisSummaryVariableNames =
			summaryStatisticsImportRequest.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream)
				.collect(Collectors.toSet());
		final Map<String, CVTerm> analysisSummaryVariablesMap =
			new CaseInsensitiveMap(
				this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisSummaryVariableNames, CvId.VARIABLES, true).stream()
					.collect(Collectors.toMap(
						CVTerm::getName, Function.identity())));

		// Create summary statistics dataset
		final DmsProject summaryStatisticsDataset =
			this.createDataset(study, DatasetTypeEnum.SUMMARY_STATISTICS_DATA, "-SUMMARY_STATISTICS");
		// Add necessary dataset project properties
		this.addSummaryStatisticsFixedVariables(summaryStatisticsDataset);
		this.addSummaryStatisticsDatasetVariables(summaryStatisticsDataset, analysisSummaryVariablesMap);
		// Save summary statistics experiment and values
		this.saveOrUpdateSummaryStatisticsExperimentAndValues(crop, summaryStatisticsDataset, analysisSummaryVariablesMap,
			summaryStatisticsImportRequest);

		return summaryStatisticsDataset.getProjectId();
	}

	@Override
	public void updateSummaryStatisticsDataset(final String crop, final Integer summaryStatisticsDatasetId,
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest) {

		// Get the existing summary statistics dataset
		final DmsProject summaryStatisticsDataset = this.daoFactory.getDmsProjectDAO().getById(summaryStatisticsDatasetId);

		final Set<String> analysisSummaryVariableNames =
			summaryStatisticsImportRequest.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream)
				.collect(Collectors.toSet());
		final Map<String, CVTerm> analysisSummaryVariablesMap =
			new CaseInsensitiveMap(
				this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisSummaryVariableNames, CvId.VARIABLES, true).stream()
					.collect(Collectors.toMap(
						CVTerm::getName, Function.identity())));

		// Add variables not yet present in the dataset if there's any.
		this.addSummaryStatisticsDatasetVariables(summaryStatisticsDataset, analysisSummaryVariablesMap);
		// Save summary statistics experiment and values
		this.saveOrUpdateSummaryStatisticsExperimentAndValues(crop, summaryStatisticsDataset, analysisSummaryVariablesMap,
			summaryStatisticsImportRequest);

	}

	@Override
	public Integer createMeansDataset(final Integer studyId) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final DmsProject meansDataset = this.createDataset(study, DatasetTypeEnum.MEANS_DATA, "-MEANS");
		this.addMeansDatasetVariables(meansDataset, new HashMap<>());
		return meansDataset.getProjectId();
	}

	private DmsProject createDataset(final DmsProject study, final DatasetTypeEnum datasetType, final String nameSuffix) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setDatasetType(new DatasetType(datasetType.getId()));
		dmsProject.setName(study.getName() + nameSuffix);
		dmsProject.setDescription(study.getDescription() + nameSuffix);
		dmsProject.setParent(study);
		dmsProject.setStudy(study);
		dmsProject.setDeleted(false);
		dmsProject.setProgramUUID(study.getProgramUUID());
		this.daoFactory.getDmsProjectDAO().save(dmsProject);
		return dmsProject;
	}

	private void saveMeansExperimentAndValues(final String crop, final int studyId, final DmsProject meansDataset,
		final Map<String, CVTerm> analysisVariablesMap, final MeansImportRequest meansImportRequest) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(crop);
		final Set<String> entryNumbers =
			meansImportRequest.getData().stream().map(m -> String.valueOf(m.getEntryNo())).collect(Collectors.toSet());
		final Map<String, StockModel>
			stockModelMap =
			this.daoFactory.getStockDao().getStocksByStudyAndEntryNumbers(studyId, entryNumbers).stream()
				.collect(Collectors.toMap(StockModel::getUniqueName, Function.identity()));
		final Map<String, Geolocation> environmentNumberGeolocationMap =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId).stream()
				.collect(Collectors.toMap(Geolocation::getDescription, Function.identity()));

		// Save means experiment and means values
		for (final MeansImportRequest.MeansData meansData : meansImportRequest.getData()) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setProject(meansDataset);
			experimentModel.setGeoLocation(environmentNumberGeolocationMap.get(String.valueOf(meansData.getEnvironmentNumber())));
			experimentModel.setTypeId(ExperimentType.AVERAGE.getTermId());
			experimentModel.setStock(stockModelMap.get(String.valueOf(meansData.getEntryNo())));
			ObservationUnitIDGenerator.generateObservationUnitIds(cropType, Collections.singletonList(experimentModel));
			this.saveOrUpdateExperimentModel(analysisVariablesMap, experimentModel, meansData.getValues());
		}
	}

	private void saveOrUpdateSummaryStatisticsExperimentAndValues(final String crop, final DmsProject summaryStatisticsDataset,
		final Map<String, CVTerm> analysisSummaryVariablesMap,
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(crop);
		final Map<String, Geolocation> environmentNumberGeolocationMap =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(summaryStatisticsDataset.getStudy().getProjectId()).stream()
				.collect(Collectors.toMap(Geolocation::getDescription, Function.identity()));

		final Map<String, ExperimentModel> experimentModelByTrialInstanceMap =
			this.daoFactory.getExperimentDao().getExperimentsByProjectIds(Arrays.asList(summaryStatisticsDataset.getProjectId())).stream()
				.collect(Collectors.toMap(e -> e.getGeoLocation().getDescription(), Function.identity()));

		// Save or update summary statistics experiment and summary values
		for (final SummaryStatisticsImportRequest.SummaryData summaryData : summaryStatisticsImportRequest.getData()) {
			final ExperimentModel newExperimentModel = new ExperimentModel();
			newExperimentModel.setProject(summaryStatisticsDataset);
			newExperimentModel.setGeoLocation(environmentNumberGeolocationMap.get(String.valueOf(summaryData.getEnvironmentNumber())));
			newExperimentModel.setTypeId(ExperimentType.SUMMARY.getTermId());
			ObservationUnitIDGenerator.generateObservationUnitIds(cropType, Collections.singletonList(newExperimentModel));
			final ExperimentModel experimentModel =
				experimentModelByTrialInstanceMap.getOrDefault(String.valueOf(summaryData.getEnvironmentNumber()), newExperimentModel);
			this.saveOrUpdateExperimentModel(analysisSummaryVariablesMap, experimentModel, summaryData.getValues());
		}
	}

	private void saveOrUpdateExperimentModel(final Map<String, CVTerm> variablesMap, final ExperimentModel experimentModel,
		final Map<String, Double> values) {
		final Map<Integer, Phenotype> phenotypesMap = CollectionUtils.isEmpty(experimentModel.getPhenotypes()) ? new HashMap<>() :
			experimentModel.getPhenotypes().stream().collect(Collectors.toMap(Phenotype::getObservableId, Function.identity()));
		for (final Map.Entry<String, Double> mapEntry : values.entrySet()) {
			final int observableId = variablesMap.get(mapEntry.getKey()).getCvTermId();
			if (mapEntry.getValue() != null) {
				if (phenotypesMap.containsKey(observableId)) {
					final Phenotype existingPhenotype = phenotypesMap.get(observableId);
					existingPhenotype.setValue(mapEntry.getValue().toString());
				} else {
					final Phenotype phenotype = new Phenotype();
					phenotype.setExperiment(experimentModel);
					phenotype.setValue(mapEntry.getValue().toString());
					phenotype.setObservableId(observableId);
					phenotypesMap.put(observableId, phenotype);
				}
			}
		}
		experimentModel.setPhenotypes(new ArrayList<>(phenotypesMap.values()));
		this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);
	}

	private void addMeansDatasetVariables(final DmsProject meansDataset, final Map<String, CVTerm> analaysisVariablesMap) {
		final AtomicInteger rank = new AtomicInteger();

		final List<CVTerm> cvTerms = this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(MEANS_DATASET_DMSPROJECT_PROPERTIES.keySet()));
		cvTerms.forEach(term -> this.addProjectProperty(meansDataset, term.getCvTermId(), term.getName(),
			MEANS_DATASET_DMSPROJECT_PROPERTIES.get(term.getCvTermId()), rank.incrementAndGet()));

		// Retrieve the germplasm descriptor variables from PLOT dataset and copy it to the means dataset
		final Map<Integer, String> germplasmDescriptorsMap =
			this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(meansDataset.getStudy().getProjectId());
		final String obsoleteDescriptors =
			this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(germplasmDescriptorsMap.keySet())).stream()
				.filter(CVTerm::isObsolete).map(CVTerm::getName).collect(Collectors.joining(DELIMITER));
		if (StringUtils.isNotEmpty(obsoleteDescriptors)) {
			throw new MiddlewareException("",  ErrorCode.ERROR_OBSOLETE_VARIABLES, obsoleteDescriptors);
		}

		for (final Map.Entry<Integer, String> entry : germplasmDescriptorsMap.entrySet()) {
			this.addProjectProperty(meansDataset, entry.getKey(), entry.getValue(), VariableType.GERMPLASM_DESCRIPTOR,
				rank.incrementAndGet());
		}

		final Set<CVTerm> obsoleteAnalysisVariables = analaysisVariablesMap.entrySet().stream()
			.filter(entry -> entry.getValue().isObsolete())
			.map(Map.Entry::getValue)
			.collect(Collectors.toSet());
		if (!obsoleteAnalysisVariables.isEmpty()) {
			throw new MiddlewareException("", ErrorCode.ERROR_OBSOLETE_VARIABLES, obsoleteAnalysisVariables.stream()
				.map(CVTerm::getName).collect(Collectors.joining(DELIMITER)));
		}
		for (final Map.Entry<String, CVTerm> entry : analaysisVariablesMap.entrySet()) {
			this.addProjectProperty(meansDataset, entry.getValue().getCvTermId(), entry.getValue().getName(), VariableType.ANALYSIS,
				rank.incrementAndGet());
		}
	}

	private void addSummaryStatisticsFixedVariables(final DmsProject summaryStatisticDataset) {
		final AtomicInteger rank = new AtomicInteger();
		final List<CVTerm> cvTerms =
			this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(SUMMARY_STATISTICS_DATASET_DMSPROJECT_PROPERTIES.keySet()));
		cvTerms.forEach(term -> this.addProjectProperty(summaryStatisticDataset, term.getCvTermId(), this.resolveAlias(term),
			SUMMARY_STATISTICS_DATASET_DMSPROJECT_PROPERTIES.get(term.getCvTermId()), rank.incrementAndGet()));
	}

	private void addSummaryStatisticsDatasetVariables(final DmsProject summaryStatisticDataset,
		final Map<String, CVTerm> analaysisSummaryVariablesMap) {
		final AtomicInteger rank =
			new AtomicInteger(this.daoFactory.getProjectPropertyDAO().getNextRank(summaryStatisticDataset.getProjectId()));

		final Set<Integer> existingVariablesInDataset =
			this.daoFactory.getProjectPropertyDAO().getByProjectId(summaryStatisticDataset.getProjectId()).stream()
				.map(ProjectProperty::getVariableId).collect(
					Collectors.toSet());

		// Only add variables not yet exist in the dataset.
		final Set<CVTerm> variablesToAddInDataset = analaysisSummaryVariablesMap.entrySet().stream()
			.filter(entry -> !existingVariablesInDataset.contains(entry.getValue().getCvTermId()))
			.map(Map.Entry::getValue)
			.collect(Collectors.toSet());

		final String obsoleteVariables = variablesToAddInDataset.stream()
			.filter(CVTerm::isObsolete).map(CVTerm::getName).collect(Collectors.joining(DELIMITER));
		if (StringUtils.isNotEmpty(obsoleteVariables)) {
			throw new MiddlewareException("",  ErrorCode.ERROR_OBSOLETE_VARIABLES, obsoleteVariables);
		}

		variablesToAddInDataset.forEach(entry ->
			this.addProjectProperty(summaryStatisticDataset, entry.getCvTermId(), entry.getName(),
				VariableType.ANALYSIS_SUMMARY,
				rank.incrementAndGet())
		);
	}

	private String resolveAlias(final CVTerm cvTerm) {
		if (cvTerm.getCvTermId().intValue() == TermId.LOCATION_ID.getId()) {
			return LOCATION_NAME;
		}
		return cvTerm.getName();
	}

	private void addProjectProperty(final DmsProject meansDataset, final Integer variableId, final String variableName,
		final VariableType variableType, final Integer rank) {
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(meansDataset);
		projectProperty.setAlias(variableName);
		projectProperty.setVariableId(variableId);
		projectProperty.setRank(rank);
		projectProperty.setTypeId(variableType.getId());
		this.daoFactory.getProjectPropertyDAO().save(projectProperty);
	}

}
