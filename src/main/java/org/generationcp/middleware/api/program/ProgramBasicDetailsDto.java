package org.generationcp.middleware.api.program;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ProgramBasicDetailsDto {

	private String name;
	private String startDate;
	private Integer defaultLocationId;

	public ProgramBasicDetailsDto() {
	}

	public ProgramBasicDetailsDto(final String name, final String startDate, final Integer defaultLocationId) {
		this.name = name;
		this.startDate = startDate;
		this.defaultLocationId = defaultLocationId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public Integer getDefaultLocationId() {
		return this.defaultLocationId;
	}

	public void setDefaultLocationId(final Integer defaultLocationId) {
		this.defaultLocationId = defaultLocationId;
	}

	public boolean allAttributesNull() {
		return this.getName() == null && this.getStartDate() == null && this.getDefaultLocationId() == null;
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
