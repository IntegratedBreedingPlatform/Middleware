package org.generationcp.middleware.service.impl.user;

import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.dao.workbench.ProgramEligibleUsersSearchRequest;
import org.generationcp.middleware.dao.workbench.ProgramMembersSearchRequest;
import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.domain.workbench.ProgramMemberDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
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
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public UserServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public WorkbenchUser getUserById(final Integer userId) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getById(userId);
	}

	@Override
	public List<WorkbenchUser> getUsersByIds(final List<Integer> userIds) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUsers(userIds);
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
	public Long countUsersByFullname(final String fullname) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().countUsersByFullName(fullname);
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
	public List<WorkbenchUser> getUsersByProjectId(final Long projectId) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getUsersByProjectId(projectId);
	}

	@Override
	public List<UserDto> getAllUsersSortedByLastName() {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAllUsersSortedByLastName();
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
	public Integer createUser(final UserDto userDto) {

		// user.access = 0 - Default User
		// user.instalid = 0 - Access all areas (legacy from the ICIS system) (not used)
		// user.status = 0 - Unassigned
		// user.type = 0 - Default user type (not used)

		try {
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
					userRoles.add(this.buildNewUserRole(user, userRoleDto));
				}
			}
			user.setRoles(userRoles);

			final Set<CropType> crops = new HashSet<>();
			for (final CropDto crop : userDto.getCrops()) {
				final CropType cropType = new CropType(crop.getCropName());
				crops.add(cropType);
			}
			person.setCrops(crops);

			final WorkbenchUser recordSaved = this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);

			final UserInfo userInfo = new UserInfo();
			userInfo.setUserId(user.getUserid());
			userInfo.setLoginCount(0);
			this.workbenchDaoFactory.getUserInfoDAO().insertOrUpdateUserInfo(userInfo);

			return recordSaved.getUserid();

		} catch (final Exception e) {

			LOG.error(e.getMessage(), e);
			throw new MiddlewareRequestException("", "user.create.error", new String[] {userDto.getUsername()});
		}
	}

	@Override
	public Integer updateUser(final UserDto userDto) {
		final Integer currentDate = Util.getCurrentDateAsIntegerValue();
		final WorkbenchUser user;
		final Integer idUserSaved;

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
						if (userRoleEqualsToUserRoleDto(userRole, userRoleDto)) {
							userRoles.add(userRole);
							found = true;
							break;
						}
					}
					if (!found) {
						userRoles.add(this.buildNewUserRole(user, userRoleDto));
					}
				}
				user.getRoles().clear();
				user.getRoles().addAll(userRoles);
			}

			final Set<CropType> crops = new HashSet<>();
			for (final CropDto crop : userDto.getCrops()) {
				final CropType cropType = new CropType(crop.getCropName());
				crops.add(cropType);
			}
			user.getPerson().setCrops(crops);

			this.workbenchDaoFactory.getWorkbenchUserDAO().saveOrUpdate(user);
			idUserSaved = user.getUserid();
		} catch (final Exception e) {

			LOG.error(e.getMessage(), e);
			throw new MiddlewareRequestException("", "user.update.error", new String[] {userDto.getUsername()});
		}

		return idUserSaved;
	}


	@Override
	public boolean isUsernameExists(final String userName) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().isUsernameExists(userName);
	}

	@Override
	public boolean isPersonWithEmailExists(final String email) {
		return this.workbenchDaoFactory.getPersonDAO().isPersonWithEmailExists(email);
	}

	@Override
	public List<UserDto> getUsersByPersonIds(final List<Integer> personIds) {
		final List<WorkbenchUser> workbenchUsers = this.workbenchDaoFactory.getWorkbenchUserDAO().getUsersByPersonIds(personIds);
		final List<UserDto> userDtos = new ArrayList<>();
		for (final WorkbenchUser workbenchUser : workbenchUsers) {
			final UserDto userDto = new UserDto(workbenchUser);
			userDtos.add(userDto);
		}
		return userDtos;
	}

	@Override
	public Person getPersonById(final int id) {
		return this.workbenchDaoFactory.getPersonDAO().getById(id, false);
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
	public Person addPerson(final Person person) {
		try {
			return this.workbenchDaoFactory.getPersonDAO().saveOrUpdate(person);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
				"Error encountered while saving Person: userService.addPerson(person=" + person + "): " + e.getMessage(), e);
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
			throw new MiddlewareQueryException("Cannot get userInfo for user_id =" + userId + "): " + e.getMessage(), e);
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
			final UserInfoDAO userInfoDAO = this.workbenchDaoFactory.getUserInfoDAO();
			UserInfo userInfo = userInfoDAO.getUserInfoByUserId(userId);
			if (userInfo == null) {
				// Edge case: fresh db, admin user
				userInfo = new UserInfo();
				userInfo.setUserId(userId);
				userInfo.setLoginCount(1);
			} else {
				userInfo.setLoginCount(userInfo.getLoginCount() + 1);
			}
			userInfoDAO.insertOrUpdateUserInfo(userInfo);
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
	public void saveOrUpdateProjectUserInfo(final ProjectUserInfo projectUserInfo) {
		this.workbenchDaoFactory.getProjectUserInfoDAO().merge(projectUserInfo);
	}

	@Override
	public void saveCropPerson(final CropPerson cropPerson) {
		this.workbenchDaoFactory.getCropPersonDAO().saveOrUpdate(cropPerson);
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

	@Override
	public List<WorkbenchUser> getUsersWithRole(final int roleId) {
		return this.workbenchDaoFactory.getUserRoleDao().getUsersByRoleId(roleId);
	}

	@Override
	public List<ProgramMemberDto> getProgramMembers(final String programUUID, final ProgramMembersSearchRequest searchRequest,
		final Pageable pageable) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getProgramMembers(programUUID, searchRequest, pageable);
	}

	@Override
	public long countAllProgramMembers(final String programUUID, final ProgramMembersSearchRequest searchRequest) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().countAllProgramMembers(programUUID, searchRequest);
	}

	@Override
	public List<UserDto> getProgramMembersEligibleUsers(final String programUUID, final ProgramEligibleUsersSearchRequest searchRequest,
		final Pageable pageable) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().getAllProgramEligibleUsers(programUUID, searchRequest, pageable);
	}

	@Override
	public long countProgramMembersEligibleUsers(final String programUUID, final ProgramEligibleUsersSearchRequest searchRequest) {
		return this.workbenchDaoFactory.getWorkbenchUserDAO().countAllProgramEligibleUsers(programUUID, searchRequest);
	}

	private UserRole buildNewUserRole(final WorkbenchUser user, final UserRoleDto userRoleDto) {
		final Role role = new Role(userRoleDto.getRole().getId());

		CropType cropType = null;
		if (userRoleDto.getCrop() != null) {
			cropType = new CropType(userRoleDto.getCrop().getCropName());
		}
		Project project = null;
		if (userRoleDto.getProgram() != null) {
			project = this.workbenchDaoFactory.getProjectDAO()
				.getByUuid(userRoleDto.getProgram().getUuid(), userRoleDto.getCrop().getCropName());
		}
		final WorkbenchUser creator = this.getUserById(userRoleDto.getCreatedBy());

		final UserRole userRole = new UserRole(user, role, cropType, project);
		userRole.setCreatedDate(new Date());
		userRole.setCreatedBy(creator);
		return userRole;
	}

	private boolean userRoleEqualsToUserRoleDto(final UserRole userRole, final UserRoleDto userRoleDto) {
		return userRole.getRole().getId().equals(userRoleDto.getRole().getId()) &&
			(userRole.getCropType() == null && userRoleDto.getCrop() == null || userRole.getCropType().getCropName()
				.equals(userRoleDto.getCrop().getCropName())) &&
			(userRole.getWorkbenchProject() == null && userRoleDto.getProgram() == null || userRole.getWorkbenchProject()
				.getUniqueID().equals(userRoleDto.getProgram().getUuid()));
	}

}
