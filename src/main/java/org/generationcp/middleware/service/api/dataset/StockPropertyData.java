package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class StockPropertyData {

	private Integer stockPropertyId;
	private Integer variableId;
	private String value;
	private Integer categoricalValueId;

	public StockPropertyData() {
	}

	public StockPropertyData(final Integer stockPropertyId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.stockPropertyId = stockPropertyId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public Integer getStockPropertyId() {
		return stockPropertyId;
	}

	public void setStockPropertyId(final Integer stockPropertyId) {
		this.stockPropertyId = stockPropertyId;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
