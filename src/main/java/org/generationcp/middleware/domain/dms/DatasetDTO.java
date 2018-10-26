package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DatasetDTO {

	private Integer projectId;
	private Integer parent;
	private Integer datasetTypeId;
	private String name;

	public DatasetDTO(final Integer projectId, final Integer parent, final Integer datasetTypeId, final String name) {
		this.projectId = projectId;
		this.parent = parent;
		this.datasetTypeId = datasetTypeId;
		this.name = name;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(final Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(final Integer parent) {
		this.parent = parent;
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
		return new EqualsBuilder().append(this.projectId, castOther.projectId)
			.append(this.parent,castOther.parent)
			.append(this.datasetTypeId,castOther.datasetTypeId)
			.append(this.name,castOther.name)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.projectId).append(this.parent).append(this.datasetTypeId).append(this.name).toHashCode();
	}
}
