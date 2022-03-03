package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.generationcp.middleware.util.serializer.ScaleCategorySerializer;

import java.util.ArrayList;
import java.util.List;

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
}
