package org.generationcp.middleware.api.program;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.workbench.AddProgramMemberRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Transactional
@Service
public class ProgramServiceImpl implements ProgramService {

	private final WorkbenchDaoFactory daoFactory;

	public ProgramServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<ProgramDTO> filterPrograms(final ProgramSearchRequest programSearchRequest, final Pageable pageable) {
		return this.daoFactory.getProjectDAO().getProjectsByFilter(pageable, programSearchRequest).stream().map(project -> {
			final ProgramDTO program = new ProgramDTO(project);
			//FIXME set createdBy (not set in constructor)
			return program;
		}).collect(toList());
	}

	@Override
	public long countFilteredPrograms(final ProgramSearchRequest programSearchRequest) {
		return this.daoFactory.getProjectDAO().countProjectsByFilter(programSearchRequest);
	}

	@Override
	public void saveOrUpdateProjectUserInfo(final Integer userId, final String programUUID) {
		final WorkbenchUser user = this.daoFactory.getWorkbenchUserDAO().getById(userId);
		final Project project = this.daoFactory.getProjectDAO().getByUuid(programUUID);

		final ProjectUserInfo projectUserInfo =
			this.daoFactory.getProjectUserInfoDAO().getByProjectIdAndUserId(project.getProjectId(), user.getUserid());
		if (projectUserInfo != null) {
			projectUserInfo.setLastOpenDate(new Date());
			this.daoFactory.getProjectUserInfoDAO().update(projectUserInfo);
		} else {
			final ProjectUserInfo pUserInfo = new ProjectUserInfo(project, user);
			pUserInfo.setLastOpenDate(new Date());
			this.daoFactory.getProjectUserInfoDAO().save(pUserInfo);
		}

		project.setLastOpenDate(new Date());
		this.daoFactory.getProjectDAO().update(project);

	}

	@Override
	public ProgramDTO getLastOpenedProject(final Integer userId) {
		final Project project = this.daoFactory.getProjectDAO().getLastOpenedProject(userId);
		return project != null ? new ProgramDTO(project) : null;
	}

	@Override
	public void addProgramMembers(
		final String programUUID,
		final AddProgramMemberRequestDto addProgramMemberRequestDto) {
		final Project project = this.daoFactory.getProjectDAO().getByUuid(programUUID);
		final Map<Integer, WorkbenchUser> userMap =
			this.daoFactory.getWorkbenchUserDAO().getUsers(new ArrayList<>(addProgramMemberRequestDto.getUserIds())).stream().collect(
				Collectors.toMap(WorkbenchUser::getUserid, Function.identity()));
		final Role role = this.daoFactory.getRoleDao().getRoleById(addProgramMemberRequestDto.getRoleId());
		final WorkbenchUser loggedInUser = this.daoFactory.getWorkbenchUserDAO().getById(ContextHolder.getLoggedInUserId());
		addProgramMemberRequestDto.getUserIds().forEach(u -> {
			final UserRole userRole = new UserRole(userMap.get(u), role, project.getCropType(), project);
			userRole.setCreatedDate(new Date());
			userRole.setCreatedBy(loggedInUser);
			this.daoFactory.getUserRoleDao().save(userRole);
		});

	}

	@Override
	public void removeProgramMembers(final String programUUID, final List<Integer> userIds) {
		final Long projectId = this.daoFactory.getProjectDAO().getByUuid(programUUID).getProjectId();
		this.daoFactory.getUserRoleDao().removeUsersFromProgram(userIds, projectId);
	}

	@Override
	public ProgramDTO addProgram(final String crop, final ProgramBasicDetailsDto programBasicDetailsDto) {
		final Project project = new Project();
		project.setUniqueID(UUID.randomUUID().toString());
		project.setProjectName(programBasicDetailsDto.getName());
		project.setUserId(ContextHolder.getLoggedInUserId());
		project.setCropType(this.daoFactory.getCropTypeDAO().getByName(crop));
		project.setLastOpenDate(null);
		project.setStartDate(Util.tryParseDate(programBasicDetailsDto.getStartDate(), Util.FRONTEND_DATE_FORMAT));
		this.daoFactory.getProjectDAO().save(project);
		final ProgramDTO programDTO = new ProgramDTO(project);
		final WorkbenchUser loggedInUser = this.daoFactory.getWorkbenchUserDAO().getById(ContextHolder.getLoggedInUserId());
		programDTO.setCreatedBy(loggedInUser.getName());
		return programDTO;
	}

	@Override
	public Project addProgram(final Project project) {
		project.setUniqueID(UUID.randomUUID().toString());
		this.daoFactory.getProjectDAO().save(project);
		return project;
	}

	@Override
	public Optional<ProgramDTO> getProgramByCropAndName(final String cropName, final String programName) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(cropName);
		if (cropType == null) {
			return Optional.empty();
		}
		final Project project = this.daoFactory.getProjectDAO().getProjectByNameAndCrop(programName, cropType);
		if (project != null) {
			final WorkbenchUser loggedInUser = this.daoFactory.getWorkbenchUserDAO().getById(ContextHolder.getLoggedInUserId());
			final ProgramDTO programDTO = new ProgramDTO(project);
			programDTO.setCreatedBy(loggedInUser.getName());
			return Optional.of(programDTO);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<ProgramDTO> getProgramByUUID(final String programUUID) {
		final Project project = this.daoFactory.getProjectDAO().getByUuid(programUUID);
		if (project != null) {
			final WorkbenchUser owner = this.daoFactory.getWorkbenchUserDAO().getById(project.getUserId());
			final ProgramDTO programDTO = new ProgramDTO(project);
			programDTO.setCreatedBy(owner.getName());
			return Optional.of(programDTO);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void deleteProgramAndDependencies(final String programUUID) {
		this.daoFactory.getProjectActivityDAO().deleteAllProjectActivities(programUUID);
		this.daoFactory.getProjectUserInfoDAO().deleteAllProjectUserInfo(programUUID);
		this.daoFactory.getUserRoleDao().deleteProgramRolesAssociations(programUUID);
		this.daoFactory.getProjectDAO().deleteProjectByUUID(programUUID);
	}

	@Override
	public void editProgram(final String programUUID, final ProgramBasicDetailsDto programBasicDetailsDto) {
		final Project project = this.daoFactory.getProjectDAO().getByUuid(programUUID);
		if (programBasicDetailsDto.getName() != null) {
			project.setProjectName(programBasicDetailsDto.getName());
		}
		if (programBasicDetailsDto.getStartDate() != null) {
			project.setStartDate(Util.tryParseDate(programBasicDetailsDto.getStartDate(), Util.FRONTEND_DATE_FORMAT));
		}
		this.daoFactory.getProjectDAO().update(project);
	}

	@Override
	public Project getLastOpenedProjectAnyUser() {
		return this.daoFactory.getProjectDAO().getLastOpenedProjectAnyUser();
	}

	@Override
	public Project getProjectById(final Long projectId) {
		return this.daoFactory.getProjectDAO().getById(projectId);
	}

	@Override
	public List<Project> getProjects(final Pageable pageable, final ProgramSearchRequest programSearchRequest) {
		return this.daoFactory.getProjectDAO().getProjectsByFilter(pageable, programSearchRequest);
	}
}
