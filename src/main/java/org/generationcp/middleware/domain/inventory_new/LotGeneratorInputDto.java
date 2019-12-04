package org.generationcp.middleware.domain.inventory_new;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * Created by clarysabel on 11/13/19.
 */
@AutoProperty
public class LotGeneratorInputDto extends LotDto {

	private Boolean generateStock;

	private String stockPrefix;

	private Integer userId;

	public Boolean getGenerateStock() {
		return this.generateStock;
	}

	public void setGenerateStock(final Boolean generateStock) {
		this.generateStock = generateStock;
	}

	public String getStockPrefix() {
		return this.stockPrefix;
	}

	public void setStockPrefix(final String stockPrefix) {
		this.stockPrefix = stockPrefix;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
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
