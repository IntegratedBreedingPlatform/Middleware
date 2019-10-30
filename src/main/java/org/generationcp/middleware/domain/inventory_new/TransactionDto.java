package org.generationcp.middleware.domain.inventory_new;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

	private Integer transactionId;
	private String user;
	private String transactionType;
	private Double amount;
	private String notes;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date transactionDate;

	private LotDto lot;

	public TransactionDto() {
		this.lot = new LotDto();
	}

	public TransactionDto(final Integer transactionId, final String user, final String transactionType, final Double amount,
		final String notes,
		final Date transactionDate, final Integer lotId, final Integer gid, final String designation, final String stockId,
		final Integer scaleId, final String scaleName) {
		this.transactionId = transactionId;
		this.user = user;
		this.transactionType = transactionType;
		this.amount = amount;
		this.notes = notes;
		this.transactionDate = transactionDate;
		this.lot = new LotDto();
		this.lot.setLotId(lotId);
		this.lot.setGid(gid);
		this.lot.setDesignation(designation);
		this.lot.setStockId(stockId);
		this.lot.setScaleId(scaleId);
		this.lot.setScaleName(scaleName);

	}

	public Integer getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(final Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(final String user) {
		this.user = user;
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

	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(final Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public LotDto getLot() {
		return lot;
	}

	public void setLot(final LotDto lot) {
		this.lot = lot;
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
