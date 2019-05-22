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

	private Integer cvTermId;

	private boolean isSubObservationType;

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

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(final Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public boolean isSubObservationType() {
		return this.isSubObservationType;
	}

	public void setSubObservationType(final boolean subObservationType) {
		this.isSubObservationType = subObservationType;
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
