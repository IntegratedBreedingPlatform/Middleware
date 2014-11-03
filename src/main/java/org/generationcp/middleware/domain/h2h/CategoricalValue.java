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

package org.generationcp.middleware.domain.h2h;

import org.generationcp.middleware.util.Debug;


/**
 * Used to contain the categorical values found in phenotype table together with
 * the frequency of occurrence of the value.
 * 
 */
public class CategoricalValue{
    
    private int id;
    
    private String name;
    
    private long count;
    
    public CategoricalValue() {
    }

    public CategoricalValue(int id) {
        this.id = id;
    }
    
    public CategoricalValue(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoricalValue(int id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

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
    
    public long getCount() {
        return count;
    }
    
    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        CategoricalValue other = (CategoricalValue) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CategoricalValue [");
        builder.append("id=");
        builder.append(id);
        builder.append(", ");
        builder.append("name=");
        builder.append(name);
        builder.append(", count=");
        builder.append(count);
        builder.append("]");
        return builder.toString();
    }
    
    public void print(int indent){
        Debug.println(indent, "Value Id: " + getId());
        Debug.println(indent, "Value Name: " + getName());
        Debug.println(indent, "Value Count: " + getCount());
        Debug.println(indent, "----");
    }
    
    
}