package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

import java.util.Map;

public class TrialEnvironmentProperty {

	private Integer id;
	private String name;
	private String description;
	private Map<Integer, String> environmentValuesMap;
	private Integer numberOfEnvironments;
	
	public TrialEnvironmentProperty(String name, String description, Map<Integer,String> environmentValuesMap) {
		this.name = name;
		this.description = description;
		this.environmentValuesMap = environmentValuesMap;
		if (environmentValuesMap != null){
			this.numberOfEnvironments = this.environmentValuesMap.size();
		}
	}
	
	public TrialEnvironmentProperty(Integer id, String name, String description, Map<Integer,String> environmentValuesMap) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.environmentValuesMap = environmentValuesMap;
		if (environmentValuesMap != null){
			this.numberOfEnvironments = this.environmentValuesMap.size();
		}
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
	
	public Map<Integer, String> getEnvironmentValuesMap() {
		return environmentValuesMap;
	}

	public void setEnvironmentValuesMap(Map<Integer, String> environmentValuesMap) {
		this.environmentValuesMap = environmentValuesMap;
	}

	public void print(int indent) {
		Debug.println(indent, "TrialEnvironmentProperty[name=" + name + ", description=" + description + ", count=" + numberOfEnvironments + "]");
		Debug.println(indent+3, "EnvironmentPropertyValues:");
		for (Map.Entry<Integer, String > entry : environmentValuesMap.entrySet()){
			Debug.println(indent+6, "Environment=" + entry.getKey() + ", PropertyValue=" + entry.getValue());
		}
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TrialEnvironmentProperty [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", environmentValuesMap=");
        builder.append(environmentValuesMap);
        builder.append(", numberOfEnvironments=");
        builder.append(numberOfEnvironments);
        builder.append("]");
        return builder.toString();
    }
	
}
