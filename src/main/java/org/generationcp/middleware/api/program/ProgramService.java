package org.generationcp.middleware.api.program;

import org.generationcp.middleware.domain.workbench.AddProgramMemberRequestDto;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProgramService {

	List<ProgramDTO> filterPrograms(ProgramSearchRequest programSearchRequest, Pageable pageable);

	long countFilteredPrograms(ProgramSearchRequest programSearchRequest);

	void saveOrUpdateProjectUserInfo(Integer userId, String  programUUID);

	ProgramDTO getLastOpenedProject(Integer userId);

	void addProgramMembers(String programUUID, AddProgramMemberRequestDto addProgramMemberRequestDto);

	void removeProgramMembers(String programUUID, List<Integer> userIds);

	ProgramDTO addProgram(String crop, ProgramBasicDetailsDto programBasicDetailsDto);

	Project addProgram(Project project);

	Optional<ProgramDTO> getProgramByCropAndName(String cropName, String programName);

	Optional<ProgramDTO> getProgramByUUID(String programUUID);

	void deleteProgramAndDependencies(String programUUID);

	void editProgram(String programUUID, ProgramBasicDetailsDto programBasicDetailsDto);
}
