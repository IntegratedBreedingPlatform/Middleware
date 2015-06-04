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

package org.generationcp.middleware.domain.gms;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.util.Debug;

/**
 * Data Transfer Object for specifying properties (columns) for GermplasmListData object
 *
 * @author Darla Ani
 *
 */
public class ListDataInfo implements Serializable {

	private static final long serialVersionUID = 3171213000673469267L;

	private Integer listDataId; // GermplasListData.id
	private List<ListDataColumn> columns;

	public ListDataInfo(Integer listDataId, List<ListDataColumn> columns) {
		super();
		this.listDataId = listDataId;
		this.columns = columns;
	}

	public Integer getListDataId() {
		return this.listDataId;
	}

	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
	}

	public List<ListDataColumn> getColumns() {
		return this.columns;
	}

	public void setColumns(List<ListDataColumn> columns) {
		this.columns = columns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.columns == null ? 0 : this.columns.hashCode());
		result = prime * result + (this.listDataId == null ? 0 : this.listDataId.hashCode());
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
		ListDataInfo other = (ListDataInfo) obj;
		if (this.columns == null) {
			if (other.columns != null) {
				return false;
			}
		} else if (!this.columns.equals(other.columns)) {
			return false;
		}
		if (this.listDataId == null) {
			if (other.listDataId != null) {
				return false;
			}
		} else if (!this.listDataId.equals(other.listDataId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListDataInfo [listDataId=");
		builder.append(this.listDataId);
		builder.append(", columns=");
		builder.append(this.columns);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "List Data : " + this.listDataId);
		if (this.columns != null) {
			Debug.println(indent + 3, "# of Columns: " + this.columns.size());
			for (ListDataColumn column : this.columns) {
				column.print(indent + 5);
			}
		}

	}

}
