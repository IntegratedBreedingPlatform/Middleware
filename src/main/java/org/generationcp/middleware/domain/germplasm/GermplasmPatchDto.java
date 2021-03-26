package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmPatchDto {

	private String creationDate;

	private String reference;

	private Integer breedingLocationId;

	private Integer breedingMethodId;

	private Integer gpid1;

	private Integer gpid2;

	private List<Integer> otherProgenitors;

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Integer getBreedingLocationId() {
		return breedingLocationId;
	}

	public void setBreedingLocationId(final Integer breedingLocationId) {
		this.breedingLocationId = breedingLocationId;
	}

	public Integer getBreedingMethodId() {
		return breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public Integer getGpid1() {
		return gpid1;
	}

	public void setGpid1(final Integer gpid1) {
		this.gpid1 = gpid1;
	}

	public Integer getGpid2() {
		return gpid2;
	}

	public void setGpid2(final Integer gpid2) {
		this.gpid2 = gpid2;
	}

	public List<Integer> getOtherProgenitors() {
		return otherProgenitors;
	}

	public void setOtherProgenitors(final List<Integer> otherProgenitors) {
		this.otherProgenitors = otherProgenitors;
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
