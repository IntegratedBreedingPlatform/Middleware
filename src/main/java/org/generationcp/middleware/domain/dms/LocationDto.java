
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

@Deprecated
public class LocationDto {

	private Integer id;

	private String locationName;

	private String provinceName;

	private String countryName;

	public LocationDto(Integer id, String locationName) {
		this.id = id;
		this.locationName = locationName;
	}

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
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public void print(int indent) {
		Debug.println(indent, "LOCATION:[locationId=" + this.id + ", locationName=" + this.locationName + ", provinceName="
				+ this.provinceName + ", countryName=" + this.countryName + "]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		LocationDto other = (LocationDto) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocationDto [id=");
		builder.append(this.id);
		builder.append(", locationName=");
		builder.append(this.locationName);
		builder.append(", provinceName=");
		builder.append(this.provinceName);
		builder.append(", countryName=");
		builder.append(this.countryName);
		builder.append("]");
		return builder.toString();
	}

}
