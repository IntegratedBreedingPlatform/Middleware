
package org.generationcp.middleware.service.pedigree.cache.keys;

import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

public class CropNameTypeKey {

	private List<String> nameTypeOrder;

	private String cropName;

	public CropNameTypeKey(List<String> nameTypeOrder, String cropName) {
		this.nameTypeOrder = nameTypeOrder;
		this.cropName = cropName;
	}

	public List<String> getNameTypeOrder() {
		return nameTypeOrder;
	}

	public void setNameTypeOrder(final List<String> nameTypeOrder) {
		this.nameTypeOrder = nameTypeOrder;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof CropNameTypeKey))
			return false;
		CropNameTypeKey castOther = (CropNameTypeKey) other;
		return new EqualsBuilder().append(nameTypeOrder, castOther.nameTypeOrder).append(cropName, castOther.cropName).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(nameTypeOrder).append(cropName).toHashCode();
	}

}
