package org.generationcp.middleware.api.nametype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class NameTypeMetadata {

	private Long studiesCount;

	private Long germplasmCount;

	private Long germplasmListCount;

	public Long getStudiesCount() {
		return this.studiesCount;
	}

	public void setStudiesCount(final Long studiesCount) {
		this.studiesCount = studiesCount;
	}

	public Long getGermplasmCount() {
		return this.germplasmCount;
	}

	public void setGermplasmCount(final Long germplasmCount) {
		this.germplasmCount = germplasmCount;
	}

	public Long getGermplasmListCount() {
		return this.germplasmListCount;
	}

	public void setGermplasmListCount(final Long germplasmListCount) {
		this.germplasmListCount = germplasmListCount;
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
