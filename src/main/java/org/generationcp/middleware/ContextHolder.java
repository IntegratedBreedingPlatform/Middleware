
package org.generationcp.middleware;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ContextHolder {

	private static ThreadLocal<String> currentCrop = new ThreadLocal<>();
	private static ThreadLocal<String> currentProgram = new ThreadLocal<>();
	private static ThreadLocal<Integer> loggedInUserId = new ThreadLocal<>();

	public static void setCurrentCrop(final String crop) {
		ContextHolder.currentCrop.set(crop);
	}

	public static void setCurrentProgram(final String program) {
		ContextHolder.currentProgram.set(program);
	}

	public static void setLoggedInUserId(final Integer loggedInUserId) {
		ContextHolder.loggedInUserId.set(loggedInUserId);
	}

	public static String getCurrentCrop() {
		final String currentCropName = ContextHolder.currentCrop.get();
		if (StringUtils.isBlank(currentCropName)) {
			// Should only rarely happen, most of the time due to programming errors.
			throw new IllegalStateException("Unable to use variable cache. Current crop database is unknown.");
		}
		return currentCropName;
	}

	/**
	 * @deprecated use {@link #getCurrentProgramOptional()}
	 */
	@Deprecated
	public static String getCurrentProgram() {
		final String currentProgramId = ContextHolder.currentProgram.get();
		if (StringUtils.isBlank(currentProgramId)) {
			// Should only rarely happen, most of the time due to programming errors.
			throw new IllegalStateException("Unable to use variable cache. Current Program database is unknown.");
		}
		return currentProgramId;
	}

	public static Optional<String> getCurrentProgramOptional() {
		final String currentProgramId = ContextHolder.currentProgram.get();
		if (StringUtils.isBlank(currentProgramId)) {
			return Optional.empty();
		}
		return Optional.of(currentProgramId);
	}

	public static Integer getLoggedInUserId() {
		final Integer loggedInUserId = ContextHolder.loggedInUserId.get();

		if (Objects.isNull(ContextHolder.loggedInUserId)) {
			throw new IllegalStateException("Unable to use variable cache. Logged in user is null.");
		}
		return loggedInUserId;
	}

}
