package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, Object> additionalInfo;

	private Integer transactionId;
	private String createdByUsername;
	private String transactionType;
	private String transactionStatus;
	private Double amount;
	private String notes;

	@JsonView(BrapiView.BrapiV2.class)
	private String transactionDescription;

	@JsonView(BrapiView.BrapiV2.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date transactionTimestamp;

	@JsonView(BrapiView.BrapiV2.class)
	private String units;

	@JsonView(BrapiView.BrapiV2.class)
	private Integer transactionDbId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date createdDate;

	private ExtendedLotDto lot;

	public TransactionDto() {
		this.lot = new ExtendedLotDto();
	}

	public TransactionDto(
		final Integer transactionId, final String createdByUsername, final String transactionType, final Double amount,
		final String notes,
		final Date createdDate, final Integer lotId, final String lotUUID, final Integer gid, final String designation,
		final String stockId,
		final Integer scaleId, final String scaleName, final String lotStatus, final String transactionStatus, final Integer locationId,
		final String locationName, final String locationAbbr, final String comments) {

		this.transactionId = transactionId;
		this.createdByUsername = createdByUsername;
		this.transactionType = transactionType;
		this.amount = amount;
		this.notes = notes;
		this.createdDate = createdDate;
		this.transactionStatus = transactionStatus;
		this.lot = new ExtendedLotDto();
		this.lot.setLotId(lotId);
		this.lot.setLotUUID(lotUUID);
		this.lot.setGid(gid);
		this.lot.setStockId(stockId);
		this.lot.setUnitId(scaleId);
		this.lot.setStatus(lotStatus);
		this.lot.setLocationId(locationId);
		this.lot.setLocationName(locationName);
		this.lot.setLocationAbbr(locationAbbr);
		this.lot.setNotes(comments);
		this.lot.setUnitName(scaleName);
		this.lot.setDesignation(designation);
	}

	public TransactionDto(
		final Integer transactionId, final String createdByUsername, final String transactionType, final Double amount,
		final String notes,	final Date createdDate, final String lotUUID, final Integer gid, final String scaleName,
		final String transactionStatus,	final Integer locationId, final String locationName, final String locationAbbr) {
		this.additionalInfo =  new HashMap<>();
		this.additionalInfo.put("createdByUsername", createdByUsername);
		this.additionalInfo.put("transactionType", transactionType);
		this.additionalInfo.put("transactionStatus", transactionStatus);
		this.additionalInfo.put("seedLotID ", lotUUID);
		this.additionalInfo.put("germplasmDbId", gid);
		this.additionalInfo.put("locationId", locationId);
		this.additionalInfo.put("locationName", locationName);
		this.additionalInfo.put("locationAbbr", locationAbbr);
		this.amount = amount;
		this.transactionDescription = notes;
		this.transactionTimestamp = createdDate;
		this.units = scaleName;
		this.transactionDbId = transactionId;
	}

	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(final Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getCreatedByUsername() {
		return this.createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(final Double amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public ExtendedLotDto getLot() {
		return this.lot;
	}

	public void setLot(final ExtendedLotDto lot) {
		this.lot = lot;
	}

	public String getTransactionStatus() {
		return this.transactionStatus;
	}

	public void setTransactionStatus(final String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(final String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}

	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public void setTransactionTimestamp(final Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(final String units) {
		this.units = units;
	}

	public Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, Object> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Integer getTransactionDbId() {
		return transactionDbId;
	}

	public void setTransactionDbId(final Integer transactionDbId) {
		this.transactionDbId = transactionDbId;
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
