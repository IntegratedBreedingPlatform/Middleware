
package org.generationcp.middleware.domain.h2h;

public class GermplasmLocationInfo {

	private final Integer environmentId;
	private final Integer gid;
	private final String germplasmName;
	private final String locationName;
	private final String countryName;

	public GermplasmLocationInfo(Integer environmentId, Integer gid, String germplasmName, String locationName, String countryName) {
		this.environmentId = environmentId;
		this.gid = gid;
		this.germplasmName = germplasmName;
		this.locationName = locationName;
		this.countryName = countryName;
	}

	public Integer getEnvironmentId() {
		return this.environmentId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}
}
