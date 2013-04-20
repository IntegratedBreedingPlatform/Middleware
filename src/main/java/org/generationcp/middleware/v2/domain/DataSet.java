package org.generationcp.middleware.v2.domain;

import java.util.List;

import org.generationcp.middleware.v2.util.Debug;

public class DataSet {

	private int id;
	
	private String name;
	
	private String description;
	
	private Study study;
	
	private List<Experiment> experiments;

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

	public List<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}
	
	public void print(int index) {
		Debug.println(index, "DataSet: ");
		Debug.println(index + 3, "Id: " + getId());
		Debug.println(index + 3, "Name: " + getName());
	    Debug.println(index + 3, "Description: " + getDescription());
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DataSet)) return false;
		DataSet other = (DataSet) obj;
		return getId() == other.getId();
	}
}
