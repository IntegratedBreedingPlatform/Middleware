package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.domain.dms.StudyReference;

import java.util.ArrayList;
import java.util.List;

public class SampleGermplasmDetailDTO {

	private String sampleBk;
	private String sampleListName;
	private String obsUnitId;
	private String plantBk;
	private String plateId;
	private String well;
	private StudyReference study;
	private List<Dataset> datasets;

	public SampleGermplasmDetailDTO(){

	}

	public String getSampleBk() {
		return this.sampleBk;
	}

	public void setSampleBk(final String sampleBk) {
		this.sampleBk = sampleBk;
	}

	public String getSampleListName() {
		return this.sampleListName;
	}

	public void setSampleListName(final String sampleListName) {
		this.sampleListName = sampleListName;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public String getPlantBk() {
		return this.plantBk;
	}

	public void setPlantBk(final String plantBk) {
		this.plantBk = plantBk;
	}

	public StudyReference getStudy() {
		return this.study;
	}

	public void setStudy(final StudyReference study) {
		this.study = study;
	}

	public List<Dataset> getDatasets() {
		return this.datasets;
	}

	public void setDatasets(final List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public void addDataset(final Integer datasetId, final String datasetName) {
		if (null == this.datasets) {
			this.datasets = new ArrayList<>();
		}

		final Dataset dataset = new Dataset(datasetId, datasetName);
		if (!this.datasets.contains(dataset)) {
			this.datasets.add(dataset);
		}
	}

	public String getPlateId() {
		return this.plateId;
	}

	public void setPlateId(final String plateId) {
		this.plateId = plateId;
	}

	public String getWell() {
		return this.well;
	}

	public void setWell(final String well) {
		this.well = well;
	}

	public class Dataset{

		private String datasetName;
		private Integer datasetId;

		public Dataset(final Integer datasetId, final String datasetName) {
			this.datasetId = datasetId;
			this.datasetName = datasetName;
		}

		public String getDatasetName() {
			return this.datasetName;
		}

		public void setDatasetName(final String datasetName) {
			this.datasetName = datasetName;
		}

		public Integer getDatasetId() {
			return this.datasetId;
		}

		public void setDatasetId(final Integer datasetId) {
			this.datasetId = datasetId;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}

			if (o == null || this.getClass() != o.getClass()) {
				return false;
			}

			final Dataset dataset = (Dataset) o;

			return new EqualsBuilder()//
				.append(this.datasetId, dataset.datasetId)
				.append(this.datasetName, dataset.datasetName)
				.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)//
				.append(this.datasetId)
				.append(this.datasetName)
				.toHashCode();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final SampleGermplasmDetailDTO sampleGermplasmDetailDTO = (SampleGermplasmDetailDTO) o;

		return new EqualsBuilder()//
			.append(this.sampleBk, sampleGermplasmDetailDTO.sampleBk)
			.append(this.obsUnitId, sampleGermplasmDetailDTO.obsUnitId)
			.append(this.plantBk, sampleGermplasmDetailDTO.plantBk)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)//
			.append(this.sampleBk)
			.append(this.obsUnitId)
			.append(this.plantBk)
			.toHashCode();
	}

}
