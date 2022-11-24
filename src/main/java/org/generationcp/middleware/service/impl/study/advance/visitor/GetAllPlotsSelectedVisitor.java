package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSampledPlantsRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;

public class GetAllPlotsSelectedVisitor implements AdvanceRequestVisitor<Boolean> {

	@Override
	public Boolean visit(final AdvanceStudyRequest request) {
		return request.getBulkingRequest() == null ? null : request.getBulkingRequest().getAllPlotsSelected();
	}

	@Override
	public Boolean visit(final AdvanceSampledPlantsRequest request) {
		return null;
	}

}
