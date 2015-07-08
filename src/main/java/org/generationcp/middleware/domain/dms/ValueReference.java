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

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.oms.TermSummary;

import java.io.Serializable;

public class ValueReference extends Reference implements Serializable, Comparable<ValueReference> {

	private static final long serialVersionUID = 1L;

	private String key;

	public ValueReference() {
		super();
	}

	public ValueReference(int id, String name) {
		super.setId(id);
		this.setKey(String.valueOf(id));
		super.setName(name);
	}

	public ValueReference(String key, String name) {
		this.setKey(key);
		super.setName(name);
	}

	public ValueReference(String key, String name, String description) {
		this.setKey(key);
		super.setName(name);
		super.setDescription(description);
	}

	public ValueReference(int id, String name, String description) {
		super.setId(id);
		this.setKey(String.valueOf(id));
		super.setName(name);
		super.setDescription(description);
	}

	public ValueReference(TermSummary termSummary) {
		super.setId(termSummary.getId());
		this.setKey(termSummary.getId().toString());
		super.setName(termSummary.getName());
		super.setDescription(termSummary.getDefinition());
	}

	@Override
	public int compareTo(ValueReference o) {
		return this.getName().compareTo(o.getName());
	}

	/**
	 * @return the code
	 */
	 public String getKey() {
		return this.key;
	}

	/**
	 * @param key the key to set
	 */
	 public void setKey(String key) {
		 this.key = key;
	 }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValueReference [key=");
		builder.append(this.key);
		builder.append(", getId()=");
		builder.append(this.getId());
		builder.append(", getName()=");
		builder.append(this.getName());
		builder.append(", getDescription()=");
		builder.append(this.getDescription());
		builder.append("]");
		return builder.toString();
	}

}
