/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.inventory;

/**
 * Row in Seed Inventory System that shows the details of ims_lot/transaction.
 * 
 * @author Joyce Avestro
 *
 */
public class InventoryDetails {
	
	Integer gid;
	
	String germplasmName;
	
	Integer lotId;
	
	Integer locationId;
	
	String locationName;
	
	Integer userId;
	
	Integer amount;
	
	Integer sourceId;
	
	String sourceName;
	
	Integer scaleId;
	
	String scaleName;

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getGermplasmName() {
		return germplasmName;
	}

	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public Integer getLotId() {
		return lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Integer getScaleId() {
		return scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	public String getScaleName() {
		return scaleName;
	}

	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((germplasmName == null) ? 0 : germplasmName.hashCode());
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result
				+ ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result + ((lotId == null) ? 0 : lotId.hashCode());
		result = prime * result + ((scaleId == null) ? 0 : scaleId.hashCode());
		result = prime * result
				+ ((scaleName == null) ? 0 : scaleName.hashCode());
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result
				+ ((sourceName == null) ? 0 : sourceName.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		InventoryDetails other = (InventoryDetails) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (germplasmName == null) {
			if (other.germplasmName != null)
				return false;
		} else if (!germplasmName.equals(other.germplasmName))
			return false;
		if (gid == null) {
			if (other.gid != null)
				return false;
		} else if (!gid.equals(other.gid))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (locationName == null) {
			if (other.locationName != null)
				return false;
		} else if (!locationName.equals(other.locationName))
			return false;
		if (lotId == null) {
			if (other.lotId != null)
				return false;
		} else if (!lotId.equals(other.lotId))
			return false;
		if (scaleId == null) {
			if (other.scaleId != null)
				return false;
		} else if (!scaleId.equals(other.scaleId))
			return false;
		if (scaleName == null) {
			if (other.scaleName != null)
				return false;
		} else if (!scaleName.equals(other.scaleName))
			return false;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		if (sourceName == null) {
			if (other.sourceName != null)
				return false;
		} else if (!sourceName.equals(other.sourceName))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InventoryDetails [gid=");
		builder.append(gid);
		builder.append(", germplasmName=");
		builder.append(germplasmName);
		builder.append(", lotId=");
		builder.append(lotId);
		builder.append(", locationId=");
		builder.append(locationId);
		builder.append(", locationName=");
		builder.append(locationName);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", sourceName=");
		builder.append(sourceName);
		builder.append(", scaleId=");
		builder.append(scaleId);
		builder.append(", scaleName=");
		builder.append(scaleName);
		builder.append("]");
		return builder.toString();
	}

}
