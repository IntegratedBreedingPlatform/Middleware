package org.generationcp.middleware.domain.labelprinting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class LabelPrintingPresetDTO extends PresetDTO {

	@AutoProperty
	public static class BarcodeSetting implements Serializable {

		@JsonView(PresetDTO.View.Configuration.class)
		private boolean barcodeNeeded;

		@JsonView(PresetDTO.View.Configuration.class)
		private boolean automaticBarcode;

		@JsonView(PresetDTO.View.Configuration.class)
		private List<String> barcodeFields;

		public BarcodeSetting(){
			barcodeFields = new ArrayList<>();
		}

		public BarcodeSetting(final boolean barcodeNeeded, final boolean automaticBarcode, final List<String> barcodeFields) {
			this.barcodeNeeded = barcodeNeeded;
			this.automaticBarcode = automaticBarcode;
			this.barcodeFields = barcodeFields;
		}

		public boolean isBarcodeNeeded() {
			return barcodeNeeded;
		}

		public void setBarcodeNeeded(final boolean barcodeNeeded) {
			this.barcodeNeeded = barcodeNeeded;
		}

		public boolean isAutomaticBarcode() {
			return automaticBarcode;
		}

		public void setAutomaticBarcode(final boolean automaticBarcode) {
			this.automaticBarcode = automaticBarcode;
		}

		public List<String> getBarcodeFields() {
			return barcodeFields;
		}

		public void setBarcodeFields(final List<String> barcodeFields) {
			if (barcodeFields == null) {
				this.barcodeFields = new ArrayList<>();
			} else {
				this.barcodeFields = barcodeFields;
			}
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

	@JsonView(PresetDTO.View.Configuration.class)
	private List<List<String>> selectedFields;

	@JsonView(PresetDTO.View.Configuration.class)
	private BarcodeSetting barcodeSetting;

	@JsonView(PresetDTO.View.Configuration.class)
	private boolean includeHeadings = true;

	@JsonView(PresetDTO.View.Configuration.class)
	private FilePresetConfigurationDTO fileConfiguration;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonView(PresetDTO.View.Configuration.class)
	private String sortBy;

	public FilePresetConfigurationDTO getFileConfiguration() {
		return fileConfiguration;
	}

	public void setFileConfiguration(final FilePresetConfigurationDTO fileConfiguration) {
		this.fileConfiguration = fileConfiguration;
	}

	public List<List<String>> getSelectedFields() {
		return selectedFields;
	}

	public void setSelectedFields(final List<List<String>> selectedFields) {
		this.selectedFields = selectedFields;
	}

	public BarcodeSetting getBarcodeSetting() {
		return barcodeSetting;
	}

	public void setBarcodeSetting(final BarcodeSetting barcodeSetting) {
		this.barcodeSetting = barcodeSetting;
	}

	public Boolean isIncludeHeadings() {
		return this.includeHeadings;
	}

	public void setIncludeHeadings(final boolean includeHeadings) {
		this.includeHeadings = includeHeadings;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(final String sortBy) {
		this.sortBy = sortBy;
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
