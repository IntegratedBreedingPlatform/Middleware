package org.generationcp.middleware.enumeration;

public enum SampleListType {
	FOLDER, SAMPLE_LIST;

	public static boolean isSampleList(String type) {
		SampleListType sampleListType;
		try {
			sampleListType = SampleListType.valueOf(type);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return isSampleList(sampleListType);
	}

	public static boolean isSampleList(SampleListType type) {
		return SAMPLE_LIST.equals(type);
	}
}
