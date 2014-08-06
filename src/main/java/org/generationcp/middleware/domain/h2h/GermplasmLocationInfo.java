package org.generationcp.middleware.domain.h2h;

public class GermplasmLocationInfo {

	private final Integer environmentId;
	private final Integer gid;
	private final String locationName;
	private final String isoabbr;

	public GermplasmLocationInfo(Integer environmentId, Integer gid, String locationName, String isoabbr) {
		this.environmentId = environmentId;
		this.gid = gid;
		this.locationName = locationName;
		this.isoabbr = isoabbr;
	}

	public Integer getEnvironmentId() {
		return environmentId;
	}

	public Integer getGid() {
		return gid;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getIsoabbr() {
		return isoabbr;
	}

}
