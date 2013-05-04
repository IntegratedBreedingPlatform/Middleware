package org.generationcp.middleware.v2.helper;

import org.generationcp.middleware.v2.util.Debug;

public class VariableInfo {

	private String localName;
	
	private String localDescription;
	
	private int rank;
	
	private int stdVariableId;

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

	public int getStdVariableId() {
		return stdVariableId;
	}

	public void setStdVariableId(int stdVariableId) {
		this.stdVariableId = stdVariableId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void print(int indent) {
		Debug.println(indent, "VariableInfo:" );
		Debug.println(indent + 3, "stdVariableId: " + stdVariableId);
		Debug.println(indent + 3, "localName: " + localName);
		Debug.println(indent + 3, "localDescription: " + localDescription);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableInfo [localName=");
		builder.append(localName);
		builder.append(", localDescription=");
		builder.append(localDescription);
		builder.append(", stdVariableId=");
		builder.append(stdVariableId);
		builder.append("]");
		return builder.toString();
	}
	
	
}
