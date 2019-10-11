package org.generationcp.middleware.domain.inventory_new;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class TransactionDto {

	private Integer lotId;
	private Integer gid;
	private String designation;
	private String stockId;
	private Integer transactionId;
	private String user;
	private String transactionType;
	private Double amount;
	private String notes;
	private Integer scaleId;
	private String scaleName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date transactionDate;

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
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

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(final Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(final Integer lotId) {
		this.lotId = lotId;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(final Date transactionDate) {
		this.transactionDate = transactionDate;
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
