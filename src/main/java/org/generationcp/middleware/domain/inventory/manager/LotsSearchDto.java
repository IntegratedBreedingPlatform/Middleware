package org.generationcp.middleware.domain.inventory.manager;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class LotsSearchDto extends SearchRequestDto {

	private Integer status;

	private List<Integer> lotIds;

	private List<String> lotUUIDs;

	private String stockId;

	private List<Integer> gids;

	private List<Integer> mgids;

	private String designation;

	private List<Integer> locationIds;

	private List<Integer> unitIds;

	private Double minActualBalance;

	private Double maxActualBalance;

	private Double minAvailableBalance;

	private Double maxAvailableBalance;

	private Double minReservedTotal;

	private Double maxReservedTotal;

	private Double minWithdrawalTotal;

	private Double maxWithdrawalTotal;

	private Double minPendingDepositsTotal;

	private Double maxPendingDepositsTotal;

	private String createdByUsername;

	private List<Integer> germplasmListIds;

	private List<Integer> plantingStudyIds;

	private List<Integer> harvestingStudyIds;

	private List<String> germplasmUUIDs;

	private Map<Integer, Object> attributeFilters;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createdDateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createdDateTo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date lastDepositDateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date lastDepositDateTo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date lastWithdrawalDateFrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date lastWithdrawalDateTo;

	private String notesContainsString;

	private String locationNameContainsString;

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public List<Integer> getLotIds() {
		return this.lotIds;
	}

	public void setLotIds(final List<Integer> lotIds) {
		this.lotIds = lotIds;
	}

	public List<String> getLotUUIDs() {
		return this.lotUUIDs;
	}

	public void setLotUUIDs(final List<String> lotUUIDs) {
		this.lotUUIDs = lotUUIDs;
	}

	public String getStockId() {
		return this.stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public List<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public List<Integer> getMgids() {
		return this.mgids;
	}

	public void setMgids(final List<Integer> mgids) {
		this.mgids = mgids;
	}

	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public List<Integer> getLocationIds() {
		return this.locationIds;
	}

	public void setLocationIds(final List<Integer> locationIds) {
		this.locationIds = locationIds;
	}

	public List<Integer> getUnitIds() {
		return this.unitIds;
	}

	public void setUnitIds(final List<Integer> unitIds) {
		this.unitIds = unitIds;
	}

	public Double getMinActualBalance() {
		return this.minActualBalance;
	}

	public void setMinActualBalance(final Double minActualBalance) {
		this.minActualBalance = minActualBalance;
	}

	public Double getMaxActualBalance() {
		return this.maxActualBalance;
	}

	public void setMaxActualBalance(final Double maxActualBalance) {
		this.maxActualBalance = maxActualBalance;
	}

	public Double getMinAvailableBalance() {
		return this.minAvailableBalance;
	}

	public void setMinAvailableBalance(final Double minAvailableBalance) {
		this.minAvailableBalance = minAvailableBalance;
	}

	public Double getMaxAvailableBalance() {
		return this.maxAvailableBalance;
	}

	public void setMaxAvailableBalance(final Double maxAvailableBalance) {
		this.maxAvailableBalance = maxAvailableBalance;
	}

	public Double getMinReservedTotal() {
		return this.minReservedTotal;
	}

	public void setMinReservedTotal(final Double minReservedTotal) {
		this.minReservedTotal = minReservedTotal;
	}

	public Double getMaxReservedTotal() {
		return this.maxReservedTotal;
	}

	public void setMaxReservedTotal(final Double maxReservedTotal) {
		this.maxReservedTotal = maxReservedTotal;
	}

	public Double getMinWithdrawalTotal() {
		return this.minWithdrawalTotal;
	}

	public void setMinWithdrawalTotal(final Double minWithdrawalTotal) {
		this.minWithdrawalTotal = minWithdrawalTotal;
	}

	public Double getMaxWithdrawalTotal() {
		return this.maxWithdrawalTotal;
	}

	public void setMaxWithdrawalTotal(final Double maxWithdrawalTotal) {
		this.maxWithdrawalTotal = maxWithdrawalTotal;
	}

	public String getCreatedByUsername() {
		return this.createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
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

	public Date getLastDepositDateFrom() {
		return this.lastDepositDateFrom;
	}

	public void setLastDepositDateFrom(final Date lastDepositDateFrom) {
		this.lastDepositDateFrom = lastDepositDateFrom;
	}

	public Date getLastDepositDateTo() {
		return this.lastDepositDateTo;
	}

	public void setLastDepositDateTo(final Date lastDepositDateTo) {
		this.lastDepositDateTo = lastDepositDateTo;
	}

	public Date getLastWithdrawalDateFrom() {
		return this.lastWithdrawalDateFrom;
	}

	public void setLastWithdrawalDateFrom(final Date lastWithdrawalDateFrom) {
		this.lastWithdrawalDateFrom = lastWithdrawalDateFrom;
	}

	public Date getLastWithdrawalDateTo() {
		return this.lastWithdrawalDateTo;
	}

	public void setLastWithdrawalDateTo(final Date lastWithdrawalDateTo) {
		this.lastWithdrawalDateTo = lastWithdrawalDateTo;
	}

	public String getNotesContainsString() {
		return this.notesContainsString;
	}

	public void setNotesContainsString(final String notesContainsString) {
		this.notesContainsString = notesContainsString;
	}

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

	public String getLocationNameContainsString() {
		return this.locationNameContainsString;
	}

	public void setLocationNameContainsString(final String locationNameContainsString) {
		this.locationNameContainsString = locationNameContainsString;
	}

	public Double getMinPendingDepositsTotal() {
		return this.minPendingDepositsTotal;
	}

	public void setMinPendingDepositsTotal(final Double minPendingDepositTotal) {
		this.minPendingDepositsTotal = minPendingDepositTotal;
	}

	public Double getMaxPendingDepositsTotal() {
		return this.maxPendingDepositsTotal;
	}

	public void setMaxPendingDepositsTotal(final Double maxPendingDepositTotal) {
		this.maxPendingDepositsTotal = maxPendingDepositTotal;
	}

	public List<String> getGermplasmUUIDs() {
		return this.germplasmUUIDs;
	}

	public void setGermplasmUUIDs(final List<String> germplasmUUIDs) {
		this.germplasmUUIDs = germplasmUUIDs;
	}

	public Map<Integer, Object> getAttributeFilters() {
		return this.attributeFilters;
	}

	public void setAttributeFilters(final Map<Integer, Object> attributeFilters) {
		this.attributeFilters = attributeFilters;
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
