package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSampledPlantsRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.manager.api.StudyDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetExperimentSamplesVisitor implements AdvanceRequestVisitor<Map<Integer, List<SampleDTO>>> {

	private final Integer studyId;
	private final StudyDataManager studyDataManager;

	public GetExperimentSamplesVisitor(final Integer studyId, final StudyDataManager studyDataManager) {
		this.studyId = studyId;
		this.studyDataManager = studyDataManager;
	}

	@Override
	public Map<Integer, List<SampleDTO>> visit(final AdvanceStudyRequest request) {
		return new HashMap<>();
	}

	@Override
	public Map<Integer, List<SampleDTO>> visit(final AdvanceSampledPlantsRequest request) {
		return this.studyDataManager.getExperimentSamplesDTOMap(this.studyId);
	}

}
