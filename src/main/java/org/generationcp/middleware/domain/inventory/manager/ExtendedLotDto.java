package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class ExtendedLotDto extends LotDto {

	private String designation;
	private String unitName;

	@JsonView({InventoryView.LotView.class})
	private String createdByUsername;

	@JsonView({InventoryView.LotView.class})
	private Integer mgid;

	private String germplasmMethodName;
	private String germplasmLocation;

	@JsonView({InventoryView.LotView.class})
	private String locationName;

	private String locationAbbr;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	@JsonView({InventoryView.LotView.class})
	private Date createdDate;

	//Aggregated Data
	@JsonView({InventoryView.LotView.class})
	private Double actualBalance;

	@JsonView({InventoryView.LotView.class})
	private Double availableBalance;

	@JsonView({InventoryView.LotView.class})
	private Double reservedTotal;

	@JsonView({InventoryView.LotView.class})
	private Double withdrawalTotal;

	@JsonView({InventoryView.LotView.class})
	private Double pendingDepositsTotal;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	@JsonView({InventoryView.LotView.class})
	private Date lastDepositDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	@JsonView({InventoryView.LotView.class})
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

	public String getGermplasmMethodName() {
		return this.germplasmMethodName;
	}

	public void setGermplasmMethodName(final String germplasmMethodName) {
		this.germplasmMethodName = germplasmMethodName;
	}

	public String getGermplasmLocation() {
		return this.germplasmLocation;
	}

	public void setGermplasmLocation(final String germplasmLocation) {
		this.germplasmLocation = germplasmLocation;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbr() {
		return locationAbbr;
	}

	public void setLocationAbbr(final String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(final String unitName) {
		this.unitName = unitName;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public Double getPendingDepositsTotal() {
		return pendingDepositsTotal;
	}

	public void setPendingDepositsTotal(final Double pendingDepositsTotal) {
		this.pendingDepositsTotal = pendingDepositsTotal;
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
