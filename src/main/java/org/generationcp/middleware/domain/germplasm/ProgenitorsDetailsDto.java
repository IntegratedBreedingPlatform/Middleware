package org.generationcp.middleware.domain.germplasm;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgenitorsDetailsDto {

	private Integer breedingMethodId;
	private String breedingMethodName;
	private String breedingMethodCode;
	private String breedingMethodType;
	private GermplasmDto femaleParent;
	private List<GermplasmDto> maleParents;
	private GermplasmDto groupSource;
	private GermplasmDto immediateSource;

	public Integer getBreedingMethodId() {
		return breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public String getBreedingMethodName() {
		return breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public GermplasmDto getFemaleParent() {
		return femaleParent;
	}

	public void setFemaleParent(final GermplasmDto femaleParent) {
		this.femaleParent = femaleParent;
	}

	public List<GermplasmDto> getMaleParents() {
		return maleParents;
	}

	public void setMaleParents(final List<GermplasmDto> maleParents) {
		this.maleParents = maleParents;
	}

	public GermplasmDto getGroupSource() {
		return groupSource;
	}

	public void setGroupSource(final GermplasmDto groupSource) {
		this.groupSource = groupSource;
	}

	public GermplasmDto getImmediateSource() {
		return immediateSource;
	}

	public void setImmediateSource(final GermplasmDto immediateSource) {
		this.immediateSource = immediateSource;
	}

	public String getBreedingMethodCode() {
		return breedingMethodCode;
	}

	public void setBreedingMethodCode(final String breedingMethodCode) {
		this.breedingMethodCode = breedingMethodCode;
	}

	public String getBreedingMethodType() {
		return breedingMethodType;
	}

	public void setBreedingMethodType(final String breedingMethodType) {
		this.breedingMethodType = breedingMethodType;
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
