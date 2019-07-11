package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.List;

public class VariableDatasetsDTO {

	private String variableName;

	private List<DatasetReference> datasets = new ArrayList<>();

	public String getVariableName() {
		return this.variableName;
	}

	public void setVariableName(final String variableName) {
		this.variableName = variableName;
	}

	public List<DatasetReference> getDatasets() {
		return this.datasets;
	}

	public void setDatasets(final List<DatasetReference> datasets) {
		this.datasets = datasets;
	}

}
