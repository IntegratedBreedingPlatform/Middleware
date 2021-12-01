package org.generationcp.middleware.api.location;

import org.generationcp.middleware.pojos.Location;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LocationDTO extends LocationRequestDto {

	private Integer id;

	public LocationDTO(){
	}

	public LocationDTO(final Location location) {
		super();
		this.setId(location.getLocid());
		this.setType(location.getLtype());
		this.setName(location.getLname());
		this.setAbbreviation(location.getLabbr());
		this.setCountryId(location.getCntryid());
		this.setProvinceId(location.getSnl1id());
		this.setAltitude(location.getAltitude());
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
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
