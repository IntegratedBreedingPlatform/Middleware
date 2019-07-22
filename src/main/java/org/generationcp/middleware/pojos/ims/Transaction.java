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

package org.generationcp.middleware.pojos.ims;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for ims_transaction table.
 *
 */
@NamedQueries({
		@NamedQuery(name = "getEmptyLot", query = "FROM Transaction WHERE status=1 GROUP BY lotid HAVING SUM(quantity) = 0"),
		@NamedQuery(name = "getLotWithMinimumAmount",
				query = "FROM Transaction WHERE status=1 GROUP BY lotid HAVING SUM(quantity) <:minAmount")

})
@Entity
@Table(name = "ims_transaction")
public class Transaction implements Serializable {

	private static final long serialVersionUID = 77866453513905521L;
	public static final String GET_EMPTY_LOT = "getEmptyLot";
	public static final String GET_LOT_WITH_MINIMUM_AMOUNT = "getLotWithMinimumAmount";
	public static final String GET_INVENTORY_ID_WITH_IDENTIFIER_QUERY = "select inventory_id FROM ims_transaction WHERE inventory_id "
			+ "RLIKE '^:identifier[0-9][0-9]*.*'";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "trnid")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "userid")
	private Integer userId;

	@ManyToOne(targetEntity = Lot.class)
	@JoinColumn(name = "lotid", nullable = false, updatable = false)
	private Lot lot;

	@Basic(optional = false)
	@Column(name = "trndate")
	private Integer transactionDate;

	@Basic(optional = false)
	@Column(name = "trnstat")
	private Integer status;

	@Basic(optional = false)
	@Column(name = "trnqty")
	private Double quantity;

	@Column(name = "comments")
	private String comments;

	@Basic(optional = false)
	@Column(name = "cmtdata")
	private Integer commitmentDate;

	@Column(name = "sourcetype")
	private String sourceType;

	@Column(name = "sourceid")
	private Integer sourceId;

	@Column(name = "recordid")
	private Integer sourceRecordId;

	@Column(name = "prevamount")
	private Double previousAmount;

	@Column(name = "inventory_id")
	private String inventoryID;

	@Column(name = "bulk_with")
	private String bulkWith;

	@Column(name = "bulk_compl")
	private String bulkCompl;

	public Transaction() {
	}

	public Transaction(Integer id) {
		super();
		this.id = id;
	}

	public Transaction(Integer id, Integer userId, Lot lot, Integer transactionDate, Integer status, Double quantity, String comments,
			Integer commitmentDate, String sourceType, Integer sourceId, Integer sourceRecordId, Double previousAmount, String inventoryID) {
		super();
		this.id = id;
		this.userId = userId;
		this.lot = lot;
		this.transactionDate = transactionDate;
		this.status = status;
		this.quantity = quantity;
		this.comments = comments;
		this.commitmentDate = commitmentDate;
		this.sourceType = sourceType;
		this.sourceId = sourceId;
		this.sourceRecordId = sourceRecordId;
		this.previousAmount = previousAmount;
		this.inventoryID = inventoryID;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Lot getLot() {
		return this.lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public Integer getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(Integer transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getCommitmentDate() {
		return this.commitmentDate;
	}

	public void setCommitmentDate(Integer commitmentDate) {
		this.commitmentDate = commitmentDate;
	}

	public String getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getSourceRecordId() {
		return this.sourceRecordId;
	}

	public void setSourceRecordId(Integer sourceRecordId) {
		this.sourceRecordId = sourceRecordId;
	}

	public Double getPreviousAmount() {
		return this.previousAmount;
	}

	public void setPreviousAmount(Double previousAmount) {
		this.previousAmount = previousAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Transaction [id=");
		builder.append(this.id);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", transactionDate=");
		builder.append(this.transactionDate);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", quantity=");
		builder.append(this.quantity);
		builder.append(", comments=");
		builder.append(this.comments);
		builder.append(", commitmentDate=");
		builder.append(this.commitmentDate);
		builder.append(", sourceType=");
		builder.append(this.sourceType);
		builder.append(", sourceId=");
		builder.append(this.sourceId);
		builder.append(", sourceRecordId=");
		builder.append(this.sourceRecordId);
		builder.append(", previousAmount=");
		builder.append(this.previousAmount);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Transaction)) {
			return false;
		}

		Transaction rhs = (Transaction) obj;
		return new EqualsBuilder().append(this.id, rhs.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.id).toHashCode();
	}

	public String getInventoryID() {
		return this.inventoryID;
	}

	public void setInventoryID(String inventoryID) {
		this.inventoryID = inventoryID;
	}

	public String getBulkWith() {
		return this.bulkWith;
	}

	public void setBulkWith(String bulkWith) {
		this.bulkWith = bulkWith;
	}

	public String getBulkCompl() {
		return this.bulkCompl;
	}

	public void setBulkCompl(String bulkCompl) {
		this.bulkCompl = bulkCompl;
	}

}
