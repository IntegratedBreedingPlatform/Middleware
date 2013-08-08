package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

public class TrialEnvironmentProperty {

	private String name;
	private String description;
	private int numberOfEnvironments;
	
	public TrialEnvironmentProperty(String name, String description, int numberOfEnvironments) {
		this.name = name;
		this.description = description;
		this.numberOfEnvironments = numberOfEnvironments;
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
	public int getNumberOfEnvironments() {
		return numberOfEnvironments;
	}
	public void setNumberOfEnvironments(int numberOfEnvironments) {
		this.numberOfEnvironments = numberOfEnvironments;
	}
	
	public void print(int indent) {
		Debug.println(indent, "TrialEnvironmentProperty[name=" + name + ", description=" + description + ", count=" + numberOfEnvironments);
	}
}
