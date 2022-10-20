package org.generationcp.middleware.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Set;

@AutoProperty
public class AdvanceStudyRequest {

	// Represents the selected instance ids
	private List<Integer> instanceIds;

	private BreedingMethodSelectionRequest breedingMethodSelectionRequest;
	private LineSelectionRequest lineSelectionRequest;

	// TODO: add replications param
	private Set<String> selectedReplications;

	private SelectionTraitRequest selectionTraitRequest;

	public List<Integer> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	public BreedingMethodSelectionRequest getBreedingMethodSelectionRequest() {
		return breedingMethodSelectionRequest;
	}

	public void setBreedingMethodSelectionRequest(
		final BreedingMethodSelectionRequest breedingMethodSelectionRequest) {
		this.breedingMethodSelectionRequest = breedingMethodSelectionRequest;
	}

	public LineSelectionRequest getLineSelectionRequest() {
		return lineSelectionRequest;
	}

	public void setLineSelectionRequest(final LineSelectionRequest lineSelectionRequest) {
		this.lineSelectionRequest = lineSelectionRequest;
	}

	public Set<String> getSelectedReplications() {
		return selectedReplications;
	}

	public void setSelectedReplications(final Set<String> selectedReplications) {
		this.selectedReplications = selectedReplications;
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
	public static class BreedingMethodSelectionRequest {

		/**
		 * This is the id of the breeding method if the user has selected advancing using the same breeding method for all lines
		 */
		private Integer breedingMethodId;

		/**
		 * This is the id of the variable if the user has selected advancing using a variate which defines the breeding method for each line
		 */
		private Integer methodVariateId;

		/**
		 * If the breedingMethodId corresponds to a bulking method or if the methodVariateId was selected, then allPlotsSelected or plotVariateId
		 * must be set
		 */
		private Boolean allPlotsSelected;
		private Integer plotVariateId;

		public Integer getBreedingMethodId() {
			return breedingMethodId;
		}

		public void setBreedingMethodId(final Integer breedingMethodId) {
			this.breedingMethodId = breedingMethodId;
		}

		public Integer getMethodVariateId() {
			return methodVariateId;
		}

		public void setMethodVariateId(final Integer methodVariateId) {
			this.methodVariateId = methodVariateId;
		}

		public Boolean getAllPlotsSelected() {
			return allPlotsSelected;
		}

		public void setAllPlotsSelected(final Boolean allPlotsSelected) {
			this.allPlotsSelected = allPlotsSelected;
		}

		public Integer getPlotVariateId() {
			return plotVariateId;
		}

		public void setPlotVariateId(final Integer plotVariateId) {
			this.plotVariateId = plotVariateId;
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


	@AutoProperty
	public static class LineSelectionRequest {

		/**
		 * This is the number of the lines selected per plot
		 */
		private Integer linesSelected;

		/**
		 * Otherwise if there is no specific number of lines selected, this is the id of the variable that defines the number of
		 * lines selected from each plot
		 */
		private Integer lineVariateId;

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
