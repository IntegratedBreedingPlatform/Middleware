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

import java.io.Serializable;

/**
 * Row in Seed Inventory System that shows the details of ims_lot/transaction.
 * 
 * @author Joyce Avestro
 *
 */
public class InventoryDetails implements Comparable<InventoryDetails>, Serializable{
	
	private static final long serialVersionUID = 1L;

	/** The index. */
	Integer index;
	
	/** The gid. */
	Integer gid;
	
	/** The germplasm name. */
	String germplasmName;
	
	/** The lot id. */
	Integer lotId;
	
	/** The location id. */
	Integer locationId;
	
	/** The location name. */
	String locationName;

	String locationAbbr;
	
	/** The user id. */
	Integer userId;
	
	/** The user name. */
	String userName;
	
	/** The amount. */
	Double amount;
	
	/** The source id. */
	Integer sourceId;	//ims_transaction.source_id
	
	/** The source name. */
	String sourceName;  // if list, listnms.listname
	
	/** The scale id. */
	Integer scaleId;
	
	/** The scale name. */
	String scaleName;
	
	
	String comment;
	
	String parentage; // listdata.grpname
	
	Integer entryId; // listdata.entryid
	
	String source; // listdata.source

	/**
	 * Instantiates a new inventory details.
	 */
	public InventoryDetails() {
	}
	
	/**
	 * Instantiates a new inventory details.
	 *
	 * @param gid the gid
	 * @param germplasmName the germplasm name
	 * @param lotId the lot id
	 * @param locationId the location id
	 * @param locationName the location name
	 * @param userId the user id
	 * @param amount the amount
	 * @param sourceId the source id
	 * @param sourceName the source name
	 * @param scaleId the scale id
	 * @param scaleName the scale name
	 */
	public InventoryDetails(Integer gid, String germplasmName, Integer lotId,
			Integer locationId, String locationName, Integer userId,
			Double amount, Integer sourceId, String sourceName,
			Integer scaleId, String scaleName, String comment) {
		this.gid = gid;
		this.germplasmName = germplasmName;
		this.lotId = lotId;
		this.locationId = locationId;
		this.locationName = locationName;
		this.userId = userId;
		this.amount = amount;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.scaleId = scaleId;
		this.scaleName = scaleName;
		this.comment = comment;
	}



	/**
	 * Gets the gid.
	 *
	 * @return the gid
	 */
	public Integer getGid() {
		return gid;
	}

	/**
	 * Sets the gid.
	 *
	 * @param gid the new gid
	 */
	public void setGid(Integer gid) {
		this.gid = gid;
	}

	/**
	 * Gets the germplasm name.
	 *
	 * @return the germplasm name
	 */
	public String getGermplasmName() {
		return germplasmName;
	}

	/**
	 * Sets the germplasm name.
	 *
	 * @param germplasmName the new germplasm name
	 */
	public void setGermplasmName(String germplasmName) {
		this.germplasmName = germplasmName;
	}

	/**
	 * Gets the lot id.
	 *
	 * @return the lot id
	 */
	public Integer getLotId() {
		return lotId;
	}

	/**
	 * Sets the lot id.
	 *
	 * @param lotId the new lot id
	 */
	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	/**
	 * Gets the location id.
	 *
	 * @return the location id
	 */
	public Integer getLocationId() {
		return locationId;
	}

	/**
	 * Sets the location id.
	 *
	 * @param locationId the new location id
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * Gets the location name.
	 *
	 * @return the location name
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * Sets the location name.
	 *
	 * @param locationName the new location name
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount.
	 *
	 * @param amount the new amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the source id.
	 *
	 * @return the source id
	 */
	public Integer getSourceId() {
		return sourceId;
	}

	/**
	 * Sets the source id.
	 *
	 * @param sourceId the new source id
	 */
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Gets the source name.
	 *
	 * @return the source name
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Sets the source name.
	 *
	 * @param sourceName the new source name
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * Gets the scale id.
	 *
	 * @return the scale id
	 */
	public Integer getScaleId() {
		return scaleId;
	}

	/**
	 * Sets the scale id.
	 *
	 * @param scaleId the new scale id
	 */
	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	/**
	 * Gets the scale name.
	 *
	 * @return the scale name
	 */
	public String getScaleName() {
		return scaleName;
	}

