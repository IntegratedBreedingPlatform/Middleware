package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class SampleGenotypeDTO {

	private Integer observationUnitId;

	private Integer gid;

	private String designation;

	private Map<String, SampleGenotypeData> genotypeDataMap;

	public Integer getObservationUnitId() {
		return this.observationUnitId;
	}

	public void setObservationUnitId(final Integer observationUnitId) {
		this.observationUnitId = observationUnitId;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
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
