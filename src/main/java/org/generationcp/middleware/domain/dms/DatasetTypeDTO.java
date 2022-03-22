package org.generationcp.middleware.domain.dms;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class DatasetTypeDTO {

	public DatasetTypeDTO() {

	}

	public DatasetTypeDTO(final Integer datasetTypeId, final String name) {
		this.datasetTypeId = datasetTypeId;
		this.name = name;
	}

	private Integer datasetTypeId;

	private String name;

	private String description;

	private boolean isSubObservationType;

	private boolean isObservationType;

	public Integer getDatasetTypeId() {
		return this.datasetTypeId;
	}

	public void setDatasetTypeId(final Integer datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isSubObservationType() {
		return this.isSubObservationType;
	}

	public void setSubObservationType(final boolean subObservationType) {
		this.isSubObservationType = subObservationType;
	}

	public boolean isObservationType() {
		return this.isObservationType;
	}

	public void setObservationType(final boolean observationType) {
		this.isObservationType = observationType;
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
