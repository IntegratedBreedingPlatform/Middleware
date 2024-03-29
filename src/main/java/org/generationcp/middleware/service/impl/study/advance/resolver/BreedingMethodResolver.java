package org.generationcp.middleware.service.impl.study.advance.resolver;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.advance.AdvanceUtils;

import java.util.Map;

public class BreedingMethodResolver {

	/**
	 *  Returns the breeding method. This could be the one provided by using the same for each advance or choosing a variate that defines
	 *  the breeding method for each advance
	 *
	 * @param request
	 * @param plotObservation
	 * @param breedingMethodsByCode
	 * @param breedingMethodsById
	 * @return
	 */
	public Method resolveBreedingMethod(final AdvanceStudyRequest.BreedingMethodSelectionRequest request,
		final ObservationUnitRow plotObservation, final Map<String, Method> breedingMethodsByCode,
		final Map<Integer, Method> breedingMethodsById) {
		final Integer breedingMethodId = request.getMethodVariateId() == null ? request.getBreedingMethodId() :
			this.getBreedingMethodId(request.getMethodVariateId(), plotObservation, breedingMethodsByCode);
		if (breedingMethodId == null) {
			return null;
		}
		return breedingMethodsById.get(breedingMethodId);
	}

	private Integer getBreedingMethodId(final Integer methodVariateId, final ObservationUnitRow plotObservation,
		final Map<String, Method> breedingMethodsByCode) {
		if (TermId.BREEDING_METHOD_VARIATE.getId() == methodVariateId) {
			return AdvanceUtils.getIntegerValue(plotObservation.getVariableValueByVariableId(methodVariateId));
		} else if (TermId.BREEDING_METHOD_VARIATE_TEXT.getId() == methodVariateId
			|| TermId.BREEDING_METHOD_VARIATE_CODE.getId() == methodVariateId) {
			final String methodName = plotObservation.getVariableValueByVariableId(methodVariateId);
			if (StringUtils.isEmpty(methodName)) {
				return null;
			}
			if (NumberUtils.isNumber(methodName)) {
				return Double.valueOf(methodName).intValue();
			}

			// coming from old fb or other sources
			final Method method = breedingMethodsByCode.get(methodName);
			if (method != null && (
				(methodVariateId == TermId.BREEDING_METHOD_VARIATE_TEXT.getId() && methodName.equalsIgnoreCase(method.getMname())) ||
					(methodVariateId == TermId.BREEDING_METHOD_VARIATE_CODE.getId() && methodName
						.equalsIgnoreCase(method.getMcode())))) {
				return method.getMid();
			}
		} else {
			// on load of study, this has been converted to id and not the code.
			return AdvanceUtils.getIntegerValue(plotObservation.getVariableValueByVariableId(methodVariateId));
		}
		return null;
	}

}



