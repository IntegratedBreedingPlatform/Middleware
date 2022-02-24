package org.generationcp.middleware.service.impl.study.generation;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

import java.util.ArrayList;

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

	public Geolocation createGeolocation(final int instanceNumber,
		final Integer locationId) {
		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription(String.valueOf(instanceNumber));
		geolocation.setProperties(new ArrayList<GeolocationProperty>());

		final GeolocationProperty geolocationProperty =
			new GeolocationProperty(geolocation, String.valueOf(locationId), 1, TermId.LOCATION_ID.getId());
		geolocation.getProperties().add(geolocationProperty);

		this.daoFactory.getGeolocationDao().save(geolocation);
		return geolocation;
	}

}
