package org.generationcp.middleware.domain.dms;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@AutoProperty
public class DatasetDTO implements Serializable {

	private static final long serialVersionUID = 736579292676142736L;

	private Integer datasetId;
	private Integer datasetTypeId;
	private String name;
	private Integer parentDatasetId;


	public DatasetDTO(){

	}

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getDatasetTypeId() {
		return datasetTypeId;
	}

	public void setDatasetTypeId(final Integer datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getParentDatasetId() {
		return parentDatasetId;
	}

	public void setParentDatasetId(final Integer parentDatasetId) {
		this.parentDatasetId = parentDatasetId;
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
