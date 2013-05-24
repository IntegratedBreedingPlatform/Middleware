package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

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
