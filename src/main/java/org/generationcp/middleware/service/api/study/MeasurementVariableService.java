package org.generationcp.middleware.service.api.study;

import java.util.List;


public interface MeasurementVariableService {

	List<MeasurementVariableDto> getVariables(final int studyIdentifier,Integer... variableTypes);

}
