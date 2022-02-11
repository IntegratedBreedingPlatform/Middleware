package org.generationcp.middleware.api.analysis;

import java.util.List;
import java.util.Map;

public class MeansRequestDto {

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
	private int germplasmId;
	private List<TraitMeans> traits;

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

	public int getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(final int germplasmId) {
		this.germplasmId = germplasmId;
	}

	public List<TraitMeans> getTraits() {
		return this.traits;
	}

	public void setTraits(final List<TraitMeans> traits) {
		this.traits = traits;
	}
}


class TraitMeans {

	private int variableId;
	private Map<String, String> values;

	public int getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final int variableId) {
		this.variableId = variableId;
	}

	public Map<String, String> getValues() {
		return this.values;
	}

	public void setValues(final Map<String, String> values) {
		this.values = values;
	}
}
