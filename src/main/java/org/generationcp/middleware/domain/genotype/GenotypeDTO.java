package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class GenotypeDTO {

	private Integer gid;

	private String designation;

	private Integer plotNumber;

	private Integer sampleNo;

	private String sampleName;

	private Map<String, GenotypeData> genotypeDataMap;

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

	public Integer getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final Integer plotNumber) {
		this.plotNumber = plotNumber;
	}

	public Integer getSampleNo() {
		return this.sampleNo;
	}

	public void setSampleNo(final Integer sampleNo) {
		this.sampleNo = sampleNo;
	}

	public String getSampleName() {
		return this.sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public Map<String, GenotypeData> getGenotypeDataMap() {
		return this.genotypeDataMap;
	}

	public void setGenotypeDataMap(final Map<String, GenotypeData> genotypeDataMap) {
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
