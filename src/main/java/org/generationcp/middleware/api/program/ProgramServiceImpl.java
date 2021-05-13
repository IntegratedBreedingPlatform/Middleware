package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
			this.daoFactory.getProjectUserInfoDAO().merge(projectUserInfo);
		} else {
			final ProjectUserInfo pUserInfo = new ProjectUserInfo(project, user);
			pUserInfo.setLastOpenDate(new Date());
			this.daoFactory.getProjectUserInfoDAO().merge(pUserInfo);
		}

		project.setLastOpenDate(new Date());
		this.daoFactory.getProjectDAO().merge(project);

	}

	@Override
	public ProgramDTO getLastOpenedProject(final Integer userId) {
		final Project project =  this.daoFactory.getProjectDAO().getLastOpenedProject(userId);
		return project != null ? new ProgramDTO(project) : null;
	}

}
