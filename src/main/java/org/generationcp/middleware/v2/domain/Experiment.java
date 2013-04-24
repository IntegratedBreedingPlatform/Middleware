package org.generationcp.middleware.v2.domain;

import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class Experiment {

	private int id;
	
	private DataSet dataSet;
	
	private Set<Variable> factors;
	
	private Set<Variable> traits;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	public Set<Variable> getFactors() {
		return factors;
	}

	public void setFactors(Set<Variable> factors) {
		this.factors = factors;
	}

	public Set<Variable> getTraits() {
		return traits;
	}

	public void setTraits(Set<Variable> traits) {
		this.traits = traits;
	}

	public void print(int indent) {
		Debug.println(indent, "Experiment: " + id);
		if (factors != null) {
			for (Variable variable : factors) {
				variable.print(indent + 3);
			}
		}
	}
}
