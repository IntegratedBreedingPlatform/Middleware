package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class FilteredPhenotypesInstancesCountDTO {

	private Integer totalFilteredPhenotypes;

	private Integer totalFilteredInstances;

	public FilteredPhenotypesInstancesCountDTO(final Integer totalFilteredPhenotypes, final Integer totalFilteredInstances) {
		this.totalFilteredPhenotypes = totalFilteredPhenotypes;
		this.totalFilteredInstances = totalFilteredInstances;
	}

	public Integer getTotalFilteredPhenotypes() {
		return totalFilteredPhenotypes;
	}

	public void setTotalFilteredPhenotypes(final Integer totalFilteredPhenotypes) {
		this.totalFilteredPhenotypes = totalFilteredPhenotypes;
	}

	public Integer getTotalFilteredInstances() {
		return totalFilteredInstances;
	}

	public void setTotalFilteredInstances(final Integer totalFilteredInstances) {
		this.totalFilteredInstances = totalFilteredInstances;
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
