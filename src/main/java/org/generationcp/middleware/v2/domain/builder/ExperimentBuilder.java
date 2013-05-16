package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.StandardVariable;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;
import org.generationcp.middleware.v2.pojos.ExperimentStock;
import org.generationcp.middleware.v2.pojos.Geolocation;
import org.generationcp.middleware.v2.pojos.GeolocationProperty;
import org.generationcp.middleware.v2.pojos.Phenotype;
import org.generationcp.middleware.v2.pojos.Stock;
import org.generationcp.middleware.v2.pojos.StockProperty;

public class ExperimentBuilder extends Builder {

	public ExperimentBuilder(HibernateSessionProvider sessionProviderForLocal,
			                 HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public long count(int dataSetId) throws MiddlewareQueryException {
		if (setWorkingDatabase(dataSetId)) {
			return getExperimentProjectDao().count(dataSetId);
		}
		return 0;
	}

	public List<Experiment> build(int projectId, TermId type, int start, int numOfRows, VariableTypeList variableTypes) throws MiddlewareQueryException {
		List<Experiment> experiments = new ArrayList<Experiment>();
		if (setWorkingDatabase(projectId)) {
			List<ExperimentProject> experimentProjects = getExperimentProjectDao().getExperimentProjects(projectId, type.getId(), start, numOfRows);
			for (ExperimentProject experimentProject : experimentProjects) {
				experiments.add(createExperiment(experimentProject.getExperiment(), variableTypes));
			}
		}
		return experiments;
	}
	
	public List<Experiment> build(int projectId, List<TermId> types, int start, int numOfRows, VariableTypeList variableTypes) throws MiddlewareQueryException {
		List<Experiment> experiments = new ArrayList<Experiment>();
		if (setWorkingDatabase(projectId)) {
			List<ExperimentProject> experimentProjects = getExperimentProjectDao().getExperimentProjects(projectId, types, start, numOfRows);
			for (ExperimentProject experimentProject : experimentProjects) {
				experiments.add(createExperiment(experimentProject.getExperiment(), variableTypes));
			}
		}
		return experiments;
	}
	
	public Experiment buildOne(int projectId, TermId type, VariableTypeList variableTypes) throws MiddlewareQueryException {
		List<Experiment> experiments = build(projectId, type, 0, 1, variableTypes);
		if (experiments != null && experiments.size() > 0) {
			return experiments.get(0);
		}
		return null;
	}
	
	private Experiment createExperiment(ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		experiment.setFactors(getFactors(experimentModel, variableTypes));
		experiment.setVariates(getVariates(experimentModel, variableTypes));
		return experiment;
	}

	private VariableList getVariates(ExperimentModel experimentModel, VariableTypeList variableTypes) {
		VariableList variates = new VariableList();
		
		addPlotVariates(experimentModel, variates, variableTypes);
		
		return variates.sort();
	}

	private void addPlotVariates(ExperimentModel experimentModel, VariableList variates, VariableTypeList variableTypes) {
		addVariates(experimentModel, variates, variableTypes);
	}

	private void addVariates(ExperimentModel experiment, VariableList variates, VariableTypeList variableTypes) {
		for (Phenotype phenotype : experiment.getPhenotypes()) {
			VariableType variableType = variableTypes.findById(phenotype.getObservableId());
			variates.add(new Variable(variableType, phenotype.getValue()));
		}
	}

	private VariableList getFactors(ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		VariableList factors = new VariableList();
		
		addPlotExperimentFactors(factors, experimentModel, variableTypes);
		
		addLocationFactors(experimentModel, factors, variableTypes);
		
		return factors.sort();
	}

	private void addLocationFactors(ExperimentModel experimentModel, VariableList factors, VariableTypeList variableTypes) {
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			if (isLocationFactor(variableType)) {
				factors.add(createLocationFactor(experimentModel.getGeoLocation(), variableType));
			}
		}
	}

