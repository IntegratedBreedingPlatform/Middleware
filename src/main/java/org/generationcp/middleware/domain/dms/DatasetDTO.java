package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

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
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof DatasetDTO)) {
			return false;
		}
		final DatasetDTO castOther = (DatasetDTO) other;
		return new EqualsBuilder().append(this.datasetId, castOther.datasetId)
			.append(this.parentDatasetId,castOther.parentDatasetId)
			.append(this.datasetTypeId,castOther.datasetTypeId)
			.append(this.name,castOther.name)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.datasetId).append(this.parentDatasetId).append(this.datasetTypeId).append(this.name).toHashCode();
	}
}
