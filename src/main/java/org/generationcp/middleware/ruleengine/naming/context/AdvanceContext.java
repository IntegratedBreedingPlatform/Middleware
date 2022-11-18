package org.generationcp.middleware.ruleengine.naming.context;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import java.util.List;
import java.util.Map;

public class AdvanceContext {

	private static ThreadLocal<List<MeasurementVariable>> studyEnvironmentVariables = new ThreadLocal<>();
	private static ThreadLocal<Map<String, String>> locationsNamesByIds = new ThreadLocal<>();
	private static ThreadLocal<Map<Integer, StudyInstance>> studyInstancesByInstanceNumber = new ThreadLocal<>();
	private static ThreadLocal<Map<Integer, MeasurementVariable>> environmentVariablesByTermId = new ThreadLocal<>();
	private static ThreadLocal<Map<Integer, Variable>> variablesByTermId = new ThreadLocal<>();

	public static List<MeasurementVariable> getStudyEnvironmentVariables() {
		return studyEnvironmentVariables.get();
	}

	public static void setStudyEnvironmentVariables(final List<MeasurementVariable> studyEnvironmentVariables) {
		AdvanceContext.studyEnvironmentVariables.set(studyEnvironmentVariables);
	}

	public static Map<String, String> getLocationsNamesByIds() {
		return locationsNamesByIds.get();
	}

	public static void setLocationsNamesByIds(final Map<String, String> locationsNamesByIds) {
		AdvanceContext.locationsNamesByIds.set(locationsNamesByIds);
	}

	public static Map<Integer, StudyInstance> getStudyInstancesByInstanceNumber() {
		return studyInstancesByInstanceNumber.get();
	}

	public static void setStudyInstancesByInstanceNumber(final Map<Integer, StudyInstance> studyInstancesByInstanceNumber) {
		AdvanceContext.studyInstancesByInstanceNumber.set(studyInstancesByInstanceNumber);
	}

	public static Map<Integer, MeasurementVariable> getEnvironmentVariablesByTermId() {
		return environmentVariablesByTermId.get();
	}

	public static void setEnvironmentVariablesByTermId(final Map<Integer, MeasurementVariable> environmentVariablesByTermId) {
		AdvanceContext.environmentVariablesByTermId.set(environmentVariablesByTermId);
	}

	public static Map<Integer, Variable> getVariablesByTermId() {
		return variablesByTermId.get();
	}

	public static void setVariablesByTermId(final Map<Integer, Variable> variablesByTermId) {
		AdvanceContext.variablesByTermId.set(variablesByTermId);
	}
}
