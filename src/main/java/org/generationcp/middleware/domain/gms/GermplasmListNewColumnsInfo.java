
package org.generationcp.middleware.domain.gms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.middleware.util.Debug;

public class GermplasmListNewColumnsInfo implements Serializable {

	private static final long serialVersionUID = 5475025243020285738L;

	private Integer listId;
	private Map<String, List<ListDataColumnValues>> columnValuesMap; // key = column name

	public GermplasmListNewColumnsInfo(Integer listId, Map<String, List<ListDataColumnValues>> columnValuesMap) {
		super();
		this.listId = listId;
		this.columnValuesMap = columnValuesMap;
	}

	public GermplasmListNewColumnsInfo(Integer listId) {
		super();
		this.listId = listId;
	}

	public Integer getListId() {
		return this.listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public Map<String, List<ListDataColumnValues>> getColumnValuesMap() {
		return this.columnValuesMap;
	}

	public void setColumnValuesMap(Map<String, List<ListDataColumnValues>> columnValuesMap) {
		this.columnValuesMap = columnValuesMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.columnValuesMap == null ? 0 : this.columnValuesMap.hashCode());
		result = prime * result + (this.listId == null ? 0 : this.listId.hashCode());
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
		GermplasmListNewColumnsInfo other = (GermplasmListNewColumnsInfo) obj;
		if (this.columnValuesMap == null) {
			if (other.columnValuesMap != null) {
				return false;
			}
		} else if (!this.columnValuesMap.equals(other.columnValuesMap)) {
			return false;
		}
		if (this.listId == null) {
			if (other.listId != null) {
				return false;
			}
		} else if (!this.listId.equals(other.listId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmListNewColumnsInfo [listId=");
		builder.append(this.listId);
		builder.append(", columnValuesMap=");
		builder.append(this.columnValuesMap);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {

		if (this.columnValuesMap != null) {
			if (this.columnValuesMap.keySet() != null) {
				Debug.println(indent, "New Columns for List " + this.listId + ", # of Columns = " + this.columnValuesMap.keySet().size());
			}

			Set<Entry<String, List<ListDataColumnValues>>> entrySet = this.columnValuesMap.entrySet();
			for (Entry<String, List<ListDataColumnValues>> entry : entrySet) {
				Debug.println(indent + 3, "COLUMN : " + entry.getKey());

				for (ListDataColumnValues columnValues : entry.getValue()) {
					Debug.println(indent + 6, "ListData ID=" + columnValues.getListDataId() + ", value=" + columnValues.getValue());
				}
			}
		}

	}
	
	public Set<String> getColumns() {
		if (this.columnValuesMap != null) {
			return this.columnValuesMap.keySet();
		}
		return new HashSet<>();
	}

}
