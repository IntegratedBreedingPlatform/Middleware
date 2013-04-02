/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_stockprop
 * 
 * A stock can have any number of slot-value property tags attached to it. 
 * This is an alternative to hardcoding a list of columns in the relational schema, 
 * and is completely extensible. There is a unique constraint, stockprop_c1, 
 * for the combination of stock_id, rank, and type_id. 
 * Multivalued property-value pairs must be differentiated by rank.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "stockprop", 
		uniqueConstraints = {
			@UniqueConstraint(columnNames = { "stock_id", "type_id", "rank" })
		})
public class StockProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name =  "stockprop_id")
	private Long stockPropId;
	
    @OneToOne
    @JoinColumn(name = "stock_id")
	private Stock stock;

    
    @OneToOne
    @JoinColumn(name="type_id", referencedColumnName="cvterm_id")
    private CVTerm type;

	@Column(name = "value")
	private String value;


	@Basic(optional = false)
	@Column(name = "rank")
	private Long rank;


	public Long getStockPropId() {
		return stockPropId;
	}


	public void setStockPropId(Long stockPropId) {
		this.stockPropId = stockPropId;
	}


	public Stock getStock() {
		return stock;
	}


	public void setStock(Stock stock) {
		this.stock = stock;
	}


	public CVTerm getType() {
		return type;
	}


	public void setType(CVTerm type) {
		this.type = type;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public Long getRank() {
		return rank;
	}


	public void setRank(Long rank) {
		this.rank = rank;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		result = prime * result
				+ ((stockPropId == null) ? 0 : stockPropId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockProperty other = (StockProperty) obj;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		if (stockPropId == null) {
			if (other.stockPropId != null)
				return false;
		} else if (!stockPropId.equals(other.stockPropId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockProperties [stockPropId=");
		builder.append(stockPropId);
		builder.append(", stock=");
		builder.append(stock);
		builder.append(", type=");
		builder.append(type);
		builder.append(", value=");
		builder.append(value);
		builder.append(", rank=");
		builder.append(rank);
		builder.append("]");
		return builder.toString();
	}

	
	
	
}