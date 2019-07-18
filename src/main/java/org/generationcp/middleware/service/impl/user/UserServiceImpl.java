package org.generationcp.middleware.service.impl.user;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public UserServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserIDFullNameMap(userIds);
	}

	@Override
	public List<UserDto> getUsersByPersonIds(final List<Integer> personIds) {
		final List<WorkbenchUser> workbenchUsers = this.workbenchDaoFactory.getWorkbenchUserDAO().getUsersByPersonIds(personIds);
		final List<UserDto> userDtos = new ArrayList<>();
		for (final WorkbenchUser workbenchUser : workbenchUsers) {
			final UserDto userDto = new UserDto();
			final Person person = workbenchUser.getPerson();
			userDto.setUsername(workbenchUser.getName());
			userDto.setEmail(person.getEmail());
			userDto.setFirstName(person.getFirstName());
			userDto.setLastName(person.getLastName());
			final Iterator<UserRole> userRoleIterator = workbenchUser.getRoles().iterator();
			if (userRoleIterator.hasNext()) {
				final UserRole userRole = userRoleIterator.next();
				userDto.setRole(userRole.getRole());
			}
		}
		return userDtos;
	}

	@Override
	public WorkbenchUser getUserByFullname(final String fullname) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserByFullName(fullname);
	}
}
