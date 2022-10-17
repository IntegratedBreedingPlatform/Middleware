package org.generationcp.middleware.api.nametype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class NameTypeMetaData {

	private Long studies;

	private Long germplasm;

	private Long germplasmList;

	public Long getStudies() {
		return this.studies;
	}

	public void setStudies(final Long studies) {
		this.studies = studies;
	}

	public Long getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(final Long germplasm) {
		this.germplasm = germplasm;
	}

	public Long getGermplasmList() {
		return this.germplasmList;
	}

	public void setGermplasmList(final Long germplasmList) {
		this.germplasmList = germplasmList;
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

}
