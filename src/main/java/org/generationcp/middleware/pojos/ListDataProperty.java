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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.generationcp.middleware.util.Debug;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for listdataprop table
 *
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "listdataprops")
public class ListDataProperty implements Serializable {

	private static final long serialVersionUID = 2527090989063203962L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "listdataprop_id")
	private Integer listDataPropertyId;

	@ManyToOne(targetEntity = GermplasmListData.class)
	@JoinColumn(name = "listdata_id", nullable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private GermplasmListData listData;

	@Basic(optional = false)
	@Column(name = "column_name")
	private String column;

	@Column(name = "value")
	private String value;

	public ListDataProperty() {

	}

	public ListDataProperty(Integer listDataPropertyId, GermplasmListData listData, String column, String value) {
		super();
		this.listDataPropertyId = listDataPropertyId;
		this.listData = listData;
		this.column = column;
		this.value = value;
	}

	public ListDataProperty(GermplasmListData listData, String column, String value) {
		super();
		this.listData = listData;
		this.column = column;
		this.value = value;
	}

	public ListDataProperty(GermplasmListData listData, String column) {
		super();
		this.listData = listData;
		this.column = column;
	}

	public Integer getListDataPropertyId() {
		return this.listDataPropertyId;
	}

	public void setListDataPropertyId(Integer listDataPropertyId) {
		this.listDataPropertyId = listDataPropertyId;
	}

	public GermplasmListData getListData() {
		return this.listData;
	}

	public void setListData(GermplasmListData listData) {
		this.listData = listData;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.column == null ? 0 : this.column.hashCode());
		result = prime * result + (this.listData == null ? 0 : this.listData.hashCode());
		result = prime * result + (this.listDataPropertyId == null ? 0 : this.listDataPropertyId.hashCode());
		result = prime * result + (this.value == null ? 0 : this.value.hashCode());
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
		ListDataProperty other = (ListDataProperty) obj;
		if (this.column == null) {
			if (other.column != null) {
				return false;
			}
		} else if (!this.column.equals(other.column)) {
			return false;
		}
		if (this.listData == null) {
			if (other.listData != null) {
				return false;
			}
		} else if (!this.listData.equals(other.listData)) {
			return false;
		}
		if (this.listDataPropertyId == null) {
			if (other.listDataPropertyId != null) {
				return false;
			}
		} else if (!this.listDataPropertyId.equals(other.listDataPropertyId)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListDataProperty [listDataPropertyId=");
		builder.append(this.listDataPropertyId);
		builder.append(", listDataId=");
		builder.append(this.listData.getId());
		builder.append(", column=");
		builder.append(this.column);
		builder.append(", value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, this.toString());
	}

}
