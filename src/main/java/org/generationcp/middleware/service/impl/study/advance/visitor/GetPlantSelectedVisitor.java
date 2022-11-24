package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSampledPlantsRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.advance.resolver.PlantSelectedResolver;

import java.util.List;
import java.util.Map;

public class GetPlantSelectedVisitor implements AdvanceRequestVisitor<Integer> {

	private final ObservationUnitRow row;
	private final Map<String, Method> breedingMethodsByCode;
	private final Method germplasmOriginBreedingMethod;
	private final List<Integer> sampleNumbers;

	public GetPlantSelectedVisitor(final ObservationUnitRow row,
		final Map<String, Method> breedingMethodsByCode, final Method germplasmOriginBreedingMethod, final List<Integer> sampleNumbers) {
		this.row = row;
		this.breedingMethodsByCode = breedingMethodsByCode;
		this.germplasmOriginBreedingMethod = germplasmOriginBreedingMethod;
		this.sampleNumbers = sampleNumbers;
	}

	@Override
	public Integer visit(final AdvanceStudyRequest request) {
		return new PlantSelectedResolver().resolveAdvanceStudyPlantSelected(request, this.row, this.breedingMethodsByCode,
			this.germplasmOriginBreedingMethod.isBulkingMethod());
	}

	@Override
	public Integer visit(final AdvanceSampledPlantsRequest request) {
		return CollectionUtils.isEmpty(this.sampleNumbers) ? null : this.sampleNumbers.size();
	}

}
