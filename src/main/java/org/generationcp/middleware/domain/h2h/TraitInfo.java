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

package org.generationcp.middleware.domain.h2h;

import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a trait - name, id, description, number of locations, germplasms and observations.
 */
public class TraitInfo implements Comparable<TraitInfo> {

	private int id;

	private String name;

	private String property;

	private String description;

	private long locationCount;

	private long germplasmCount;

	private long observationCount;

	private TraitType type;

	private String scaleName;

	public TraitInfo() {
	}

	public TraitInfo(int id) {
		this.id = id;
	}

	public TraitInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public TraitInfo(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public TraitInfo(int id, String name, String description, String scaleName, Integer typeId) {
		this(id, name, description);
		this.scaleName = scaleName;
		this.type = TraitType.valueOf(typeId);
		this.description = description;
	}

	public TraitInfo(int id, String name, String property, String description, String scaleName, Integer typeId) {
		this(id, name, description);
		this.scaleName = scaleName;
		this.type = TraitType.valueOf(typeId);
		this.description = description;
		this.property = property;
	}

	public TraitInfo(int id, String name, String description, long locationCount, long germplasmCount, long observationCount) {
		this(id, name, description);
		this.locationCount = locationCount;
		this.germplasmCount = germplasmCount;
		this.observationCount = observationCount;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

	public long getLocationCount() {
		return this.locationCount;
	}

	public void setLocationCount(long locationCount) {
		this.locationCount = locationCount;
	}

	public long getGermplasmCount() {
		return this.germplasmCount;
	}

	public void setGermplasmCount(long germplasmCount) {
		this.germplasmCount = germplasmCount;
	}

	public void setType(TraitType type) {
		this.type = type;
	}

	public TraitType getType() {
		return this.type;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	public void setObservationCount(long observationCount) {
		this.observationCount = observationCount;
	}

	public long getObservationCount() {
		return this.observationCount;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + (int) (this.germplasmCount ^ this.germplasmCount >>> 32);
		result = prime * result + (int) (this.locationCount ^ this.locationCount >>> 32);
		result = prime * result + (int) (this.observationCount ^ this.observationCount >>> 32);
		result = prime * result + this.id;
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
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
		TraitInfo other = (TraitInfo) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TraitInfo [");
		builder.append("traitId=");
		builder.append(this.id);
		builder.append(", traitName=");
		builder.append(this.name);
		builder.append(", property=");
		builder.append(this.property);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", locationCount=");
		builder.append(this.locationCount);
		builder.append(", germplasmCount=");
		builder.append(this.germplasmCount);
		builder.append(", observationCount=");
		builder.append(this.observationCount);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", scaleName=");
		builder.append(this.scaleName);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, this.getEntityName() + ":");
		Debug.println(indent + 3, "Trait Id: " + this.getId());
		Debug.println(indent + 3, "Trait Name: " + this.getName());
		Debug.println(indent + 3, "Description: " + this.getDescription());
		Debug.println(indent + 3, "Location Count: " + this.getLocationCount());
		Debug.println(indent + 3, "Germplasm Count: " + this.getGermplasmCount());
		Debug.println(indent + 3, "Observation Count: " + this.getObservationCount());
		Debug.println(indent + 3, "Trait Type: " + this.getType());
		Debug.println(indent + 3, "Scale Name: " + this.getScaleName());
	}

	private String getEntityName() {
		return this.getClass().getName();
	}

	@Override
	// Sort in ascending order by trait id
	public int compareTo(TraitInfo compareValue) {
		int compareId = compareValue.getId();
		return Integer.valueOf(this.getId()).compareTo(compareId);
	}

}
