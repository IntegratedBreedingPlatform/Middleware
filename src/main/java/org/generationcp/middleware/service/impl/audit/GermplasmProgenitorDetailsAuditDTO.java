package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmProgenitorDetailsAuditDTO extends AbstractAuditDTO {

	private String breedingMethodName;
	private Integer femaleParent;
	private Integer maleParent;
	private Integer progenitorsNumber;

	private boolean breedingMethodChanged;
	private boolean femaleParentChanged;
	private boolean maleParentChanged;
	private boolean progenitorsNumberChanged;

	public GermplasmProgenitorDetailsAuditDTO() {
	}

	public String getBreedingMethodName() {
		return breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public Integer getFemaleParent() {
		return femaleParent;
	}

	public void setFemaleParent(final Integer femaleParent) {
		this.femaleParent = femaleParent;
	}

	public Integer getMaleParent() {
		return maleParent;
	}

	public void setMaleParent(final Integer maleParent) {
		this.maleParent = maleParent;
	}

	public Integer getProgenitorsNumber() {
		return progenitorsNumber;
	}

	public void setProgenitorsNumber(final Integer progenitorsNumber) {
		this.progenitorsNumber = progenitorsNumber;
	}

	public boolean isBreedingMethodChanged() {
		return breedingMethodChanged;
	}

	public void setBreedingMethodChanged(final boolean breedingMethodChanged) {
		this.breedingMethodChanged = breedingMethodChanged;
	}

	public boolean isFemaleParentChanged() {
		return femaleParentChanged;
	}

	public void setFemaleParentChanged(final boolean femaleParentChanged) {
		this.femaleParentChanged = femaleParentChanged;
	}

	public boolean isMaleParentChanged() {
		return maleParentChanged;
	}

	public void setMaleParentChanged(final boolean maleParentChanged) {
		this.maleParentChanged = maleParentChanged;
	}

	public boolean isProgenitorsNumberChanged() {
		return progenitorsNumberChanged;
	}

	public void setProgenitorsNumberChanged(final boolean progenitorsNumberChanged) {
		this.progenitorsNumberChanged = progenitorsNumberChanged;
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
