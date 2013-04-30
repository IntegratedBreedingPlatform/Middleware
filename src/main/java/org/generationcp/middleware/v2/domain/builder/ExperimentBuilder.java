package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableType;
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

	public List<Experiment> create(Database database, List<ExperimentModel> experimentModels, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		this.setWorkingDatabase(database);
		
		List<Experiment> experiments = new ArrayList<Experiment>();
		for (ExperimentModel experimentModel : experimentModels) {
			if (experimentModel.getTypeId().equals(CVTermId.PLOT_EXPERIMENT.getId())) {
	            experiments.add(create(experimentModel, experimentModels, variableTypes));
			}
		}
		return experiments;
	}

	public Experiment create(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		experiment.setFactors(getFactors(experimentModel, experimentModels, variableTypes));
		experiment.setTraits(getTraits(experimentModel, experimentModels, variableTypes));
		return experiment;
	}

	private Set<Variable> getTraits(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, Set<VariableType> variableTypes) {
		Set<Variable> traits = new HashSet<Variable>();
		
		addPlotTraits(experimentModel, traits, variableTypes);
		addDataSetTraits(experimentModels, traits, variableTypes);
		addStudyTraits(experimentModels, traits, variableTypes);
		
		return traits;
	}

	private void addPlotTraits(ExperimentModel experimentModel, Set<Variable> traits, Set<VariableType> variableTypes) {
		addTraits(experimentModel, traits, variableTypes);
	}

	private void addDataSetTraits(List<ExperimentModel> experimentModels, Set<Variable> traits, Set<VariableType> variableTypes) {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.DATASET_EXPERIMENT.getId())) {
			    addTraits(experiment, traits, variableTypes);
			}
		}
	}

	private void addTraits(ExperimentModel experiment, Set<Variable> traits, Set<VariableType> variableTypes) {
		for (Phenotype phenotype : experiment.getPhenotypes()) {
			VariableType variableType = findVariableType(phenotype.getObservableId(), variableTypes);
			traits.add(new Variable(variableType, phenotype.getValue()));
		}
	}

	private void addStudyTraits(List<ExperimentModel> experimentModels, Set<Variable> traits, Set<VariableType> variableTypes) {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.STUDY_EXPERIMENT.getId())) {
				addTraits(experiment, traits, variableTypes);
			}
		}
	}

	private Set<Variable> getFactors(ExperimentModel experimentModel, List<ExperimentModel> experimentModels, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		Set<Variable> factors = new HashSet<Variable>();
		
		addPlotExperimentFactors(factors, experimentModel, variableTypes);
		addDataSetExperimentFactors(experimentModels, factors, variableTypes);
		addStudyExperimentFactors(experimentModels, factors, variableTypes);
		
		addLocationFactors(experimentModel, factors, variableTypes);
		
		return factors;
	}

	private void addLocationFactors(ExperimentModel experimentModel, Set<Variable> factors, Set<VariableType> variableTypes) {
		for (VariableType variableType : variableTypes) {
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

	private void addPlotExperimentFactors(Set<Variable> variables, ExperimentModel experimentModel, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		addExperimentFactors(variables, experimentModel, variableTypes);
		addGermplasmFactors(variables, experimentModel, variableTypes);
	}

	private void addDataSetExperimentFactors(List<ExperimentModel> experimentModels, Set<Variable> variables, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.DATASET_EXPERIMENT.getId())) {
			    addExperimentFactors(variables, experiment, variableTypes);
			    addGermplasmFactors(variables, experiment, variableTypes);
			}
		}
	}

	private void addStudyExperimentFactors(List<ExperimentModel> experimentModels, Set<Variable> variables, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		for (ExperimentModel experiment : experimentModels) {
			if (experiment.getTypeId().equals(CVTermId.STUDY_EXPERIMENT.getId())) {
				addExperimentFactors(variables, experiment, variableTypes);
				addGermplasmFactors(variables, experiment, variableTypes);
			}
		}
	}
	
	private void addGermplasmFactors(Set<Variable> factors, ExperimentModel experimentModel, Set<VariableType> variableTypes) throws MiddlewareQueryException {
		List<ExperimentStock> experimentStocks = experimentModel.getExperimentStocks();
		if (experimentStocks != null && experimentStocks.size() == 1) {
			Stock stock = getStockBuilder().get(experimentStocks.get(0).getStockId());
			for (VariableType variableType : variableTypes) {
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

	private void addExperimentFactors(Set<Variable> variables, ExperimentModel experimentModel, Set<VariableType> variableTypes) {
		for (ExperimentProperty property : experimentModel.getProperties()) {
			variables.add(createVariable(property, variableTypes));
		}
	}
	
	private Variable createVariable(ExperimentProperty property, Set<VariableType> variableTypes) {
		Variable variable = new Variable();
		variable.setVariableType(findVariableType(property.getTypeId(), variableTypes));
		variable.setValue(property.getValue());
		return variable;
	}

	private VariableType findVariableType(Integer stdVariableId, Set<VariableType> variableTypes) {
		for (VariableType variableType : variableTypes) {
			if (variableType.getId() == stdVariableId) {
				return variableType;
			}
		}
		return null;
	}
}
