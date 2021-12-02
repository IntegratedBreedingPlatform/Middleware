
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationTestDataInitializer {

	private static final int LOCATION_ID = 1;
	private static final String LOCATION_NAME = "Sample Location";

	public static Location createLocation() {
		return LocationTestDataInitializer.createLocation(LocationTestDataInitializer.LOCATION_ID,
				LocationTestDataInitializer.LOCATION_NAME);
	}

	public static Location createLocation(final Integer locId, final String lname) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		return location;
	}
	
	public static Location createLocationWithLabbr(final Integer locId, final String lname, final String labbr) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		location.setLabbr(labbr);
		return location;
	}

	public static Location createLocation(final Integer locId, final String lname, final Integer locationType,
		final String locationAbbreviation) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		location.setLtype(locationType);
		location.setLabbr(locationAbbreviation);
		location.setNllp(0);
		location.setProvince(null);
		location.setSnl2id(0);
		location.setSnl3id(0);
		location.setCountry(null);
		location.setLrplce(0);
		location.setLdefault(Boolean.FALSE);

		return location;
	}

	public static List<Location> createLocationList(final int noOfLocations) {
		final List<Location> locations = new ArrayList<>();

		for (int i = 1; i <= noOfLocations; i++) {
			locations.add(
					LocationTestDataInitializer.createLocation(i, LocationTestDataInitializer.LOCATION_NAME + " " + i));
		}

		return locations;
	}
}
