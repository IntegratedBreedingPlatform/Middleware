package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmNameDto {

	@JsonIgnore
	private Integer gid;

	private Integer id;

	private String name;

	private String date;

	private Integer locationId;

	private String locationName;

	private String nameTypeCode;

	private String nameTypeDescription;

	private Boolean preferred;

	private Integer nameTypeId;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getNameTypeCode() {
		return this.nameTypeCode;
	}

	public void setNameTypeCode(final String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
	}

	public String getNameTypeDescription() {
		return this.nameTypeDescription;
	}

	public void setNameTypeDescription(final String nameTypeDescription) {
		this.nameTypeDescription = nameTypeDescription;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Boolean isPreferred() {
		return this.preferred;
	}

	public void setPreferred(final Boolean preferred) {
		this.preferred = preferred;
	}

	public Integer getNameTypeId() {
		return this.nameTypeId;
	}

	public void setNameTypeId(final Integer nameTypeId) {
		this.nameTypeId = nameTypeId;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
