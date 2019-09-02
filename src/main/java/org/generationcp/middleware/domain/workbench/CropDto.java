package org.generationcp.middleware.domain.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.CropType;

public class CropDto {

	private String cropName;

	public CropDto() {
	}

	public CropDto(final CropType cropType) {
		this.cropName = cropType.getCropName();
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		final CropDto cropDto = (CropDto) o;

		return new EqualsBuilder()
			.append(cropName, cropDto.cropName)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(cropName)
			.toHashCode();
	}
}
