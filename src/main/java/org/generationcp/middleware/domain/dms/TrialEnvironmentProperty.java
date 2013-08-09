package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

public class TrialEnvironmentProperty {

	private Integer id;
	private String name;
	private String description;
	private Integer numberOfEnvironments;
	
	public TrialEnvironmentProperty(String name, String description, Integer numberOfEnvironments) {
		this.name = name;
		this.description = description;
		this.numberOfEnvironments = numberOfEnvironments;
	}
	
	public TrialEnvironmentProperty(Integer id, String name, String description, Integer numberOfEnvironments) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.numberOfEnvironments = numberOfEnvironments;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	public Integer getNumberOfEnvironments() {
		return numberOfEnvironments;
	}
	public void setNumberOfEnvironments(Integer numberOfEnvironments) {
		this.numberOfEnvironments = numberOfEnvironments;
	}
	
	public void print(int indent) {
		Debug.println(indent, "TrialEnvironmentProperty[name=" + name + ", description=" + description + ", count=" + numberOfEnvironments + "]");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrialEnvironmentProperty other = (TrialEnvironmentProperty) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
