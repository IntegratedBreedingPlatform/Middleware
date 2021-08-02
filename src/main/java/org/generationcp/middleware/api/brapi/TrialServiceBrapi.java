package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.trial.TrialImportRequestDTO;
import org.generationcp.middleware.domain.dms.StudySummary;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrialServiceBrapi {

	long countStudies(StudySearchFilter studySearchFilter);

	List<StudySummary> getStudies(StudySearchFilter studySearchFilter, Pageable pageable);

	List<StudySummary> saveStudies(String crop, List<TrialImportRequestDTO> trialImportRequestDtoList, Integer userId);

}
