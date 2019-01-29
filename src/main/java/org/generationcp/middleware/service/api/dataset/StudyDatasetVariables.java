package org.generationcp.middleware.service.api.dataset;

import java.util.List;

import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

public class StudyDatasetVariables {
	
	private List<String> environmentFactors;
	private List<String> environmentConditions;
	private List<String> genericGermplasmDescriptors;
	private List<String> additionalDesignFactors;
	private List<MeasurementVariableDto> selectionMethodsAndTraits;
	
	
	public List<String> getEnvironmentFactors() {
		return environmentFactors;
	}
	
	public void setEnvironmentFactors(List<String> environmentFactors) {
		this.environmentFactors = environmentFactors;
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
	
}
