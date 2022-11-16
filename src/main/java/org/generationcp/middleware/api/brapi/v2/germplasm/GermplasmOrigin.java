package org.generationcp.middleware.api.brapi.v2.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class GermplasmOrigin {

	private String coordinateUncertainty = "";

	private Map<String, Object> coordinates;

	public String getCoordinateUncertainty() {
		return this.coordinateUncertainty;
	}

	public void setCoordinateUncertainty(final String coordinateUncertainty) {
		this.coordinateUncertainty = coordinateUncertainty;
	}

	public Map<String, Object> getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(final Map<String, Object> coordinates) {
		this.coordinates = coordinates;
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
