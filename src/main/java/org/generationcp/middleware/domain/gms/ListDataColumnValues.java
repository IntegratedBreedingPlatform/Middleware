package org.generationcp.middleware.domain.gms;

import java.io.Serializable;

import org.generationcp.middleware.util.Debug;

public class ListDataColumnValues implements Serializable {

	private static final long serialVersionUID = 7758932229889246290L;
	
	private String column;
	private Integer listDataId;
	private String value;
	
	
	public ListDataColumnValues(String column, Integer listDataId, String value) {
		super();
		this.column = column;
		this.listDataId = listDataId;
		this.value = value;
	}

	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public Integer getListDataId() {
		return listDataId;
	}
	
	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
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
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((listDataId == null) ? 0 : listDataId.hashCode());
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
		ListDataColumnValues other = (ListDataColumnValues) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (listDataId == null) {
			if (other.listDataId != null)
				return false;
		} else if (!listDataId.equals(other.listDataId))
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
		builder.append("ListDataColumnValues [column=");
		builder.append(column);
		builder.append(", listDataId=");
		builder.append(listDataId);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	public void print(int indent){
		Debug.print(indent, toString());
	}
	
	

}
