package org.generationcp.middleware.api.study;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyStudiesService {

	List<MyStudiesDTO> getMyStudies(final String programUUID, Pageable pageable, Integer studyId);
}
