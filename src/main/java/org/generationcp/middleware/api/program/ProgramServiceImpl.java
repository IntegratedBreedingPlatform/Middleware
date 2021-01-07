package org.generationcp.middleware.api.program;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Transactional
@Service
public class ProgramServiceImpl implements ProgramService {

	private final DaoFactory daoFactory;

	public ProgramServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ProgramDTO> listPrograms(final Pageable pageable) {
		final int pageSize = pageable.getPageSize();
		final int start = pageSize * pageable.getPageNumber();

		return this.daoFactory.getProjectDAO().getAll(start, pageSize).stream().map(project -> {
			final ProgramDTO program = new ProgramDTO();
			program.setId(Long.valueOf(project.getProjectId()).intValue());
			program.setCropName(project.getCropType().getCropName());
			program.setName(project.getProjectName());
			program.setProgramUUID(project.getUniqueID());
			// TODO get username
			// program.setCreatedBy();
			program.setStartDate(Util.formatDateAsStringValue(project.getStartDate(), Util.FRONTEND_DATE_FORMAT));
			final Set<WorkbenchUser> members = project.getMembers();
			if (members != null && !members.isEmpty()) {
				program.setMembers(members.stream().map(WorkbenchUser::getName).collect(toSet()));
			}
			return program;
		}).collect(toList());
	}

	@Override
	public long countPrograms() {
		return this.daoFactory.getProjectDAO().countAll();
	}
}
