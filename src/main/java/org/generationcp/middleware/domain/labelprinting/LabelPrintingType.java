package org.generationcp.middleware.domain.labelprinting;

public enum LabelPrintingType {

	OBSERVATION_DATASET("ObservationDataset"), SUBOBSERVATION_DATASET("SubObservationDataset"),
	LOT("Lot"), GERMPLASM("Germplasm"), GERMPLASM_LIST("Germplasm List"),
	STUDY_ENTRIES("Study Entries");

	private String code;

	LabelPrintingType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public static LabelPrintingType getEnumByCode(final String code) {
		for (final LabelPrintingType e : LabelPrintingType.values()) {
			if (code.equals(e.getCode()))
				return e;
		}
		return null;
	}
}
