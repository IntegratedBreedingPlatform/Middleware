
package org.generationcp.middleware.service.pedigree.cache.keys;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CropNameTypeKey {

	private List<String> nameTypeOrder;

	private String cropName;

	public CropNameTypeKey() {

	}

	public CropNameTypeKey(final List<String> nameTypeOrder, final String cropName) {
		this.nameTypeOrder = nameTypeOrder;
		this.cropName = cropName;
	}

	public List<String> getNameTypeOrder() {
		return this.nameTypeOrder;
	}

	public void setNameTypeOrder(final List<String> nameTypeOrder) {
		this.nameTypeOrder = nameTypeOrder;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof CropNameTypeKey)) {
			return false;
		}
		final CropNameTypeKey castOther = (CropNameTypeKey) other;
		return new EqualsBuilder().append(this.nameTypeOrder, castOther.nameTypeOrder).append(this.cropName, castOther.cropName).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.nameTypeOrder).append(this.cropName).toHashCode();
	}

}
