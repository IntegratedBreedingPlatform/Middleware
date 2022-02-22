/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import com.google.common.base.Preconditions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_stockprop
 *
 * A stock can have any number of slot-value property tags attached to it. This is an alternative to hardcoding a list of columns in the
 * relational schema, and is completely extensible. There is a unique constraint, stockprop_c1, for the combination of stock_id, rank, and
 * type_id. Multivalued property-value pairs must be differentiated by rank.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "stockprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"stock_id", "type_id"})})
public class StockProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "stockprop_id")
	private Integer stockPropId;

	@ManyToOne(targetEntity = StockModel.class)
	@JoinColumn(name = "stock_id", nullable = false)
	private StockModel stockModel;

	// References cvterm
	@Column(name = "type_id")
	private Integer typeId;

	@Column(name = "value")
	private String value;

	@Column(name = "cvalue_id")
	private Integer categoricalValueId;

	private StockProperty() {

	}

	public StockProperty (final StockModel stockModel, final Integer variableId, final String value, final Integer categoricalValueId) {
		Preconditions.checkArgument(!(value == null && categoricalValueId == null), "stock property not have set a value nor a categorical value");

		this.setStock(stockModel);
		this.setTypeId(variableId);
		this.setValue(value);
		this.setCategoricalValueId(categoricalValueId);
	}

	public Integer getStockPropId() {
		return this.stockPropId;
	}

	public void setStockPropId(Integer stockPropId) {
		this.stockPropId = stockPropId;
	}

	public StockModel getStock() {
		return this.stockModel;
	}

	public void setStock(StockModel stockModel) {
		this.stockModel = stockModel;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	// TODO: review property name
	public String getPropertyValue() {
		if (this.value == null) {
			return String.valueOf(this.categoricalValueId);
		}
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.stockModel == null ? 0 : this.stockModel.hashCode());
		result = prime * result + (this.stockPropId == null ? 0 : this.stockPropId.hashCode());
		result = prime * result + (this.typeId == null ? 0 : this.typeId.hashCode());
		result = prime * result + (this.value == null ? 0 : this.value.hashCode());
		result = prime * result + (this.categoricalValueId == null ? 0 : this.categoricalValueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		StockProperty other = (StockProperty) obj;
		if (this.stockModel == null) {
			if (other.stockModel != null) {
				return false;
			}
		} else if (!this.stockModel.equals(other.stockModel)) {
			return false;
		}
		if (this.stockPropId == null) {
			if (other.stockPropId != null) {
				return false;
			}
		} else if (!this.stockPropId.equals(other.stockPropId)) {
			return false;
		}
		if (this.typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!this.typeId.equals(other.typeId)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		if (this.categoricalValueId == null) {
			if (other.categoricalValueId != null) {
				return false;
			}
		} else if (!this.categoricalValueId.equals(other.categoricalValueId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockProperties [stockPropId=");
		builder.append(this.stockPropId);
		builder.append(", stock=");
		builder.append(this.stockModel);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}

}
