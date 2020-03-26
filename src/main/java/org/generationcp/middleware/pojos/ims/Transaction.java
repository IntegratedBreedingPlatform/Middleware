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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
import java.io.Serializable;
import java.util.Date;

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
	private Date transactionDate;

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

	@Column(name = "personid")
	private Integer personId;

	@Column(name = "bulk_with")
	private String bulkWith;

	@Column(name = "bulk_compl")
	private String bulkCompl;

	@Basic(optional = false)
	@Column(name = "trntype")
	private Integer type;

	public Transaction() {
	}

	public Transaction(final Integer id) {
		super();
		this.id = id;
	}

	public Transaction(final Integer id, final Integer userId, final Lot lot, final Date transactionDate, final Integer status, final Double quantity, final String comments,
			final Integer commitmentDate, final String sourceType, final Integer sourceId, final Integer sourceRecordId, final Double previousAmount, final Integer personId, final Integer type) {
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
		this.personId = personId;
		this.type = type;

	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	public Lot getLot() {
		return this.lot;
	}

	public void setLot(final Lot lot) {
		this.lot = lot;
	}

	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(final Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(final Double quantity) {
		this.quantity = quantity;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public Integer getCommitmentDate() {
		return this.commitmentDate;
	}

	public void setCommitmentDate(final Integer commitmentDate) {
		this.commitmentDate = commitmentDate;
	}

	public String getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(final String sourceType) {
		this.sourceType = sourceType;
	}

	public Integer getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(final Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getSourceRecordId() {
		return this.sourceRecordId;
	}

	public void setSourceRecordId(final Integer sourceRecordId) {
		this.sourceRecordId = sourceRecordId;
	}

	public Double getPreviousAmount() {
		return this.previousAmount;
	}

	public void setPreviousAmount(final Double previousAmount) {
		this.previousAmount = previousAmount;
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(final Integer personId) {
		this.personId = personId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(final Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
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
		builder.append(", personId=");
		builder.append(this.personId);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Transaction)) {
			return false;
		}

		final Transaction rhs = (Transaction) obj;
		return new EqualsBuilder().append(this.id, rhs.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.id).toHashCode();
	}

	public String getBulkWith() {
		return this.bulkWith;
	}

	public void setBulkWith(final String bulkWith) {
		this.bulkWith = bulkWith;
	}

	public String getBulkCompl() {
		return this.bulkCompl;
	}

	public void setBulkCompl(final String bulkCompl) {
		this.bulkCompl = bulkCompl;
	}

}
