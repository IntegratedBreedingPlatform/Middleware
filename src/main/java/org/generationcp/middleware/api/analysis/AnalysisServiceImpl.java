package org.generationcp.middleware.api.analysis;

import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalysisServiceImpl implements AnalysisService {

	private final DaoFactory daoFactory;
	private final StandardVariableBuilder standardVariableBuilder;

	public AnalysisServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.standardVariableBuilder = new StandardVariableBuilder(sessionProvider);
	}

	@Override
	public void importMeansData(final MeansRequestDto meansRequestDto) {

		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(meansRequestDto.getStudyId());

		// Create standard variables
		final Set<String> meansAnalysisNames = meansRequestDto.getData().get(0).getTraits().get(0).getValues().keySet();
		final Set<Integer> variableIds =
			meansRequestDto.getData().stream().map(MeansData::getTraits).collect(Collectors.toList()).stream().flatMap(
				Collection::stream).map(TraitMeans::getVariableId).collect(Collectors.toSet());

		final List<StandardVariable> standardVariables = this.standardVariableBuilder.create(new ArrayList<>(variableIds), "");
		this.createAnalysisStandardVariablesForTraits(standardVariables, meansAnalysisNames);

		// Create means dataset
		final DmsProject meansDataset = new DmsProject();
		meansDataset.setDatasetType(new DatasetType(DatasetTypeEnum.MEANS_DATA.getId()));
		meansDataset.setName(study.getName() + "-MEANS");
		meansDataset.setDescription("Means dataset for " + study.getName());
		meansDataset.setParent(study);
		meansDataset.setDeleted(false);
		this.daoFactory.getDmsProjectDAO().save(meansDataset);

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
			this.daoFactory.getExperimentDao().save(experimentModel);
		}

	}

	private Map<Integer, String> createAnalysisStandardVariablesForTraits(final List<StandardVariable> standardVariables,
		final Set<String> methodNames) {
		final Map<Integer, String> createdVariableMap = new HashMap<>();
		final Map<String, CVTerm> methodsMap = this.createOntologyMethodsIfNecessary(methodNames);
		standardVariables.stream().forEach((standardVariable -> {
			for (final String methodName : methodNames) {
				final Integer existingVariableId = this.daoFactory.getCvTermRelationshipDao()
					.retrieveAnalysisDerivedVariableID(standardVariable.getId(), methodsMap.get(methodName).getCvTermId());
				if (existingVariableId == null) {
					final CVTerm savedVariable = this.createAnalysisStandardVariable(standardVariable, methodsMap.get(methodName));
					createdVariableMap.putIfAbsent(savedVariable.getCvTermId(), savedVariable.getName());
				} else {
					final CVTerm existingVariable = this.daoFactory.getCvTermDao().getById(existingVariableId);
					createdVariableMap.putIfAbsent(existingVariable.getCvTermId(), existingVariable.getName());
				}
			}
		}));
		return createdVariableMap;
	}

	private CVTerm createAnalysisStandardVariable(final StandardVariable standardVariable, final CVTerm method) {
		// Create variable
		final CVTerm cvTermVariable = this.daoFactory.getCvTermDao()
			.save(standardVariable.getName() + "_" + method.getName(), standardVariable.getDescription(), CvId.VARIABLES);
		// Assign Property, Scale, Method
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_PROPERTY.getId(), standardVariable.getProperty().getId());
		this.daoFactory.getCvTermRelationshipDao()
			.save(cvTermVariable.getCvTermId(), TermId.HAS_SCALE.getId(), standardVariable.getScale().getId());
		this.daoFactory.getCvTermRelationshipDao().save(cvTermVariable.getCvTermId(), TermId.HAS_METHOD.getId(), method.getCvTermId());
		// Assign variable type
		this.daoFactory.getCvTermPropertyDao().save(cvTermVariable.getCvTermId(), TermId.VARIABLE_TYPE.getId(), "Analysis", 0);
		// Link the new analysis standar variable to the analyzed trait
		this.daoFactory.getCvTermRelationshipDao()
			.save(standardVariable.getId(), TermId.HAS_ANALYSIS_VARIABLE.getId(), cvTermVariable.getCvTermId());
		return cvTermVariable;
	}

	private Map<String, CVTerm> createOntologyMethodsIfNecessary(final Set<String> methodNames) {
		final Map<String, CVTerm> methodsMap =
			this.daoFactory.getCvTermDao().getTermsByNameAndCvId(methodNames, CvId.METHODS.getId()).stream()
				.collect(Collectors.toMap(CVTerm::getName, Function.identity()));

		for (final String methodName : methodNames) {
			if (!methodsMap.containsKey(methodName)) {
				final CVTerm newMethod = new CVTerm();
				newMethod.setName(methodName);
				newMethod.setDefinition(methodName + " - system generated method");
				newMethod.setCv(CvId.METHODS.getId());
				newMethod.setIsObsolete(false);
				newMethod.setIsRelationshipType(false);
				newMethod.setIsSystem(false);
				methodsMap.putIfAbsent(methodName, this.daoFactory.getCvTermDao().save(newMethod));
			}
		}
		return methodsMap;
	}
}
