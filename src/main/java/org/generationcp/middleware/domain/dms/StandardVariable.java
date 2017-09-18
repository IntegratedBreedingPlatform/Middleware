/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.util.Debug;

/**
 * The Standard Variable with term, property, scale, method, data type, etc.
 *
 */
public class StandardVariable implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Term term = new Term();

	private Term property;

	private Term scale;

	private Term method;

	private Term dataType;

	private Term isA;

	private PhenotypicType phenotypicType;

	private VariableConstraints constraints;

	private List<Enumeration> enumerations;

	private Map<Integer, Integer> overridenEnumerations;

	private String cropOntologyId;
	
	private Set<VariableType> variableTypes;

	public StandardVariable() {
	}

	public StandardVariable(Term property, Term scale, Term method, Term dataType, Term isA, PhenotypicType phenotypicType) {
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.isA = isA;
		this.phenotypicType = phenotypicType;
	}

	/* Copy constructor. Used by the copy method */
	private StandardVariable(StandardVariable stdVar) {
		this(stdVar.getProperty(), stdVar.getScale(), stdVar.getMethod(), stdVar.getDataType(), stdVar.getIsA(),
				stdVar.getPhenotypicType());
		this.setId(0);
		this.setName(stdVar.getName());
		this.setDescription(stdVar.getDescription());
		this.setCropOntologyId(stdVar.getCropOntologyId());
		this.setConstraints(stdVar.getConstraints());
		this.setEnumerations(stdVar.getEnumerations());
	}

	public int getId() {
		return this.term.getId();
	}

	public void setId(int id) {
		this.term.setId(id);
	}

	public String getName() {
		return this.term.getName();
	}

	public void setName(String name) {
		this.term.setName(name);
	}

	public String getDescription() {
		return this.term.getDefinition();
	}

	public void setDescription(String description) {
		this.term.setDefinition(description);
	}

	public boolean isObsolete() {
		return this.term.isObsolete();
	}

	public void setObsolete(boolean isObsolete) {
		this.term.setObsolete(isObsolete);
	}

	public Term getProperty() {
		return this.property;
	}

	public void setProperty(Term property) {
		this.property = property;
	}

	public Term getScale() {
		return this.scale;
	}

	public void setScale(Term scale) {
		this.scale = scale;
	}

	public Term getMethod() {
		return this.method;
	}

	public void setMethod(Term method) {
		this.method = method;
	}

	public Term getDataType() {
		return this.dataType;
	}

	public void setDataType(Term dataType) {
		this.dataType = dataType;
	}

	public VariableConstraints getConstraints() {
		return this.constraints;
	}

	public void setConstraints(VariableConstraints constraints) {
		this.constraints = constraints;
	}

	public List<Enumeration> getEnumerations() {
		return this.enumerations;
	}

	public Enumeration getEnumeration(Integer id) {
		if (this.enumerations == null) {
			return null;
		}
		for (Enumeration enumeration : this.enumerations) {
			if (enumeration.getId().equals(id)) {
				return enumeration;
			}
		}
		return null;
	}

	public Enumeration getEnumeration(String name, String description) {
		if (this.enumerations == null) {
			return null;
		}
		for (Enumeration enumeration : this.enumerations) {
			if (enumeration.getName().equalsIgnoreCase(name) && enumeration.getDescription().equalsIgnoreCase(description)) {
				return enumeration;
			}
		}
		return null;
	}

	public Enumeration getEnumerationByName(String name) {
		if (this.enumerations == null) {
			return null;
		}
		for (Enumeration enumeration : this.enumerations) {
			if (enumeration.getName().equalsIgnoreCase(name)) {
				return enumeration;
			}
		}
		return null;
	}

	public Enumeration getEnumerationByDescription(String description) {
		if (this.enumerations == null) {
			return null;
		}
		for (Enumeration enumeration : this.enumerations) {
			if (enumeration.getDescription().equalsIgnoreCase(description)) {
				return enumeration;
			}
		}
		return null;
	}

	public void setEnumerations(List<Enumeration> enumerations) {
		this.enumerations = enumerations;
	}

	public PhenotypicType getPhenotypicType() {
		return this.phenotypicType;
	}

	public void setPhenotypicType(PhenotypicType phenotypicType) {
		this.phenotypicType = phenotypicType;
	}

	public String getCropOntologyId() {
		return this.cropOntologyId;
	}

	public void setCropOntologyId(String cropOntologyId) {
		this.cropOntologyId = cropOntologyId;
	}

	public Enumeration findEnumerationByName(String name) {
		if (this.enumerations != null) {
			for (Enumeration enumeration : this.enumerations) {
				if (enumeration.getName().equals(name)) {
					return enumeration;
				}
			}
		}
		return null;
	}

	public Enumeration findEnumerationById(int id) {
		if (this.enumerations != null) {
			for (Enumeration enumeration : this.enumerations) {
				if (enumeration.getId()!=null && enumeration.getId() == id) {
					return enumeration;
				}
			}
		}
		return null;
	}

	public boolean hasEnumerations() {
		return this.enumerations != null && !this.enumerations.isEmpty();
	}

	public StandardVariable copy() {
		return new StandardVariable(this);
	}

	public void print(int previousIndent) {
		Debug.println(previousIndent, "Standard Variable: ");
		int indent = previousIndent + 3;
		Debug.println(indent, "term: " + this.term);
		Debug.println(indent, "property: " + this.property);
		Debug.println(indent, "method " + this.method);
		Debug.println(indent, "scale: " + this.scale);
		Debug.println(indent, "dataType: " + this.dataType);
		Debug.println(indent, "isA: " + this.isA);
		Debug.println(indent, "phenotypicType: " + this.phenotypicType);
		if (this.constraints != null) {
			Debug.println(indent, "constraints: ");
			this.constraints.print(indent + 3);
		}
		if (this.enumerations != null) {
			Debug.println(indent, "enumerations: " + this.enumerations);
		}
	}

	@Override
	public int hashCode() {
		return this.term.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof StandardVariable) {
			StandardVariable other = (StandardVariable) obj;
			return other.getId() == this.getId();
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StandardVariable [");
		builder.append("term=");
		builder.append(this.term);
		builder.append(", property=");
		builder.append(this.property);
		builder.append(", scale=");
		builder.append(this.scale);
		builder.append(", method=");
		builder.append(this.method);
		builder.append(", dataType=");
		builder.append(this.dataType);
		builder.append(", isA=");
		builder.append(this.isA);
		builder.append(", phenotypicType=");
		builder.append(this.phenotypicType);
		builder.append(", constraints=");
		builder.append(this.constraints);
		if (this.enumerations != null) {
			builder.append(", enumerations=");
			builder.append(this.enumerations);
		}
		builder.append(", cropOntologyId=");
		builder.append(this.cropOntologyId);
		builder.append("]");
		return builder.toString();
	}

	public Term getIsA() {
		return this.isA;
	}

	public void setIsA(Term isA) {
		this.isA = isA;
	}

	/**
	 * @return the overridenEnumerations
	 */
	public Map<Integer, Integer> getOverridenEnumerations() {
		return this.overridenEnumerations;
	}

	/**
	 * @param overridenEnumerations the overridenEnumerations to set
	 */
	public void setOverridenEnumerations(Map<Integer, Integer> overridenEnumerations) {
		this.overridenEnumerations = overridenEnumerations;
	}

	public boolean isNumeric() {
		if (this.dataType != null && this.dataType.getId() == TermId.NUMERIC_VARIABLE.getId()) {
			return true;
		} else if (this.isNumericCategoricalVariate()) {
			return true;
		}
		return false;
	}

	public boolean isNumericCategoricalVariate() {
		if (this.dataType != null && this.dataType.getId() == TermId.CATEGORICAL_VARIABLE.getId() && this.enumerations != null
				&& !this.enumerations.isEmpty()) {
			for (Enumeration enumeration : this.enumerations) {
				if (enumeration.getName() == null || !NumberUtils.isNumber(enumeration.getName().trim())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void setVariableTypes(Set<VariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}
	
	public Set<VariableType> getVariableTypes() {
		return this.variableTypes;
	}

}
