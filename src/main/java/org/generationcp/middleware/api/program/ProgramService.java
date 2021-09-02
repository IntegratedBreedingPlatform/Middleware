package org.generationcp.middleware.api.program;

import org.generationcp.middleware.domain.workbench.AddProgramMemberRequestDto;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramService {

	List<ProgramDTO> filterPrograms(ProgramSearchRequest programSearchRequest, Pageable pageable);

	long countFilteredPrograms(ProgramSearchRequest programSearchRequest);

	void saveOrUpdateProjectUserInfo(Integer userId, String  programUUID);

	/**
	 * Returns the project last accessed by the user.
	 *
	 * @param userId - the user id to match
	 * @return the last Project opened by the given user
	 */
	ProgramDTO getLastOpenedProject(Integer userId);

	void addProgramMembers(String programUUID, AddProgramMemberRequestDto addProgramMemberRequestDto);

	void removeProgramMembers(String programUUID, List<Integer> userIds);

	/**
	 * Gets count projects.
	 *
	 * @param filters - the number of rows to retrieve
	 * @return the number of all the projects
	 */
	long countProjectsByFilter(final ProgramSearchRequest programSearchRequest);
}
