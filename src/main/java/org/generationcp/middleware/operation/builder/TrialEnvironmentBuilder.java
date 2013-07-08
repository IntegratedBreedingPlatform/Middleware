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

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.FactorType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

public class TrialEnvironmentBuilder extends Builder {

	public TrialEnvironmentBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
		    DataSet dataSet = getDataSetBuilder().build(datasetId);
		    Study study = getStudyBuilder().createStudy(dataSet.getStudyId());
		
		    VariableTypeList trialEnvironmentVariableTypes = getTrialEnvironmentVariableTypes(study, dataSet);
		    Set<Geolocation> locations = getGeoLocations(datasetId);
		
		    return buildTrialEnvironments(locations, trialEnvironmentVariableTypes);
		}
		return new TrialEnvironments();
	}

	private VariableTypeList getTrialEnvironmentVariableTypes(Study study, DataSet dataSet) {
		VariableTypeList trialEnvironmentVariableTypes = new VariableTypeList();
		trialEnvironmentVariableTypes.addAll(study.getVariableTypesByFactorType(FactorType.TRIAL_ENVIRONMENT));
		trialEnvironmentVariableTypes.addAll(dataSet.getFactorsByFactorType(FactorType.TRIAL_ENVIRONMENT));
		return trialEnvironmentVariableTypes;
	}

	private Set<Geolocation> getGeoLocations(int datasetId) throws MiddlewareQueryException {
		return getGeolocationDao().findInDataSet(datasetId);
	}

	private TrialEnvironments buildTrialEnvironments(Set<Geolocation> locations,
			                                         VariableTypeList trialEnvironmentVariableTypes) {
		
		TrialEnvironments trialEnvironments = new TrialEnvironments();
		for (Geolocation location : locations) {
			VariableList variables = new VariableList();
			for (VariableType variableType : trialEnvironmentVariableTypes.getVariableTypes()) {
				Variable variable = new Variable(variableType, getValue(location, variableType));
				variables.add(variable);
			}
			trialEnvironments.add(new TrialEnvironment(location.getLocationId(), variables));
		}
		return trialEnvironments;
	}

	private String getValue(Geolocation location, VariableType variableType) {
		String value = null;
		int storedInId = variableType.getStandardVariable().getStoredIn().getId();
		if (storedInId == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
			value = location.getDescription();
		}
		else if (storedInId == TermId.LATITUDE_STORAGE.getId()) {
			value = location.getLatitude() == null ? null : Double.toString(location.getLatitude());
		}
		else if (storedInId == TermId.LONGITUDE_STORAGE.getId()) {
			value = location.getLongitude() == null ? null : Double.toString(location.getLongitude());
		}
	    else if (storedInId == TermId.DATUM_STORAGE.getId()) {
	    	value = location.getGeodeticDatum();
	    }
		else if (storedInId == TermId.ALTITUDE_STORAGE.getId()) {
			value = location.getAltitude() == null ? null : Double.toString(location.getAltitude());
		}
		else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			value = getPropertyValue(variableType.getId(), location.getProperties());
		}
		return value;
	}

	private String getPropertyValue(int id, List<GeolocationProperty> properties) {
		String value = null;
		if (properties != null) {
		    for (GeolocationProperty property : properties) {
		    	if (property.getTypeId() == id) {
		    		value = property.getValue();
		    		break;
		    	}
		    }
		}
		return value;
	}
}
