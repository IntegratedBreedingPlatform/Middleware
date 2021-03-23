package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmNameRequestDto {

	@JsonIgnore
	private Integer id;

	@JsonIgnore
	private Integer gid;

	private String name;

	private String date;

	private Integer locationId;

	private String nameTypeCode;

	private Boolean preferredName;

	public GermplasmNameRequestDto() {
	}

	public GermplasmNameRequestDto(final Integer id, final Integer gid){
		this.setId(id);
		this.setGid(gid);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

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

	public Boolean isPreferredName() {
		return this.preferredName;
	}

	public void setPreferredName(final Boolean preferredName) {
		this.preferredName = preferredName;
	}

	public String getNameTypeCode() {
		return nameTypeCode;
	}

	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
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
