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
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
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
	
	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row, boolean isNursery) throws MiddlewareQueryException {
		return saveGeolocation(variableList, row, isNursery, true);
	}
	
	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row, boolean isNursery, boolean isCreate) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		
		Integer locationId = null;
		if (row != null && !isCreate && row.getLocationId() != 0) {
			locationId = (int) row.getLocationId();
		}
		Geolocation geolocation = createOrUpdate(variableList, row, locationId);
		if (geolocation != null) {
			if(isNursery && geolocation.getDescription()==null) {
				geolocation.setDescription("1");//GCP-7340, GCP-7346 - OCC should have a default value of 1
			}
			if (isCreate) {
				getGeolocationDao().save(geolocation);
			}
			else {
				getGeolocationDao().saveOrUpdate(geolocation);
			}
			if (geolocation.getVariates() != null) {
			    for (Variable var : geolocation.getVariates().getVariables()) {
			        if (var.getPhenotypeId() == null) {
			            getPhenotypeSaver().save(row.getExperimentId(), var);
			        } else {
			            getPhenotypeSaver().saveOrUpdate(row.getExperimentId(), var.getVariableType().getStandardVariable().getId(),
			                    var.getVariableType().getStandardVariable().getStoredIn().getId(), var.getValue(), 
			                    getPhenotypeDao().getById(var.getPhenotypeId()));
			        }
			    }
			}
			return geolocation;
		}
		return null;
	}
	
	private Geolocation createOrUpdate(VariableList factors, MeasurementRow row, Integer locationId) throws MiddlewareQueryException {
		Geolocation geolocation = null;
		
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			int propertyIndex = getGeolocationPropertyDao().getNegativeId("geolocationPropertyId");
			
			for (Variable variable : factors.getVariables()) {

				Integer storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();
				
				if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					geolocation.setDescription(value);
					
				} else if (TermId.LATITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.LONGITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.DATUM_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					geolocation.setGeodeticDatum(value);
					
				} else if (TermId.ALTITUDE_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
					addProperty(geolocation, createOrUpdateProperty(propertyIndex--, variable, geolocation));
				
				} else if (TermId.OBSERVATION_VARIATE.getId() == storedInId || TermId.CATEGORICAL_VARIATE.getId() == storedInId) {
					geolocation = getGeolocationObject(geolocation, locationId);
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
	
	private Geolocation getGeolocationObject(Geolocation geolocation, Integer locationId) throws MiddlewareQueryException {
		if (geolocation == null) {
			if (locationId != null) {
				geolocation = getGeolocationDao().getById(locationId);
			}
			if (geolocation == null) {
				geolocation = new Geolocation();
				geolocation.setLocationId(getGeolocationDao().getNegativeId("locationId"));
			}
		}
		return geolocation;
	}
	
	private GeolocationProperty createOrUpdateProperty(int index, Variable variable, Geolocation geolocation) throws MiddlewareQueryException {
		GeolocationProperty property = getGeolocationProperty(variable.getVariableType().getId(), geolocation);
		
		if (property == null) {
			property = new GeolocationProperty();
			property.setGeolocationPropertyId(index);
			property.setType(variable.getVariableType().getId());
			property.setRank(variable.getVariableType().getRank());
		}
		property.setValue(variable.getValue());
		
		return property;
	}
	
	private GeolocationProperty getGeolocationProperty(Integer typeId, Geolocation geolocation) {
		if (typeId != null && geolocation != null && geolocation.getProperties() != null) {
			for (GeolocationProperty property : geolocation.getProperties()) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
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
	
	public Geolocation createMinimumGeolocation() throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		Geolocation geolocation = getGeolocationObject(null, null);
		geolocation.setDescription("1");
		getGeolocationDao().save(geolocation);
		
		return geolocation;
	}
	
	public Geolocation updateGeolocationInformation(MeasurementRow row, boolean isNursery) throws MiddlewareQueryException, MiddlewareException {
		setWorkingDatabase(Database.LOCAL);
		VariableTypeList variableTypes = getVariableTypeListTransformer().transform(row.getMeasurementVariables(), false);
		VariableList variableList = getVariableListTransformer().transformTrialEnvironment(row, variableTypes);
		
		return saveGeolocation(variableList, row, isNursery, false);
	}
	
	public void setGeolocation(Geolocation geolocation, int termId, int storedInId, String value) {
		if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
			geolocation.setDescription(value);
			
		} else if (TermId.LATITUDE_STORAGE.getId() == storedInId) {
			geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
			
		} else if (TermId.LONGITUDE_STORAGE.getId() == storedInId) {
			geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
			
		} else if (TermId.DATUM_STORAGE.getId() == storedInId) {
			geolocation.setGeodeticDatum(value);
			
		} else if (TermId.ALTITUDE_STORAGE.getId() == storedInId) {
			geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
		}	
	}
	
	public Geolocation saveGeolocationOrRetrieveIfExisting(String studyName, 
			VariableList variableList, MeasurementRow row, boolean isNursery, boolean isDeleteTrialObservations) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		Geolocation geolocation = null;
		
		if (variableList != null && variableList.getVariables() != null && variableList.getVariables().size() > 0) {
			String trialInstanceNumber = null;
			for (Variable variable : variableList.getVariables()) {
				Integer storedInId = variable.getVariableType().getStandardVariable().getStoredIn().getId();
				String value = variable.getValue();				
				if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
					trialInstanceNumber = value;
					break;
				}
			}
			if(isNursery && trialInstanceNumber==null) {
				trialInstanceNumber = "1";
			}
			//check if existing
			Integer locationId = getGeolocationDao().getLocationIdByProjectNameAndDescription(studyName, trialInstanceNumber);
			if (isDeleteTrialObservations) {
			    locationId = null;
			}
			geolocation = createOrUpdate(variableList, row, locationId);
			geolocation.setDescription(trialInstanceNumber);
			getGeolocationDao().saveOrUpdate(geolocation);
			return geolocation;
		}
		return null;
	}
}
