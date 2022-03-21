package org.generationcp.middleware.service.impl.analysis;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;

import java.util.ArrayList;
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
			.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), VariableType.ENVIRONMENT_DETAIL).build();

	private final DaoFactory daoFactory;

	public SiteAnalysisServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Integer createMeansDataset(final Integer studyId, final MeansImportRequest meansImportRequest) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final Set<String> analysisVariableNames =
			meansImportRequest.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream).collect(Collectors.toSet());
		final Map<String, CVTerm> analysisVariablesMap =
			new CaseInsensitiveMap(
				this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisVariableNames, CvId.VARIABLES).stream().collect(Collectors.toMap(
					CVTerm::getName, Function.identity())));

		// Create means dataset
		final DmsProject meansDataset = this.createDataset(study, DatasetTypeEnum.MEANS_DATA, "-MEANS");
		// Add necessary dataset project properties
		this.addMeansDatasetVariables(meansDataset, analysisVariablesMap);
		// Save means experiment and means values
		this.saveMeansExperimentAndValues(studyId, meansDataset, analysisVariablesMap, meansImportRequest);

		return meansDataset.getProjectId();
	}

	@Override
	public Integer createSummaryStatisticsDataset(final Integer studyId,
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		final Set<String> analysisSummaryVariableNames =
			summaryStatisticsImportRequest.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream)
				.collect(Collectors.toSet());
		final Map<String, CVTerm> analysisSummaryVariablesMap =
			new CaseInsensitiveMap(
				this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisSummaryVariableNames, CvId.VARIABLES).stream()
					.collect(Collectors.toMap(
						CVTerm::getName, Function.identity())));

		// Create summary statistics dataset
		final DmsProject summaryStatisticsDataset =
			this.createDataset(study, DatasetTypeEnum.SUMMARY_STATISTICS_DATA, "-SUMMARY_STATISTICS");
		// Add necessary dataset project properties
		this.addSummaryStatisticsDatasetVariables(summaryStatisticsDataset, analysisSummaryVariablesMap);
		// Save summary statistics experiment and values
		this.saveSummaryStatisticsExperimentAndValues(studyId, summaryStatisticsDataset, analysisSummaryVariablesMap,
			summaryStatisticsImportRequest);

		return summaryStatisticsDataset.getProjectId();
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

	private void saveMeansExperimentAndValues(final int studyId, final DmsProject meansDataset,
		final Map<String, CVTerm> analaysisVariablesMap, final MeansImportRequest meansImportRequest) {

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
			this.saveExperimentModel(analaysisVariablesMap, experimentModel, meansData.getValues());
		}
	}

	private void saveSummaryStatisticsExperimentAndValues(final int studyId, final DmsProject summaryStatisticsDataset,
		final Map<String, CVTerm> analaysisSummaryVariablesMap,
		final SummaryStatisticsImportRequest summaryStatisticsImportRequest) {

		final Map<String, Geolocation> environmentNumberGeolocationMap =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId).stream()
				.collect(Collectors.toMap(Geolocation::getDescription, Function.identity()));

		// Save summary statistics experiment and summary values
		for (final SummaryStatisticsImportRequest.SummaryData meansData : summaryStatisticsImportRequest.getData()) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setProject(summaryStatisticsDataset);
			experimentModel.setGeoLocation(environmentNumberGeolocationMap.get(String.valueOf(meansData.getEnvironmentNumber())));
			experimentModel.setTypeId(ExperimentType.SUMMARY.getTermId());
			this.saveExperimentModel(analysisSummaryVariablesMap, experimentModel, meansData.getValues());
		}
	}

	private void saveExperimentModel(final Map<String, CVTerm> analaysisVariablesMap, final ExperimentModel experimentModel,
		final Map<String, Double> values) {
		final List<Phenotype> phenotypes = new ArrayList<>();
		for (final Map.Entry<String, Double> meansMapEntryValue : values.entrySet()) {
			if (meansMapEntryValue.getValue() != null) {
				final Phenotype phenotype = new Phenotype();
				phenotype.setExperiment(experimentModel);
				phenotype.setValue(meansMapEntryValue.getValue().toString());
				phenotype.setObservableId(analaysisVariablesMap.get(meansMapEntryValue.getKey()).getCvTermId());
				phenotypes.add(phenotype);
			}
		}
		experimentModel.setPhenotypes(phenotypes);
		this.daoFactory.getExperimentDao().save(experimentModel);
	}

	private void addMeansDatasetVariables(final DmsProject meansDataset, final Map<String, CVTerm> analaysisVariablesMap) {
		final AtomicInteger rank = new AtomicInteger();

		final List<CVTerm> cvTerms = this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(MEANS_DATASET_DMSPROJECT_PROPERTIES.keySet()));
		cvTerms.forEach(term -> this.addProjectProperty(meansDataset, term.getCvTermId(), term.getName(),
			MEANS_DATASET_DMSPROJECT_PROPERTIES.get(term.getCvTermId()), rank.incrementAndGet()));

		// Retrieve the germplasm descriptor variables from PLOT dataset and copy it to the means dataset
		final Map<Integer, String> germplasmDescriptorsMap =
			this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(meansDataset.getStudy().getProjectId());
		for (final Map.Entry<Integer, String> entry : germplasmDescriptorsMap.entrySet()) {
			this.addProjectProperty(meansDataset, entry.getKey(), entry.getValue(), VariableType.GERMPLASM_DESCRIPTOR,
				rank.incrementAndGet());
		}

		for (final Map.Entry<String, CVTerm> entry : analaysisVariablesMap.entrySet()) {
			this.addProjectProperty(meansDataset, entry.getValue().getCvTermId(), entry.getValue().getName(), VariableType.ANALYSIS,
				rank.incrementAndGet());
		}
	}

	private void addSummaryStatisticsDatasetVariables(final DmsProject summaryStatisticDataset,
		final Map<String, CVTerm> analaysisSummaryVariablesMap) {
		final AtomicInteger rank = new AtomicInteger();

		final List<CVTerm> cvTerms =
			this.daoFactory.getCvTermDao().getByIds(new ArrayList<>(SUMMARY_STATISTICS_DATASET_DMSPROJECT_PROPERTIES.keySet()));
		cvTerms.forEach(term -> this.addProjectProperty(summaryStatisticDataset, term.getCvTermId(), term.getName(),
			SUMMARY_STATISTICS_DATASET_DMSPROJECT_PROPERTIES.get(term.getCvTermId()), rank.incrementAndGet()));

		for (final Map.Entry<String, CVTerm> entry : analaysisSummaryVariablesMap.entrySet()) {
			this.addProjectProperty(summaryStatisticDataset, entry.getValue().getCvTermId(), entry.getValue().getName(),
				VariableType.ANALYSIS_SUMMARY,
				rank.incrementAndGet());
		}
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
