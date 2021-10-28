package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
public class TransactionsSearchDto extends SearchRequestDto {

	private String designation;
	private String stockId;
	private List<Integer> transactionIds;
	private String createdByUsername;
	private List<Integer> transactionTypes;
	private List<Integer> transactionStatus;
	private String notes;
	private List<Integer> lotIds;
	private List<String> lotUUIDs;
	private String lotLocationAbbr;
	private List<Integer> gids;
	private List<Integer> unitIds;
	private Double minAmount;
	private Double maxAmount;
	private List<String> germplasmUUIDs;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createdDateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createdDateTo;

	private List<Integer> statusIds;
	private Integer lotStatus;
	private List<Integer> germplasmListIds;
	private List<Integer> plantingStudyIds;
	private List<Integer> harvestingStudyIds;
	private List<String> observationUnitIds;

	public List<Integer> getGermplasmListIds() {
		return this.germplasmListIds;
	}

	public void setGermplasmListIds(final List<Integer> germplasmListIds) {
		this.germplasmListIds = germplasmListIds;
	}

	public List<Integer> getPlantingStudyIds() {
		return this.plantingStudyIds;
	}

	public void setPlantingStudyIds(final List<Integer> plantingStudyIds) {
		this.plantingStudyIds = plantingStudyIds;
	}

	public List<Integer> getHarvestingStudyIds() {
		return this.harvestingStudyIds;
	}

	public void setHarvestingStudyIds(final List<Integer> harvestingStudyIds) {
		this.harvestingStudyIds = harvestingStudyIds;
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

	public String getLotLocationAbbr() {
		return this.lotLocationAbbr;
	}

	public void setLotLocationAbbr(final String lotLocationAbbr) {
		this.lotLocationAbbr = lotLocationAbbr;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public List<Integer> getUnitIds() {
		return this.unitIds;
	}

	public void setUnitIds(final List<Integer> unitIds) {
		this.unitIds = unitIds;
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

	public Date getCreatedDateFrom() {
		return this.createdDateFrom;
	}

	public void setCreatedDateFrom(final Date createdDateFrom) {
		this.createdDateFrom = createdDateFrom;
	}

	public Date getCreatedDateTo() {
		return this.createdDateTo;
	}

	public void setCreatedDateTo(final Date createdDateTo) {
		this.createdDateTo = createdDateTo;
	}

	public List<Integer> getStatusIds() {
		return this.statusIds;
	}

	public void setStatusIds(final List<Integer> statusIds) {
		this.statusIds = statusIds;
	}

	public Integer getLotStatus() {
		return this.lotStatus;
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

	public List<String> getLotUUIDs() {
		return this.lotUUIDs;
	}

	public void setLotUUIDs(final List<String> lotUUIDs) {
		this.lotUUIDs = lotUUIDs;
	}

	public List<String> getGermplasmUUIDs() {
		return this.germplasmUUIDs;
	}

	public void setGermplasmUUIDs(final List<String> germplasmUUIDs) {
		this.germplasmUUIDs = germplasmUUIDs;
	}

	public List<String> getObservationUnitIds() {
		return this.observationUnitIds;
	}

	public void setObservationUnitIds(final List<String> observationUnitIds) {
		this.observationUnitIds = observationUnitIds;
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
