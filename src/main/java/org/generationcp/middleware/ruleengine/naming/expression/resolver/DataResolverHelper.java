package org.generationcp.middleware.ruleengine.naming.expression.resolver;

import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.util.CollectionUtils;

class DataResolverHelper {

	static boolean checkHasTrailInstanceObservations(final ObservationUnitRow observationUnitRow) {
		return observationUnitRow != null && !CollectionUtils.isEmpty(observationUnitRow.getEnvironmentVariables());
	}

}
