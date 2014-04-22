/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.workbench.settings;

import java.util.List;


/**
 * The Class TrialDataset.
 */
public class TrialDataset  extends ParentDataset {
	
	private static final long serialVersionUID = 1L;

	/** The name. */
	private String name;
	
	/** The conditions. */
	private List<Condition> conditions;
	
	/** The factors. */
	private List<Factor> factors;
	
	/** The variates. */
	private List<Variate> variates;
	
	
	/** The trial level conditions. */
	private List<Factor> trialLevelFactor;

	/** The treatment factors. */
	private List<TreatmentFactor> treatmentFactors;
	
	/**
	 * Instantiates a new trial dataset.
	 */
	public TrialDataset(){
		super();
	}
	
	/**
	 * Instantiates a new trial dataset.
	 *
	 * @param trialLevelFactor the trial level factor
	 */
	public TrialDataset(List<Factor> trialLevelFactor) {
		super();
		this.trialLevelFactor = trialLevelFactor;
	}
	
	
	
	/**
	 * Gets the trial level factor.
	 *
	 * @return the trial level factor
	 */
	public List<Factor> getTrialLevelFactor() {
		return trialLevelFactor;
	}
	
	/**
	 * Sets the trial level factor.
	 *
	 * @param trialLevelFactor the new trial level factor
	 */
	public void setTrialLevelFactor(List<Factor> trialLevelFactor) {
		this.trialLevelFactor = trialLevelFactor;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the conditions.
	 *
	 * @return the conditions
	 */
	public List<Condition> getConditions() {
		return conditions;
	}
	
	/**
	 * Sets the conditions.
	 *
	 * @param conditions the new conditions
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	/**
	 * Gets the factors.
	 *
	 * @return the factors
	 */
	public List<Factor> getFactors() {
		return factors;
	}
	
	/**
	 * Sets the factors.
	 *
	 * @param factors the new factors
	 */
	public void setFactors(List<Factor> factors) {
		this.factors = factors;
	}
	
	/**
	 * Gets the variates.
	 *
	 * @return the variates
	 */
	public List<Variate> getVariates() {
		return variates;
	}
	
	/**
	 * Sets the variates.
	 *
	 * @param variates the new variates
	 */
	public void setVariates(List<Variate> variates) {
		this.variates = variates;
	}

	/**
	 * @return the treatmentFactors
	 */
	public List<TreatmentFactor> getTreatmentFactors() {
		return treatmentFactors;
	}

	/**
	 * @param treatmentFactors the treatmentFactors to set
	 */
	public void setTreatmentFactors(List<TreatmentFactor> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
