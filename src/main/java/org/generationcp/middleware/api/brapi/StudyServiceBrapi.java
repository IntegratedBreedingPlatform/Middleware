package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.brapi.v2.study.StudyUpdateRequestDTO;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyServiceBrapi {

	Optional<StudyDetailsDto> getStudyDetailsByInstance(Integer instanceId);

	List<StudyInstanceDto> getStudyInstancesWithMetadata(StudySearchFilter studySearchFilter, Pageable pageable);

	List<StudyInstanceDto> getStudyInstances(StudySearchFilter studySearchFilter, Pageable pageable);

	long countStudyInstances(StudySearchFilter studySearchFilter);

	List<StudyInstanceDto> saveStudyInstances(String crop, List<StudyImportRequestDTO> studyImportRequestDTOS, Integer userId);

	StudyInstanceDto updateStudyInstance(Integer studyDbId, StudyUpdateRequestDTO studyUpdateRequestDTO);
}
