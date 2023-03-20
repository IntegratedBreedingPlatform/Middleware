package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.sample.SampleDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSampleNumbersVisitor implements AdvanceRequestVisitor<List<SampleDTO>> {

	private final Integer experimentId;
	private final Map<Integer, List<SampleDTO>> samplesByExperimentId;

	public GetSampleNumbersVisitor(final Integer experimentId,
		final Map<Integer, List<SampleDTO>> samplesByExperimentId) {
		this.experimentId = experimentId;
		this.samplesByExperimentId = samplesByExperimentId;
	}

	@Override
	public List<SampleDTO> visit(final AdvanceStudyRequest request) {
		return new ArrayList<>();
	}

	@Override
	public List<SampleDTO> visit(final AdvanceSamplesRequest request) {
		final List<SampleDTO> sampleDTOS = this.samplesByExperimentId.get(this.experimentId);
		if (CollectionUtils.isEmpty(sampleDTOS)) {
			return new ArrayList<>();
		}
		return sampleDTOS;
	}

}
