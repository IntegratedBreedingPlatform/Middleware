package org.generationcp.middleware.service.api.dataset;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.SortedPageRequest;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

public class ObservationUnitsSearchDTO {
	
	private List<String> environmentDetails;
	private List<String> environmentConditions;
	private List<String> genericGermplasmDescriptors;
	private List<String> additionalDesignFactors;
	private List<MeasurementVariableDto> selectionMethodsAndTraits;
	private SortedPageRequest sortedRequest;
	private int datasetId;
	private Integer instanceId;
	private Integer environmentDatasetId;
	
	
	public ObservationUnitsSearchDTO(final int datasetId, final Integer instanceId, final List<String> genericGermplasmDescriptors, final List<String> additionalDesignFactors,
			final List<MeasurementVariableDto> selectionMethodsAndTraits) {
		super();
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
		this.additionalDesignFactors = additionalDesignFactors;
		this.selectionMethodsAndTraits = selectionMethodsAndTraits;
		this.datasetId = datasetId;
		this.instanceId = instanceId;
		this.environmentDetails = new ArrayList<>();
		this.environmentConditions = new ArrayList<>();
	}

	public List<String> getEnvironmentDetails() {
		return environmentDetails;
	}
	
	public void setEnvironmentDetails(List<String> environmentFactors) {
		this.environmentDetails = environmentFactors;
	}
	
	public List<String> getEnvironmentConditions() {
		return environmentConditions;
	}
	
	public void setEnvironmentConditions(List<String> environmentConditions) {
		this.environmentConditions = environmentConditions;
	}
	
	public List<String> getGenericGermplasmDescriptors() {
		return genericGermplasmDescriptors;
	}
	
	public void setGenericGermplasmDescriptors(List<String> genericGermplasmDescriptors) {
		this.genericGermplasmDescriptors = genericGermplasmDescriptors;
	}
	
	public List<String> getAdditionalDesignFactors() {
		return additionalDesignFactors;
	}
	
	public void setAdditionalDesignFactors(List<String> additionalDesignFactors) {
		this.additionalDesignFactors = additionalDesignFactors;
	}
	
	public List<MeasurementVariableDto> getSelectionMethodsAndTraits() {
		return selectionMethodsAndTraits;
	}
	
	public void setSelectionMethodsAndTraits(List<MeasurementVariableDto> selectionMethodsAndTraits) {
		this.selectionMethodsAndTraits = selectionMethodsAndTraits;
	}

	
	
	public int getDatasetId() {
		return datasetId;
	}

	
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}

	
	public Integer getInstanceId() {
		return instanceId;
	}

	
	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	
	public Integer getEnvironmentDatasetId() {
		return environmentDatasetId;
	}

	
	public void setEnvironmentDatasetId(Integer environmentDatasetId) {
		this.environmentDatasetId = environmentDatasetId;
	}

	public SortedPageRequest getSortedRequest() {
		return sortedRequest;
	}

	
	public void setSortedRequest(SortedPageRequest sortedRequest) {
		this.sortedRequest = sortedRequest;
	}
	
}
