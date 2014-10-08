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

import org.generationcp.middleware.util.Debug;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object for specifying properties (columns) for
 * GermplasmListData object
 * 
 * @author Darla Ani
 *
 */
public class ListDataInfo implements Serializable {

	private static final long serialVersionUID = 3171213000673469267L;
	
	private Integer listDataId; //GermplasListData.id
	private List<ListDataColumn> columns;
	
	public ListDataInfo(Integer listDataId, List<ListDataColumn> columns) {
		super();
		this.listDataId = listDataId;
		this.columns = columns;
	}

	public Integer getListDataId() {
		return listDataId;
	}
	
	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
	}
	
	public List<ListDataColumn> getColumns() {
		return columns;
	}
	
	public void setColumns(List<ListDataColumn> columns) {
		this.columns = columns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result
				+ ((listDataId == null) ? 0 : listDataId.hashCode());
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
		ListDataInfo other = (ListDataInfo) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (listDataId == null) {
			if (other.listDataId != null)
				return false;
		} else if (!listDataId.equals(other.listDataId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListDataInfo [listDataId=");
		builder.append(listDataId);
		builder.append(", columns=");
		builder.append(columns);
		builder.append("]");
		return builder.toString();
	}
	
	public void print(int indent){
		Debug.println(indent, "List Data : " + listDataId);
		if (columns != null){
			Debug.println(indent + 3, "# of Columns: " + columns.size());
            for (ListDataColumn column : columns){
                column.print(indent + 5);
            }
		}
		
	}

}
