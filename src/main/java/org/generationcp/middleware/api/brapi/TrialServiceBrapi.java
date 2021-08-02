package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
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

	long countStudies(StudySearchFilter studySearchFilter);

	List<StudySummary> getStudies(StudySearchFilter studySearchFilter, Pageable pageable);

	List<StudySummary> saveStudies(String crop, List<TrialImportRequestDTO> trialImportRequestDtoList, Integer userId);

}
