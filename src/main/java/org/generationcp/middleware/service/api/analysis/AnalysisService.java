package org.generationcp.middleware.service.api.analysis;

import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;

public interface AnalysisService {

	Integer createMeansDataset(MeansImportRequest meansRequestDto);
}
