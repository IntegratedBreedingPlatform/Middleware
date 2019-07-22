/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * <p>
 * Generation Challenge Programme (GCP)
 * <p>
 * <p>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.Util;

/**
 * Row in Seed Inventory System that shows the details of ims_lot/transaction.
 *
 * @author Joyce Avestro
 *
 */
public class InventoryDetails implements Comparable<InventoryDetails>, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String BULK_COMPL_Y = "Y";
	public static final String BULK_COMPL_COMPLETED = "Completed";

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
	// ims_transaction.source_id
	Integer sourceId;

	/** The source name. */
	// if list, listnms.listname
	String sourceName;

	/** The scale id. */
	Integer scaleId;

	/** The scale name. */
	String scaleName;

	String comment;

	// listdata.grpname
	String parentage;

	// listdata.entryid
	Integer entryId;

	// listdata.source
	String source;

	String inventoryID;

	/** The ff. fields are from seed inventory import */
	private String entryCode;
	private String cross;

	/** The ff. fields are used for export inventory template for stock list */
	private String duplicate;
	private String bulkWith;
	private String bulkCompl;
	private List<String> bulkWithStockIds;

	/** The ff. fields are used for importing inventory for stock list */
	private Integer listDataProjectId;
	private Integer trnId;

	/** This is used for executing bulking instructions */
	private Integer sourceRecordId;
	private Integer lotGid;
	private Integer stockSourceRecordId;

	private Integer instanceNumber;
	private Integer plotNumber;
	private Integer replicationNumber;

	private Integer groupId;

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
	public InventoryDetails(final Integer gid, final String germplasmName, final Integer lotId, final Integer locationId, final String locationName, final Integer userId,
			final Double amount, final Integer sourceId, final String sourceName, final Integer scaleId, final String scaleName, final String comment) {
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

	public InventoryDetails(final Integer gid, final String germplasmName, final Integer locationId, final Integer userId, final Double amount, final Integer sourceId,
			final Integer scaleId, final Integer entryId) {
		this.gid = gid;
		this.germplasmName = germplasmName;
		this.locationId = locationId;
		this.userId = userId;
		this.amount = amount;
		this.sourceId = sourceId;
		this.scaleId = scaleId;
		this.entryId = entryId;
	}

	public InventoryDetails(final Integer entryId, final String desig, final Integer gid, final String cross, final String source, final String entryCode, final String stockId,
			final Double seedQuantity) {
		this.entryId = entryId;
		this.germplasmName = desig;
		this.gid = gid;
		this.cross = cross;
		this.source = source;
		this.entryCode = entryCode;
		this.inventoryID = stockId;
		this.amount = seedQuantity;
	}

	/**
	 * Gets the original gid of the inventory
	 *
	 * @return the gid
	 */
	public Integer getOriginalGid() {
		return this.gid;
	}

	/**
	 * Gets the gid displayed for the given inventory. 
	 * Note that if the inventory is a bulking donor, it returns null. 
	 * To get the original gid of the inventory, use getOriginalGid()
	 *
	 * @return the gid
	 */
	public Integer getGid() {
		if (this.isBulkingDonor()) {
			return null;
		}
		return this.gid;
	}

	public boolean isBulkingDonor() {
		return this.isBulkingCompleted() && !this.sourceRecordId.equals(this.stockSourceRecordId);
	}

	/**
	 * Sets the gid.
	 *
	 * @param gid the new gid
	 */
	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	/**
	 * Gets the germplasm name.
	 *
	 * @return the germplasm name
	 */
	public String getGermplasmName() {
		if (this.isBulkingDonor()) {
			return null;
		}
		return this.germplasmName;
	}

	/**
	 * Sets the germplasm name.
	 *
	 * @param germplasmName the new germplasm name
	 */
	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	/**
	 * Gets the lot id.
	 *
	 * @return the lot id
	 */
	public Integer getLotId() {
		return this.lotId;
	}

	/**
	 * Sets the lot id.
	 *
	 * @param lotId the new lot id
	 */
	public void setLotId(final Integer lotId) {
		this.lotId = lotId;
	}

	/**
	 * Gets the location id.
	 *
	 * @return the location id
	 */
	public Integer getLocationId() {
		return this.locationId;
	}

	/**
	 * Sets the location id.
	 *
	 * @param locationId the new location id
	 */
	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * Gets the location name.
	 *
	 * @return the location name
	 */
	public String getLocationName() {
		return this.locationName;
	}

	/**
	 * Sets the location name.
	 *
	 * @param locationName the new location name
	 */
	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public Integer getUserId() {
		return this.userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public Double getAmount() {
		return this.amount;
	}

	/**
	 * Sets the amount.
	 *
	 * @param amount the new amount
	 */
	public void setAmount(final Double amount) {
		this.amount = amount;
	}

	/**
	 * Gets the source id.
	 *
	 * @return the source id
	 */
	public Integer getSourceId() {
		return this.sourceId;
	}

	/**
	 * Sets the source id.
	 *
	 * @param sourceId the new source id
	 */
	public void setSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Gets the source name.
	 *
	 * @return the source name
	 */
	public String getSourceName() {
		return this.sourceName;
	}

	/**
	 * Sets the source name.
	 *
	 * @param sourceName the new source name
	 */
	public void setSourceName(final String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * Gets the scale id.
	 *
	 * @return the scale id
	 */
	public Integer getScaleId() {
		return this.scaleId;
	}

	/**
	 * Sets the scale id.
	 *
	 * @param scaleId the new scale id
	 */
	public void setScaleId(final Integer scaleId) {
		this.scaleId = scaleId;
	}

	/**
	 * Gets the scale name.
	 *
	 * @return the scale name
	 */
	public String getScaleName() {
		return this.scaleName;
	}

	/**
	 * Sets the scale name.
	 *
	 * @param scaleName the new scale name
	 */
	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public Integer getIndex() {
		return this.index;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(final Integer index) {
		this.index = index;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public String getParentage() {
		return this.parentage;
	}

	public void setParentage(final String parentage) {
		this.parentage = parentage;
	}

	public Integer getEntryId() {
		return this.entryId;
	}

	public void setEntryId(final Integer entryId) {
		this.entryId = entryId;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public String getLocationAbbr() {
		return this.locationAbbr;
	}

	public void setLocationAbbr(final String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.amount == null ? 0 : this.amount.hashCode());
		result = prime * result + (this.comment == null ? 0 : this.comment.hashCode());
		result = prime * result + (this.entryId == null ? 0 : this.entryId.hashCode());
		result = prime * result + (this.germplasmName == null ? 0 : this.germplasmName.hashCode());
		result = prime * result + (this.gid == null ? 0 : this.gid.hashCode());
		result = prime * result + (this.index == null ? 0 : this.index.hashCode());
		result = prime * result + (this.locationId == null ? 0 : this.locationId.hashCode());
		result = prime * result + (this.locationName == null ? 0 : this.locationName.hashCode());
		result = prime * result + (this.lotId == null ? 0 : this.lotId.hashCode());
		result = prime * result + (this.parentage == null ? 0 : this.parentage.hashCode());
		result = prime * result + (this.scaleId == null ? 0 : this.scaleId.hashCode());
		result = prime * result + (this.scaleName == null ? 0 : this.scaleName.hashCode());
		result = prime * result + (this.source == null ? 0 : this.source.hashCode());
		result = prime * result + (this.sourceId == null ? 0 : this.sourceId.hashCode());
		result = prime * result + (this.sourceName == null ? 0 : this.sourceName.hashCode());
		result = prime * result + (this.userId == null ? 0 : this.userId.hashCode());
		result = prime * result + (this.userName == null ? 0 : this.userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final InventoryDetails other = (InventoryDetails) obj;
		if (this.amount == null) {
			if (other.amount != null) {
				return false;
			}
		} else if (!this.amount.equals(other.amount)) {
			return false;
		}
		if (this.comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!this.comment.equals(other.comment)) {
			return false;
		}
		if (this.entryId == null) {
			if (other.entryId != null) {
				return false;
			}
		} else if (!this.entryId.equals(other.entryId)) {
			return false;
		}
		if (this.germplasmName == null) {
			if (other.germplasmName != null) {
				return false;
			}
		} else if (!this.germplasmName.equals(other.germplasmName)) {
			return false;
		}
		if (this.gid == null) {
			if (other.gid != null) {
				return false;
			}
		} else if (!this.gid.equals(other.gid)) {
			return false;
		}
		if (this.index == null) {
			if (other.index != null) {
				return false;
			}
		} else if (!this.index.equals(other.index)) {
			return false;
		}
		if (this.locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!this.locationId.equals(other.locationId)) {
			return false;
		}
		if (this.locationName == null) {
			if (other.locationName != null) {
				return false;
			}
		} else if (!this.locationName.equals(other.locationName)) {
			return false;
		}
		if (this.lotId == null) {
			if (other.lotId != null) {
				return false;
			}
		} else if (!this.lotId.equals(other.lotId)) {
			return false;
		}
		if (this.parentage == null) {
			if (other.parentage != null) {
				return false;
			}
		} else if (!this.parentage.equals(other.parentage)) {
			return false;
		}
		if (this.scaleId == null) {
			if (other.scaleId != null) {
				return false;
			}
		} else if (!this.scaleId.equals(other.scaleId)) {
			return false;
		}
		if (this.scaleName == null) {
			if (other.scaleName != null) {
				return false;
			}
		} else if (!this.scaleName.equals(other.scaleName)) {
			return false;
		}
		if (this.source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!this.source.equals(other.source)) {
			return false;
		}
		if (this.sourceId == null) {
			if (other.sourceId != null) {
				return false;
			}
		} else if (!this.sourceId.equals(other.sourceId)) {
			return false;
		}
		if (this.sourceName == null) {
			if (other.sourceName != null) {
				return false;
			}
		} else if (!this.sourceName.equals(other.sourceName)) {
			return false;
		}
		if (this.userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!this.userId.equals(other.userId)) {
			return false;
		}
		if (this.userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!this.userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("InventoryDetails [index=");
		builder.append(this.index);
		builder.append(", gid=");
		builder.append(this.gid);
		builder.append(", germplasmName=");
		builder.append(this.germplasmName);
		builder.append(", lotId=");
		builder.append(this.lotId);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", locationName=");
		builder.append(this.locationName);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", userName=");
		builder.append(this.userName);
		builder.append(", amount=");
		builder.append(this.amount);
		builder.append(", sourceId=");
		builder.append(this.sourceId);
		builder.append(", sourceName=");
		builder.append(this.sourceName);
		builder.append(", scaleId=");
		builder.append(this.scaleId);
		builder.append(", scaleName=");
		builder.append(this.scaleName);
		builder.append(", comment=");
		builder.append(this.comment);
		builder.append(", parentage=");
		builder.append(this.parentage);
		builder.append(", entryId=");
		builder.append(this.entryId);
		builder.append(", source=");
		builder.append(this.source);
		builder.append(", instanceNumber=");
		builder.append(this.instanceNumber);
		builder.append(", plotNumber=");
		builder.append(this.plotNumber);
		builder.append(", replicationNumber=");
		builder.append(this.replicationNumber);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(final InventoryDetails o) {
		if (this.gid != null && o != null) {

			return this.entryId.compareTo(o.entryId);
		}
		return 0;
	}

	public String getInventoryID() {
		if (this.isBulkingRecipient()) {
			return Util.prependToCSVAndArrange(this.inventoryID, this.bulkWith);
		}
		return this.inventoryID;
	}

	public boolean isBulkingRecipient() {
		return this.isBulkingCompleted() && this.sourceRecordId.equals(this.stockSourceRecordId);
	}

	public void setInventoryID(final String inventoryID) {
		this.inventoryID = inventoryID;
	}

	public String getEntryCode() {
		return this.entryCode;
	}

	public void setEntryCode(final String entryCode) {
		this.entryCode = entryCode;
	}

	public String getCross() {
		return this.cross;
	}

	public void setCross(final String cross) {
		this.cross = cross;
	}

	public void copyFromGermplasmListData(final GermplasmListData datum) {
		this.gid = datum.getGid();
		this.setGermplasmName(datum.getDesignation());
		this.setEntryId(datum.getEntryId());
		this.setParentage(datum.getGroupName());
		this.setSource(datum.getSeedSource());
	}

	public String getDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(final String duplicate) {
		this.duplicate = duplicate;
	}

	public String getBulkWith() {
		return this.bulkWith;
	}

	public void setBulkWith(final String bulkWith) {
		this.bulkWith = bulkWith;
		this.bulkWithStockIds = new ArrayList<>();
	}

	public String getBulkCompl() {
		return this.bulkCompl;
	}

	public void setBulkCompl(final String bulkCompl) {
		this.bulkCompl = bulkCompl;
	}

	public Integer getListDataProjectId() {
		return this.listDataProjectId;
	}

	public void setListDataProjectId(final Integer listDataProjectId) {
		this.listDataProjectId = listDataProjectId;
	}

	public Integer getTrnId() {
		return this.trnId;
	}

	public void setTrnId(final Integer trnId) {
		this.trnId = trnId;
	}

	public Integer getSourceRecordId() {
		return this.sourceRecordId;
	}

	public void setSourceRecordId(final Integer sourceRecordId) {
		this.sourceRecordId = sourceRecordId;
	}

	public Integer getLotGid() {
		return this.lotGid;
	}

	public void setLotGid(final Integer lotGid) {
		this.lotGid = lotGid;
	}

	public void addBulkWith(final String bulkWith) {
		if (bulkWith.equals(this.inventoryID)) {
			return;
		}
		if (this.bulkWith == null) {
			this.bulkWith = bulkWith;
			this.bulkWithStockIds = new ArrayList<>();
			this.bulkWithStockIds.add(bulkWith);
		} else if (!this.bulkWithStockIds.contains(bulkWith)) {
			this.bulkWith += ", " + bulkWith;
			this.bulkWithStockIds.add(bulkWith);
		}
	}

	public boolean isBulkingCompleted() {
		return InventoryDetails.BULK_COMPL_COMPLETED.equals(this.bulkCompl);
	}

	public Integer getStockSourceRecordId() {
		return this.stockSourceRecordId;
	}

	public void setStockSourceRecordId(final Integer stockSourceRecordId) {
		this.stockSourceRecordId = stockSourceRecordId;
	}

	public Integer getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(final Integer instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public Integer getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(final Integer plotNumber) {
		this.plotNumber = plotNumber;
	}

	public Integer getReplicationNumber() {
		return this.replicationNumber;
	}

	public void setReplicationNumber(final Integer replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

	public List<String> getBulkWithStockIds() {
		return this.bulkWithStockIds;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}
}
