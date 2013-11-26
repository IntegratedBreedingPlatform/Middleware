/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.dms;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a dataset.
 */
public class DataSet {

	private int id;
	
	private String name;
	
	private String description;
	
	private int studyId;
	
	private DataSetType dataSetType;
	
	private VariableTypeList variableTypes;
	
	private Set<Integer> locationIds = new HashSet<Integer>();

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

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public DataSetType getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType(DataSetType dataSetType) {
		this.dataSetType = dataSetType;
	}

	public VariableTypeList getVariableTypes() {
		return variableTypes.sort();
	}

	public void setVariableTypes(VariableTypeList variableTypes) {
		this.variableTypes = variableTypes;
	}
	
	public Set<Integer> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(Set<Integer> locationIds) {
		this.locationIds = locationIds;
		if (this.locationIds == null) this.locationIds = new HashSet<Integer>();
	}
	
	public boolean containsLocationId(int locationId) {
		for (Integer locId : locationIds) {
			if (locId == locationId) return true;
		}
		return false;
	}

	public VariableTypeList getFactorsByProperty(int propertyId) {
		VariableTypeList filteredFactors = new VariableTypeList();
		
		VariableTypeList factors = getVariableTypes() != null ? getVariableTypes().getFactors() : null;
		if (factors != null && factors.getVariableTypes() != null) {
			for (VariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getProperty().getId() == propertyId) {
					filteredFactors.add(factor);
				}
			}
		}
		
		return filteredFactors.sort();
	}
	
	public VariableTypeList getFactorsByPhenotypicType(PhenotypicType factorType) {
		VariableTypeList filteredFactors = new VariableTypeList();
		
		VariableTypeList factors = getVariableTypes() != null ? getVariableTypes().getFactors() : null;
		if (factors != null && factors.getVariableTypes() != null) {
			for (VariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getPhenotypicType() == factorType) {
					filteredFactors.add(factor);
				}
			}
		}
		return filteredFactors.sort();
	}

	public void print(int indent) {
		Debug.println(indent, "DataSet: ");
		Debug.println(indent + 3, "Id: " + getId());
		Debug.println(indent + 3, "Name: " + getName());
	    Debug.println(indent + 3, "Description: " + getDescription());
	    Debug.println(indent + 3, "Location Ids: " + this.getLocationIds());
	    Debug.println(indent + 3, "Variable Types: ");
	    variableTypes.print(indent + 6);
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dataSetType == null) ? 0 : dataSetType.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result
                + ((locationIds == null) ? 0 : locationIds.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + studyId;
        result = prime * result
                + ((variableTypes == null) ? 0 : variableTypes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof DataSet)) return false;
        DataSet other = (DataSet) obj;
        return getId() == other.getId();
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSet [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", studyId=");
		builder.append(studyId);
		builder.append(", variableTypes=");
		builder.append(variableTypes);
		builder.append("]");
		return builder.toString();
	}
	
	public VariableType findVariableTypeByLocalName(String localName) {
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			if (variableType.getLocalName().equals(localName)) {
				return variableType;
			}
		}
		return null;
	}
}
