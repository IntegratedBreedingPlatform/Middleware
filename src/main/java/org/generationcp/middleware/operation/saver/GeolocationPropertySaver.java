
package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

public class GeolocationPropertySaver extends Saver {

	public GeolocationPropertySaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void saveFieldmapProperties(final List<FieldMapInfo> infos) throws MiddlewareQueryException {
		for (final FieldMapInfo info : infos) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
					// GCP-8093 handle old data saved using the default location, default location is no longer used
					int locationId = trial.getGeolocationId();
					if (trial.getGeolocationId() != null && trial.getGeolocationId().intValue() == 1) {
						locationId = this.getExperimentModelSaver().moveStudyToNewGeolocation(info.getFieldbookId());
					}

					if (trial.getLocationId() != null) {
						this.saveOrUpdate(locationId, TermId.LOCATION_ID.getId(), trial.getLocationId().toString());
					}

					if (trial.getBlockId() != null) {
						this.saveOrUpdate(locationId, TermId.BLOCK_ID.getId(), trial.getBlockId().toString());
					}
				}
			}
		}
	}

	public void saveOrUpdate(final int geolocationId, final int typeId, final String value) throws MiddlewareQueryException {
		final Geolocation geolocation = this.getGeolocationDao().getById(geolocationId);
		GeolocationProperty property = null;
		if (geolocation.getProperties() != null && !geolocation.getProperties().isEmpty()) {
			property = this.findProperty(geolocation.getProperties(), typeId);
		}
		if (property == null) {
			property = new GeolocationProperty();
			property.setRank(this.getMaxRank(geolocation.getProperties()));
			property.setGeolocation(geolocation);
			property.setType(typeId);
		}
		property.setValue(value);
		this.getGeolocationPropertyDao().saveOrUpdate(property);
	}

	private int getMaxRank(final List<GeolocationProperty> properties) {
		int maxRank = 1;
		if(properties != null){
			for (final GeolocationProperty property : properties) {
				if (property.getRank() >= maxRank) {
					maxRank = property.getRank() + 1;
				}
			}
		}
		return maxRank;
	}

	private GeolocationProperty findProperty(final List<GeolocationProperty> properties, final int typeId) {
		for (final GeolocationProperty property : properties) {
			if (property.getTypeId() == typeId) {
				return property;
			}
		}
		return null;
	}

	public void saveOrUpdate(final Geolocation geolocation, final int typeId, final String value) throws MiddlewareQueryException {
		GeolocationProperty property = null;
		if (geolocation.getProperties() != null && !geolocation.getProperties().isEmpty()) {
			property = this.findProperty(geolocation.getProperties(), typeId);
		}
		if (property == null) {
			property = new GeolocationProperty();
			property.setRank(this.getMaxRank(geolocation.getProperties()));
			property.setGeolocation(geolocation);
			property.setType(typeId);
			geolocation.getProperties().add(property);
		}
		property.setValue(value);
		this.getGeolocationPropertyDao().saveOrUpdate(property);
	}
}
