package org.generationcp.middleware.service.impl.analysis;

import java.util.List;
import java.util.Map;

public class MeansImportRequest {

	private Integer studyId;
	private List<MeansData> data;

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public List<MeansData> getData() {
		return this.data;
	}

	public void setData(final List<MeansData> data) {
		this.data = data;
	}

	public static class MeansData {

		private Integer environmentId;
		private Integer entryNo;
		private Map<String, Double> values;

		public Integer getEnvironmentId() {
			return this.environmentId;
		}

		public void setEnvironmentId(final Integer environmentId) {
			this.environmentId = environmentId;
		}

		public Integer getEntryNo() {
			return this.entryNo;
		}

		public void setEntryNo(final Integer entryNo) {
			this.entryNo = entryNo;
		}

		public Map<String, Double> getValues() {
			return this.values;
		}

		public void setValues(final Map<String, Double> values) {
			this.values = values;
		}
	}
}


