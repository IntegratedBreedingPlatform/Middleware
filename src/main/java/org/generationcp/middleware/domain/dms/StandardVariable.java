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

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.util.Debug;

/**
 * The Standard Variable with term, property, scale, method, data type, etc.
 *
 */
public class StandardVariable {

	private Term term = new Term();
	
	private Term property;
    
    private Term scale;
    
    private Term method;
    
    private Term dataType;
    
    private Term storedIn;
    
    private Term isA;
    
    private PhenotypicType phenotypicType;
    
    private VariableConstraints constraints;  // may be null
    
    private List<Enumeration> enumerations;
    
    private Map<Integer, Integer> overridenEnumerations;
    
    private String cropOntologyId;
    
    public StandardVariable() {    	
    }
    
	public StandardVariable(Term property, Term scale, Term method,
			Term dataType, Term storedIn, Term isA, PhenotypicType phenotypicType,
			VariableConstraints constraints,
			List<Enumeration> enumerations) {
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.storedIn = storedIn;
		this.isA = isA;
		this.phenotypicType = phenotypicType;
		this.constraints = constraints;
		this.enumerations = enumerations;
	}

    /* Copy constructor. Used by the copy method */
    private StandardVariable(StandardVariable stdVar) {
    	this(stdVar.getProperty(), stdVar.getScale(), stdVar.getMethod(),
			stdVar.getDataType(), stdVar.getStoredIn(), stdVar.getIsA(), 
			stdVar.getPhenotypicType(), stdVar.getConstraints(),
			stdVar.getEnumerations());
    	this.setId(0);  
    	this.setName(stdVar.getName());
    	this.setDescription(stdVar.getDescription());
    	this.setCropOntologyId(stdVar.getCropOntologyId());
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
	
    public Enumeration getEnumeration(Integer id){
        if (enumerations == null){
            return null;
        }
        for (Enumeration enumeration : enumerations){
            if (enumeration.getId().equals(id)){
                return enumeration;
            }
        }
        return null;
    }
    
    public Enumeration getEnumeration(String name, String description){
        if (enumerations == null){
            return null;
        }
        for (Enumeration enumeration : enumerations){
            if (enumeration.getName().equalsIgnoreCase(name) && enumeration.getDescription().equalsIgnoreCase(description)){
                return enumeration;
            }
        }
        return null;
    }
    
    public Enumeration getEnumerationByName(String name){
        if (enumerations == null){
            return null;
        }
        for (Enumeration enumeration : enumerations){
            if (enumeration.getName().equalsIgnoreCase(name) ){
                return enumeration;
            }
        }
        return null;
    }
    
    public Enumeration getEnumerationByDescription(String description){
        if (enumerations == null){
            return null;
        }
        for (Enumeration enumeration : enumerations){
            if (enumeration.getDescription().equalsIgnoreCase(description)){
                return enumeration;
            }
        }
        return null;
    }


	public void setEnumerations(List<Enumeration> enumerations) {
		this.enumerations = enumerations;
	}
	
	public PhenotypicType getPhenotypicType() {
		return phenotypicType;
	}

	public void setPhenotypicType(PhenotypicType phenotypicType) {
		this.phenotypicType = phenotypicType;
	}

    public String getCropOntologyId() {
        return cropOntologyId;
    }
    
    public void setCropOntologyId(String cropOntologyId) {
        this.cropOntologyId = cropOntologyId;
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
        Debug.println(indent, "dataType: " + dataType);
		Debug.println(indent, "isA: " + isA);
		Debug.println(indent, "phenotypicType: " + phenotypicType);
		if (constraints != null) {
	        Debug.println(indent, "constraints: ");
			constraints.print(indent + 3);
		}
		if (enumerations != null) {
			Debug.println(indent, "enumerations: " + enumerations);
		}
	}
	
	public int hashCode() {
		return term.getId();
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
		builder.append(", isA=");
		builder.append(isA);
		builder.append(", phenotypicType=");
		builder.append(phenotypicType);
		builder.append(", constraints=");
		builder.append(constraints);
		if (enumerations != null) {
			builder.append(", enumerations=");
		    builder.append(enumerations);
		}
        builder.append(", cropOntologyId=");
        builder.append(cropOntologyId);
		builder.append("]");
		return builder.toString();
	}

	public Term getIsA() {
		return isA;
	}

	public void setIsA(Term isA) {
		this.isA = isA;
	}

	/**
	 * @return the overridenEnumerations
	 */
	public Map<Integer, Integer> getOverridenEnumerations() {
		return overridenEnumerations;
	}

	/**
	 * @param overridenEnumerations the overridenEnumerations to set
	 */
	public void setOverridenEnumerations(Map<Integer, Integer> overridenEnumerations) {
		this.overridenEnumerations = overridenEnumerations;
	}
	
	
}
