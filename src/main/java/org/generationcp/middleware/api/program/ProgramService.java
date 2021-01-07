package org.generationcp.middleware.api.program;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramService {

	List<ProgramDTO> listPrograms(Pageable pageable);

	long countPrograms();
}
