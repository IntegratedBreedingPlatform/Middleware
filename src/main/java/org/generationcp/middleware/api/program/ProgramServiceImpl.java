package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
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

}
