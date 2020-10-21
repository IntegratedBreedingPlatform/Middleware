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

import java.io.Serializable;

import org.generationcp.middleware.util.Debug;

/**
 * Used to contain sortable list of values.
 */
public class Enumeration implements Serializable, Comparable<Enumeration> {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String name;

	private String description;

	private int rank;

	public Enumeration(Integer id, String name, String description, int rank) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.rank = rank;
	}

	public Enumeration() {

	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enumeration [id=");
		builder.append(this.id);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", rank=");
		builder.append(this.rank);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Enumeration: ");
		indent += 3;
		Debug.println(indent, "id: " + this.id);
		Debug.println(indent, "name: " + this.name);
		Debug.println(indent, "description " + this.description);
		Debug.println(indent, "rank: " + this.rank);
	}

	@Override
	public int compareTo(Enumeration other) {
		if (this.rank < other.rank) {
			return -1;
		}
		if (this.rank > other.rank) {
			return 1;
		}
		return this.name.compareTo(other.name);
	}
}
