package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused") // Used in POST body
@AutoProperty
public class ObservationUnitsSearchDTO extends SearchRequestDto {

	private List<MeasurementVariableDto> environmentDetails;
	private List<MeasurementVariableDto> environmentConditions;
	private List<String> genericGermplasmDescriptors;
	private List<String> additionalDesignFactors;
	private List<MeasurementVariableDto> selectionMethodsAndTraitsAndAnalysisSummary;
	private List<MeasurementVariableDto> entryDetails;
	private int datasetId;
	private Integer instanceId;
	private Integer environmentDatasetId;
	private Boolean draftMode;

	/** This is used by Visualization tool, to specify the columns that will be included in the data returned from the server. **/
	private List<String> filterColumns = new ArrayList<>();

	/**
	 * This is used by DataTables to ensure that the Ajax returns from server-side processing requests are drawn in sequence
	 */
	private String draw;

	private Filter filter;


	public class Filter {

		private Boolean byOutOfBound;
		private Boolean byOverwritten;
		private Boolean byOutOfSync;
		private Boolean byMissing;
		private Map<String, List<String>> filteredValues;
		private Map<String, String> filteredTextValues;
		private Set<Integer> filteredNdExperimentIds;
		private Map<String, String> variableTypeMap;
		private Integer variableId;

		public Filter() {
			this.byMissing = false;
			this.byOutOfBound = false;
			this.byOutOfSync = false;
			this.byOverwritten = false;
			this.filteredValues = new HashMap<>();
			this.filteredTextValues = new HashMap<>();
			this.variableId = null;
		}

		public Map<String, String> getFilteredTextValues() {
			return this.filteredTextValues;
		}

		public void setFilteredTextValues(final Map<String, String> filteredTextValues) {
			this.filteredTextValues = filteredTextValues;
		}

		public void setByOutOfBound(final Boolean byOutOfBound) {
			this.byOutOfBound = byOutOfBound;
		}

		public void setByOverwritten(final Boolean byOverwritten) {
			this.byOverwritten = byOverwritten;
		}

		public void setByOutOfSync(final Boolean byOutOfSync) {
			this.byOutOfSync = byOutOfSync;
		}

		public void setByMissing(final Boolean byMissing) {
			this.byMissing = byMissing;
		}

		public Boolean getByOutOfBound() {
			return this.byOutOfBound;
		}

		public Boolean getByOverwritten() {
			return this.byOverwritten;
		}

		public Boolean getByOutOfSync() {
			return this.byOutOfSync;
		}

		public Boolean getByMissing() {
			return this.byMissing;
		}

		public Map<String, List<String>> getFilteredValues() {
			return this.filteredValues;
		}

		public void setFilteredValues(final Map<String, List<String>> filteredValues) {
			this.filteredValues = filteredValues;
		}

		public Set<Integer> getFilteredNdExperimentIds() {
			return this.filteredNdExperimentIds;
		}

		public void setFilteredNdExperimentIds(final Set<Integer> filteredNdExperimentIds) {
			this.filteredNdExperimentIds = filteredNdExperimentIds;
		}

		public Integer getVariableId() {
			return this.variableId;
		}

		public void setVariableId(final Integer variableId) {
			this.variableId = variableId;
		}

		public Map<String, String> getVariableTypeMap() {
			return variableTypeMap;
		}

		public void setVariableTypeMap(final Map<String, String> variableTypeMap) {
			this.variableTypeMap = variableTypeMap;
		}

	}

	public ObservationUnitsSearchDTO() {
		this.filter = new Filter();
		this.environmentDetails = new ArrayList<>();
		this.environmentConditions = new ArrayList<>();
		this.draftMode = false;
	}

	public ObservationUnitsSearchDTO(final int datasetId, final Integer instanceId, final List<String> genericGermplasmDescriptors,
		final List<String> additionalDesignFactors,
		final List<MeasurementVariableDto> selectionMethodsAndTraitsAndAnalysisSummary) {
		this();
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
		this.additionalDesignFactors = additionalDesignFactors;
		this.selectionMethodsAndTraitsAndAnalysisSummary = selectionMethodsAndTraitsAndAnalysisSummary;
		this.datasetId = datasetId;
		this.instanceId = instanceId;
	}

	public List<MeasurementVariableDto> getEnvironmentDetails() {
		return this.environmentDetails;
	}

	public void setEnvironmentDetails(final List<MeasurementVariableDto> environmentFactors) {
		this.environmentDetails = environmentFactors;
	}

	public List<MeasurementVariableDto> getEnvironmentConditions() {
		return this.environmentConditions;
	}

	public void setEnvironmentConditions(final List<MeasurementVariableDto> environmentConditions) {
		this.environmentConditions = environmentConditions;
	}

	public List<String> getGenericGermplasmDescriptors() {
		return this.genericGermplasmDescriptors;
	}

	public void setGenericGermplasmDescriptors(final List<String> genericGermplasmDescriptors) {
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
	}

	public List<String> getAdditionalDesignFactors() {
		return this.additionalDesignFactors;
	}

	public void setAdditionalDesignFactors(final List<String> additionalDesignFactors) {
		this.additionalDesignFactors = additionalDesignFactors;
	}

	public List<MeasurementVariableDto> getSelectionMethodsAndTraitsAndAnalysisSummary() {
		return this.selectionMethodsAndTraitsAndAnalysisSummary;
	}

	public void setSelectionMethodsAndTraitsAndAnalysisSummary(final List<MeasurementVariableDto> selectionMethodsAndTraitsAndAnalysisSummary) {
		this.selectionMethodsAndTraitsAndAnalysisSummary = selectionMethodsAndTraitsAndAnalysisSummary;
	}

	public List<MeasurementVariableDto> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(final List<MeasurementVariableDto> entryDetails) {
		this.entryDetails = entryDetails;
	}

	public int getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(final int datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getInstanceId() {
		return this.instanceId;
	}

	public void setInstanceId(final Integer instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getEnvironmentDatasetId() {
		return this.environmentDatasetId;
	}

	public void setEnvironmentDatasetId(final Integer environmentDatasetId) {
		this.environmentDatasetId = environmentDatasetId;
	}

	public Boolean getDraftMode() {
		return this.draftMode;
	}

	public void setDraftMode(final Boolean draftMode) {
		this.draftMode = draftMode;
	}

	public String getDraw() {
		return this.draw;
	}

	public void setDraw(final String draw) {
		this.draw = draw;
	}

	public Filter getFilter() {
		return this.filter;
	}

	public void setFilter(final Filter filter) {
		this.filter = filter;
	}

	public List<String> getFilterColumns() {
		return this.filterColumns;
	}

	public void setFilterColumns(final List<String> filterColumns) {
		this.filterColumns = filterColumns;
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
