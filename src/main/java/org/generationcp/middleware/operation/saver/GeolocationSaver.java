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
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;

import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.util.StringUtil;

public class GeolocationSaver extends Saver {

	public GeolocationSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		Geolocation geolocation = create(variableList, row);
		if (geolocation != null) {
			getGeolocationDao().save(geolocation);
			return geolocation;
		}
		return null;
	}
	
	private Geolocation create(VariableList factors, MeasurementRow row) throws MiddlewareQueryException {
		Geolocation geolocation = null;
		
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			int propertyIndex = getGeolocationPropertyDao().getNegativeId("geolocationPropertyId");
			
			for (Variable variable : factors.getVariables()) {

				Integer storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();
				
				if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setDescription(value);
					
				} else if (TermId.LATITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.LONGITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.DATUM_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setGeodeticDatum(value);
					
				} else if (TermId.ALTITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					addProperty(geolocation, createProperty(propertyIndex--, variable));
				
				} else if (TermId.OBSERVATION_VARIATE.getId() == storedInId || TermId.CATEGORICAL_VARIATE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation);
					if(row!=null) {//value is in observation sheet
						variable.setValue(row.getMeasurementDataValue(variable.getVariableType().getLocalName()));
					}
					addVariate(geolocation, variable);
					
				} else {
					throw new MiddlewareQueryException("Non-Trial Environment Variable was used in calling create location: " + variable.getVariableType().getId());
				}
			}
		}
		
		return geolocation;
	}
	
	private Geolocation getGeolocationObject(Geolocation geolocation) throws MiddlewareQueryException {
		if (geolocation == null) {
			geolocation = new Geolocation();
			geolocation.setLocationId(getGeolocationDao().getNegativeId("locationId"));
		}
		return geolocation;
	}
	
	private GeolocationProperty createProperty(int index, Variable variable) throws MiddlewareQueryException {
		GeolocationProperty property = new GeolocationProperty();
		
		property.setGeolocationPropertyId(index);
		property.setType(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());
		
		return property;
	}
	
	private void addProperty(Geolocation geolocation, GeolocationProperty property) {
		if (geolocation.getProperties() == null) {
			geolocation.setProperties(new ArrayList<GeolocationProperty>());
		}
		property.setGeolocation(geolocation);
		geolocation.getProperties().add(property);
	}
	
	private void addVariate(Geolocation geolocation, Variable variable) {
		if (geolocation.getVariates() == null) {
			geolocation.setVariates(new VariableList());
		}
		geolocation.getVariates().add(variable);
	}
	
}
