package org.generationcp.middleware.api.study;

public interface AdvanceRequestVisitor<T> {

	public static final String DELIMETER = "-";

	T visit(AdvanceStudyRequest request);

	T visit(AdvanceSamplesRequest request);

}
