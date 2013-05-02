package org.generationcp.middleware.v2.domain;

import java.util.Set;

import org.generationcp.middleware.v2.util.Debug;

public class VariableType {

	private CvTerm term = new CvTerm();
	
	private CvTerm property;
    
    private CvTerm scale;
    
    private CvTerm method;
    
    private CvTerm dataType;
    
    private CvTerm storedIn;
    
    private String localName;
    
    private String localDescription;
    
    private Integer rank;
    
    private VariableConstraints constraints;  // may be null
    
    private Set<NameSynonym> nameSynonyms;

    public int getId() {
    	return term.getId();
    }
    
	public void setId(int id) {
		term.setId(id);
	}

	public String getName() {
		return term.getName();
	}

	public void setName(String name) {
		term.setName(name);
	}

	public String getDescription() {
		return term.getDescription();
	}

	public void setDescription(String description) {
		term.setDescription(description);
	}

	public CvTerm getProperty() {
		return property;
	}

	public void setProperty(CvTerm property) {
		this.property = property;
	}

	public CvTerm getScale() {
		return scale;
	}

	public void setScale(CvTerm scale) {
		this.scale = scale;
	}

	public CvTerm getMethod() {
		return method;
	}

	public void setMethod(CvTerm method) {
		this.method = method;
	}

	public CvTerm getDataType() {
		return dataType;
	}

	public void setDataType(CvTerm dataType) {
		this.dataType = dataType;
	}

	public CvTerm getStoredIn() {
		return storedIn;
	}

	public void setStoredIn(CvTerm storedIn) {
		this.storedIn = storedIn;
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

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable Type: ");
		indent += 3;
		//super.print(indent);
		Debug.println(indent, "localName: " + localName);
		Debug.println(indent, "localDescription: "  + localDescription);
		Debug.println(indent, "property: " + property);
		Debug.println(indent, "method " + method);
		Debug.println(indent, "scale: " + scale);
		Debug.println(indent, "storedIn: " + storedIn);
		if (this.constraints != null) {
			this.constraints.print(indent);
		}
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
		builder.append("VariableType [property=");
		builder.append(property);
		builder.append(", scale=");
		builder.append(scale);
		builder.append(", method=");
		builder.append(method);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", storedIn=");
		builder.append(storedIn);
		builder.append(", localName=");
		builder.append(localName);
		builder.append(", localDescription=");
		builder.append(localDescription);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", constraints=");
		builder.append(constraints);
		builder.append(", nameSynonyms=");
		builder.append(nameSynonyms);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
