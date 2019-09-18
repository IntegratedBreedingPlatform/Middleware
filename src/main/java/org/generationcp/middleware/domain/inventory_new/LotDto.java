package org.generationcp.middleware.domain.inventory_new;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class LotDto {

	private Integer id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date createdDate;

	private Integer gid;

	private Integer mgid;

	private String locationName;

	private String designation;

	private String status;

	private String scaleName;

	private String stockId;

	private Double actualBalance;

	private Double availableBalance;

	private Double reservedTotal;

	private Double withdrawalTotal;

	private String createdByUsername;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date lastDepositDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date lastWithdrawalDate;

	private String comments;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public Double getActualBalance() {
		return actualBalance;
	}

	public void setActualBalance(final Double actualBalance) {
		this.actualBalance = actualBalance;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(final Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Double getReservedTotal() {
		return reservedTotal;
	}

	public void setReservedTotal(final Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}

	public Double getWithdrawalTotal() {
		return withdrawalTotal;
	}

	public void setWithdrawalTotal(final Double withdrawalTotal) {
		this.withdrawalTotal = withdrawalTotal;
	}

	public Date getLastDepositDate() {
		return lastDepositDate;
	}

	public void setLastDepositDate(final Date lastDepositDate) {
		this.lastDepositDate = lastDepositDate;
	}

	public Date getLastWithdrawalDate() {
		return lastWithdrawalDate;
	}

	public void setLastWithdrawalDate(final Date lastWithdrawalDate) {
		this.lastWithdrawalDate = lastWithdrawalDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public Integer getMgid() {
		return mgid;
	}

	public void setMgid(final Integer mgid) {
		this.mgid = mgid;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getScaleName() {
		return scaleName;
	}

	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
