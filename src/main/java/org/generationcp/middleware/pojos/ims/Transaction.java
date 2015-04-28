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

package org.generationcp.middleware.pojos.ims;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

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
public class Transaction implements Serializable{

    private static final long serialVersionUID = 77866453513905521L;
    public static final String GET_EMPTY_LOT = "getEmptyLot";
    public static final String GET_LOT_WITH_MINIMUM_AMOUNT = "getLotWithMinimumAmount";
    public static final String GET_INVENTORY_ID_WITH_IDENTIFIER_QUERY = "select inventory_id FROM ims_transaction WHERE inventory_id "
            + "RLIKE '^:identifier[0-9][0-9]*.*'";

    @Id
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

    @Column(name = "personid")
    private Integer personId;

    @Column(name = "inventory_id")
    private String inventoryID;

    public Transaction() {
    }

    public Transaction(Integer id) {
        super();
        this.id = id;
    }

    public Transaction(Integer id, Integer userId, Lot lot, Integer transactionDate, Integer status, 
    		Double quantity, String comments, Integer commitmentDate, String sourceType, Integer sourceId, 
    		Integer sourceRecordId, Double previousAmount, Integer personId, String inventoryID) {
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
        this.inventoryID = inventoryID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public Integer getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Integer transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getCommitmentDate() {
        return commitmentDate;
    }

    public void setCommitmentDate(Integer commitmentDate) {
        this.commitmentDate = commitmentDate;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getSourceRecordId() {
        return sourceRecordId;
    }

    public void setSourceRecordId(Integer sourceRecordId) {
        this.sourceRecordId = sourceRecordId;
    }

    public Double getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(Double previousAmount) {
        this.previousAmount = previousAmount;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transaction [id=");
        builder.append(id);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", transactionDate=");
        builder.append(transactionDate);
        builder.append(", status=");
        builder.append(status);
        builder.append(", quantity=");
        builder.append(quantity);
        builder.append(", comments=");
        builder.append(comments);
        builder.append(", commitmentDate=");
        builder.append(commitmentDate);
        builder.append(", sourceType=");
        builder.append(sourceType);
        builder.append(", sourceId=");
        builder.append(sourceId);
        builder.append(", sourceRecordId=");
        builder.append(sourceRecordId);
        builder.append(", previousAmount=");
        builder.append(previousAmount);
        builder.append(", personId=");
        builder.append(personId);
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
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }
}
