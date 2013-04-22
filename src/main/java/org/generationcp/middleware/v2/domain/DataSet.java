package org.generationcp.middleware.v2.domain;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class DataSet {

	private int id;
	
	private String name;
	
	private String description;
	
	private Study study;
	
	private List<Experiment> experiments;
	
	private Set<VariableType> variableTypes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
		if (study != null) {
			study.addDataSet(this);
		}
	}

	public Set<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(Set<VariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}

	public List<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
		if (experiments != null) {
			for (Experiment experiment : experiments) {
				experiment.setDataSet(this);
			}
		}
	}
	
	public void print(int indent) {
		Debug.println(indent, "DataSet: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    
	    Debug.println(indent + 3, "Experiments:" );
	    for (Experiment experiment : experiments) {
	    	experiment.print(indent + 6);
	    }
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DataSet)) return false;
		DataSet other = (DataSet) obj;
		return getId() == other.getId();
	}
}
