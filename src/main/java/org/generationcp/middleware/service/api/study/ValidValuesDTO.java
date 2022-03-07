package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.generationcp.middleware.util.serializer.ScaleCategorySerializer;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class ValidValuesDTO {

	@JsonSerialize(using = ScaleCategorySerializer.class)
	private List<ScaleCategoryDTO> categories = new ArrayList<>();
	private Integer max;
	private Integer min;

	// Getter Methods

	public Integer getMax() {
		return this.max;
	}

	public Integer getMin() {
		return this.min;
	}

	// Setter Methods
	public void setMax(final Integer max) {
		this.max = max;
	}

	public void setMin(final Integer min) {
		this.min = min;
	}

	public List<ScaleCategoryDTO> getCategories() {
		return this.categories;
	}

	public void setCategories(final List<ScaleCategoryDTO> categories) {
		this.categories = categories;
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
