package org.generationcp.middleware.service.impl.user;

import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserRoleDto;
import org.generationcp.middleware.service.api.user.UserRoleMapper;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
	public WorkbenchUser getUserById(final Integer userId) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getById(userId);
	}

	@Override
	public List<WorkbenchUser> getUserByName(final String name, final int start, final int numOfRows, final Operation op) {
		final WorkbenchUserDAO dao = this.workbenchDaoFactory.getWorkbenchUserDAO();
		List<WorkbenchUser> users = new ArrayList<>();
		if (op == Operation.EQUAL) {
			users = dao.getByNameUsingEqual(name, start, numOfRows);
		} else if (op == Operation.LIKE) {
			users = dao.getByNameUsingLike(name, start, numOfRows);
		}
		return users;
	}

	@Override
	public WorkbenchUser getUserByFullname(final String fullname) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserByFullName(fullname);
	}

	@Override
	public Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds) {
		if (!userIds.isEmpty()) {
			return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserIDFullNameMap(userIds);
		}
		return new HashMap<>();
	}

	@Override
	public Map<Integer, String> getAllUserIDFullNameMap() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAllUserIDFullNameMap();
	}

	@Override
	public List<WorkbenchUser> getUsersByCrop(final String cropName) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUsersByCrop(cropName);
	}

	@Override
	public List<WorkbenchUser> getAllUsers() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAll();
	}

	@Override
	public List<Integer> getActiveUserIDsByProjectId(final Long projectId, final String cropName) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getActiveUserIDsByProjectId(projectId);
	}

	@Override
	public List<UserDto> getUsersByProjectUuid(final String projectUuid, final String cropName) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUsersByProjectUUId(projectUuid, cropName);
	}

	@Override
	public List<WorkbenchUser> getUsersWithoutAssociatedPrograms(final CropType cropType) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getUsersWithoutAssociatedPrograms(cropType);
	}

	@Override
	public List<WorkbenchUser> getUsersByProjectId(final Long projectId) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getUsersByProjectId(projectId);
	}

	@Override
	public Map<Integer, Person> getPersonsByProjectId(final Long projectId) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getPersonsByProjectId(projectId);
	}

	@Override
	public List<UserDto> getAllUsersSortedByLastName() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAllUsersSortedByLastName();
	}

	@Override
	public List<WorkbenchUser> getAllActiveUsersSorted() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAllActiveUsersSorted();
	}

	@Override
	public WorkbenchUser addUser(final WorkbenchUser user) {
		try {
			return this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while saving User: userService.addUser(user=" + user + "): " + e.getMessage(), e);
		}
	}

	@Override
	public long countAllUsers() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().countAll();
	}

	@Override
	public Integer createUser(final UserDto userDto) {

		Integer idUserSaved = null;
		// user.access = 0 - Default User
		// user.instalid = 0 - Access all areas (legacy from the ICIS system) (not used)
		// user.status = 0 - Unassigned
		// user.type = 0 - Default user type (not used)

		final Integer currentDate = Util.getCurrentDateAsIntegerValue();
		final Person person = this.createPersonFromDto(userDto, new Person());

		final WorkbenchUser user = new WorkbenchUser();
		user.setPerson(person);
		user.setName(userDto.getUsername());
		user.setPassword(userDto.getPassword());
		user.setAccess(0);
		user.setAssignDate(currentDate);
		user.setCloseDate(currentDate);
		user.setInstalid(0);
		user.setStatus(userDto.getStatus());
		user.setType(0);

		// Add user roles to the particular user
		final List<UserRole> userRoles = new ArrayList<>();
		if (userDto.getUserRoles() != null) {
			for (final UserRoleDto userRoleDto : userDto.getUserRoles()) {
				final UserRole userRole = new UserRole();
				final Role role = new Role();
				role.setId(userRoleDto.getRole().getId());
				userRole.setRole(role);
				userRole.setUser(user);
				if (userRoleDto.getCrop() != null) {
					userRole.setCropType(new CropType(userRoleDto.getCrop().getCropName()));
				}
				if (userRoleDto.getProgram() != null) {
					final Project project =
						this.workbenchDaoFactory.getProjectDAO().getByUuid(userRoleDto.getProgram().getUuid(), userRoleDto.getCrop().getCropName());
					userRole.setWorkbenchProject(project);
				}
				userRole.setCreatedDate(new Date());
				userRole.setCreatedBy(this.getUserById(userRoleDto.getCreatedBy()));
				userRoles.add(userRole);
			}
		}
		user.setRoles(userRoles);

		final List<CropType> crops = new ArrayList<>();
		for (final CropDto crop : userDto.getCrops()) {
			final CropType cropType = new CropType();
			cropType.setCropName(crop.getCropName());
			crops.add(cropType);
		}
		user.setCrops(crops);

		try {

			final WorkbenchUser recordSaved = this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving User: userService.addUser(user=" + user + "): " + e.getMessage(), e);
		}

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getUserid());
		userInfo.setLoginCount(0);
		this.workbenchDaoFactory.getUserInfoDAO().insertOrUpdateUserInfo(userInfo);

		return idUserSaved;

	}

	@Override
	public Integer updateUser(final UserDto userDto) {
		final Integer currentDate = Util.getCurrentDateAsIntegerValue();
		WorkbenchUser user = null;
		Integer idUserSaved = null;

		try {
			user = this.getUserById(userDto.getUserId());
			this.createPersonFromDto(userDto, user.getPerson());

			user.setName(userDto.getUsername());
			user.setAssignDate(currentDate);
			user.setCloseDate(currentDate);
			user.setStatus(userDto.getStatus());

			final List<UserRole> userRoles = new ArrayList<>();
			if (userDto.getUserRoles() != null) {
				for (final UserRoleDto userRoleDto : userDto.getUserRoles()) {
					boolean found = false;
					for (final UserRole userRole : user.getRoles()) {
						if (userRole.getRole().getId().equals(userRoleDto.getRole().getId()) &&
							(userRole.getCropType() == null && userRoleDto.getCrop() == null || userRole.getCropType().getCropName()
								.equals(userRoleDto.getCrop().getCropName())) &&
							(userRole.getWorkbenchProject() == null && userRoleDto.getProgram() == null || userRole.getWorkbenchProject()
								.getUniqueID().equals(userRoleDto.getProgram().getUuid()))) {
							userRoles.add(userRole);
							found = true;
							break;
						}
					}
					if (!found) {
						final UserRole userRole = new UserRole();
						final Role role = new Role();
						role.setId(userRoleDto.getRole().getId());
						userRole.setRole(role);
						userRole.setUser(user);
						if (userRoleDto.getCrop() != null) {
							userRole.setCropType(new CropType(userRoleDto.getCrop().getCropName()));
						}
						if (userRoleDto.getProgram() != null) {
							final Project project =
								this.workbenchDaoFactory.getProjectDAO().getByUuid(userRoleDto.getProgram().getUuid(), userRoleDto.getCrop().getCropName());
							userRole.setWorkbenchProject(project);
						}
						userRole.setCreatedDate(new Date());
						userRole.setCreatedBy(this.getUserById(userRoleDto.getCreatedBy()));
						userRoles.add(userRole);
					}
				}
				user.getRoles().clear();
				user.getRoles().addAll(userRoles);
			}

			final List<CropType> crops = new ArrayList<>();
			for (final CropDto crop : userDto.getCrops()) {
				final CropType cropType = new CropType();
				cropType.setCropName(crop.getCropName());
				crops.add(cropType);
			}
			user.setCrops(crops);

			this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);
			idUserSaved = user.getUserid();
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving User: UserServiceImpl.updateUser(org.generationcp.middleware.service.api.user.UserDto)(user=" + user + "): " + e.getMessage(), e);
		}

		return idUserSaved;
	}

	@Override
	public void updateUser(final WorkbenchUser user) {
		this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);
		this.workbenchDaoFactory.getPersonDAO().saveOrUpdate(user.getPerson());
	}

	@Override
	public void deleteUser(final WorkbenchUser user) {

		try {

			this.workbenchDaoFactory.getWorkbenchUserDAO().makeTransient(user);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting User: userService.deleteUser(user=" + user + "):  " + e.getMessage(), e);
		}
	}

	@Override
	public boolean isUsernameExists(final String userName) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().isUsernameExists(userName);
	}

	@Override
	public boolean isPersonExists(final String firstName, final String lastName) {
		return this.workbenchDaoFactory.getPersonDAO().isPersonExists(firstName, lastName);
	}

	@Override
	public boolean isPersonWithEmailExists(final String email) {
		return this.workbenchDaoFactory.getPersonDAO().isPersonWithEmailExists(email);
	}

	@Override
	public boolean isSuperAdminUser(final Integer userId) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().isSuperAdminUser(userId);
	}

	@Override
	public void removeUsersFromProgram(final List<Integer> workbenchUserIds, final Long projectId) {
		this.workbenchDaoFactory.getProjectUserInfoDAO().removeUsersFromProgram(workbenchUserIds, projectId);
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
			userDto.setUserRoles(UserRoleMapper.map(workbenchUser.getRoles()));
		}
		return userDtos;
	}

	@Override
	public Person getPersonById(final int id) {
		return this.workbenchDaoFactory.getPersonDAO().getById(id, false);
	}

	@Override
	public Person getPersonByEmail(final String email) {
		return this.workbenchDaoFactory.getPersonDAO().getPersonByEmail(email);
	}

	@Override
	public Person getPersonByEmailAndName(final String email, final String firstName, final String lastName) {
		return this.workbenchDaoFactory.getPersonDAO().getPersonByEmailAndName(email, firstName, lastName);
	}

	@Override
	public String getPersonNameForUserId(final int userId) {
		final WorkbenchUser workbenchUser = this.workbenchDaoFactory.getWorkbenchUserDAO().getById(userId);
		if (workbenchUser != null) {
			return workbenchUser.getPerson().getDisplayName();
		}
		return "";
	}

	@Override
	public String getPersonNameForPersonId(final int personId) {
		final Person person= this.workbenchDaoFactory.getPersonDAO().getById(personId);
		if (person != null) {
			return person.getDisplayName();
		}
		return "";
	}

	@Override
	public List<Person> getPersonsByCrop(final CropType cropType) {
		return this.workbenchDaoFactory.getPersonDAO().getPersonsByCrop(cropType);
	}

	@Override
	public List<Person> getAllPersons() {
		return this.workbenchDaoFactory.getPersonDAO().getAll();
	}

	@Override
	public long countAllPersons() {
		return this.workbenchDaoFactory.getPersonDAO().countAll();
	}

	@Override
	public Person addPerson(final Person person) {
		try {
			return this.workbenchDaoFactory.getPersonDAO().saveOrUpdate(person);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while saving Person: userService.addPerson(person=" + person + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void deletePerson(final Person person) {

		try {

			this.workbenchDaoFactory.getPersonDAO().makeTransient(person);

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while deleting Person: userService.deletePerson(person=" + person + "): " + e.getMessage(),
				e);
		}
	}

	@Override
	public boolean changeUserPassword(final String username, final String password) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().changePassword(username, password);
	}

	@Override
	public boolean isPersonWithUsernameAndEmailExists(final String username, final String email) {
		return this.workbenchDaoFactory.getPersonDAO().isPersonWithUsernameAndEmailExists(username, email);
	}

	@Override
	public UserInfo getUserInfo(final int userId) {
		try {
			return this.workbenchDaoFactory.getUserInfoDAO().getUserInfoByUserId(userId);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}

	}

	@Override
	public UserInfo getUserInfoByUsername(final String username) {
		final WorkbenchUser user = this.getUserByName(username, 0, 1, Operation.EQUAL).get(0);

		return this.getUserInfo(user.getUserid());
	}

	@Override
	public WorkbenchUser getUserByUsername(final String userName) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUserByUserName(userName);
	}

	@Override
	public UserInfo getUserInfoByResetToken(final String token) {
		return this.workbenchDaoFactory.getUserInfoDAO().getUserInfoByToken(token);
	}

	@Override
	public UserInfo updateUserInfo(final UserInfo userInfo) {

		try {

			this.workbenchDaoFactory.getUserInfoDAO().update(userInfo);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Cannot update userInfo =" + userInfo.getUserId() + "): " + e.getMessage(), e);

		}
		return userInfo;
	}

	@Override
	public void incrementUserLogInCount(final int userId) {

		try {

			final UserInfo userdetails = this.workbenchDaoFactory.getUserInfoDAO().getUserInfoByUserId(userId);
			if (userdetails != null) {
				this.workbenchDaoFactory.getUserInfoDAO().updateLoginCounter(userdetails);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
		}
	}

	@Override
	public void insertOrUpdateUserInfo(final UserInfo userDetails) {
		this.workbenchDaoFactory.getUserInfoDAO().insertOrUpdateUserInfo(userDetails);
	}

	private Person createPersonFromDto(final UserDto userDto, final Person person) {

		person.setFirstName(userDto.getFirstName());
		person.setMiddleName("");
		person.setLastName(userDto.getLastName());
		person.setEmail(userDto.getEmail());
		person.setTitle("-");
		person.setContact("-");
		person.setExtension("-");
		person.setFax("-");
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("-");
		person.setPositionName("-");
		person.setPhone("-");
		return this.addPerson(person);
	}

	@Override
	public List<ProjectUserInfo> getProjectUserInfoByProjectIdAndUserIds(final Long projectId, final List<Integer> userIds) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getByProjectIdAndUserIds(projectId, userIds);
	}

	@Override
	public ProjectUserInfo getProjectUserInfoByProjectIdAndUserId(final Long projectId, final Integer userId) {
		return this.workbenchDaoFactory.getProjectUserInfoDAO().getByProjectIdAndUserId(projectId, userId);
	}

	@Override
	public void saveProjectUserInfo(final ProjectUserInfo projectUserInfo) {
		final ProjectUserInfo existingProjectUserInfo = this.workbenchDaoFactory.getProjectUserInfoDAO()
			.getByProjectIdAndUserId(projectUserInfo.getProject().getProjectId(), projectUserInfo.getUser().getUserid());
		// Only save if the record with the same projectId and userId not yet exists.
		if (existingProjectUserInfo == null) {
			this.workbenchDaoFactory.getProjectUserInfoDAO().saveOrUpdate(projectUserInfo);
		}
	}

	@Override
	public void saveCropPerson(final CropPerson cropPerson) {
		this.workbenchDaoFactory.getCropPersonDAO().saveOrUpdate(cropPerson);
	}

	@Override
	public void removeCropPerson(final CropPerson cropPerson) {
		this.workbenchDaoFactory.getCropPersonDAO().makeTransient(cropPerson);
	}

	@Override
	public CropPerson getCropPerson(final String cropName, final Integer personId) {
		return this.workbenchDaoFactory.getCropPersonDAO().getByCropNameAndPersonId(cropName, personId);
	}

	@Override
	public Map<Integer, String> getPersonNamesByPersonIds(final List<Integer> personIds) {
		return this.workbenchDaoFactory.getPersonDAO().getPersonNamesByPersonIds(personIds);
	}

	@Override
	public List<WorkbenchUser> getSuperAdminUsers() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getSuperAdminUsers();
	}

	@Override
	public WorkbenchUser getUserWithAuthorities(final String userName, final String cropName, final String programUuid) {
		final WorkbenchUser user = this.workbenchDaoFactory.getWorkbenchUserDAO().getUserByUserName(userName);
		final Project project = this.workbenchDaoFactory.getProjectDAO().getByUuid(programUuid);
		final Integer programId = project != null ? project.getProjectId().intValue() : null;
		final List<PermissionDto> permissions = this.workbenchDaoFactory.getPermissionDAO().getPermissions(user.getUserid(), cropName, programId);
		user.setPermissions(permissions);
		return user;
	}

}
