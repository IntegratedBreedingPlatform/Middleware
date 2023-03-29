package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.TrialSummary;
import org.generationcp.middleware.domain.search_request.brapi.v2.TrialSearchRequestDTO;
import org.generationcp.middleware.service.api.study.TrialObservationTable;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrialServiceBrapi {

	TrialObservationTable getTrialObservationTable(int studyIdentifier);

	/**
	 * @param studyIdentifier id for the study (Nursery / Trial)
	 * @param instanceDbId    id for a Trial instance of a Trial (Nursery has 1 instance). If present studyIdentifier will not be used
	 * @return
	 */
	TrialObservationTable getTrialObservationTable(int studyIdentifier, Integer instanceDbId);

	long countSearchTrials(TrialSearchRequestDTO trialSearchRequestDTO);

	List<TrialSummary> searchTrials(TrialSearchRequestDTO trialSearchRequestDTO, Pageable pageable);

	List<TrialSummary> saveTrials(String crop, List<TrialImportRequestDTO> trialImportRequestDtoList, Integer userId);

}
