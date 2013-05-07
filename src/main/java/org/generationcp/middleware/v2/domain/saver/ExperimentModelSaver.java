package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Values;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;
import org.generationcp.middleware.v2.pojos.ExperimentStock;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public void addExperiment(int projectId, Values values) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		TermId experimentType = null;
		if (values instanceof StudyValues) {
			experimentType = TermId.STUDY_EXPERIMENT;
		} else {
			experimentType = TermId.PLOT_EXPERIMENT;
		}
		ExperimentModel experimentModel = create(projectId, values, experimentType);
		getExperimentDao().save(experimentModel);

		addExperimentProject(experimentModel, projectId);
		getPhenotypeSaver().savePhenotypes(experimentModel, values.getVariableList());
		getProjectPropertySaver().saveProjectPropValues(values.getVariableList());
	}
	
	private ExperimentModel create(int projectId, Values values, TermId expType) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();
		
		experimentModel.setNdExperimentId(getExperimentDao().getNegativeId("ndExperimentId"));
		experimentModel.setTypeId(expType.getId());
		experimentModel.setProperties(createProperties(experimentModel, values.getVariableList()));
		//TODO: what if values.locationId is null?
		if (values.getLocationId() != null) {
			experimentModel.setGeoLocation(getGeolocationDao().getById(values.getLocationId())); 
		}
		if (values.getGermplasmId() != null) {
			experimentModel.setExperimentStocks(new ArrayList<ExperimentStock>());
			experimentModel.getExperimentStocks().add(createExperimentStock(experimentModel.getNdExperimentId(), values.getGermplasmId()));
		}
		return experimentModel;
	}

	private Set<ExperimentProperty> createProperties(ExperimentModel experimentModel, VariableList factors) throws MiddlewareQueryException {
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
						
			for (Variable variable : factors.getVariables()) {
				if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId() == variable.getVariableType().getId()) {
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
	
	private void addExperimentProject(ExperimentModel experimentModel, int projectId) throws MiddlewareQueryException {
		ExperimentProject exproj = new ExperimentProject();
		
		exproj.setExperimentProjectId(getExperimentProjectDao().getNegativeId("experimentProjectId"));
		exproj.setProjectId(projectId);
		exproj.setExperiment(experimentModel);
		
		getExperimentProjectDao().save(exproj);
	}
	
	private ExperimentStock createExperimentStock(int experimentModelId, int stockId) throws MiddlewareQueryException {
		ExperimentStock experimentStock = new ExperimentStock();
		experimentStock.setExperimentStockId(getExperimentStockDao().getNegativeId("experimentStockId"));
		experimentStock.setTypeId(TermId.IBDB_STRUCTURE.getId());
		experimentStock.setStockId(stockId);
		experimentStock.setExperimentId(experimentModelId);
		
		return experimentStock;
	}
	
}
