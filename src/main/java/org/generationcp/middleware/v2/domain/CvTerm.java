package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public abstract class CvTerm {

	private int id;
	
	private String name;
	
	private String description;

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
	
	public void print(int index) {
		Debug.println(index, "Id: " + getId());
		Debug.println(index, "Name: " + getName());
	    Debug.println(index, "Description: " + getDescription());
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof CvTerm)) return false;
		CvTerm other = (CvTerm) obj;
		return getId() == other.getId();
	}
}
