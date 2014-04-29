package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;

public class TreatmentFactor implements Serializable {

	private static final long serialVersionUID = -3067470952889656613L;

	private Factor levelFactor;
	
	private Factor valueFactor;
	
	private Integer levelNumber;
	
	private String value;
	

	public TreatmentFactor() {
	}
	
	public TreatmentFactor(Factor levelFactor, Factor valueFactor) {
		this.levelFactor = levelFactor;
		this.valueFactor = valueFactor;
	}
	
	public TreatmentFactor(Factor levelFactor, Factor valueFactor, Integer levelNumber, String value) {
		this(levelFactor, valueFactor);
		this.levelNumber = levelNumber;
		this.value = value;
	}

	/**
	 * @return the levelFactor
	 */
	public Factor getLevelFactor() {
		return levelFactor;
	}

	/**
	 * @param levelFactor the levelFactor to set
	 */
	public void setLevelFactor(Factor levelFactor) {
		this.levelFactor = levelFactor;
	}

	/**
	 * @return the valueFactor
	 */
	public Factor getValueFactor() {
		return valueFactor;
	}

	/**
	 * @param valueFactor the valueFactor to set
	 */
	public void setValueFactor(Factor valueFactor) {
		this.valueFactor = valueFactor;
	}

	public Integer getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(Integer levelNumber) {
		this.levelNumber = levelNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TreatmentFactor [levelFactor=" + levelFactor + ", valueFactor="
				+ valueFactor + ", levelNumber=" + levelNumber + ", value="
				+ value + "]";
	}

}
