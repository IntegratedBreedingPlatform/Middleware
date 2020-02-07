package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionsSearchDto extends SearchRequestDto {

	private String designation;
	private String stockId;
	private List<Integer> transactionIds;
	private String createdByUsername;
	private List<Integer> transactionTypes;
	private List<Integer> transactionStatus;
	private String notes;
	private List<Integer> lotIds;
	private List<Integer> gids;
	private List<Integer> scaleIds;
	private Double minAmount;
	private Double maxAmount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date transactionDateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date transactionDateTo;

	private List<Integer> statusIds;

	private Integer lotStatus;

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

	public String getCreatedByUsername() {
		return this.createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public List<Integer> getTransactionIds() {
		return this.transactionIds;
	}

	public void setTransactionIds(final List<Integer> transactionIds) {
		this.transactionIds = transactionIds;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public List<Integer> getLotIds() {
		return this.lotIds;
	}

	public void setLotIds(final List<Integer> lotIds) {
		this.lotIds = lotIds;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public List<Integer> getScaleIds() {
		return this.scaleIds;
	}

	public void setScaleIds(final List<Integer> scaleIds) {
		this.scaleIds = scaleIds;
	}

	public Double getMinAmount() {
		return this.minAmount;
	}

	public void setMinAmount(final Double minAmount) {
		this.minAmount = minAmount;
	}

	public Double getMaxAmount() {
		return this.maxAmount;
	}

	public void setMaxAmount(final Double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Date getTransactionDateFrom() {
		return this.transactionDateFrom;
	}

	public void setTransactionDateFrom(final Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return this.transactionDateTo;
	}

	public void setTransactionDateTo(final Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	public List<Integer> getStatusIds() {
		return statusIds;
	}

	public void setStatusIds(final List<Integer> statusIds) {
		this.statusIds = statusIds;
	}

	public Integer getLotStatus() {
		return lotStatus;
	}

	public void setLotStatus(final Integer lotStatus) {
		this.lotStatus = lotStatus;
	}

	public List<Integer> getTransactionTypes() {
		return this.transactionTypes;
	}

	public void setTransactionTypes(final List<Integer> transactionTypes) {
		this.transactionTypes = transactionTypes;
	}

	public List<Integer> getTransactionStatus() {
		return this.transactionStatus;
	}

	public void setTransactionStatus(final List<Integer> transactionStatus) {
		this.transactionStatus = transactionStatus;
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
