
package org.generationcp.middleware.domain.gms;

public enum SystemDefinedEntryType {
	TEST_ENTRY(10170, "T", "Test Entry"), CHECK_ENTRY(10180, "C", "Check Entry"), DISEASE_CHECK(10190, "D", "Disease Check"), STRESS_CHECK(
			10200, "S", "Stress Check");

	private final int entryTypeCategoricalId;
	private final String entryTypeValue;
	private final String entryTypeName;

	SystemDefinedEntryType(int entryTypeCategoricalId, String entryTypeValue, String entryTypeName) {
		this.entryTypeCategoricalId = entryTypeCategoricalId;
		this.entryTypeValue = entryTypeValue;
		this.entryTypeName = entryTypeName;
	}

    public int getEntryTypeCategoricalId() {
        return entryTypeCategoricalId;
    }

    public String getEntryTypeName() {
        return entryTypeName;
    }

    public String getEntryTypeValue() {
        return entryTypeValue;
    }
}
