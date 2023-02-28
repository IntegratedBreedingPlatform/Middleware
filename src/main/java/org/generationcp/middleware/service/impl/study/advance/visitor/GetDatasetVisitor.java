package org.generationcp.middleware.service.impl.study.advance.visitor;

import org.generationcp.middleware.api.study.AdvanceRequestVisitor;
import org.generationcp.middleware.api.study.AdvanceSamplesRequest;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.service.api.dataset.DatasetService;

import java.util.Collections;

public class GetDatasetVisitor implements AdvanceRequestVisitor<DatasetDTO> {

	private final Integer studyId;
	private final DatasetService datasetService;

	public GetDatasetVisitor(final Integer studyId, final DatasetService datasetService) {
		this.studyId = studyId;
		this.datasetService = datasetService;
	}

	@Override
	public DatasetDTO visit(final AdvanceStudyRequest request) {
		return this.datasetService.getDataset(request.getDatasetId());
	}

	@Override
	public DatasetDTO visit(final AdvanceSamplesRequest request) {
		return this.datasetService.getDatasetsWithVariables(studyId, Collections.singleton(DatasetTypeEnum.PLOT_DATA.getId())).get(0);
	}

}
