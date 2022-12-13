package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.advance.resolver.BreedingMethodResolver;

import java.util.Map;

public class GetBreedingMethodVisitor implements AdvanceRequestVisitor<Method> {

	private final ObservationUnitRow row;
	private final Map<Integer, Method> breedingMethodsById;
	private final Map<String, Method> breedingMethodsByCode;

	public GetBreedingMethodVisitor(final ObservationUnitRow row,
		final Map<Integer, Method> breedingMethodsById,
		final Map<String, Method> breedingMethodsByCode) {
		this.row = row;
		this.breedingMethodsById = breedingMethodsById;
		this.breedingMethodsByCode = breedingMethodsByCode;
	}

	@Override
	public Method visit(final AdvanceStudyRequest request) {
		return new BreedingMethodResolver()
			.resolveBreedingMethod(request.getBreedingMethodSelectionRequest(), this.row, this.breedingMethodsByCode,
				this.breedingMethodsById);
	}

	@Override
	public Method visit(final AdvanceSamplesRequest request) {
		return this.breedingMethodsById.get(request.getBreedingMethodId());
	}

}
