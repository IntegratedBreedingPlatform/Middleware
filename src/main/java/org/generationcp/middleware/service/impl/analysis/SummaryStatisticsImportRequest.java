package org.generationcp.middleware.service.impl.analysis;

import java.util.List;
import java.util.Map;

public class SummaryStatisticsImportRequest {

	private List<SummaryData> data;

	public List<SummaryData> getData() {
		return this.data;
	}

	public void setData(final List<SummaryData> data) {
		this.data = data;
	}

	public static class SummaryData {

		private Integer environmentNumber;
		private Map<String, Double> values;

		public Integer getEnvironmentNumber() {
			return this.environmentNumber;
		}

		public void setEnvironmentNumber(final Integer environmentNumber) {
			this.environmentNumber = environmentNumber;
		}

		public Map<String, Double> getValues() {
			return this.values;
		}

		public void setValues(final Map<String, Double> values) {
			this.values = values;
		}
	}
}


