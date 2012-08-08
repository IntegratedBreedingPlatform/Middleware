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

package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "ims_lot")
public class Lot implements Serializable{

    private static final long serialVersionUID = -7110592680243974512L;

    // string contants for name of queries
    public static final String GENERATE_REPORT_ON_DORMANT = 
            "SELECT l.lotid, l.eid, SUM(t.trnqty) balance, l.locid, l.scaleid " +
            "FROM ims_transaction t, ims_lot l " + 
            "WHERE t.lotid = l.lotid " + 
                    "AND t.trndate < (:year + 1) * 10000 " +
                    "AND t.trnstat = 1 " + 
            "GROUP BY l.lotid, l.eid, l.locid, l.scaleid " + 
            "HAVING SUM(trnqty) <> 0";

    @Id
    @Basic(optional = false)
    @Column(name = "lotid")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "userid")
    private Integer userId;

    @Basic(optional = false)
    @Column(name = "etype")
    private String entityType;

    @Basic(optional = false)
    @Column(name = "eid")
    private Integer entityId;

    @Basic(optional = false)
    @Column(name = "locid")
    private Integer locationId;

    @Basic(optional = false)
    @Column(name = "scaleid")
    private Integer scaleId;

    @Basic(optional = false)
    @Column(name = "status")
    private Integer status;

    @ManyToOne(targetEntity = Lot.class)
    @JoinColumn(name = "sourceid", nullable = true)
    private Lot source;

    @Column(name = "comments")
    private String comments;

    @OneToMany(mappedBy = "lot")
    private Set<Transaction> transactions = new HashSet<Transaction>();

    public Lot() {

    }

    public Lot(Integer id) {
        super();
        this.id = id;
    }

    public Lot(Integer id, Integer userId, String entityType, Integer entityId, Integer locationId, Integer scaleId, Integer status,
            String comments) {
        super();
        this.id = id;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.locationId = locationId;
        this.scaleId = scaleId;
        this.status = status;
        this.comments = comments;
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

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getScaleId() {
        return scaleId;
    }

    public void setScaleId(Integer scaleId) {
        this.scaleId = scaleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Lot getSource() {
        return source;
    }

    public void setSource(Lot source) {
        this.source = source;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Lot [id=" + id + ", userId=" + userId + ", entityType=" + entityType + ", entityId=" + entityId + ", locationId="
                + locationId + ", scaleId=" + scaleId + ", status=" + status + ", comments=" + comments + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Lot)) {
            return false;
        }

        Lot rhs = (Lot) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