	/**
	 * Sets the scale name.
	 *
	 * @param scaleName the new scale name
	 */
	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getParentage() {
		return parentage;
	}

	public void setParentage(String parentage) {
		this.parentage = parentage;
	}

	public Integer getEntryId() {
		return entryId;
	}

	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLocationAbbr() {
		return locationAbbr;
	}

	public void setLocationAbbr(String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((entryId == null) ? 0 : entryId.hashCode());
		result = prime * result
				+ ((germplasmName == null) ? 0 : germplasmName.hashCode());
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result
				+ ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result + ((lotId == null) ? 0 : lotId.hashCode());
		result = prime * result
				+ ((parentage == null) ? 0 : parentage.hashCode());
		result = prime * result + ((scaleId == null) ? 0 : scaleId.hashCode());
		result = prime * result
				+ ((scaleName == null) ? 0 : scaleName.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result
				+ ((sourceName == null) ? 0 : sourceName.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		InventoryDetails other = (InventoryDetails) obj;
		if (amount == null) {
			if (other.amount != null) {
                return false;
            }
		} else if (!amount.equals(other.amount)) {
            return false;
        }
		if (comment == null) {
			if (other.comment != null) {
                return false;
            }
		} else if (!comment.equals(other.comment)) {
            return false;
        }
		if (entryId == null) {
			if (other.entryId != null) {
                return false;
            }
		} else if (!entryId.equals(other.entryId)) {
            return false;
        }
		if (germplasmName == null) {
			if (other.germplasmName != null) {
                return false;
            }
		} else if (!germplasmName.equals(other.germplasmName)) {
            return false;
        }
		if (gid == null) {
			if (other.gid != null) {
                return false;
            }
		} else if (!gid.equals(other.gid)) {
            return false;
        }
		if (index == null) {
			if (other.index != null) {
                return false;
            }
		} else if (!index.equals(other.index)) {
            return false;
        }
		if (locationId == null) {
			if (other.locationId != null) {
                return false;
            }
		} else if (!locationId.equals(other.locationId)) {
            return false;
        }
		if (locationName == null) {
			if (other.locationName != null) {
                return false;
            }
		} else if (!locationName.equals(other.locationName)) {
            return false;
        }
		if (lotId == null) {
			if (other.lotId != null) {
                return false;
            }
		} else if (!lotId.equals(other.lotId)) {
            return false;
        }
		if (parentage == null) {
			if (other.parentage != null) {
                return false;
            }
		} else if (!parentage.equals(other.parentage)) {
            return false;
        }
		if (scaleId == null) {
			if (other.scaleId != null) {
                return false;
            }
		} else if (!scaleId.equals(other.scaleId)) {
            return false;
        }
		if (scaleName == null) {
			if (other.scaleName != null) {
                return false;
            }
		} else if (!scaleName.equals(other.scaleName)) {
            return false;
        }
		if (source == null) {
			if (other.source != null) {
                return false;
            }
		} else if (!source.equals(other.source)) {
            return false;
        }
		if (sourceId == null) {
			if (other.sourceId != null) {
                return false;
            }
		} else if (!sourceId.equals(other.sourceId)) {
            return false;
        }
		if (sourceName == null) {
			if (other.sourceName != null) {
                return false;
            }
		} else if (!sourceName.equals(other.sourceName)) {
            return false;
        }
		if (userId == null) {
			if (other.userId != null) {
                return false;
            }
		} else if (!userId.equals(other.userId)) {
            return false;
        }
		if (userName == null) {
			if (other.userName != null) {
                return false;
            }
		} else if (!userName.equals(other.userName)) {
            return false;
        }
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InventoryDetails [index=");
		builder.append(index);
		builder.append(", gid=");
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
		builder.append(", userName=");
		builder.append(userName);
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
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", parentage=");
		builder.append(parentage);
		builder.append(", entryId=");
		builder.append(entryId);
		builder.append(", source=");
		builder.append(source);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(InventoryDetails o) {
        if (this.gid != null && o != null) {

        	return this.entryId.compareTo(o.entryId);
        }
        return 0;
	}

}
