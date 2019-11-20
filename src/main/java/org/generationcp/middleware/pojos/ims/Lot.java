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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.domain.inventory.LotAggregateData;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * POJO for ims_lot table.
 *
 */
@Entity
@Table(name = "ims_lot", uniqueConstraints = {@UniqueConstraint(columnNames = {"etype", "eid", "locid", "scaleid"})})
public class Lot implements Serializable {

	private static final long serialVersionUID = -7110592680243974512L;

	// string contants for name of queries
	public static final String GENERATE_REPORT_ON_DORMANT = "SELECT l.lotid, l.eid, SUM(t.trnqty) balance, l.locid, l.scaleid "
			+ "FROM ims_transaction t, ims_lot l " + "WHERE t.lotid = l.lotid " + "AND t.trndate < (:year + 1) * 10000 "
			+ "AND t.trnstat = 1 " + "GROUP BY l.lotid, l.eid, l.locid, l.scaleid " + "HAVING SUM(trnqty) <> 0";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "locid")
	private Integer locationId;

	@Column(name = "scaleid")
	private Integer scaleId;

	@Basic(optional = false)
	@Column(name = "status")
	private Integer status;

	@Column(name = "sourceid")
	private Integer sourceId;

	@Column(name = "comments")
	private String comments;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "stock_id")
	private String stockId;

	@Column(name = "lot_uuid")
	private String lotUuId;

	@OneToMany(mappedBy = "lot")
	private Set<Transaction> transactions = new HashSet<Transaction>();

	@Transient
	private LotAggregateData aggregateData;

	public Lot() {
		this.createdDate = new Date();
	}

	public Lot(final Integer id) {
		super();
		this.id = id;
	}

	public Lot(
		final Integer id, final Integer userId, final String entityType, final Integer entityId, final Integer locationId, final Integer scaleId, final Integer status,
			final Integer sourceId, final String comments) {
		super();
		this.id = id;
		this.userId = userId;
		this.entityType = entityType;
		this.entityId = entityId;
		this.locationId = locationId;
		this.scaleId = scaleId;
		this.status = status;
		this.sourceId = sourceId;
		this.comments = comments;
		this.createdDate = new Date();
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

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(final String entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(final Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(final Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Integer getSource() {
		return this.sourceId;
	}

	public void setSource(final Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public Set<Transaction> getTransactions() {
		return this.transactions;
	}

	public void setTransactions(final Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public LotAggregateData getAggregateData() {
		return this.aggregateData;
	}

	public void setAggregateData(final LotAggregateData aggregateData) {
		this.aggregateData = aggregateData;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public String getLotUuId() {
		return this.lotUuId;
	}

	public void setLotUuId(final String lotUuId) {
		this.lotUuId = lotUuId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Lot [id=");
		builder.append(this.id);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", entityType=");
		builder.append(this.entityType);
		builder.append(", entityId=");
		builder.append(this.entityId);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", scaleId=");
		builder.append(this.scaleId);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", sourceId=");
		builder.append(this.sourceId);
		builder.append(", comments=");
		builder.append(this.comments);
		builder.append(", transactions=");
		builder.append(this.transactions);
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
		if (!(obj instanceof Lot)) {
			return false;
		}

		final Lot rhs = (Lot) obj;
		return new EqualsBuilder().append(this.id, rhs.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.id).toHashCode();
	}
}
