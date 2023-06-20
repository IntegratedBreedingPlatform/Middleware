package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public abstract class AbstractAdvanceRequest implements AdvanceRequest {

	// Represents the selected instance ids
	private List<Integer> instanceIds;
	private List<Integer> selectedReplications;

	private SelectionTraitRequest selectionTraitRequest;

	private boolean propagateAttributesData;

	private boolean propagatePassportDescriptorData;

	@Override
	public List<Integer> getInstanceIds() {
		return this.instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	@Override
	public List<Integer> getSelectedReplications() {
		return this.selectedReplications;
	}

	public void setSelectedReplications(final List<Integer> selectedReplications) {
		this.selectedReplications = selectedReplications;
	}

	@Override
	public SelectionTraitRequest getSelectionTraitRequest() {
		return this.selectionTraitRequest;
	}

	public void setSelectionTraitRequest(final SelectionTraitRequest selectionTraitRequest) {
		this.selectionTraitRequest = selectionTraitRequest;
	}

	@Override
	public boolean isPropagateAttributesData() {
		return this.propagateAttributesData;
	}

	public void setPropagateAttributesData(final boolean propagateAttributesData) {
		this.propagateAttributesData = propagateAttributesData;
	}

	@Override
	public boolean isPropagatePassportDescriptorData() {
		return this.propagatePassportDescriptorData;
	}

	public void setPropagatePassportDescriptorData(final boolean propagatePassportDescriptorData) {
		this.propagatePassportDescriptorData = propagatePassportDescriptorData;
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
			return this.datasetId;
		}

		public void setDatasetId(final Integer datasetId) {
			this.datasetId = datasetId;
		}

		public Integer getVariableId() {
			return this.variableId;
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
