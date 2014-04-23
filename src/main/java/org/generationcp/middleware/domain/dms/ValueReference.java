/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
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

import java.io.Serializable;

public class ValueReference extends Reference implements Serializable, Comparable<ValueReference>{

	private static final long serialVersionUID = 1L;
	
	private String key;
	
	public ValueReference() {
		super();
	}
	
    public ValueReference(int id, String name) {
        super.setId(id);
        setKey(String.valueOf(id));
        super.setName(name);
    }

    public ValueReference(String key, String name) {
        setKey(key);
        super.setName(name);
    }

    public ValueReference(String key, String name, String description) {
        setKey(key);
        super.setName(name);
        super.setDescription(description);
    }

    public ValueReference(int id, String name, String description) {
        super.setId(id);
        setKey(String.valueOf(id));
        super.setName(name);
        super.setDescription(description);
    }

    @Override
    public int compareTo(ValueReference o) {
        return getName().compareTo(o.getName());
    }

	/**
	 * @return the code
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param code the code to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ValueReference [key=");
        builder.append(key);
        builder.append(", getId()=");
        builder.append(getId());
        builder.append(", getName()=");
        builder.append(getName());
        builder.append(", getDescription()=");
        builder.append(getDescription());
        builder.append("]");
        return builder.toString();
    }
	
	
}
