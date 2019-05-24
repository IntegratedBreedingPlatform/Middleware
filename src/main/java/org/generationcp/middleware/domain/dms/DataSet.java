/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.util.Debug;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains the details of a dataset.
 */
public class DataSet {

	private int id;

	private String name;

	private String description;

	private int studyId;

	private DatasetType datasetType;

	private VariableTypeList variableTypes;

	private Set<Integer> locationIds = new HashSet<>();

	private String programUUID;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public DatasetType getDatasetType() {
		return this.datasetType;
	}

	public void setDatasetType(final DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	public VariableTypeList getVariableTypes() {
		return this.variableTypes.sort();
	}

	public void setVariableTypes(final VariableTypeList variableTypes) {
		this.variableTypes = variableTypes;
	}

	public Set<Integer> getLocationIds() {
		return this.locationIds;
	}

	public void setLocationIds(final Set<Integer> locationIds) {
		this.locationIds = locationIds;
		if (this.locationIds == null) {
			this.locationIds = new HashSet<Integer>();
		}
	}

	public boolean containsLocationId(final int locationId) {
		for (final Integer locId : this.locationIds) {
			if (locId == locationId) {
				return true;
			}
		}
		return false;
	}

	public VariableTypeList getFactorsByProperty(final int propertyId) {
		final VariableTypeList filteredFactors = new VariableTypeList();

		final VariableTypeList factors = this.getVariableTypes() != null ? this.getVariableTypes().getFactors() : null;
		if (factors != null && factors.getVariableTypes() != null) {
			for (final DMSVariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getProperty().getId() == propertyId) {
					filteredFactors.add(factor);
				}
			}
		}

		return filteredFactors.sort();
	}

	public VariableTypeList getFactorsByPhenotypicType(final PhenotypicType factorType) {
		final VariableTypeList filteredFactors = new VariableTypeList();

		final VariableTypeList factors = this.getVariableTypes() != null ? this.getVariableTypes().getFactors() : null;
		if (factors != null && factors.getVariableTypes() != null) {
			for (final DMSVariableType factor : factors.getVariableTypes()) {
				if (factor.getStandardVariable().getPhenotypicType() == factorType) {
					filteredFactors.add(factor);
				}
			}
		}
		return filteredFactors.sort();
	}

	public void print(final int indent) {
		Debug.println(indent, "DataSet: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Description: " + this.getDescription());
		Debug.println(indent + 3, "Location Ids: " + this.getLocationIds());
		Debug.println(indent + 3, "Variable Types: ");
		this.variableTypes.print(indent + 6);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.datasetType == null ? 0 : this.datasetType.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + this.id;
		result = prime * result + (this.locationIds == null ? 0 : this.locationIds.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + this.studyId;
		result = prime * result + (this.variableTypes == null ? 0 : this.variableTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataSet)) {
			return false;
		}
		final DataSet other = (DataSet) obj;
		return this.getId() == other.getId();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DataSet [id=");
		builder.append(this.id);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", studyId=");
		builder.append(this.studyId);
		builder.append(", variableTypes=");
		builder.append(this.variableTypes);
		builder.append("]");
		return builder.toString();
	}

	public DMSVariableType findVariableTypeByLocalName(final String localName) {
		for (final DMSVariableType variableType : this.variableTypes.getVariableTypes()) {
			if (variableType.getLocalName().equals(localName)) {
				return variableType;
			}
		}
		return null;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}
}
