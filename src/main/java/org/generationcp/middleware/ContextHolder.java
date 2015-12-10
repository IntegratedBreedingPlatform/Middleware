
package org.generationcp.middleware;

import org.apache.commons.lang3.StringUtils;

public class ContextHolder {

	private static ThreadLocal<String> currentCrop = new ThreadLocal<>();

	public static void setCurrentCrop(final String crop) {
		ContextHolder.currentCrop.set(crop);
	}

	public static String getCurrentCrop() {
		final String currentCropName = ContextHolder.currentCrop.get();
		if (StringUtils.isBlank(currentCropName)) {
			// Should only rarely happen, most of the time due to programming errors.
			throw new IllegalStateException("Unable to use variable cache. Current crop database is unknown.");
		}
		return currentCropName;
	}
}
