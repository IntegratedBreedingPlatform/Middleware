package org.generationcp.middleware.domain.labelprinting;

import com.fasterxml.jackson.annotation.JsonView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PDFFilePresetConfigurationDTO extends FilePresetConfigurationDTO {

	@JsonView(PresetDTO.View.Configuration.class)
	private Integer numberOfRowsPerPage;

	@JsonView(PresetDTO.View.Configuration.class)
	private Integer sizeOfLabelSheet;

	public Integer getNumberOfRowsPerPage() {
		return numberOfRowsPerPage;
	}

	public void setNumberOfRowsPerPage(final Integer numberOfRowsPerPage) {
		this.numberOfRowsPerPage = numberOfRowsPerPage;
	}

	public Integer getSizeOfLabelSheet() {
		return sizeOfLabelSheet;
	}

	public void setSizeOfLabelSheet(final Integer sizeOfLabelSheet) {
		this.sizeOfLabelSheet = sizeOfLabelSheet;
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
