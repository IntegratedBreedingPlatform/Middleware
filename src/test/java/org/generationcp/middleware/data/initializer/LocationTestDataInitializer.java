
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Location;

public class LocationTestDataInitializer {

	private static final int LOCATION_ID = 1;
	private static final String LOCATION_NAME = "Sample Location";

	public LocationTestDataInitializer() {
		// do nothing
	}

	public Location createLocation() {
		return this.createLocation(LOCATION_ID, LOCATION_NAME);
	}

	public Location createLocation(final Integer locId, final String lname) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		return location;
	}

	public Location createLocation(final Integer locId, final String lname, final Integer locationType,
			final String locationAbbreviation, final String programUUID) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(lname);
		location.setLtype(locationType);
		location.setLabbr(locationAbbreviation);
		if (programUUID != null){
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

	public List<Location> createLocationList(final int noOfLocations) {
		final List<Location> locations = new ArrayList<Location>();

		for (int i = 1; i <= noOfLocations; i++) {
			locations.add(this.createLocation(i, LOCATION_NAME + " " + i));
		}

		return locations;
	}
}
