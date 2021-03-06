package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@AutoProperty
public class ProgenitorsUpdateRequestDto {

	private Integer breedingMethodId;

	private Integer gpid1;

	private Integer gpid2;

	private List<Integer> otherProgenitors;

	public ProgenitorsUpdateRequestDto() {
	}

	public ProgenitorsUpdateRequestDto(final Integer breedingMethodId, final Integer gpid1, final Integer gpid2,
		final List<Integer> otherProgenitors) {
		this.breedingMethodId = breedingMethodId;
		this.gpid1 = gpid1;
		this.gpid2 = gpid2;
		this.otherProgenitors = otherProgenitors;
	}

	public boolean allAttributesNull() {
		return Objects.isNull(breedingMethodId) && Objects.isNull(gpid1) && Objects.isNull(gpid2) && CollectionUtils
			.isEmpty(otherProgenitors);
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
