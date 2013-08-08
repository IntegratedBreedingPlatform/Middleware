package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

public class LocationDto {
	
	private String locationName;
	
	private String provinceName;
	
	private String countryName;
	
	public LocationDto(String locationName, String provinceName, String countryName) {
		this.locationName = locationName;
		this.provinceName = provinceName;
		this.countryName = countryName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public void print(int indent) {
		Debug.println(indent, "LOCATION:[locationName=" + locationName + ", provinceName=" + provinceName + ", countryName=" + countryName + "]");
	}

}
