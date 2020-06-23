
package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class StudyGermplasmDto implements Serializable {

	private Integer entryId;

	private String entryType;

	private Integer germplasmId;

	private String designation;

	private Integer entryNumber;

	private String entryCode;

	private String position;

	private String cross;

	private String seedSource;

	private Integer checkType;

	private Integer groupId;

	private String stockIds;

	public StudyGermplasmDto() { }

	public StudyGermplasmDto(final Integer entryId) {
		this.entryId = entryId;
	}

	public StudyGermplasmDto(final Integer entryId, final String entryType, final Integer germplasmId, final String designation, final Integer entryNumber, final String entryCode, final String cross, final String seedSource) {
		this.entryId = entryId;
		this.entryType = entryType;
		this.germplasmId = germplasmId;
		this.designation = designation;
		this.entryNumber = entryNumber;
		this.entryCode = entryCode;
		this.cross = cross;
		this.seedSource = seedSource;
	}

	public StudyGermplasmDto(final StockModel stock) {
		this.setEntryId(stock.getStockId());
		this.setDesignation(stock.getName());
		this.setGermplasmId(stock.getGermplasm().getGid());
		this.setEntryCode(stock.getValue());
		this.setEntryNumber(Integer.valueOf(stock.getUniqueName()));


		final Optional<StockProperty> entryType = stock.getProperties().stream().filter(prop -> TermId.ENTRY_TYPE.getId() == (prop.getTypeId())).findFirst();
		entryType.ifPresent(entry -> this.setEntryType(entry.getValue()) );

		final Optional<StockProperty> seedSource = stock.getProperties().stream().filter(prop -> TermId.SEED_SOURCE.getId() == (prop.getTypeId())).findFirst();
		seedSource.ifPresent(source -> this.setSeedSource(source.getValue()) );

		final Optional<StockProperty> cross = stock.getProperties().stream().filter(prop -> TermId.CROSS.getId() == (prop.getTypeId())).findFirst();
		cross.ifPresent(crs -> this.setCross(crs.getValue()) );

		final Optional<StockProperty> groupGID = stock.getProperties().stream().filter(prop -> TermId.GROUPGID.getId() == (prop.getTypeId())).findFirst();
		if (groupGID.isPresent() && groupGID.get().getValue() != null) {
			this.setGroupId(Integer.valueOf(groupGID.get().getValue()));
		}
	}

	/**
	 * @return the entryType
	 */
	public String getEntryType() {
		return this.entryType;
	}

	/**
	 * @param entryType the entryType to set
	 */
	public void setEntryType(final String entryType) {
		this.entryType = entryType;
	}

	/**
	 * @return the germplasmId
	 */
	public Integer getGermplasmId() {
		return this.germplasmId;
	}

	/**
	 * @param germplasmId the germplasmId to set
	 */
	public void setGermplasmId(final Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	/**
	 * @return the desingation
	 */
	public String getDesignation() {
		return this.designation;
	}

	/**
	 * @param designation the desingation to set
	 */
	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	/**
	 * @return the entryNumber
	 */
	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	/**
	 * @param entryNumber the entryNo to set
	 */
	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return this.position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(final String position) {
		this.position = position;
	}

	/**
	 * @return the cross
	 */
	public String getCross() {
		return this.cross;
	}

	/**
	 * @param cross the cross to set
	 */
	public void setCross(final String cross) {
		this.cross = cross;
	}

	/**
	 * @return the seedSource
	 */
	public String getSeedSource() {
		return this.seedSource;
	}

	/**
	 * @param seedSource the seedSource to set
	 */
	public void setSeedSource(final String seedSource) {
		this.seedSource = seedSource;
	}

	/**
	 * @return the entryCode
	 */
	public String getEntryCode() {
		return this.entryCode;
	}

	/**
	 * @param entryCode the entryCode to set
	 */
	public void setEntryCode(final String entryCode) {
		this.entryCode = entryCode;
	}

	/**
	 * @return the check type
	 */
	public Integer getCheckType() {
		return this.checkType;
	}

	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(final Integer checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the groupGid
	 */
	public Integer getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId the groupGid to set
	 */
	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the stockIds
	 */
	public String getStockIds() {
		return this.stockIds;
	}

	/**
	 * @param stockIds the stockIds to set
	 */
	public void setStockIds(final String stockIds) {
		this.stockIds = stockIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StudyGermplasmDto that = (StudyGermplasmDto) o;
		return getEntryId().equals(that.getEntryId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEntryId());
	}

	public Integer getEntryId() {
		return entryId;
	}

	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
	}
}
