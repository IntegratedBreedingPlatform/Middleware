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
		final Map<String, Geolocation> environmentNumberGeolocationMap =
			this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId).stream()
				.collect(Collectors.toMap(Geolocation::getDescription, Function.identity()));

		// Create means dataset
		final DmsProject meansDataset = this.createMeansDataset(study);
		// Add necessary dataset project properties
		this.addMeansDatasetVariables(meansDataset, analysisVariablesMap);
		// Save means experiment and means values
		this.saveMeansExperimentAndValues(studyId, meansDataset, analysisVariablesMap, environmentNumberGeolocationMap, meansImportRequest);

		return meansDataset.getProjectId();
	}

	private DmsProject createMeansDataset(final DmsProject study) {
		final DmsProject meansDataset = new DmsProject();
		meansDataset.setDatasetType(new DatasetType(DatasetTypeEnum.MEANS_DATA.getId()));
		meansDataset.setName(study.getName() + "-MEANS");
		meansDataset.setDescription(study.getName() + "-MEANS");
		meansDataset.setParent(study);
		meansDataset.setStudy(study);
		meansDataset.setDeleted(false);
		meansDataset.setProgramUUID(study.getProgramUUID());
		this.daoFactory.getDmsProjectDAO().save(meansDataset);
		return meansDataset;
	}

	private void saveMeansExperimentAndValues(final int studyId, final DmsProject meansDataset,
		final Map<String, CVTerm> analaysisVariablesMap,
		final Map<String, Geolocation> environmentNumberGeolocationMap, final MeansImportRequest meansImportRequest) {

		final Set<String> entryNumbers =
			meansImportRequest.getData().stream().map(m -> String.valueOf(m.getEntryNo())).collect(Collectors.toSet());
		final Map<String, StockModel>
			stockModelMap =
			this.daoFactory.getStockDao().getStocksByStudyAndEntryNumbers(studyId, entryNumbers).stream()
				.collect(Collectors.toMap(StockModel::getUniqueName, Function.identity()));

		// Save means experiment and means values
		for (final MeansImportRequest.MeansData meansData : meansImportRequest.getData()) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setProject(meansDataset);
			experimentModel.setGeoLocation(environmentNumberGeolocationMap.get(String.valueOf(meansData.getEnvironmentNumber())));
			experimentModel.setTypeId(ExperimentType.AVERAGE.getTermId());
			experimentModel.setStock(stockModelMap.get(String.valueOf(meansData.getEntryNo())));
			final List<Phenotype> phenotypes = new ArrayList<>();
			for (final Map.Entry<String, Double> meansMapEntryValue : meansData.getValues().entrySet()) {
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
