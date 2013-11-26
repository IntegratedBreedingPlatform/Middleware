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
package org.generationcp.middleware.domain.gms;

import java.io.Serializable;

import org.generationcp.middleware.util.Debug;

/**
 * Data Transfer Object for ListDataProperty POJO
 * 
 * @author Darla Ani
 *
 */
public class ListDataColumn implements Serializable {

	private static final long serialVersionUID = -8994156381257150996L;
	
	private Integer listDataColumnId;
	private String columnName;
	private String value;
	
	public ListDataColumn(Integer listDataColumnId, String columnName,
			String value) {
		super();
		this.listDataColumnId = listDataColumnId;
		this.columnName = columnName;
		this.value = value;
	}
		
	public Integer getListDataColumnId() {
		return listDataColumnId;
	}
	
	public void setListDataColumnId(Integer listDataColumnId) {
		this.listDataColumnId = listDataColumnId;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime
				* result
				+ ((listDataColumnId == null) ? 0 : listDataColumnId.hashCode());
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
		ListDataColumn other = (ListDataColumn) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (listDataColumnId == null) {
			if (other.listDataColumnId != null)
				return false;
		} else if (!listDataColumnId.equals(other.listDataColumnId))
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
		builder.append("ListDataColumn [id=");
		builder.append(listDataColumnId);
		builder.append(", columnName=");
		builder.append(columnName);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	public void print(int indent){
		Debug.println(indent, "Column:[id=" + listDataColumnId + ", name=" + columnName + ", value=" + value + "]");
	}

}
