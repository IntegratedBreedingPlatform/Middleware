package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.Map;

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

	@JsonView({InventoryView.LotView.class})
	private Map<Integer, Object> attributeTypesValueMap;

	@JsonIgnore
	private String germplasmUUID;

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public Double getActualBalance() {
		return this.actualBalance;
	}

	public void setActualBalance(final Double actualBalance) {
		this.actualBalance = actualBalance;
	}

	public Double getAvailableBalance() {
		return this.availableBalance;
	}

	public void setAvailableBalance(final Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Double getReservedTotal() {
		return this.reservedTotal;
	}

	public void setReservedTotal(final Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}

	public Double getWithdrawalTotal() {
		return this.withdrawalTotal;
	}

	public void setWithdrawalTotal(final Double withdrawalTotal) {
		this.withdrawalTotal = withdrawalTotal;
	}

	public Date getLastDepositDate() {
		return this.lastDepositDate;
	}

	public void setLastDepositDate(final Date lastDepositDate) {
		this.lastDepositDate = lastDepositDate;
	}

	public Date getLastWithdrawalDate() {
		return this.lastWithdrawalDate;
	}

	public void setLastWithdrawalDate(final Date lastWithdrawalDate) {
		this.lastWithdrawalDate = lastWithdrawalDate;
	}

	public Integer getMgid() {
		return this.mgid;
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
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbr() {
		return this.locationAbbr;
	}

	public void setLocationAbbr(final String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	public String getUnitName() {
		return this.unitName;
	}

	public void setUnitName(final String unitName) {
		this.unitName = unitName;
	}

	public String getCreatedByUsername() {
		return this.createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public Double getPendingDepositsTotal() {
		return this.pendingDepositsTotal;
	}

	public void setPendingDepositsTotal(final Double pendingDepositsTotal) {
		this.pendingDepositsTotal = pendingDepositsTotal;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public Map<Integer, Object> getAttributeTypesValueMap() {
		return attributeTypesValueMap;
	}

	public void setAttributeTypesValueMap(final Map<Integer, Object> attributeTypesValueMap) {
		this.attributeTypesValueMap = attributeTypesValueMap;
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
