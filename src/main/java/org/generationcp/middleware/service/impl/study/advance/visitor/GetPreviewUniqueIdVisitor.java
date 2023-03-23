package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

public class GetPreviewUniqueIdVisitor implements AdvanceRequestVisitor<String> {

	private final ObservationUnitRow row;
	private final SampleDTO sampleDTO;
	private final Integer selectionNumber;

	public GetPreviewUniqueIdVisitor(final ObservationUnitRow row,
		final SampleDTO sampleDTO, final Integer selectionNumber) {
		this.row = row;
		this.sampleDTO = sampleDTO;
		this.selectionNumber = selectionNumber;
	}

	@Override
	public String visit(final AdvanceStudyRequest request) {
		if (request.hasMultipleLinesSelected()) {
			return this.row.getObservationUnitId() + DELIMETER + this.selectionNumber;
		} else {
			return String.valueOf(this.row.getObservationUnitId());
		}
	}

	@Override
	public String visit(final AdvanceSamplesRequest request) {
		return String.valueOf(this.sampleDTO.getSampleId());
	}

}
