package org.generationcp.middleware.service.impl.analysis;

import java.util.List;
import java.util.Map;

public class MeansImportRequest {

	private int studyId;
	private List<MeansData> data;

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public List<MeansData> getData() {
		return this.data;
	}

	public void setData(final List<MeansData> data) {
		this.data = data;
	}
}


class MeansData {

	private int environmentId;
	private int entryNo;
	private Map<String, String> values;

	public int getEnvironmentId() {
		return this.environmentId;
	}

	public void setEnvironmentId(final int environmentId) {
		this.environmentId = environmentId;
	}

	public int getEntryNo() {
		return this.entryNo;
	}

	public void setEntryNo(final int entryNo) {
		this.entryNo = entryNo;
	}

	public Map<String, String> getValues() {
		return this.values;
	}

	public void setValues(final Map<String, String> values) {
		this.values = values;
	}
}
