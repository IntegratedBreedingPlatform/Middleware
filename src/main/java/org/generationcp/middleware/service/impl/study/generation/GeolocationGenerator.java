package org.generationcp.middleware.service.impl.study.generation;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

import java.util.ArrayList;
import java.util.List;

public class GeolocationGenerator {

	private final DaoFactory daoFactory;

	public GeolocationGenerator(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public Geolocation createGeoLocation() {
		final Geolocation location = new Geolocation();
		location.setDescription("1");
		this.daoFactory.getGeolocationDao().save(location);
		return location;
	}

	public Geolocation createGeolocation(final List<MeasurementVariable> measurementVariables, final int instanceNumber,
		final Integer locationId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setProperties(new ArrayList<GeolocationProperty>());

		int rank = 1;
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			final int variableId = measurementVariable.getTermId();
			if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId) {
				geolocation.setDescription(String.valueOf(instanceNumber));
			} else if (VariableType.ENVIRONMENT_DETAIL == measurementVariable.getVariableType()) {
				String value = "";
				if (measurementVariable.getTermId() == TermId.LOCATION_ID.getId()) {
					value = String.valueOf(locationId);
				}
				final GeolocationProperty geolocationProperty =
					new GeolocationProperty(geolocation, value, rank, measurementVariable.getTermId());
				geolocation.getProperties().add(geolocationProperty);
				rank++;
			}

		}
		this.daoFactory.getGeolocationDao().save(geolocation);
		return geolocation;
	}

}
