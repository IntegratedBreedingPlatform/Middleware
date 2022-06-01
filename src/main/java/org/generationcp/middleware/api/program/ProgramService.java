package org.generationcp.middleware.api.program;

import org.generationcp.middleware.domain.workbench.AddProgramMemberRequestDto;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
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

	/**
	 * Gets a project by Uuid. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match
	 * @return the project matching the given Uuid
	 */
	Project getProjectByUuid(String projectUuid);

	/**
	 * Returns the project last accessed regardless of user.
	 *
	 * @return the last Project opened by the given user
	 */

	Project getLastOpenedProjectAnyUser();

	/**
	 * Gets a project by id.
	 *
	 * @param projectId - the project id to match
	 * @return the project matching the given id
	 */
	Project getProjectById(Long projectId);

	/**
	 * Gets the projects.
	 *
	 * @param pageable             - the starting record and number of page
	 * @param programSearchRequest - the filters that to be included in the query
	 * @return All projects based on the given start, numOfRows and filters Map
	 */
	List<Project> getProjects(final Pageable pageable, final ProgramSearchRequest programSearchRequest);

	/**
	 * Gets the list of Projects that the specified User is associated with.
	 *
	 * @param cropName - the Crop Name associated with the projects to be retrieved
	 * @return the projects which the specified user is involved
	 */
	List<Project> getProjectsByCropName(final String cropName);

	/**
	 * Gets a project by Uuid and CropType. Should return only one value.
	 *
	 * @param projectUuid - the project Uuid to match (uuid is unique per crop type)
	 * @param cropType    - the crop type to match
	 * @return the project matching the given Uuid and crop type
	 */
	Project getProjectByUuidAndCrop(String projectUuid, String cropType);

	/**
	 * Adds a project activity.
	 *
	 * @param projectActivity - the project activity
	 * @return Returns the id of the {@code ProjectActivity} record added
	 */
	Integer addProjectActivity(ProjectActivity projectActivity);


}
