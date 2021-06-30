package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmOtherProgenitorsAuditDTO extends AbstractAuditDTO {

	private Integer progenitorGid;
	private Integer progenitorsNumber;

	public GermplasmOtherProgenitorsAuditDTO() {
	}

	public Integer getProgenitorGid() {
		return progenitorGid;
	}

	public void setProgenitorGid(final Integer progenitorGid) {
		this.progenitorGid = progenitorGid;
	}

	public Integer getProgenitorsNumber() {
		return progenitorsNumber;
	}

	public void setProgenitorsNumber(final Integer progenitorsNumber) {
		this.progenitorsNumber = progenitorsNumber;
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
