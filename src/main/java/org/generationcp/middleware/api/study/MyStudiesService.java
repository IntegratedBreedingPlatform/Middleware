package org.generationcp.middleware.api.study;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyStudiesService {

	long countMyStudies(String programUUID, Integer userId);

	List<MyStudiesDTO> getMyStudies(String programUUID, Pageable pageable, Integer userId);

}
