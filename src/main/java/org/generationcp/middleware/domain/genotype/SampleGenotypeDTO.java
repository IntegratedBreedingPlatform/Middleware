package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class SampleGenotypeDTO {

	private Integer observationUnitId;

	private Map<String, SampleGenotypeData> genotypeDataMap;

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Map<String, SampleGenotypeData> getGenotypeDataMap() {
		return this.genotypeDataMap;
	}

	public void setGenotypeDataMap(final Map<String, SampleGenotypeData> genotypeDataMap) {
		this.genotypeDataMap = genotypeDataMap;
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
