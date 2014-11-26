package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

import java.util.List;

public class GeolocationPropertySaver extends Saver {

	public GeolocationPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}
	
	public void saveFieldmapProperties(List<FieldMapInfo> infos) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
        for (FieldMapInfo info : infos) {
            for (FieldMapDatasetInfo dataset : info.getDatasets()) {
                for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
                	//GCP-8093 handle old data saved using the default location, default location is no longer used
                	int locationId = trial.getGeolocationId();
                	if (!info.isTrial() && trial.getGeolocationId() != null && trial.getGeolocationId().intValue() == 1) {
                		locationId = getExperimentModelSaver().moveStudyToNewGeolocation(info.getFieldbookId());
                	}
                	
                	if (trial.getLocationId() != null) {
                		saveOrUpdate(locationId, TermId.LOCATION_ID.getId(), trial.getLocationId().toString());
                	}
                	
                	if (trial.getBlockId() != null) {
                		saveOrUpdate(locationId, TermId.BLOCK_ID.getId(), trial.getBlockId().toString());
                	}
                }
            }
        }
    }

	private void saveOrUpdate(int geolocationId, int typeId, String value) throws MiddlewareQueryException {
		Geolocation geolocation = getGeolocationDao().getById(geolocationId);
		GeolocationProperty property = null;
		if (geolocation.getProperties() != null && !geolocation.getProperties().isEmpty()) {
			property = findProperty(geolocation.getProperties(), typeId);
		}
		if (property == null) {
			property = new GeolocationProperty();
			property.setGeolocationPropertyId(getGeolocationPropertyDao().getNegativeId("geolocationPropertyId"));
			property.setRank(getMaxRank(geolocation.getProperties()));
			property.setGeolocation(geolocation);
			property.setType(typeId);
		}
		property.setValue(value);
		getGeolocationPropertyDao().saveOrUpdate(property);
	}
	
	private int getMaxRank(List<GeolocationProperty> properties) {
		int maxRank = 1;
		for (GeolocationProperty property : properties) {
			if (property.getRank() >= maxRank) {
				maxRank = property.getRank() + 1;
			}
		}
		return maxRank;
	}
	
	private GeolocationProperty findProperty(List<GeolocationProperty> properties, int typeId) {
		for (GeolocationProperty property : properties) {
			if (property.getTypeId() == typeId) {
				return property;
			}
		}
		return null;
	}

	public void saveOrUpdate(Geolocation geolocation, int typeId, String value) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		GeolocationProperty property = null;
		if (geolocation.getProperties() != null && !geolocation.getProperties().isEmpty()) {
			property = findProperty(geolocation.getProperties(), typeId);
		}
		if (property == null) {
			property = new GeolocationProperty();
			property.setGeolocationPropertyId(getGeolocationPropertyDao().getNegativeId("geolocationPropertyId"));
			property.setRank(getMaxRank(geolocation.getProperties()));
			property.setGeolocation(geolocation);
			property.setType(typeId);
			geolocation.getProperties().add(property);
		}
		property.setValue(value);
		getGeolocationPropertyDao().saveOrUpdate(property);
	}
}
