package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Set;

@AutoProperty
public class AdvanceStudyRequest {

	private Integer breedingMethodId;
	private List<Integer> instanceIds;
	private Integer methodVariateId;
	private Integer linesSelected;
	private Integer lineVariateId;
	// TODO: add replications param
	private Set<String> selectedReplications;
	private SelectionTraitRequest selectionTraitRequest;

	public Integer getBreedingMethodId() {
		return breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public List<Integer> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	public Integer getMethodVariateId() {
		return methodVariateId;
	}

	public void setMethodVariateId(final Integer methodVariateId) {
		this.methodVariateId = methodVariateId;
	}

	public Integer getLinesSelected() {
		return linesSelected;
	}

	public void setLinesSelected(final Integer linesSelected) {
		this.linesSelected = linesSelected;
	}

	public Integer getLineVariateId() {
		return lineVariateId;
	}

	public void setLineVariateId(final Integer lineVariateId) {
		this.lineVariateId = lineVariateId;
	}

	public SelectionTraitRequest getSelectionTraitRequest() {
		return selectionTraitRequest;
	}

	public void setSelectionTraitRequest(final SelectionTraitRequest selectionTraitRequest) {
		this.selectionTraitRequest = selectionTraitRequest;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@AutoProperty
	public static class SelectionTraitRequest {

		private Integer datasetId;
		private Integer variableId;

		public Integer getDatasetId() {
			return datasetId;
		}

		public void setDatasetId(final Integer datasetId) {
			this.datasetId = datasetId;
		}

		public Integer getVariableId() {
			return variableId;
		}

		public void setVariableId(final Integer variableId) {
			this.variableId = variableId;
		}
	}

}
