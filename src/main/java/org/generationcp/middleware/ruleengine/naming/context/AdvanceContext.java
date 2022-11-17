package org.generationcp.middleware.ruleengine.naming.context;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import java.util.List;

public class AdvanceContext {

	private static ThreadLocal<Integer> studyId = new ThreadLocal<>();
	private static ThreadLocal<Integer> environmentDatasetId = new ThreadLocal<>();
	private static ThreadLocal<List<MeasurementVariable>> studyEnvironmentVariables = new ThreadLocal<>();

	public static void setStudyId(final Integer studyId) {
		AdvanceContext.studyId.set(studyId);
	}

	public static Integer getStudyId() {
		return studyId.get();
	}

	public static Integer getEnvironmentDatasetId() {
		return environmentDatasetId.get();
	}

	public static void setEnvironmentDatasetId(final Integer environmentDatasetId) {
		AdvanceContext.environmentDatasetId.set(environmentDatasetId);
	}

	public static List<MeasurementVariable> getStudyEnvironmentVariables() {
		return studyEnvironmentVariables.get();
	}

	public static void setStudyEnvironmentVariables(final List<MeasurementVariable> studyEnvironmentVariables) {
		AdvanceContext.studyEnvironmentVariables.set(studyEnvironmentVariables);
	}

}
