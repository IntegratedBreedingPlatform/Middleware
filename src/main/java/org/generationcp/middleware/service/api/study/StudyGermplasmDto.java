
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class StudyGermplasmDto implements Serializable {

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
	 * @param desingation the desingation to set
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
	public boolean equals(final Object other) {
		if (!(other instanceof StudyGermplasmDto)) {
			return false;
		}
		final StudyGermplasmDto castOther = (StudyGermplasmDto) other;
		return new EqualsBuilder().append(this.entryType, castOther.entryType).append(this.germplasmId, castOther.germplasmId)
			.append(this.designation, castOther.designation).append(this.entryNumber, castOther.entryNumber)
			.append(this.entryCode, castOther.entryCode).append(this.position, castOther.position).append(this.cross, castOther.cross)
			.append(this.seedSource, castOther.seedSource).append(this.checkType, castOther.checkType)
			.append(this.groupId, castOther.groupId).append(this.stockIds, castOther.stockIds).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.entryType).append(this.germplasmId).append(this.designation).append(this.entryNumber)
			.append(this.entryCode).append(this.position).append(this.cross).append(this.seedSource).append(this.checkType)
			.append(this.groupId).append(this.stockIds).toHashCode();
	}

}
