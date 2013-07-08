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

/** 
 * Contains the details of a variable type - local name, local description and rank.
 */
public class VariableType implements Comparable<VariableType>{
    
    private String localName;
    
    private String localDescription;
    
    private int rank;
    
    private StandardVariable standardVariable;
    
    public VariableType() { } 
    
    public VariableType(String localName, String localDescription, StandardVariable standardVariable, int rank) {
    	this.localName = localName;
    	this.localDescription = localDescription;
    	this.standardVariable = standardVariable;
    	this.rank = rank;
    }
    
    public int getId() {
    	return standardVariable.getId();
    }

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getLocalDescription() {
		return localDescription;
	}

	public void setLocalDescription(String localDescription) {
		this.localDescription = localDescription;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public StandardVariable getStandardVariable() {
		return standardVariable;
	}

	public void setStandardVariable(StandardVariable standardVariable) {
		this.standardVariable = standardVariable;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable Type: ");
		indent += 3;
		Debug.println(indent, "localName: " + localName);
		Debug.println(indent, "localDescription: "  + localDescription);
		Debug.println(indent, "rank: " + rank);
		standardVariable.print(indent);
	}
	
	public int hashCode() {
		return standardVariable.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof VariableType)) return false;
		VariableType other = (VariableType) obj;
		return other.getId() == getId();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableType [");		
		builder.append(", localName=");
		builder.append(localName);
		builder.append(", localDescription=");
		builder.append(localDescription);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", standardVariable=");
		builder.append(standardVariable);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	// Sort in ascending order by rank
	public int compareTo(VariableType compareValue) { 
        int compareRank = ((VariableType) compareValue).getRank(); 
        return Integer.valueOf(this.rank).compareTo(compareRank);
 	}

}
