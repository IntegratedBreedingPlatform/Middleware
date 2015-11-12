
package org.generationcp.middleware;

public class ContextHolder {

	private static ThreadLocal<String> currentCrop = new ThreadLocal<>();

	public static void setCurrentCrop(final String crop) {
		ContextHolder.currentCrop.set(crop);
	}

	public static String getCurrentCrop() {
		return ContextHolder.currentCrop.get();
	}
}
