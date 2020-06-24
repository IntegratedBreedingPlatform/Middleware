
package org.generationcp.middleware.domain.gms;

import java.io.Serializable;

import org.generationcp.middleware.util.Debug;

public class ListDataColumnValues implements Serializable {

	private static final long serialVersionUID = 7758932229889246290L;

	private String column;
	private Integer listDataId;
	private String value;

	public ListDataColumnValues(final String column, final Integer listDataId, final String value) {
		super();
		this.column = column;
		this.listDataId = listDataId;
		this.value = value;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Integer getListDataId() {
		return this.listDataId;
	}

	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
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
		result = prime * result + (this.listDataId == null ? 0 : this.listDataId.hashCode());
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
		ListDataColumnValues other = (ListDataColumnValues) obj;
		if (this.column == null) {
			if (other.column != null) {
				return false;
			}
		} else if (!this.column.equals(other.column)) {
			return false;
		}
		if (this.listDataId == null) {
			if (other.listDataId != null) {
				return false;
			}
		} else if (!this.listDataId.equals(other.listDataId)) {
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
		builder.append("ListDataColumnValues [column=");
		builder.append(this.column);
		builder.append(", listDataId=");
		builder.append(this.listDataId);
		builder.append(", value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.print(indent, this.toString());
	}

}
