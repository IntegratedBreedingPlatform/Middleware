package org.generationcp.middleware.v2.domain;

import java.util.List;

import org.generationcp.middleware.v2.util.Debug;

public class StandardVariable {

	private Term term = new Term();
	
	private Term property;
    
    private Term scale;
    
    private Term method;
    
    private Term dataType;
    
    private Term storedIn;
    
    private FactorType factorType;
    
    private VariableConstraints constraints;  // may be null
    
    private List<Enumeration> enumerations;
    
    public StandardVariable() {    	
    }
    
	public StandardVariable(Term property, Term scale, Term method,
			Term dataType, Term storedIn, FactorType factorType,
			VariableConstraints constraints,
			List<Enumeration> enumerations) {
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.storedIn = storedIn;
		this.factorType = factorType;
		this.constraints = constraints;
		this.enumerations = enumerations;
	}

    /* Copy constructor. Used by the copy method */
    private StandardVariable(StandardVariable stdVar) {
    	this(stdVar.getProperty(), stdVar.getScale(), stdVar.getMethod(),
			stdVar.getDataType(), stdVar.getStoredIn(), stdVar.getFactorType(), stdVar.getConstraints(),
			stdVar.getEnumerations());
    	this.setId(0);  
    	this.setName(stdVar.getName());
    	this.setDescription(stdVar.getDescription());
	}
    
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
		return term.getDefinition();
	}

	public void setDescription(String description) {
		term.setDefinition(description);
	}

	public Term getProperty() {
		return property;
	}

	public void setProperty(Term property) {
		this.property = property;
	}

	public Term getScale() {
		return scale;
	}

	public void setScale(Term scale) {
		this.scale = scale;
	}

	public Term getMethod() {
		return method;
	}

	public void setMethod(Term method) {
		this.method = method;
	}

	public Term getDataType() {
		return dataType;
	}

	public void setDataType(Term dataType) {
		this.dataType = dataType;
	}

	public Term getStoredIn() {
		return storedIn;
	}

	public void setStoredIn(Term storedIn) {
		this.storedIn = storedIn;
	}

	public VariableConstraints getConstraints() {
		return constraints;
	}

	public void setConstraints(VariableConstraints constraints) {
		this.constraints = constraints;
	}

	public List<Enumeration> getEnumerations() {
		return enumerations;
	}

	public void setEnumerations(List<Enumeration> enumerations) {
		this.enumerations = enumerations;
	}
	
	public FactorType getFactorType() {
		return factorType;
	}

	public void setFactorType(FactorType factorType) {
		this.factorType = factorType;
	}

	public Enumeration findEnumerationByName(String name) {
		if (enumerations != null) {
			for (Enumeration enumeration : enumerations) {
				if (enumeration.getName().equals(name)) {
					return enumeration;
				}
			}
		}
		return null;
	}
	
	public Enumeration findEnumerationById(int id) {
		if (enumerations != null) {
			for (Enumeration enumeration : enumerations) {
				if (enumeration.getId() == id) {
					return enumeration;
				}
			}
		}
		return null;
	}
	
	public boolean hasEnumerations() {
		return (enumerations != null && enumerations.size() > 0);
	}
	
	public StandardVariable copy() {
		return new StandardVariable(this);
	}
	
	public List<NameSynonym> getNameSynonyms() {
		return term.getNameSynonyms();
	}
	
	public void setNameSynonyms(List<NameSynonym> nameSynonyms) {
		this.term.setNameSynonyms(nameSynonyms);
	}

	public void print(int indent) {
		Debug.println(indent, "Standard Variable: ");
		indent += 3;
		Debug.println(indent, "term: " + term);
		Debug.println(indent, "property: " + property);
		Debug.println(indent, "method " + method);
		Debug.println(indent, "scale: " + scale);
		Debug.println(indent, "storedIn: " + storedIn);
		Debug.println(indent, "factorType: " + factorType);
		if (constraints != null) {
			Debug.println(indent, "constraints: " + constraints);
		}
		if (this.constraints != null) {
			this.constraints.print(indent);
		}
		if (enumerations != null) {
			Debug.println(indent, "enumerations: " + enumerations);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof StandardVariable)) return false;
		StandardVariable other = (StandardVariable) obj;
		return other.getId() == getId();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StandardVariable [");
		builder.append("term=");
		builder.append(term);
		builder.append(", property=");
		builder.append(property);
		builder.append(", scale=");
		builder.append(scale);
		builder.append(", method=");
		builder.append(method);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", storedIn=");
		builder.append(storedIn);
		builder.append(", constraints=");
		builder.append(constraints);
		if (enumerations != null) {
			builder.append(", enumerations=");
		    builder.append(enumerations);
		}
		builder.append("]");
		return builder.toString();
	}
}
