/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.oms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the primary details of a trait class - id, name, description, plus the list of properties
 *
 * @author Joyce Avestro
 *
 */
public class TraitClassReference extends Reference implements Serializable, Comparable<TraitClassReference> {

	private static final long serialVersionUID = 1L;

	private int parentTraitClassId; // Either TermId.ONTOLOGY_TRAIT_CLASS or TermId.ONTOLOGY_RESEARCH_CLASS for first-level class

	private List<TraitClassReference> traitClassChildren;

	private List<PropertyReference> properties;

	public TraitClassReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
		this.properties = new ArrayList<>();
	}

	public TraitClassReference(Integer id, String name, String description) {
		this(id, name);
		super.setDescription(description);
	}

	public TraitClassReference(Integer id, String name, String description, int parentTraitClassId) {
		this(id, name);
		super.setDescription(description);
		this.setParentTraitClassId(parentTraitClassId);
	}

	/**
	 * @return the properties
	 */
	public List<PropertyReference> getProperties() {
		return this.properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<PropertyReference> properties) {
		this.properties = properties;
	}

	/**
	 * @return the parentTraitClassId
	 */
	public int getParentTraitClassId() {
		return this.parentTraitClassId;
	}

	/**
	 * @param parentTraitClassId the parent trait class ID to set
	 */
	public void setParentTraitClassId(int parentTraitClassId) {
		this.parentTraitClassId = parentTraitClassId;
	}

	/**
	 * @return the traitClassChildren
	 */
	public List<TraitClassReference> getTraitClassChildren() {
		return this.traitClassChildren;
	}

	/**
	 * @param traitClassChildren the traitClassChildren to set
	 */
	public void setTraitClassChildren(List<TraitClassReference> traitClassChildren) {
		this.traitClassChildren = traitClassChildren;
	}

	public void addTraitClassChild(TraitClassReference traitClassChild) {
		if (this.traitClassChildren == null) {
			this.traitClassChildren = new ArrayList<>();
		}
		this.traitClassChildren.add(traitClassChild);
	}

	public void sortTraitClassChildren() {
		Collections.sort(this.traitClassChildren);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TraitReference [id=");
		builder.append(this.getId());
		builder.append(", name=");
		builder.append(this.getName());
		builder.append(", description=");
		builder.append(this.getDescription());
		builder.append(", parentTraitClassId=");
		builder.append(this.getParentTraitClassId());
		builder.append(", traitClassChildren=");
		builder.append(this.traitClassChildren);
		builder.append(", properties=");
		builder.append(this.properties);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void print(int indent) {
		Debug.println(indent, "TraitReference: ");
		Debug.println(indent + 3, "Id: " + this.getId());
		Debug.println(indent + 3, "Name: " + this.getName());
		Debug.println(indent + 3, "Description: " + this.getDescription());
		Debug.println(indent + 3, "Parent Class ID: " + this.getParentTraitClassId());

		Debug.println(indent + 3, "Trait Class Children: ");
		if (this.traitClassChildren != null && !this.traitClassChildren.isEmpty()) {
			for (TraitClassReference child : this.traitClassChildren) {
				child.print(indent + 6);
			}
		}

		Debug.println(indent + 3, "Properties: ");
		for (PropertyReference property : this.properties) {
			property.print(indent + 6);
		}
		if (this.properties.isEmpty()) {
			Debug.println(indent + 6, "None");
		}
	}

	/*
	 * (non-Javadoc) Sort in ascending order by trait group name
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TraitClassReference compareValue) {
		String compareName = compareValue.getName();
		return this.getName().compareToIgnoreCase(compareName);
	}
}
