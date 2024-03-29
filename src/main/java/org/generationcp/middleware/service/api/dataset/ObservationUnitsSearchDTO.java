package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	// Contains variables with variable types depending on the dataset type
	// Plot and Subobservations: TRAIT and SELECTION_METHOD
	// MEANS_DATA: TRAIT and ANALYSIS
	// SUMMARY_STATISTICS_DATA: ANALYSIS_SUMMARY
	private List<MeasurementVariableDto> datasetVariables;
	private List<MeasurementVariableDto> entryDetails;
	private int datasetId;
	private List<Integer> instanceIds;
	private Integer environmentDatasetId;
	private Boolean draftMode;

	/**
	 * List of columns that will be included and retrieved in the query.
	 * If no columns are specified, the query will retrieve all columns by default.
	 */
	private Set<String> visibleColumns = new HashSet<>();

	/**
	 * This is used by DataTables to ensure that the Ajax returns from server-side processing requests are drawn in sequence
	 */
	private String draw;

	private Filter filter;
	private List<MeasurementVariableDto> passportAndAttributes;

	private List<MeasurementVariableDto> nameTypes;


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
		private Boolean variableHasValue;
		// FIXME: Remove it after solver the filter for Female and Male Parents into the query.
		private Set<Integer> preFilteredGids;

		public Filter() {
			this.byMissing = false;
			this.byOutOfBound = false;
			this.byOutOfSync = false;
			this.byOverwritten = false;
			this.filteredValues = new HashMap<>();
			this.filteredTextValues = new HashMap<>();
			this.variableId = null;
			this.variableHasValue = false;
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

		public Boolean getVariableHasValue() {
			return this.variableHasValue;
		}

		public void setVariableHasValue(final Boolean variableHasValue) {
			this.variableHasValue = variableHasValue;
		}

		public Map<String, String> getVariableTypeMap() {
			return this.variableTypeMap;
		}

		public void setVariableTypeMap(final Map<String, String> variableTypeMap) {
			this.variableTypeMap = variableTypeMap;
		}

		public Set<Integer> getPreFilteredGids() {
			return this.preFilteredGids;
		}

		public void setPreFilteredGids(final Set<Integer> preFilteredGids) {
			this.preFilteredGids = preFilteredGids;
		}

	}

	public ObservationUnitsSearchDTO() {
		this.filter = new Filter();
		this.environmentDetails = new ArrayList<>();
		this.environmentConditions = new ArrayList<>();
		this.draftMode = false;
	}

	public ObservationUnitsSearchDTO(final int datasetId, final List<Integer> instanceIds, final List<String> genericGermplasmDescriptors,
		final List<String> additionalDesignFactors,
		final List<MeasurementVariableDto> datasetVariables) {
		this();
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
		this.additionalDesignFactors = additionalDesignFactors;
		this.datasetVariables = datasetVariables;
		this.datasetId = datasetId;
		this.instanceIds = instanceIds;
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

	public List<MeasurementVariableDto> getDatasetVariables() {
		return this.datasetVariables;
	}

	public void setDatasetVariables(final List<MeasurementVariableDto> datasetVariables) {
		this.datasetVariables = datasetVariables;
	}

	public List<MeasurementVariableDto> getEntryDetails() {
		return this.entryDetails;
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

	public List<Integer> getInstanceIds() {
		return this.instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
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

	public List<MeasurementVariableDto> getPassportAndAttributes() {
		return this.passportAndAttributes;
	}

	public void setPassportAndAttributes(final List<MeasurementVariableDto> passportAndAttributes) {
		this.passportAndAttributes = passportAndAttributes;
	}

	public List<MeasurementVariableDto> getNameTypes() {
		return this.nameTypes;
	}

	public void setNameTypes(final List<MeasurementVariableDto> nameTypes) {
		this.nameTypes = nameTypes;
	}

	public Set<String> getVisibleColumns() {
		return this.visibleColumns;
	}

	public void setVisibleColumns(final Set<String> visibleColumns) {
		this.visibleColumns = visibleColumns;
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
