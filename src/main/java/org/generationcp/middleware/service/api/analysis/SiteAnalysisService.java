package org.generationcp.middleware.service.api.analysis;

import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;
import org.generationcp.middleware.service.impl.analysis.SummaryStatisticsImportRequest;

public interface SiteAnalysisService {

	Integer createMeansDataset(Integer studyId, MeansImportRequest meansRequestDto);

	Integer createSummaryStatisticsDataset(Integer studyId, SummaryStatisticsImportRequest summaryStatisticsImportRequest);

	void updateSummaryStatisticsDataset(Integer summaryStatisticsDatasetId,
		SummaryStatisticsImportRequest summaryStatisticsImportRequest);
}
