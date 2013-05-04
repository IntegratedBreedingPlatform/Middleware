package org.generationcp.middleware.v2.domain;

public class Enumeration implements Comparable<Enumeration> {

	private int id;
	
	private String name;
	
	private String description;
	
	private int rank;
	
	public Enumeration(int id, String name, String description, int rank) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.rank = rank;
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

	@Override
	public int compareTo(Enumeration other) {
		if (rank < other.rank) return -1;
		if (rank > other.rank) return 1;
		return name.compareTo(other.name);
	}
}
