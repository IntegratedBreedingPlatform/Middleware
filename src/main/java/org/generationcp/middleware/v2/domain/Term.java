package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Term {

	private int id;
	
	private String name;
	
	private String definition;

	public Term() { }
	
	public Term(int id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public void print(int indent) {
		Debug.println(indent, "Id: " + getId());
		Debug.println(indent, "Name: " + getName());
	    Debug.println(indent, "Definition: " + getDefinition());
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Term)) return false;
		Term other = (Term) obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Term [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", definition=");
		builder.append(definition);
		builder.append("]");
		return builder.toString();
	}
}
