package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
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

	public List<Experiment> create(Database database, List<ExperimentModel> experimentModels, VariableTypeList variableTypes) throws MiddlewareQueryException {
		this.setWorkingDatabase(database);
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		for (ExperimentModel experimentModel : experimentModels) {
			if (experimentModel.getTypeId().equals(CVTermId.PLOT_EXPERIMENT.getId())) {
	            experiments.add(create(experimentModel, experimentModels, variableTypes));
			}
		}
		return experiments;
	}

	public Experiment create(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, VariableTypeList variableTypes) throws MiddlewareQueryException {
		Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		experiment.setFactors(getFactors(experimentModel, experimentModels, variableTypes));
		experiment.setVariates(getVariates(experimentModel, experimentModels, variableTypes));
		return experiment;
	}

	private VariableList getVariates(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, VariableTypeList variableTypes) {
		VariableList variates = new VariableList();
		
		addPlotVariates(experimentModel, variates, variableTypes);
		addDataSetVariates(experimentModels, variates, variableTypes);
		addStudyVariates(experimentModels, variates, variableTypes);
		
		return variates;
	}

	private void addPlotVariates(ExperimentModel experimentModel, VariableList variates, VariableTypeList variableTypes) {
		addVariates(experimentModel, variates, variableTypes);
	}

	private void addDataSetVariates(List<ExperimentModel> experimentModels, VariableList variates, VariableTypeList variableTypes) {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.DATASET_EXPERIMENT.getId())) {
			    addVariates(experiment, variates, variableTypes);
			}
		}
	}

	private void addVariates(ExperimentModel experiment, VariableList variates, VariableTypeList variableTypes) {
		for (Phenotype phenotype : experiment.getPhenotypes()) {
			VariableType variableType = variableTypes.findById(phenotype.getObservableId());
			variates.add(new Variable(variableType, phenotype.getValue()));
		}
	}

	private void addStudyVariates(List<ExperimentModel> experimentModels, VariableList variates, VariableTypeList variableTypes) {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.STUDY_EXPERIMENT.getId())) {
				addVariates(experiment, variates, variableTypes);
			}
		}
	}

	private VariableList getFactors(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, VariableTypeList variableTypes) throws MiddlewareQueryException {
		VariableList factors = new VariableList();
		
		addPlotExperimentFactors(factors, experimentModel, variableTypes);
		addDataSetExperimentFactors(experimentModels, factors, variableTypes);
		addStudyExperimentFactors(experimentModels, factors, variableTypes);
		
		addLocationFactors(experimentModel, factors, variableTypes);
		
		return factors;
	}

	private void addLocationFactors(ExperimentModel experimentModel, VariableList factors, VariableTypeList variableTypes) {
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			if (isLocationFactor(variableType)) {
				factors.add(createLocationFactor(experimentModel.getGeoLocation(), variableType));
			}
		}
	}

	private boolean isLocationFactor(VariableType variableType) {
		if (variableType.getStoredInId().equals(CVTermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.TRIAL_INSTANCE_STORAGE.getId())) {
			return true;
		}
	    if (variableType.getStoredInId().equals(CVTermId.LATITUDE_STORAGE.getId())) {
	    	return true;
	    }
		if (variableType.getStoredInId().equals(CVTermId.LONGITUDE_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.DATUM_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.ALTITUDE_STORAGE.getId())) {
			return true;
		}
		
		return false;
	}

	private Variable createLocationFactor(Geolocation geoLocation, VariableType variableType) {
		if (variableType.getStoredInId().equals(CVTermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId())) {
			return new Variable(variableType, findLocationValue(variableType.getId(), geoLocation.getProperties()));
		}
		if (variableType.getStoredInId().equals(CVTermId.TRIAL_INSTANCE_STORAGE.getId())) {
			return new Variable(variableType, geoLocation.getDescription());
		}
	    if (variableType.getStoredInId().equals(CVTermId.LATITUDE_STORAGE.getId())) {
	    	return new Variable(variableType, geoLocation.getLatitude());
	    }
		if (variableType.getStoredInId().equals(CVTermId.LONGITUDE_STORAGE.getId())) {
			return new Variable(variableType, geoLocation.getLongitude());
		}
		if (variableType.getStoredInId().equals(CVTermId.DATUM_STORAGE.getId())) {
			return new Variable(variableType, geoLocation.getGeodeticDatum());
		}
		if (variableType.getStoredInId().equals(CVTermId.ALTITUDE_STORAGE.getId())) {
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

	private void addDataSetExperimentFactors(List<ExperimentModel> experimentModels, VariableList variables, VariableTypeList variableTypes) throws MiddlewareQueryException {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.DATASET_EXPERIMENT.getId())) {
			    addExperimentFactors(variables, experiment, variableTypes);
			    addGermplasmFactors(variables, experiment, variableTypes);
			}
		}
	}

	private void addStudyExperimentFactors(List<ExperimentModel> experimentModels, VariableList variables, VariableTypeList variableTypes) throws MiddlewareQueryException {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.STUDY_EXPERIMENT.getId())) {
				addExperimentFactors(variables, experiment, variableTypes);
				addGermplasmFactors(variables, experiment, variableTypes);
			}
		}
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
		if (variableType.getStoredInId().equals(CVTermId.GERMPLASM_ENTRY_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_NUMBER_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_GID_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_DESIGNATION_STORAGE.getId())) {
			return true;
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_CODE_STORAGE.getId())) {
			return true;
		}
		return false;
	}
	
	private Variable createGermplasmFactor(Stock stock, VariableType variableType) {
		if (variableType.getStoredInId().equals(CVTermId.GERMPLASM_ENTRY_STORAGE.getId())) {
			return new Variable(variableType, findStockValue(variableType.getId(), stock.getProperties()));
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_NUMBER_STORAGE.getId())) {
			return new Variable(variableType, stock.getUniqueName());
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_GID_STORAGE.getId())) {
			return new Variable(variableType, stock.getDbxrefId());
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_DESIGNATION_STORAGE.getId())) {
			return new Variable(variableType, stock.getName());
		}
		if (variableType.getStoredInId().equals(CVTermId.ENTRY_CODE_STORAGE.getId())) {
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