	private boolean isLocationFactor(VariableType variableType) {
		StandardVariable standardVariable = variableType.getStandardVariable();
		if (standardVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
			return true;
		}
	    if (standardVariable.getStoredIn().getId() == TermId.LATITUDE_STORAGE.getId()) {
	    	return true;
	    }
		if (standardVariable.getStoredIn().getId() == TermId.LONGITUDE_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.DATUM_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.ALTITUDE_STORAGE.getId()) {
			return true;
		}
		
		return false;
	}

	private Variable createLocationFactor(Geolocation geoLocation, VariableType variableType) {
		StandardVariable standardVariable = variableType.getStandardVariable();
		if (standardVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			return new Variable(variableType, findLocationValue(variableType.getId(), geoLocation.getProperties()));
		}
		if (standardVariable.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
			return new Variable(variableType, geoLocation.getDescription());
		}
	    if (standardVariable.getStoredIn().getId() == TermId.LATITUDE_STORAGE.getId()) {
	    	return new Variable(variableType, geoLocation.getLatitude());
	    }
		if (standardVariable.getStoredIn().getId() == TermId.LONGITUDE_STORAGE.getId()) {
			return new Variable(variableType, geoLocation.getLongitude());
		}
		if (standardVariable.getStoredIn().getId() == TermId.DATUM_STORAGE.getId()) {
			return new Variable(variableType, geoLocation.getGeodeticDatum());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ALTITUDE_STORAGE.getId()) {
			return new Variable(variableType, geoLocation.getAltitude());
		}
		return null;
	}

	private String findLocationValue(int stdVariableId, List<GeolocationProperty> properties) {
		if (properties != null) {
			for (GeolocationProperty property : properties) {
				if (property.getTypeId().equals(stdVariableId)) {
					return property.getValue();
				}
			}
		}
		return null;
	}

	private void addPlotExperimentFactors(VariableList variables, ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		addExperimentFactors(variables, experimentModel, variableTypes);
		addGermplasmFactors(variables, experimentModel, variableTypes);
	}
	
	private void addGermplasmFactors(VariableList factors, ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		List<ExperimentStock> experimentStocks = experimentModel.getExperimentStocks();
		if (experimentStocks != null && experimentStocks.size() == 1) {
			Stock stock = getStockBuilder().get(experimentStocks.get(0).getStockId());
			for (VariableType variableType : variableTypes.getVariableTypes()) {
				if (isGermplasmFactor(variableType)) {
					factors.add(createGermplasmFactor(stock, variableType));
				}
			}
		}
	}

	private boolean isGermplasmFactor(VariableType variableType) {
		StandardVariable standardVariable = variableType.getStandardVariable();
		if (standardVariable.getStoredIn().getId() == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()) {
			return true;
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_CODE_STORAGE.getId()) {
			return true;
		}
		return false;
	}
	
	private Variable createGermplasmFactor(Stock stock, VariableType variableType) {
		StandardVariable standardVariable = variableType.getStandardVariable();
		if (standardVariable.getStoredIn().getId() == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			return new Variable(variableType, findStockValue(variableType.getId(), stock.getProperties()));
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
			return new Variable(variableType, stock.getUniqueName());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()) {
			return new Variable(variableType, stock.getDbxrefId());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()) {
			return new Variable(variableType, stock.getName());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_CODE_STORAGE.getId()) {
			return new Variable(variableType, stock.getValue());
		}
		return null;
	}

	private String findStockValue(int stdVariableId, Set<StockProperty> properties) {
		if (properties != null) {
			for (StockProperty property : properties) {
				if (stdVariableId == property.getTypeId()) {
					return property.getValue();
				}
			}
		}
		return null;
	}

	private void addExperimentFactors(VariableList variables, ExperimentModel experimentModel, VariableTypeList variableTypes) {
		for (ExperimentProperty property : experimentModel.getProperties()) {
			variables.add(createVariable(property, variableTypes));
		}
	}
	
	private Variable createVariable(ExperimentProperty property, VariableTypeList variableTypes) {
		Variable variable = new Variable();
		variable.setVariableType(variableTypes.findById(property.getTypeId()));
		variable.setValue(property.getValue());
		return variable;
	}
}
