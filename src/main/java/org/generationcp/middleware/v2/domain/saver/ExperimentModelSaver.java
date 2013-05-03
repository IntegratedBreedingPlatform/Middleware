package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void saveForDataSet(DmsProject project, Collection<Experiment> experiments) throws MiddlewareQueryException {
		Collection<ExperimentModel> experimentModels = createForDataSet(project, experiments);
		for (ExperimentModel experimentModel : experimentModels) {
			getExperimentDao().save(experimentModel);
			//getPhenotypeSaver().saveExperimentPhenotypes(experimentModel, variates);
		}
	}

	public void saveForStudy(DmsProject project, Study study) throws MiddlewareQueryException {
		ExperimentModel experimentModel = createForStudy(project, study);
		getExperimentDao().save(experimentModel);
	}

	public List<ExperimentModel> createForDataSet(DmsProject project, Collection<Experiment> experiments) throws MiddlewareQueryException {
		List<ExperimentModel> experimentModels = new ArrayList<ExperimentModel>();
		for (Experiment experiment : experiments) {
			ExperimentModel experimentModel = create(project, experiment.getFactors(), experiment.getVariates());
			experimentModel.setTypeId(CVTermId.PLOT_EXPERIMENT.getId());
			experimentModels.add(experimentModel);
		}
		
		return experimentModels;
	}

	public ExperimentModel createForStudy(DmsProject project, Study study) throws MiddlewareQueryException {
		ExperimentModel experimentModel = create(project, study.getConditions(), study.getConstants());
		experimentModel.setTypeId(CVTermId.STUDY_EXPERIMENT.getId());
		
		return experimentModel;
	}

	private ExperimentModel create(DmsProject project, VariableList factors, VariableList variates) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();
		
		experimentModel.setNdExperimentId(getExperimentDao().getNegativeId("ndExperimentId"));
		experimentModel.setProject(project); //TODO: can not use cascade save,need neg id for experiment_project
		experimentModel.setGeoLocation(getGeolocationSaver().create(factors));
		experimentModel.setProperties(createProperties(experimentModel, factors));
		experimentModel.setExperimentStocks(getStockSaver().createExperimentStocks(experimentModel, factors));
		//experimentModel.setPhenotypes(getPhenotypeSaver().create(variates)); //TODO: can not use cascade save, need neg id for experiment_phenotype
		
		return experimentModel;
	}

	public Set<ExperimentProperty> createProperties(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
						
			for (Variable variable : factors.getVariables()) {
				ExperimentProperty property = new ExperimentProperty();
				property.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
				
				if (CVTermId.TRIAL_DESIGN_INFO_STORAGE.getId().equals(variable.getVariableType().getId())) {
					addProperty(experimentModel, property);
				}
			}
		}
		
		return experimentModel.getProperties();
	}
	
	private void addProperty(ExperimentModel experimentModel, ExperimentProperty property) throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new HashSet<ExperimentProperty>());
		}
		property.setExperiment(experimentModel);
		experimentModel.getProperties().add(property);
	}
}
