
package org.generationcp.middleware.service.pedigree.cache.keys;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CropMethodKey {

	private String cropName;

	private Integer methodId;

	public CropMethodKey() {

	}

	public CropMethodKey(final String cropName, final Integer methodId) {
		this.cropName = cropName;
		this.methodId = methodId;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public Integer getMethodId() {
		return this.methodId;
	}

	public void setMethodId(final Integer methodId) {
		this.methodId = methodId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof CropMethodKey)) {
			return false;
		}
		final CropMethodKey castOther = (CropMethodKey) other;
		return new EqualsBuilder().append(this.cropName, castOther.cropName).append(this.methodId, castOther.methodId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.cropName).append(this.methodId).toHashCode();
	}

}
