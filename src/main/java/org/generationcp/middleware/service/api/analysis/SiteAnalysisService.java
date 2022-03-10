package org.generationcp.middleware.service.api.analysis;

import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;

public interface SiteAnalysisService {

	Integer createMeansDataset(Integer studyId, MeansImportRequest meansRequestDto);
}
