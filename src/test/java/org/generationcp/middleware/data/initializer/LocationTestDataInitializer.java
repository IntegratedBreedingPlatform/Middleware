
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Location;

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

	public static Location createLocation(final Integer locId, final String lname, final String uuid) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		location.setUniqueID(uuid);
		return location;
	}

	public static Location createLocation(final Integer locId, final String lname, final Integer locationType,
			final String locationAbbreviation, final String programUUID) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		location.setLtype(locationType);
		location.setLabbr(locationAbbreviation);
		if (programUUID != null) {
			location.setUniqueID(programUUID);
		}
		location.setNllp(0);
		location.setSnl1id(0);
		location.setSnl2id(0);
		location.setSnl3id(0);
		location.setCntryid(0);
		location.setLrplce(0);
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
