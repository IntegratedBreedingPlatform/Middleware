package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AdvanceSampledPlantsRequest extends AbstractAdvanceRequest {

	/**
	 * This is the id of the breeding method if the user has selected advancing using the same breeding method for all lines
	 */
	private Integer breedingMethodId;

	// TODO: what it's the purpose of selecting a date 'cause currently (old advance) we are not doing anything with the date
	private Integer harvestYear;
	private Integer harvestMonth;

	public Integer getBreedingMethodId() {
		return breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public Integer getHarvestYear() {
		return harvestYear;
	}

	public void setHarvestYear(final Integer harvestYear) {
		this.harvestYear = harvestYear;
	}

	public Integer getHarvestMonth() {
		return harvestMonth;
	}

	public void setHarvestMonth(final Integer harvestMonth) {
		this.harvestMonth = harvestMonth;
	}

	@Override
	public <T> T accept(final AdvanceRequestVisitor<T> visitor) {
		return visitor.visit(this);
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
