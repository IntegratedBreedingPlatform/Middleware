
package org.generationcp.middleware.service.pedigree.cache.keys;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CropGermplasmKey {

	private Integer gid;

	private String cropName;

	public CropGermplasmKey() {

	}

	public CropGermplasmKey(final String cropName, final Integer gid) {
		this.cropName = cropName;
		this.gid = gid;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof CropGermplasmKey)) {
			return false;
		}
		final CropGermplasmKey castOther = (CropGermplasmKey) other;
		return new EqualsBuilder().append(this.cropName, castOther.cropName).append(this.gid, castOther.gid).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.cropName).append(this.gid).toHashCode();
	}

}
