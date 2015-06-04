
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StudyGermplasmDto {

	private String entryType;

	private Integer germplasmId;

	private String designation;

	private String entryNo;

	private String entryCode;

	private String position;

	private String cross;

	private String seedSource;

	/**
	 * @return the entryType
	 */
	public String getEntryType() {
		return this.entryType;
	}

	/**
	 * @param entryType the entryType to set
	 */
	public void setEntryType(String entryType) {
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
	public void setGermplasmId(Integer germplasmId) {
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
	 *
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the entryNo
	 */
	public String getEntryNo() {
		return this.entryNo;
	}

	/**
	 * @param entryNo the entryNo to set
	 */
	public void setEntryNo(String entryNo) {
		this.entryNo = entryNo;
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
	public void setPosition(String position) {
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
	public void setCross(String cross) {
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
	public void setSeedSource(String seedSource) {
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
	public void setEntryCode(String entryCode) {
		this.entryCode = entryCode;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudyGermplasmDto)) {
			return false;
		}
		StudyGermplasmDto castOther = (StudyGermplasmDto) other;
		return new EqualsBuilder().append(this.entryType, castOther.entryType).append(this.germplasmId, castOther.germplasmId)
				.append(this.designation, castOther.designation).append(this.entryNo, castOther.entryNo)
				.append(this.entryCode, castOther.entryCode).append(this.position, castOther.position).append(this.cross, castOther.cross)
				.append(this.seedSource, castOther.seedSource).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.entryType).append(this.germplasmId).append(this.designation).append(this.entryNo)
				.append(this.entryCode).append(this.position).append(this.cross).append(this.seedSource).toHashCode();
	}

}
