package org.generationcp.middleware.api.study;

public interface AdvanceRequestVisitor<T> {

	T visit(AdvanceStudyRequest request);

	T visit(AdvanceSampledPlantsRequest request);

}
