package org.generationcp.middleware.v2.domain.saver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void saveForDataSet(int projectId, Collection<Experiment> experiments) throws MiddlewareQueryException {
		for (Experiment experiment : experiments) {
			saveExperiment(projectId, experiment.getFactors(), experiment.getVariates(), TermId.PLOT_EXPERIMENT);
		}
	}

	public void saveForStudy(int projectId, Study study) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		saveExperiment(projectId, study.getConditions(), study.getConstants(), TermId.STUDY_EXPERIMENT);
	}
	
	private void saveExperiment(int projectId, VariableList factors, VariableList variates, TermId expType) 
	throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		ExperimentModel experimentModel = create(projectId, factors, variates, expType);
		getExperimentDao().save(experimentModel);

		//save fk tables for those where cascade save can't be used
		saveExperimentProject(experimentModel, projectId);
		getPhenotypeSaver().savePhenotypes(experimentModel, variates);
	}
	
	private ExperimentModel create(int projectId, VariableList factors, VariableList variates, TermId expType) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();
		
		experimentModel.setNdExperimentId(getExperimentDao().getNegativeId("ndExperimentId"));
		experimentModel.setTypeId(expType.getId());
		experimentModel.setProperties(createProperties(experimentModel, factors));
		experimentModel.setGeoLocation(getGeolocationSaver().create(projectId, factors)); 
		experimentModel.setExperimentStocks(getStockSaver().createExperimentStocks(experimentModel, factors));
		
		return experimentModel;
	}

	private Set<ExperimentProperty> createProperties(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
						
			for (Variable variable : factors.getVariables()) {
				if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId().equals(variable.getVariableType().getId())) {
					addProperty(experimentModel, variable);
				}
			}
		}
		
		return experimentModel.getProperties();
	}
	
	private void addProperty(ExperimentModel experimentModel, Variable variable) throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new HashSet<ExperimentProperty>());
		}
		ExperimentProperty property = new ExperimentProperty();
		
		property.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
		property.setExperiment(experimentModel);
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());
		
		experimentModel.getProperties().add(property);
	}
	
	private void saveExperimentProject(ExperimentModel experimentModel, int projectId) throws MiddlewareQueryException {
		ExperimentProject exproj = new ExperimentProject();
		
		exproj.setExperimentProjectId(getExperimentProjectDao().getNegativeId("experimentProjectId"));
		exproj.setProjectId(projectId);
		exproj.setExperiment(experimentModel);
		
		getExperimentProjectDao().save(exproj);
	}
}
