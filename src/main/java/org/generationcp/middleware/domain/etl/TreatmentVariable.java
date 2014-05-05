package org.generationcp.middleware.domain.etl;

public class TreatmentVariable {

	private MeasurementVariable levelVariable;
	
	private MeasurementVariable valueVariable;
	

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
	
	
}
