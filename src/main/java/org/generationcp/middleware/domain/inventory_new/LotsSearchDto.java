package org.generationcp.middleware.domain.inventory_new;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotsSearchDto extends SearchRequestDto {

	private Integer status;

	private List<Integer> lotIds;

	private String stockId;

	private List<Integer> gids;

	private List<Integer> mgids;

	private String designation;

	private List<Integer> locationIds;

	private List<Integer> scaleIds;

	private Double minActualBalance;

	private Double maxActualBalance;

	private Double minAvailableBalance;

	private Double maxAvailableBalance;

	private Double minReservedTotal;

	private Double maxReservedTotal;

	private Double minWithdrawalTotal;

	private Double maxWithdrawalTotal;

	private String createdByUsername;

	private List<Integer> germplasmListIds;

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

	private String commentContainsString;

	private String locationNameContainsString;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public List<Integer> getLotIds() {
		return lotIds;
	}

	public void setLotIds(final List<Integer> lotIds) {
		this.lotIds = lotIds;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public List<Integer> getGids() {
		return gids;
	}

	public void setGids(final List<Integer> gids) {
		this.gids = gids;
	}

	public List<Integer> getMgids() {
		return mgids;
	}

	public void setMgids(final List<Integer> mgids) {
		this.mgids = mgids;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public List<Integer> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(final List<Integer> locationIds) {
		this.locationIds = locationIds;
	}

	public List<Integer> getScaleIds() {
		return scaleIds;
	}

	public void setScaleIds(final List<Integer> scaleIds) {
		this.scaleIds = scaleIds;
	}

	public Double getMinActualBalance() {
		return minActualBalance;
	}

	public void setMinActualBalance(final Double minActualBalance) {
		this.minActualBalance = minActualBalance;
	}

	public Double getMaxActualBalance() {
		return maxActualBalance;
	}

	public void setMaxActualBalance(final Double maxActualBalance) {
		this.maxActualBalance = maxActualBalance;
	}

	public Double getMinAvailableBalance() {
		return minAvailableBalance;
	}

	public void setMinAvailableBalance(final Double minAvailableBalance) {
		this.minAvailableBalance = minAvailableBalance;
	}

	public Double getMaxAvailableBalance() {
		return maxAvailableBalance;
	}

	public void setMaxAvailableBalance(final Double maxAvailableBalance) {
		this.maxAvailableBalance = maxAvailableBalance;
	}

	public Double getMinReservedTotal() {
		return minReservedTotal;
	}

	public void setMinReservedTotal(final Double minReservedTotal) {
		this.minReservedTotal = minReservedTotal;
	}

	public Double getMaxReservedTotal() {
		return maxReservedTotal;
	}

	public void setMaxReservedTotal(final Double maxReservedTotal) {
		this.maxReservedTotal = maxReservedTotal;
	}

	public Double getMinWithdrawalTotal() {
		return minWithdrawalTotal;
	}

	public void setMinWithdrawalTotal(final Double minWithdrawalTotal) {
		this.minWithdrawalTotal = minWithdrawalTotal;
	}

	public Double getMaxWithdrawalTotal() {
		return maxWithdrawalTotal;
	}

	public void setMaxWithdrawalTotal(final Double maxWithdrawalTotal) {
		this.maxWithdrawalTotal = maxWithdrawalTotal;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(final String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	public Date getCreatedDateFrom() {
		return createdDateFrom;
	}

	public void setCreatedDateFrom(final Date createdDateFrom) {
		this.createdDateFrom = createdDateFrom;
	}

	public Date getCreatedDateTo() {
		return createdDateTo;
	}

	public void setCreatedDateTo(final Date createdDateTo) {
		this.createdDateTo = createdDateTo;
	}

	public Date getLastDepositDateFrom() {
		return lastDepositDateFrom;
	}

	public void setLastDepositDateFrom(final Date lastDepositDateFrom) {
		this.lastDepositDateFrom = lastDepositDateFrom;
	}

	public Date getLastDepositDateTo() {
		return lastDepositDateTo;
	}

	public void setLastDepositDateTo(final Date lastDepositDateTo) {
		this.lastDepositDateTo = lastDepositDateTo;
	}

	public Date getLastWithdrawalDateFrom() {
		return lastWithdrawalDateFrom;
	}

	public void setLastWithdrawalDateFrom(final Date lastWithdrawalDateFrom) {
		this.lastWithdrawalDateFrom = lastWithdrawalDateFrom;
	}

	public Date getLastWithdrawalDateTo() {
		return lastWithdrawalDateTo;
	}

	public void setLastWithdrawalDateTo(final Date lastWithdrawalDateTo) {
		this.lastWithdrawalDateTo = lastWithdrawalDateTo;
	}

	public String getCommentContainsString() {
		return commentContainsString;
	}

	public void setCommentContainsString(final String commentContainsString) {
		this.commentContainsString = commentContainsString;
	}

	public List<Integer> getGermplasmListIds() {
		return germplasmListIds;
	}

	public void setGermplasmListIds(final List<Integer> germplasmListIds) {
		this.germplasmListIds = germplasmListIds;
	}

	public String getLocationNameContainsString() {
		return locationNameContainsString;
	}

	public void setLocationNameContainsString(final String locationNameContainsString) {
		this.locationNameContainsString = locationNameContainsString;
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
