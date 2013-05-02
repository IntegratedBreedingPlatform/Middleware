package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Experiment {

	private int id;
	
	private DataSet dataSet;
	
	private VariableList factors;
	
	private VariableList variates;

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

	public VariableList getFactors() {
		return factors;
	}

	public void setFactors(VariableList factors) {
		this.factors = factors;
	}

	public VariableList getVariates() {
		return variates;
	}

	public void setVariates(VariableList variates) {
		this.variates = variates;
	}

	public void print(int indent) {
		Debug.println(indent, "Experiment: " + id);
		Debug.println(indent + 3, "Factors:");
		if (factors != null) {
			for (Variable variable : factors.getVariables()) {
				variable.print(indent + 6);
			}
		}
		Debug.println(indent + 3, "Variates:");
		if (factors != null) {
			for (Variable trait : variates.getVariables()) {
				trait.print(indent + 6);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Experiment [id=");
		builder.append(id);
		builder.append(", dataSet=");
		builder.append(dataSet);
		builder.append(", factors=");
		builder.append(factors);
		builder.append(", variates=");
		builder.append(variates);
		builder.append("]");
		return builder.toString();
	}
	
	
}
