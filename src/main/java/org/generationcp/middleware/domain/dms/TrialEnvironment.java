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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the details of a trial environment - id and variables.
 */
public class TrialEnvironment {

	private final int id;
	private VariableList variables;
	private LocationDto location;
	private List<TraitInfo> traits;
	private StudyReference study;

	public TrialEnvironment(int id) {
		this.id = id;
	}

	public TrialEnvironment(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}

	public TrialEnvironment(int id, LocationDto location, StudyReference study) {
		this.id = id;
		this.location = location;
		this.study = study;
	}

	public TrialEnvironment(int id, LocationDto location, List<TraitInfo> traits, StudyReference study) {
		this.id = id;
		this.traits = traits;
		this.location = location;
		this.study = study;
		this.variables = null;
	}

	public int getId() {
		return this.id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return this.variables.containsValueByLocalName(localName, value);
	}

	public VariableList getVariables() {
		return this.variables;
	}

	public List<TraitInfo> getTraits() {
		return this.traits;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public LocationDto getLocation() {
		return this.location;
	}

	public StudyReference getStudy() {
		return this.study;
	}

	public void setTraits(List<TraitInfo> traits) {
		this.traits = traits;
	}

	public void addTrait(TraitInfo trait) {
		if (this.traits == null) {
			this.traits = new ArrayList<>();
		}
		this.traits.add(trait);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
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
		TrialEnvironment other = (TrialEnvironment) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrialEnvironment [id=");
		builder.append(this.id);
		builder.append(", variables=");
		builder.append(this.variables);
		builder.append(", location=");
		builder.append(this.location);
		builder.append(", traits=");
		builder.append(this.traits);
		builder.append(", study=");
		builder.append(this.study);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Trial Environment: " + this.id);
		if (this.variables != null) {
			this.variables.print(indent + 3);
		}
		if (this.location != null) {
			this.location.print(indent + 3);
		}
		if (this.traits != null) {
			Debug.println(indent + 3, "Traits: " + this.traits.size());
			for (TraitInfo trait : this.traits) {
				trait.print(indent + 6);
			}
		}
		if (this.study != null) {
			this.study.print(indent + 3);
		}

	}
}
