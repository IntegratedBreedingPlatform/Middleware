package org.generationcp.middleware.api.program;

import org.generationcp.middleware.domain.workbench.AddProgramMemberRequestDto;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramService {

	List<ProgramDTO> filterPrograms(ProgramSearchRequest programSearchRequest, Pageable pageable);

	long countFilteredPrograms(ProgramSearchRequest programSearchRequest);

	void saveOrUpdateProjectUserInfo(Integer userId, String  programUUID);

	ProgramDTO getLastOpenedProject(Integer userId);

	void addProgramMembers(WorkbenchUser createdBy, String programUUID, AddProgramMemberRequestDto addProgramMemberRequestDto);

	void removeProgramMembers(String programUUID, List<Integer> userIds);

}
