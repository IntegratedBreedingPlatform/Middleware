
package org.generationcp.middleware.domain.dms;

import java.util.Map;

import org.generationcp.middleware.util.Debug;

public class TrialEnvironmentProperty {

	private Integer id;
	private String name;
	private String description;
	private Map<Integer, String> environmentValuesMap;
	private Integer numberOfEnvironments;

	public TrialEnvironmentProperty(String name, String description, Map<Integer, String> environmentValuesMap) {
		this.name = name;
		this.description = description;
		this.environmentValuesMap = environmentValuesMap;
		if (environmentValuesMap != null) {
			this.numberOfEnvironments = this.environmentValuesMap.size();
		}
	}

	public TrialEnvironmentProperty(Integer id, String name, String description, Map<Integer, String> environmentValuesMap) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.environmentValuesMap = environmentValuesMap;
		if (environmentValuesMap != null) {
			this.numberOfEnvironments = this.environmentValuesMap.size();
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getNumberOfEnvironments() {
		return this.numberOfEnvironments;
	}

	public void setNumberOfEnvironments(Integer numberOfEnvironments) {
		this.numberOfEnvironments = numberOfEnvironments;
	}

	public Map<Integer, String> getEnvironmentValuesMap() {
		return this.environmentValuesMap;
	}

	public void setEnvironmentValuesMap(Map<Integer, String> environmentValuesMap) {
		this.environmentValuesMap = environmentValuesMap;
	}

	public void print(int indent) {
		Debug.println(indent, "TrialEnvironmentProperty[name=" + this.name + ", description=" + this.description + ", count="
				+ this.numberOfEnvironments + "]");
		Debug.println(indent + 3, "EnvironmentPropertyValues:");
		for (Map.Entry<Integer, String> entry : this.environmentValuesMap.entrySet()) {
			Debug.println(indent + 6, "Environment=" + entry.getKey() + ", PropertyValue=" + entry.getValue());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		TrialEnvironmentProperty other = (TrialEnvironmentProperty) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrialEnvironmentProperty [id=");
		builder.append(this.id);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", environmentValuesMap=");
		builder.append(this.environmentValuesMap);
		builder.append(", numberOfEnvironments=");
		builder.append(this.numberOfEnvironments);
		builder.append("]");
		return builder.toString();
	}

}
