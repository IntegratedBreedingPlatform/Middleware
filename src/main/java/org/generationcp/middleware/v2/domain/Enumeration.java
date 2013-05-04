package org.generationcp.middleware.v2.domain;

public class Enumeration {

	private int id;
	
	private String name;
	
	private String description;
	
	public Enumeration(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return "[" + id + ":" + name + "]";
	}
}
