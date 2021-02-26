package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramService {

	List<ProgramDTO> listPrograms(Pageable pageable);

	long countPrograms();

	List<ProgramDTO> getProgramsByUser(WorkbenchUser user, Pageable pageable);

	long countProgramsByUser(WorkbenchUser user);

}
