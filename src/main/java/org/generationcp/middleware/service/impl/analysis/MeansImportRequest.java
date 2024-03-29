package org.generationcp.middleware.service.impl.analysis;

import java.util.List;
import java.util.Map;

public class MeansImportRequest {

	private List<MeansData> data;

	public List<MeansData> getData() {
		return this.data;
	}

	public void setData(final List<MeansData> data) {
		this.data = data;
	}

	public static class MeansData {

		private Integer environmentNumber;
		private Integer entryNo;
		private Map<String, Double> values;

		public Integer getEnvironmentNumber() {
			return this.environmentNumber;
		}

		public void setEnvironmentNumber(final Integer environmentNumber) {
			this.environmentNumber = environmentNumber;
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


