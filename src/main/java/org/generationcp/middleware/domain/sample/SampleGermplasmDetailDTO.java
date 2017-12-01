package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.domain.dms.StudyReference;

import java.util.ArrayList;
import java.util.List;

public class SampleGermplasmDetailDTO {

	private String sampleBk;
	private String sampleListName;
	private String plotId;
	private String plantBk;
	private StudyReference study;
	private List<Dataset> datasets;

	public SampleGermplasmDetailDTO(){

	}

	public String getSampleBk() {
		return sampleBk;
	}

	public void setSampleBk(final String sampleBk) {
		this.sampleBk = sampleBk;
	}

	public String getSampleListName() {
		return sampleListName;
	}

	public void setSampleListName(final String sampleListName) {
		this.sampleListName = sampleListName;
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(final String plotId) {
		this.plotId = plotId;
	}

	public String getPlantBk() {
		return plantBk;
	}

	public void setPlantBk(final String plantBk) {
		this.plantBk = plantBk;
	}

	public StudyReference getStudy() {
		return study;
	}

	public void setStudy(final StudyReference study) {
		this.study = study;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(final List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public void addDataset(final Integer datasetId, final String datasetName) {
		if (null == this.datasets) {
			this.datasets = new ArrayList<>();
		}

		final Dataset dataset = new Dataset(datasetId, datasetName);
		if (!datasets.contains(dataset)) {
			this.datasets.add(dataset);
		}
	}

	public class Dataset{

		private String datasetName;
		private Integer datasetId;

		public Dataset(final Integer datasetId, final String datasetName) {
			this.datasetId = datasetId;
			this.datasetName = datasetName;
		}

		public String getDatasetName() {
			return datasetName;
		}

		public void setDatasetName(final String datasetName) {
			this.datasetName = datasetName;
		}

		public Integer getDatasetId() {
			return datasetId;
		}

		public void setDatasetId(final Integer datasetId) {
			this.datasetId = datasetId;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}

			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			final Dataset dataset = (Dataset) o;

			return new EqualsBuilder()//
				.append(datasetId, dataset.datasetId)
				.append(datasetName, dataset.datasetName)
				.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)//
				.append(datasetId)
				.append(datasetName)
				.toHashCode();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final SampleGermplasmDetailDTO sampleGermplasmDetailDTO = (SampleGermplasmDetailDTO) o;

		return new EqualsBuilder()//
			.append(sampleBk, sampleGermplasmDetailDTO.sampleBk)
			.append(plotId, sampleGermplasmDetailDTO.plotId)
			.append(plantBk, sampleGermplasmDetailDTO.plantBk)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)//
			.append(sampleBk)
			.append(plotId)
			.append(plantBk)
			.toHashCode();
	}

}
