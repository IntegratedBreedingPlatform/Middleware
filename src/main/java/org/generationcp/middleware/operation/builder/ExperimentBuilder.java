/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

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
			
			//System.out.println("experimentProjects.size() = " + experimentProjects.size());
			
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

	private VariableList getVariates(ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		VariableList variates = new VariableList();
		
		addPlotVariates(experimentModel, variates, variableTypes);
		
		return variates.sort();
	}

	private void addPlotVariates(ExperimentModel experimentModel, VariableList variates, VariableTypeList variableTypes) throws MiddlewareQueryException {
		addVariates(experimentModel, variates, variableTypes);
	}

	private void addVariates(ExperimentModel experiment, VariableList variates, VariableTypeList variableTypes) throws MiddlewareQueryException {
		this.getExperimentDao().refresh(experiment);
		if (experiment.getPhenotypes() != null) {
			for (Phenotype phenotype : experiment.getPhenotypes()) {
				VariableType variableType = variableTypes.findById(phenotype.getObservableId());
				if (variableType.getStandardVariable().getStoredIn().getId() == TermId.OBSERVATION_VARIATE.getId()) {
					variates.add(new Variable(variableType, phenotype.getValue()));
				}
				else {
					variates.add(new Variable(variableType, phenotype.getcValueId()));
				}
			}
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
			StockModel stockModel = getStockBuilder().get(experimentStocks.get(0).getStock().getStockId());
			for (VariableType variableType : variableTypes.getVariableTypes()) {
				if (isGermplasmFactor(variableType)) {
					factors.add(createGermplasmFactor(stockModel, variableType));
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
	
	private Variable createGermplasmFactor(StockModel stockModel, VariableType variableType) {
		StandardVariable standardVariable = variableType.getStandardVariable();
		if (standardVariable.getStoredIn().getId() == TermId.GERMPLASM_ENTRY_STORAGE.getId()) {
			return new Variable(variableType, findStockValue(variableType.getId(), stockModel.getProperties()));
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
			return new Variable(variableType, stockModel.getUniqueName());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()) {
			return new Variable(variableType, stockModel.getDbxrefId());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()) {
			return new Variable(variableType, stockModel.getName());
		}
		if (standardVariable.getStoredIn().getId() == TermId.ENTRY_CODE_STORAGE.getId()) {
			return new Variable(variableType, stockModel.getValue());
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

	private void addExperimentFactors(VariableList variables, ExperimentModel experimentModel, VariableTypeList variableTypes) throws MiddlewareQueryException {
		if (experimentModel.getProperties() != null) {
			for (ExperimentProperty property : experimentModel.getProperties()) {
				variables.add(createVariable(property, variableTypes));
			}
		}
	}
	
	private Variable createVariable(ExperimentProperty property, VariableTypeList variableTypes) throws MiddlewareQueryException {
		Variable variable = new Variable();
		variable.setVariableType(variableTypes.findById(property.getTypeId()));
		variable.setValue(property.getValue());
		return variable;
	}
}
