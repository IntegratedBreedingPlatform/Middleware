package org.generationcp.middleware.domain.inventory_new;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class ExtendedLotDto extends LotDto {

	private Integer mgid;
	private String designation;
	private String locationName;
	private String scaleName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date createdDate;

	//Aggregated Data
	private Double actualBalance;
	private Double availableBalance;
	private Double reservedTotal;
	private Double withdrawalTotal;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date lastDepositDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date lastWithdrawalDate;


	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
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
