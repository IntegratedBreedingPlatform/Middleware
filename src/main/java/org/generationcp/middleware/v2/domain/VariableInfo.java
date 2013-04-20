package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class VariableInfo {

	private String localName;
	
	private String localDescription;
	
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

	public void print(int index) {
		Debug.println(index, "stdVariableId: " + stdVariableId);
		Debug.println(index, "localName: " + localName);
		Debug.println(index, "localDescription: " + localDescription);
	}
}
