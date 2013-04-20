package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class VariableConstraints {

	private Integer minValue;
	
	private Integer maxValue;

	public VariableConstraints(Integer minValue, Integer maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public void print(int index) {
		Debug.println(index, "minValue: " + minValue);
		Debug.println(index, "maxValue: " + maxValue);
	}
}
