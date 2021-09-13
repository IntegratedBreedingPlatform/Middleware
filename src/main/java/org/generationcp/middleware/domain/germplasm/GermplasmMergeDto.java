package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class GermplasmMergeDto {

	private Integer gid;
	private String designation;
	private Date modifiedDate;

	public GermplasmMergeDto(final Integer gid, final String designation, final Date modifiedDate) {
		this.gid = gid;
		this.designation = designation;
		this.modifiedDate = modifiedDate;
	}

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

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(final Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
