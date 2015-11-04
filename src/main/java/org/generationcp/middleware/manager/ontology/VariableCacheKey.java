
package org.generationcp.middleware.manager.ontology;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VariableCacheKey {

	private Integer id;
	private String cropName;

	public VariableCacheKey() {

	}

	public VariableCacheKey(final Integer id, final String cropName) {
		this.id = id;
		this.cropName = cropName;
	}

	public Integer getId() {
		return this.id;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof VariableCacheKey)) {
			return false;
		}
		final VariableCacheKey castOther = (VariableCacheKey) other;
		return new EqualsBuilder().append(this.id, castOther.id).append(this.cropName, castOther.cropName).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).append(this.cropName).hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(this.id).append(this.cropName).toString();
	}

}
