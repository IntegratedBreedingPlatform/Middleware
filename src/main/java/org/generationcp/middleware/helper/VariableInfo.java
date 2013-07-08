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
package org.generationcp.middleware.helper;

import org.generationcp.middleware.util.Debug;

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
