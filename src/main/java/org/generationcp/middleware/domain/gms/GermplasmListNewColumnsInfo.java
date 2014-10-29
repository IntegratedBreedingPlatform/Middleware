package org.generationcp.middleware.domain.gms;

import org.generationcp.middleware.util.Debug;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GermplasmListNewColumnsInfo implements Serializable {
	
	private static final long serialVersionUID = 5475025243020285738L;

	private Integer listId;
	private Map<String, List<ListDataColumnValues>> columnValuesMap; //key = column name
	
	public GermplasmListNewColumnsInfo(Integer listId,
			Map<String, List<ListDataColumnValues>> columnValuesMap) {
		super();
		this.listId = listId;
		this.columnValuesMap = columnValuesMap;
	}
	
	public GermplasmListNewColumnsInfo(Integer listId) {
		super();
		this.listId = listId;
	}

	public Integer getListId() {
		return listId;
	}
	
	public void setListId(Integer listId) {
		this.listId = listId;
	}
	
	public Map<String, List<ListDataColumnValues>> getColumnValuesMap() {
		return columnValuesMap;
	}
	
	public void setColumnValuesMap(
			Map<String, List<ListDataColumnValues>> columnValuesMap) {
		this.columnValuesMap = columnValuesMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnValuesMap == null) ? 0 : columnValuesMap.hashCode());
		result = prime * result + ((listId == null) ? 0 : listId.hashCode());
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
		if (getClass() != obj.getClass()) {
            return false;
        }
		GermplasmListNewColumnsInfo other = (GermplasmListNewColumnsInfo) obj;
		if (columnValuesMap == null) {
			if (other.columnValuesMap != null) {
                return false;
            }
		} else if (!columnValuesMap.equals(other.columnValuesMap)) {
            return false;
        }
		if (listId == null) {
			if (other.listId != null) {
                return false;
            }
		} else if (!listId.equals(other.listId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmListNewColumnsInfo [listId=");
		builder.append(listId);
		builder.append(", columnValuesMap=");
		builder.append(columnValuesMap);
		builder.append("]");
		return builder.toString();
	}

    public void print(int indent) {

        if (columnValuesMap != null) {
            if (columnValuesMap.keySet() != null) {
                Debug.println(indent, "New Columns for List " + listId + ", # of Columns = "
                        + columnValuesMap.keySet().size());
            }

            Set<Entry<String, List<ListDataColumnValues>>> entrySet = columnValuesMap.entrySet();
            for (Entry<String, List<ListDataColumnValues>> entry : entrySet) {
                Debug.println(indent + 3, "COLUMN : " + entry.getKey());

                for (ListDataColumnValues columnValues : entry.getValue()) {
                    Debug.println(indent + 6,
                            "ListData ID=" + columnValues.getListDataId() + ", value=" + columnValues.getValue());
                }
            }
        }

    }
	

}
