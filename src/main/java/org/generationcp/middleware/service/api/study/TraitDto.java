
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
		if (!(other instanceof TraitDto)) {
			return false;
		}
		TraitDto castOther = (TraitDto) other;
		return new EqualsBuilder().append(this.traitId, castOther.traitId).append(this.traitName, castOther.traitName).isEquals();
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = new HashCodeBuilder().append(this.traitId).append(this.traitName).toHashCode();
		}
		return this.hashCode;
	}

}
