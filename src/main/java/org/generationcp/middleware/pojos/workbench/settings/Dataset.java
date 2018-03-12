
package org.generationcp.middleware.pojos.workbench.settings;

import java.util.List;

public class Dataset extends ParentDataset {

	private static final long serialVersionUID = 1L;
	private String name;
	private List<Condition> conditions;
	private List<Factor> factors;
	private List<Variate> variates;
	private List<Constant> constants;
	private List<Factor> trialLevelFactor;
	private List<TreatmentFactor> treatmentFactors;
	private String description;
	private String 	startDate;
	private String 	endDate;
	private String 	studyUpdate;


	public Dataset() {
		super();
	}

	public Dataset(final String name, final List<Condition> conditions, final List<Factor> factors, final List<Variate> variates,
		final String description) {
		super();
		this.name = name;
		this.conditions = conditions;
		this.factors = factors;
		this.variates = variates;
		this.description = description;
	}

	public Dataset(List<Factor> trialLevelFactor) {
		super();
		this.trialLevelFactor = trialLevelFactor;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Condition> getConditions() {
		return this.conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public List<Factor> getFactors() {
		return this.factors;
	}

	public void setFactors(List<Factor> factors) {
		this.factors = factors;
	}

	public List<Variate> getVariates() {
		return this.variates;
	}

	public void setVariates(List<Variate> variates) {
		this.variates = variates;
	}

	public List<Constant> getConstants() {
		return this.constants;
	}

	public void setConstants(List<Constant> constants) {
		this.constants = constants;
	}

	public List<Factor> getTrialLevelFactor() {
		return this.trialLevelFactor;
	}

	public void setTrialLevelFactor(List<Factor> trialLevelFactor) {
		this.trialLevelFactor = trialLevelFactor;
	}

	public List<TreatmentFactor> getTreatmentFactors() {
		return this.treatmentFactors;
	}

	public void setTreatmentFactors(List<TreatmentFactor> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getStudyUpdate() {
		return this.studyUpdate;
	}

	public void setStudyUpdate(final String studyUpdate) {
		this.studyUpdate = studyUpdate;
	}
}
