package org.generationcp.middleware.domain.labelprinting;

public enum PresetType {

	LABEL_PRINTING_PRESET ("LabelPrintingPreset"),
	CROSSING_PRESET("CrossingPreset"),
	ATTRIBUTES_PROPAGATION_PRESET("AttributesPropagationPreset");

	private String name;

	PresetType(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public static PresetType getEnum(final String name) {
		for (PresetType e : PresetType.values()) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}
}
