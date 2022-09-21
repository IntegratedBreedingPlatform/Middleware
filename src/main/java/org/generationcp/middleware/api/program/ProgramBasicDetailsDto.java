package org.generationcp.middleware.api.program;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ProgramBasicDetailsDto {

	private String name;
	private String startDate;
	private Integer breedingLocationDefaultId;
	private Integer storageLocationDefaultId;

	public ProgramBasicDetailsDto() {
	}

	public ProgramBasicDetailsDto(final String name, final String startDate, final Integer breedingLocationDefaultId,
		final Integer storageLocationDefaultId) {
		this.name = name;
		this.startDate = startDate;
		this.breedingLocationDefaultId = breedingLocationDefaultId;
		this.storageLocationDefaultId = storageLocationDefaultId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public Integer getBreedingLocationDefaultId() {
		return this.breedingLocationDefaultId;
	}

	public void setBreedingLocationDefaultId(final Integer breedingLocationDefaultId) {
		this.breedingLocationDefaultId = breedingLocationDefaultId;
	}

	public Integer getStorageLocationDefaultId() {
		return this.storageLocationDefaultId;
	}

	public void setStorageLocationDefaultId(final Integer storageLocationDefaultId) {
		this.storageLocationDefaultId = storageLocationDefaultId;
	}

	public boolean allAttributesNull() {
		return this.getName() == null && this.getStartDate() == null && this.getBreedingLocationDefaultId() == null
			&& this.storageLocationDefaultId == null;
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
