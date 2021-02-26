package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public List<ProgramDTO> listPrograms(final Pageable pageable) {
		final int pageSize = pageable.getPageSize();
		final int start = pageSize * pageable.getPageNumber();

		return this.daoFactory.getProjectDAO().getAll(start, pageSize).stream().map(project -> {
			final ProgramDTO program = new ProgramDTO(project);
			//FIXME set createdBy (not set in constructor)
			return program;
		}).collect(toList());
	}

	@Override
	public long countPrograms() {
		return this.daoFactory.getProjectDAO().countAll();
	}

	@Override
	public List<ProgramDTO> getProgramsByUser(final WorkbenchUser user, final Pageable pageable) {
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		programSearchRequest.setLoggedInUserId(user.getUserid());
		return this.daoFactory.getProjectDAO().getProjectsByFilter(pageable, programSearchRequest).stream().map(project -> {
			final ProgramDTO program = new ProgramDTO(project);
			//FIXME set createdBy (not set in constructor)
			return program;
		}).collect(toList());
	}

	@Override
	public long countProgramsByUser(final WorkbenchUser user) {
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		programSearchRequest.setLoggedInUserId(user.getUserid());
		return this.daoFactory.getProjectDAO().countProjectsByFilter(programSearchRequest);
	}

}
