
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TraitDto {

	private final Integer traitId;

	private final String traitName;

	private transient int hashCode;

	public TraitDto(final Integer traitId, final String traitName) {
		this.traitId = traitId;
		this.traitName = traitName;
	}

	public Integer getTraitId() {
		return this.traitId;
	}

	public String getTraitName() {
		return this.traitName;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof TraitDto))
			return false;
		TraitDto castOther = (TraitDto) other;
		return new EqualsBuilder().append(traitId, castOther.traitId).append(traitName, castOther.traitName).isEquals();
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(traitId).append(traitName).toHashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return "TraitDto [traitId=" + traitId + ", traitName=" + traitName + "]";
	}
}
