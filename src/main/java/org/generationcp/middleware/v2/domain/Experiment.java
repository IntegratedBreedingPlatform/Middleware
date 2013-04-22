package org.generationcp.middleware.v2.domain;

import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class Experiment {

	private int id;
	
	private DataSet dataSet;
	
	private Set<Variable> variables;

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
	
	public void print(int indent) {
		Debug.println(indent, "Experiment: " + id);
	}
}
