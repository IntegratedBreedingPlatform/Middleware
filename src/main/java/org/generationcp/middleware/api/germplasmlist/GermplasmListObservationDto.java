package org.generationcp.middleware.api.germplasmlist;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListObservationDto extends GermplasmListObservationRequestDto {

	private Integer observationId;

	public Integer getObservationId() {
		return observationId;
	}

	public void setObservationId(final Integer observationId) {
		this.observationId = observationId;
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
