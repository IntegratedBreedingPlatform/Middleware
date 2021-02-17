package org.generationcp.middleware.api.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"coordinates", "type"})
public class Geometry {

	private List<Double> coordinates;

	private String type;

	public Geometry() {

	}

	private Geometry(final List<Double> coordinates, final String type) {
		this.coordinates = coordinates;
		this.type = type;
	}

	public List<Double> getCoordinates() {
		return this.coordinates;
	}

	public Geometry setCoordinates(final List<Double> coordinates) {
		this.coordinates = coordinates;
		return this;
	}

	public String getType() {
		return this.type;
	}

	public Geometry setType(final String type) {
		this.type = type;
		return this;
	}


}
