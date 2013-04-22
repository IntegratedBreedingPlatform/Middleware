package org.generationcp.middleware.v2.domain;

import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class VariableType extends CvTerm {

    private Integer propertyId;
    
    private Integer scaleId;
    
    private Integer methodId;
    
    private Integer dataTypeId;
    
    private Integer storedInId;
    
    private String localName;
    
    private String localDescription;
    
    private VariableConstraints constraints;  // may be null
    
    private Set<NameSynonym> nameSynonyms;

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Integer getScaleId() {
		return scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public Integer getDataTypeId() {
		return dataTypeId;
	}

	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public Integer getStoredInId() {
		return storedInId;
	}

	public void setStoredInId(Integer storedInId) {
		this.storedInId = storedInId;
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

	public VariableConstraints getConstraints() {
		return constraints;
	}

	public void setConstraints(VariableConstraints constraints) {
		this.constraints = constraints;
	}

	public Set<NameSynonym> getNameSynonyms() {
		return nameSynonyms;
	}

	public void setNameSynonyms(Set<NameSynonym> nameSynonyms) {
		this.nameSynonyms = nameSynonyms;
	}

	public void print(int indent) {
		super.print(indent);
		Debug.println(indent, "localName: " + localName);
		Debug.println(indent, "localDescription: "  + localDescription);
		Debug.println(indent, "propertyId: " + propertyId);
		Debug.println(indent, "methodId: " + methodId);
		Debug.println(indent, "scaleId: " + scaleId);
		Debug.println(indent, "storedInId: " + storedInId);
		if (this.constraints != null) {
			this.constraints.print(indent);
		}
	}
}
