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

	public void print(int indent) {
		Debug.println(indent, "minValue: " + minValue);
		Debug.println(indent, "maxValue: " + maxValue);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableConstraints [minValue=");
		builder.append(minValue);
		builder.append(", maxValue=");
		builder.append(maxValue);
		builder.append("]");
		return builder.toString();
	}
	
	
}
