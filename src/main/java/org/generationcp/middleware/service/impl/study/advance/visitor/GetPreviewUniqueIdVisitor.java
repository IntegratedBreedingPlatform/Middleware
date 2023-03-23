package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

public class GetPreviewUniqueIdVisitor implements AdvanceRequestVisitor<Integer> {

	private final ObservationUnitRow row;
	private final SampleDTO sampleDTO;

	public GetPreviewUniqueIdVisitor(final ObservationUnitRow row,
		final SampleDTO sampleDTO) {
		this.row = row;
		this.sampleDTO = sampleDTO;
	}

	@Override
	public Integer visit(final AdvanceStudyRequest request) {
		return this.row.getObservationUnitId();
	}

	@Override
	public Integer visit(final AdvanceSamplesRequest request) {
		return this.sampleDTO.getSampleId();
	}

}
