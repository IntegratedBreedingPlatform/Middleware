package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

public class LocationDto {
	
	private Integer id;
	
	private String locationName;
	
	private String provinceName;
	
	private String countryName;
	
	public LocationDto(Integer id, String locationName, String provinceName, String countryName) {
		this.id = id;
		this.locationName = locationName;
		this.provinceName = provinceName;
		this.countryName = countryName;
	}
	
	public LocationDto(String locationName, String provinceName, String countryName) {
		this.locationName = locationName;
		this.provinceName = provinceName;
		this.countryName = countryName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
