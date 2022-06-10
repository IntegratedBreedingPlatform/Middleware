package org.generationcp.middleware.api.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.api.program.ProgramFavoriteDTO;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO extends LocationRequestDto {

	private Integer id;
	private String locationTypeName;
	private String countryName;
	private String countryCode;
	private String provinceName;

	private List<ProgramFavoriteDTO> programFavorites;

	public LocationDTO(){
	}

	public LocationDTO(final Location location) {
		super();
		this.setId(location.getLocid());
		this.setType(location.getLtype());
		this.setName(location.getLname());
		this.setAbbreviation(location.getLabbr());
		this.setAltitude(location.getAltitude());
		this.setLatitude(location.getLatitude());
		this.setLongitude(location.getLongitude());

		final Country country = location.getCountry();
		if (country != null) {
			this.setCountryId(country.getCntryid());
			this.setCountryName(country.getIsoabbr());
			this.setCountryCode(country.getIsothree());
		}

		final Location province = location.getProvince();
		if (province != null) {
			this.setProvinceId(province.getLocid());
			this.setProvinceName(province.getLname());
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getLocationTypeName() {
		return this.locationTypeName;
	}

	public void setLocationTypeName(final String locationTypeName) {
		this.locationTypeName = locationTypeName;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	public List<ProgramFavoriteDTO> getProgramFavorites() {
		return this.programFavorites;
	}

	public void setProgramFavorites(final List<ProgramFavoriteDTO> programFavorites) {
		this.programFavorites = programFavorites;
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
