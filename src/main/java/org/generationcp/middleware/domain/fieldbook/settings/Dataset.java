package org.generationcp.middleware.domain.fieldbook.settings;

import java.io.Serializable;
import java.util.List;

public class Dataset implements Serializable {
	private String name;
	private List<Condition> conditions;
	private List<Factor> factors;
	private List<Variate> variates;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	public List<Factor> getFactors() {
		return factors;
	}
	public void setFactors(List<Factor> factors) {
		this.factors = factors;
	}
	public List<Variate> getVariates() {
		return variates;
	}
	public void setVariates(List<Variate> variates) {
		this.variates = variates;
	}
	
	
}
