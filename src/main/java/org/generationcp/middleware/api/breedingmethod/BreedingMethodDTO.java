package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.Method;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BreedingMethodDTO {

	private String code;
	private String name;
	private String description;
	private String type;
	private String group;
	private Integer methodClass;
	private Integer mid;
	private Integer numberOfProgenitors;

	public BreedingMethodDTO() {
	}

	public BreedingMethodDTO(final Method method) {
		this();
		this.code = method.getMcode();
		this.name = method.getMname();
		this.description = method.getMdesc();
		this.type = method.getMtype();
		this.group = method.getMgrp();
		this.methodClass = method.getGeneq();
		this.numberOfProgenitors = method.getMprgn();
		this.mid = method.getMid();
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}

	public Integer getMethodClass() {
		return this.methodClass;
	}

	public void setMethodClass(final Integer methodClass) {
		this.methodClass = methodClass;
	}

	public Integer getNumberOfProgenitors() {
		return numberOfProgenitors;
	}

	public void setNumberOfProgenitors(final Integer numberOfProgenitors) {
		this.numberOfProgenitors = numberOfProgenitors;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	public Integer getMid() {
		return this.mid;
	}

	public void setMid(final Integer mid) {
		this.mid = mid;
	}
}
