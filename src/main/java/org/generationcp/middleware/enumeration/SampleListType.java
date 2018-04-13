package org.generationcp.middleware.enumeration;

public enum SampleListType {
	FOLDER("FOLDER"), SAMPLE_LIST("SAMPLE LIST");

	private final String displayName;

	SampleListType(final String displayName) {
		this.displayName = displayName;
	}

	public static boolean isSampleList(final String type) {
		SampleListType sampleListType;
		try {
			sampleListType = SampleListType.valueOf(type);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return isSampleList(sampleListType);
	}

	public static boolean isSampleList(final SampleListType type) {
		return SAMPLE_LIST.equals(type);
	}

	public String getDisplayName() {
		return this.displayName;
	}
}
