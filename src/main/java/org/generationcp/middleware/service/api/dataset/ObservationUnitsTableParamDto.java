package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.pojos.SortedPageRequest;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class ObservationUnitsTableParamDto {
	
	private List<String> environmentDetails;
	private List<String> environmentConditions;
	private List<String> genericGermplasmDescriptors;
	private List<String> additionalDesignFactors;
	private List<MeasurementVariableDto> selectionMethodsAndTraits;
	private SortedPageRequest sortedRequest;
	private int datasetId;
	private Integer instanceId;
	private Integer environmentDatasetId;
	private Boolean draftMode;
	private String draw;

	private Filter filter;

	public class Filter {

		private Boolean byOutOfBound;
		private Boolean byOverwritten;
		private Boolean byOutOfSync;
		private Boolean byMissing;
		private Map<String, List<String>> filteredValues;
		private Map<String, String> filteredTextValues;

		public Filter() {
			this.byMissing = false;
			this.byOutOfBound = false;
			this.byOutOfSync =  false;
			this.byOverwritten = false;
			this.filteredValues = new HashMap<>();
			this.filteredTextValues = new HashMap<>();
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
	}

	@SuppressWarnings("unused")
	public ObservationUnitsTableParamDto() {
		this.filter = new Filter();
		this.environmentDetails = new ArrayList<>();
		this.environmentConditions = new ArrayList<>();
		this.draftMode = false;
	}

	public ObservationUnitsTableParamDto(final int datasetId, final Integer instanceId, final List<String> genericGermplasmDescriptors, final List<String> additionalDesignFactors,
			final List<MeasurementVariableDto> selectionMethodsAndTraits) {
		this();
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
		this.additionalDesignFactors = additionalDesignFactors;
		this.selectionMethodsAndTraits = selectionMethodsAndTraits;
		this.datasetId = datasetId;
		this.instanceId = instanceId;
	}

	public List<String> getEnvironmentDetails() {
		return this.environmentDetails;
	}
	
	public void setEnvironmentDetails(final List<String> environmentFactors) {
		this.environmentDetails = environmentFactors;
	}
	
	public List<String> getEnvironmentConditions() {
		return this.environmentConditions;
	}
	
	public void setEnvironmentConditions(final List<String> environmentConditions) {
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
	
	public List<MeasurementVariableDto> getSelectionMethodsAndTraits() {
		return this.selectionMethodsAndTraits;
	}
	
	public void setSelectionMethodsAndTraits(final List<MeasurementVariableDto> selectionMethodsAndTraits) {
		this.selectionMethodsAndTraits = selectionMethodsAndTraits;
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

	public SortedPageRequest getSortedRequest() {
		return this.sortedRequest;
	}
	
	public void setSortedRequest(final SortedPageRequest sortedRequest) {
		this.sortedRequest = sortedRequest;
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
