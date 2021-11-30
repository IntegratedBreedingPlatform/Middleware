package org.generationcp.middleware.api.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.pojos.Location;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO extends LocationRequestDto {

	private Integer id;
	private String countryName;
	private String provinceName;

	public LocationDTO(){
	}

	public LocationDTO(final Location location) {
		super();
		this.setId(location.getLocid());
		this.setType(location.getLtype());
		this.setName(location.getLname());
		this.setAbbreviation(location.getLabbr());
		this.setProvinceId(location.getSnl1id());
		this.setAltitude(location.getAltitude());
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());

		final Location country = location.getCountry();
		if (country != null) {
			this.setCountryId(country.getLocid());
			this.setCountryName(country.getLname());
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}
