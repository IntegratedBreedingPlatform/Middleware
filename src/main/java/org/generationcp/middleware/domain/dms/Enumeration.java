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

import org.generationcp.middleware.util.Debug;

import java.io.Serializable;

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
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Enumeration [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", rank=");
        builder.append(rank);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent) {
        Debug.println(indent, "Enumeration: ");
        indent += 3;
        Debug.println(indent, "id: " + id);
        Debug.println(indent, "name: " + name);
        Debug.println(indent, "description " + description);
        Debug.println(indent, "rank: " + rank);
    }

	@Override
	public int compareTo(Enumeration other) {
		if (rank < other.rank) return -1;
		if (rank > other.rank) return 1;
		return name.compareTo(other.name);
	}
}
