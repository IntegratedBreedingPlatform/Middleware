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
	
	public Dataset(){
		super();
	}
	public Dataset(String name, List<Condition> conditions,
			List<Factor> factors, List<Variate> variates) {
		super();
		this.name = name;
		this.conditions = conditions;
		this.factors = factors;
		this.variates = variates;
	}
	public Dataset(List<Factor> trialLevelFactor) {
	    super();
	    this.trialLevelFactor = trialLevelFactor;
	}
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
	public List<Constant> getConstants() {
	        return constants;
	}
	public void setConstants(List<Constant> constants) {
	        this.constants = constants;
	}
    public List<Factor> getTrialLevelFactor() {
        return trialLevelFactor;
    }
    public void setTrialLevelFactor(List<Factor> trialLevelFactor) {
        this.trialLevelFactor = trialLevelFactor;
    }
    public List<TreatmentFactor> getTreatmentFactors() {
        return treatmentFactors;
    }
    public void setTreatmentFactors(List<TreatmentFactor> treatmentFactors) {
        this.treatmentFactors = treatmentFactors;
    }
}
