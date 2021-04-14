package org.generationcp.middleware.api.study;

import java.util.ArrayList;
import java.util.List;

public class MyStudiesDTO {

	public static class MyStudyMetadata {
		public static class ObservationMetadata {
			private Integer studyId;
			private String datasetName;
			private String instanceName;
			private Integer confirmedCount;
			private Integer pendingCount;
			private Integer unobservedCount;

			public Integer getStudyId() {
				return this.studyId;
			}

			public void setStudyId(final Integer studyId) {
				this.studyId = studyId;
			}

			public String getDatasetName() {
				return this.datasetName;
			}

			public void setDatasetName(final String datasetName) {
				this.datasetName = datasetName;
			}

			public String getInstanceName() {
				return this.instanceName;
			}

			public void setInstanceName(final String instanceName) {
				this.instanceName = instanceName;
			}

			public Integer getConfirmedCount() {
				return this.confirmedCount;
			}

			public void setConfirmedCount(final Integer confirmedCount) {
				this.confirmedCount = confirmedCount;
			}

			public Integer getPendingCount() {
				return this.pendingCount;
			}

			public void setPendingCount(final Integer pendingCount) {
				this.pendingCount = pendingCount;
			}

			public Integer getUnobservedCount() {
				return this.unobservedCount;
			}

			public void setUnobservedCount(final Integer unobservedCount) {
				this.unobservedCount = unobservedCount;
			}
		}

		private List<ObservationMetadata> observations;

		public List<ObservationMetadata> getObservations() {
			if (this.observations == null) {
				this.observations = new ArrayList<>();
			}
			return this.observations;
		}

		public void setObservations(final List<ObservationMetadata> observations) {
			this.observations = observations;
		}
	}

	private String name;
	private String type;
	private String date;
	private String folder;
	private MyStudyMetadata metadata;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getFolder() {
		return this.folder;
	}

	public void setFolder(final String folder) {
		this.folder = folder;
	}

	public MyStudyMetadata getMetadata() {
		if (this.metadata == null) {
			this.metadata = new MyStudyMetadata();
		}
		return this.metadata;
	}

	public void setMetadata(final MyStudyMetadata metadata) {
		this.metadata = metadata;
	}
}
