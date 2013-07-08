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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	@Column(name =  "stockprop_id")
	private Integer stockPropId;
	
    @ManyToOne(targetEntity = StockModel.class)
    @JoinColumn(name = "stock_id", nullable = false)
	private StockModel stockModel;

    // References cvterm
    @Column(name="type_id")
    private Integer typeId;

	@Column(name = "value")
	private String value;

	@Basic(optional = false)
	@Column(name = "rank")
	private Integer rank;

	public Integer getStockPropId() {
		return stockPropId;
	}

	public void setStockPropId(Integer stockPropId) {
		this.stockPropId = stockPropId;
	}

	public StockModel getStock() {
		return stockModel;
	}

	public void setStock(StockModel stockModel) {
		this.stockModel = stockModel;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((stockModel == null) ? 0 : stockModel.hashCode());
		result = prime * result
				+ ((stockPropId == null) ? 0 : stockPropId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
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
		if (stockModel == null) {
			if (other.stockModel != null)
				return false;
		} else if (!stockModel.equals(other.stockModel))
			return false;
		if (stockPropId == null) {
			if (other.stockPropId != null)
				return false;
		} else if (!stockPropId.equals(other.stockPropId))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
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
		builder.append(stockModel);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", value=");
		builder.append(value);
		builder.append(", rank=");
		builder.append(rank);
		builder.append("]");
		return builder.toString();
	}
	
}