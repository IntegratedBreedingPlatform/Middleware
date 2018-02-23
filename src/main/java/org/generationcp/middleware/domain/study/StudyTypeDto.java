package org.generationcp.middleware.domain.study;

import org.pojomatic.Pojomatic;

import java.io.Serializable;

public class StudyTypeDto implements Serializable, Comparable<StudyTypeDto> {

	private Integer id;
	private String label;
	private String name;

	public StudyTypeDto(final Integer id, final String label, final String name){
			this.setId(id);
			this.setLabel(label);
			this.setName(name);
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int compareTo(final StudyTypeDto o) {
		int compareId = o.getId();
		return Integer.valueOf(this.getId()).compareTo(compareId);
	}
}
