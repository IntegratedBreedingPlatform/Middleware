package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;

/**
 * POJO for storing aggregate inventory data for specific
 * GermplasmListData record
 * 
 * @author Darla Ani
 *
 */
public class ListDataInventory implements Serializable {
	
	private static final long serialVersionUID = -8594381810347667269L;

	private Integer listDataId;
	
	//gid needs to be redundant for easier access to imslot info
	private Integer gid;
	
	//number of lots with actual inventory available for given germplasm
	private Integer actualInventoryLotCount;
	
	//number of lots with reserved amount for given list entry
	private Integer reservedLotCount;

	
	public ListDataInventory(Integer listDataId) {
		super();
		this.listDataId = listDataId;
	}
	
	public ListDataInventory(Integer listDataId, Integer gid) {
		super();
		this.listDataId = listDataId;
		this.gid = gid;
	}

	public Integer getListDataId() {
		return listDataId;
	}

	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getActualInventoryLotCount() {
		return actualInventoryLotCount;
	}

	public void setActualInventoryLotCount(Integer actualInventoryLotCount) {
		this.actualInventoryLotCount = actualInventoryLotCount;
	}

	public Integer getReservedLotCount() {
		return reservedLotCount;
	}

	public void setReservedLotCount(Integer reservedLotCount) {
		this.reservedLotCount = reservedLotCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((actualInventoryLotCount == null) ? 0
						: actualInventoryLotCount.hashCode());
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result
				+ ((listDataId == null) ? 0 : listDataId.hashCode());
		result = prime
				* result
				+ ((reservedLotCount == null) ? 0 : reservedLotCount.hashCode());
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
		ListDataInventory other = (ListDataInventory) obj;
		if (actualInventoryLotCount == null) {
			if (other.actualInventoryLotCount != null)
				return false;
		} else if (!actualInventoryLotCount
				.equals(other.actualInventoryLotCount))
			return false;
		if (gid == null) {
			if (other.gid != null)
				return false;
		} else if (!gid.equals(other.gid))
			return false;
		if (listDataId == null) {
			if (other.listDataId != null)
				return false;
		} else if (!listDataId.equals(other.listDataId))
			return false;
		if (reservedLotCount == null) {
			if (other.reservedLotCount != null)
				return false;
		} else if (!reservedLotCount.equals(other.reservedLotCount))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListDataInventory [listDataId=");
		builder.append(listDataId);
		builder.append(", gid=");
		builder.append(gid);
		builder.append(", actualInventoryLotCount=");
		builder.append(actualInventoryLotCount);
		builder.append(", reservedLotCount=");
		builder.append(reservedLotCount);
		builder.append("]");
		return builder.toString();
	}
	
	

}
