package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.Method;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class BreedingMethodDTO extends BreedingMethodNewRequest {

	private Integer mid;
	private Boolean isBulkingMethod;
	private Boolean isFavorite;

	public BreedingMethodDTO() {
	}

	public BreedingMethodDTO(final Method method) {
		this();
		this.setCode(method.getMcode());
		this.setName(method.getMname());
		this.setDescription(method.getMdesc());
		this.setType(method.getMtype());
		this.setGroup(method.getMgrp());
		this.setMethodClass(method.getGeneq());
		this.setNumberOfProgenitors(method.getMprgn());
		this.setIsBulkingMethod(method.isBulkingMethod());

		this.setSeparator(method.getSeparator());
		this.setPrefix(method.getPrefix());
		this.setCount(method.getCount());
		this.setSuffix(method.getSuffix());

		this.mid = method.getMid();
	}

	public Integer getMid() {
		return this.mid;
	}

	public void setMid(final Integer mid) {
		this.mid = mid;
	}

	public Boolean getIsBulkingMethod() {
		return this.isBulkingMethod;
	}

	public void setIsBulkingMethod(final Boolean isBulkingMethod) {
		this.isBulkingMethod = isBulkingMethod;
	}

	public Boolean getFavorite() {
		return isFavorite;
	}

	public void setFavorite(final Boolean favorite) {
		isFavorite = favorite;
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
}
