package org.generationcp.middleware.api.analysis;

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalysisServiceImpl implements AnalysisService {

	private final DaoFactory daoFactory;

	public AnalysisServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void createMeansDataset(final MeansRequestDto meansRequestDto) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(meansRequestDto.getStudyId());
		final Set<String> analysisVariableNames =
			meansRequestDto.getData().stream().map(o -> o.getValues().keySet()).flatMap(Set::stream).collect(Collectors.toSet());

		final Map<String, CVTerm> analaysisVariablesMap =
			this.daoFactory.getCvTermDao().getByNamesAndCvId(analysisVariableNames, CvId.VARIABLES).stream().collect(Collectors.toMap(
				CVTerm::getName, Function.identity()));

		// Create means dataset
		final DmsProject meansDataset = new DmsProject();
		meansDataset.setDatasetType(new DatasetType(DatasetTypeEnum.MEANS_DATA.getId()));
		meansDataset.setName(study.getName() + "-MEANS");
		meansDataset.setDescription(study.getName() + "-MEANS");
		meansDataset.setParent(study);
		meansDataset.setDeleted(false);
		meansDataset.setProgramUUID(study.getProgramUUID());
		this.daoFactory.getDmsProjectDAO().save(meansDataset);

		final ProjectProperty datasetNameProperty = new ProjectProperty();
		datasetNameProperty.setProject(meansDataset);
		datasetNameProperty.setAlias(TermId.DATASET_NAME.name());
		datasetNameProperty.setVariableId(TermId.DATASET_NAME.getId());
		datasetNameProperty.setDescription("Dataset name (local)");
		datasetNameProperty.setRank(1);
		datasetNameProperty.setTypeId(VariableType.STUDY_DETAIL.getId());
		this.daoFactory.getProjectPropertyDAO().save(datasetNameProperty);

		final ProjectProperty datasetTitleProperty = new ProjectProperty();
		datasetTitleProperty.setProject(meansDataset);
		datasetTitleProperty.setAlias(TermId.DATASET_TITLE.name());
		datasetTitleProperty.setVariableId(TermId.DATASET_TITLE.getId());
		datasetTitleProperty.setDescription("Dataset title (local)");
		datasetTitleProperty.setRank(1);
		datasetTitleProperty.setTypeId(VariableType.STUDY_DETAIL.getId());
		this.daoFactory.getProjectPropertyDAO().save(datasetTitleProperty);

		for (final Map.Entry<String, CVTerm> entry : analaysisVariablesMap.entrySet()) {
			final ProjectProperty property = new ProjectProperty();
			property.setProject(meansDataset);
			property.setAlias(entry.getValue().getName());
			property.setVariableId(entry.getValue().getCvTermId());
			property.setRank(1);
			property.setTypeId(VariableType.ANALYSIS.getId());
			this.daoFactory.getProjectPropertyDAO().save(property);
		}

		final Map<String, StockModel>
			stockModelMap = this.daoFactory.getStockDao().getStocksForStudy(meansRequestDto.getStudyId()).stream()
			.collect(Collectors.toMap(StockModel::getUniqueName, Function.identity()));

		// Save means experiment and means values
		for (final MeansData meansData : meansRequestDto.getData()) {
			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setProject(meansDataset);
			experimentModel.setGeoLocation(new Geolocation(meansData.getEnvironmentId()));
			experimentModel.setTypeId(ExperimentType.AVERAGE.getTermId());
			experimentModel.setStock(stockModelMap.get(String.valueOf(meansData.getEntryNo())));
			final List<Phenotype> phenotypes = new ArrayList<>();
			for (final Map.Entry<String, String> meansMapEntryValue : meansData.getValues().entrySet()) {
				final Phenotype phenotype = new Phenotype();
				phenotype.setExperiment(experimentModel);
				phenotype.setValue(meansMapEntryValue.getValue());
				phenotype.setObservableId(analaysisVariablesMap.get(meansMapEntryValue.getKey()).getCvTermId());
				phenotypes.add(phenotype);
			}
			experimentModel.setPhenotypes(phenotypes);
			this.daoFactory.getExperimentDao().save(experimentModel);
		}

	}

}
