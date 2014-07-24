package org.generationcp.middleware.domain.etl;

import java.util.List;

public class TreatmentVariable {

	private MeasurementVariable levelVariable;
	
	private MeasurementVariable valueVariable;
	
	private List<String> values;
	

	public MeasurementVariable getLevelVariable() {
		return levelVariable;
	}

	public void setLevelVariable(MeasurementVariable levelVariable) {
		this.levelVariable = levelVariable;
	}

	public MeasurementVariable getValueVariable() {
		return valueVariable;
	}

	public void setValueVariable(MeasurementVariable valueVariable) {
		this.valueVariable = valueVariable;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
}
