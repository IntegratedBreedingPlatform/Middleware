package org.generationcp.middleware.service.api.analysis;

import org.generationcp.middleware.service.impl.analysis.MeansImportRequest;
import org.generationcp.middleware.service.impl.analysis.SummaryStatisticsImportRequest;

public interface SiteAnalysisService {

	Integer createMeansDataset(String crop, Integer studyId, MeansImportRequest meansRequestDto);

	Integer createSummaryStatisticsDataset(String crop, Integer studyId, SummaryStatisticsImportRequest summaryStatisticsImportRequest);

	void updateSummaryStatisticsDataset(String crop, Integer summaryStatisticsDatasetId,
		SummaryStatisticsImportRequest summaryStatisticsImportRequest);
}
