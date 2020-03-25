package org.generationcp.middleware.domain.dms;

import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class DatasetBasicDTO {

	private Integer datasetId;
	private Integer datasetTypeId;
	private String name;
	private Integer parentDatasetId;

	public DatasetBasicDTO(){

	}

	public DatasetBasicDTO(final Integer datasetId) {
		this();
		this.datasetId = datasetId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

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

	public Integer getParentDatasetId() {
		return this.parentDatasetId;
	}

	public void setParentDatasetId(final Integer parentDatasetId) {
		this.parentDatasetId = parentDatasetId;
	}

}
