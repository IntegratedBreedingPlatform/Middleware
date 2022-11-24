package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public abstract class AbstractAdvanceRequest implements AdvanceRequest {

	// Represents the selected instance ids
	private List<Integer> instanceIds;
	private List<String> selectedReplications;

	private SelectionTraitRequest selectionTraitRequest;

	@Override
	public List<Integer> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	@Override
	public List<String> getSelectedReplications() {
		return selectedReplications;
	}

	public void setSelectedReplications(final List<String> selectedReplications) {
		this.selectedReplications = selectedReplications;
	}

	@Override
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
	}

}
