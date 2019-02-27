package org.generationcp.middleware.domain.labelprinting;

public enum LabelPrintingType {

	SUBOBSERVATION_DATASET("SubObservationDataset");

	private String code;

	LabelPrintingType(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String value) {
		this.code = code;
	}

	public static LabelPrintingType getEnumByCode(final String code) {
		for (LabelPrintingType e : LabelPrintingType.values()) {
			if (code.equals(e.getCode()))
				return e;
		}
		return null;
	}
}
