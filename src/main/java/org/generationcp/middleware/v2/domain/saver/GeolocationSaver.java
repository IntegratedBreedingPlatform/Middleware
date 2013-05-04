package org.generationcp.middleware.v2.domain.saver;

import java.util.ArrayList;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.pojos.Geolocation;
import org.generationcp.middleware.v2.pojos.GeolocationProperty;

public class GeolocationSaver extends Saver {

	public GeolocationSaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Geolocation create(VariableList factors) throws MiddlewareQueryException {
		Geolocation geolocation = null;
		
		if (factors != null && factors.getVariables() != null && factors.getVariables().size() > 0) {
			
			for (Variable variable : factors.getVariables()) {
				Integer variableId = variable.getVariableType().getId();
				String value = variable.getValue();
				
				if (TermId.TRIAL_INSTANCE_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setDescription(value);
					
				} else if (TermId.LATITUDE_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.LONGITUDE_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.DATUM_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setGeodeticDatum(value);
					
				} else if (TermId.ALTITUDE_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
					
				} else if (TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId().equals(variableId)) {
					geolocation = getGeolocationObject(geolocation);
					addProperty(geolocation, createProperty(variable));
					
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
	
	private GeolocationProperty createProperty(Variable variable) throws MiddlewareQueryException {
		GeolocationProperty property = new GeolocationProperty();
		
		property.setGeolocationPropertyId(getGeolocationPropertyDao().getNegativeId("geolocationPropertyId"));
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
}
