package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.util.List;
import java.util.Map;

public interface UserService {

	/**
	 * Gets the user by id.
	 *
	 * @param userId - the user id to match
	 * @return the user matching the given id
	 */
	WorkbenchUser getUserById(final Integer userId);

	/**
	 * Gets the user by name.
	 *
	 * @param name      - the name to match
	 * @param start     - the starting record
	 * @param numOfRows - the number of rows to retrieve
	 * @param op        the op
	 * @return the user by name
	 */
	List<WorkbenchUser> getUserByName(String name, int start, int numOfRows, Operation op);

	WorkbenchUser getUserByFullname(final String fullname);

	Map<Integer, String> getUserIDFullNameMap(final List<Integer> userIds);

	Map<Integer, String> getAllUserIDFullNameMap();

	List<WorkbenchUser> getUsersByCrop(final String cropName);

	/**
	 * Returns all the Workbench users.
	 *
	 * @return A {@code List} of all the {@code WorkbenchUser}s in the Workbench database.
	 */
	List<WorkbenchUser> getAllUsers();

	/**
	 * Gets the user by project_uuid.
	 *
	 * @param projectUuid
	 * @return the user matching the given project_uuid
	 */
	List<UserDto> getUsersByProjectUuid(final String projectUuid);

	/**
	 * Retrieves the user ids of the program members using the project id
	 *
	 * @param projectId
	 * @return
	 */
	List<Integer> getActiveUserIDsByProjectId(final Long projectId);

	/**
	 * Return a List of {@link WorkbenchUser} records associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @return the List of {@link WorkbenchUser} records
	 */
	List<WorkbenchUser> getUsersByProjectId(Long projectId);

	/**
	 * Return a Map of {@link Person} records identified by {@link WorkbenchUser} ids associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @return the Maps of {@link Person} records identified by {@link WorkbenchUser} ids
	 */
	Map<Integer, Person> getPersonsByProjectId(final Long projectId);

	/**
	 * Gets the all Users Sorted
	 *
	 * @return
	 */
	List<UserDto> getAllUsersSortedByLastName();

	/**
	 * Returns all the Workbench users ordered by First Name then Last Name.
	 *
	 * @return A {@code List} of all the {@code WorkbenchUser}s in the Workbench database.
	 */
	List<WorkbenchUser> getAllActiveUsersSorted();

	/**
	 * Adds a user.
	 *
	 * @param user - the user to add
	 * @return Returns the id of the {@code User} record added
	 */
	Integer addUser(WorkbenchUser user);

	/**
	 * Returns number of all Users.
	 *
	 * @return the number of all Users
	 */
	long countAllUsers();

	/**
	 * create the user.
	 *
	 * @param userDto the user
	 * @return Returns the id of the {@code UserDto} record added
	 */
	public Integer createUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param userDto the user to update
	 * @return Returns the id of the {@code UserDto} record added
	 */
	public Integer updateUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param user the user to update
	 * @return Returns the id of the {@code User} record updated
	 */
	public void updateUser(WorkbenchUser user);

	/**
	 * Deletes a user.
	 *
	 * @param user - the Workbench User to delete
	 */
	void deleteUser(WorkbenchUser user);

	/**
	 * Checks if a username exists.
	 *
	 * @param userName - the user name to check
	 * @return true, if is username exists
	 */
	boolean isUsernameExists(String userName);

	/**
	 * Returns true if user has SUPERADMIN role assigned
	 *
	 * @param userId
	 * @return
	 */
	boolean isSuperAdminUser(Integer userId);

	/**
	 * Deletes the Project_User_Info entries of the removed program members
	 *
	 * @param workbenchUserIds - the user ids of the removed program members
	 * @param projectId        - the project id
	 */
	void removeUsersFromProgram(List<Integer> workbenchUserIds, Long projectId);

	List<UserDto> getUsersByPersonIds(final List<Integer> personIds);

	/**
	 * Gets the person by id.
	 *
	 * @param id - the id to match
	 * @return the person matching the given id
	 */
	Person getPersonById(int id);

	/**
	 * @param email
	 * @return
	 */
	Person getPersonByEmail(String email);

	Person getPersonByEmailAndName(String email, String firstName, String lastName);

	String getPersonName(int userId);

	/**
	 * Returns all Persons.
	 *
	 * @return all Persons
	 */
	List<Person> getAllPersons();

	/**
	 * Returns number of all Persons.
	 *
	 * @return the number of all Persons
	 */
	long countAllPersons();

	/**
	 * Adds the person.
	 *
	 * @param person - the Person to add
	 * @return Returns the id of the {@code Person} record added
	 */
	Integer addPerson(Person person);

	/**
	 * Deletes a person.
	 *
	 * @param person - the Person to delete
	 */
	void deletePerson(Person person);

	/**
	 * Changes the password of the user.
	 *
	 * @param username - the username
	 * @param password - the new password
	 * @return true, if is user login is completed
	 */
	boolean changeUserPassword(String username, String password);

	/**
	 * Checks if is person exists.
	 *
	 * @param firstName - the first name
	 * @param lastName  - the last name
	 * @return true, if is person exists
	 */
	boolean isPersonExists(String firstName, String lastName);

	/**
	 * Checks if person with specified email exists.
	 *
	 * @param email
	 * @return
	 */
	boolean isPersonWithEmailExists(String email);

	/**
	 * Checks if person with specified username AND email exists.
	 *
	 * @param username
	 * @param email
	 * @return
	 */
	boolean isPersonWithUsernameAndEmailExists(String username, String email);

	/**
	 * Get the user info record for the specified user.
	 *
	 * @param userId the user id
	 * @return the user info
	 */
	UserInfo getUserInfo(int userId);

	/**
	 * Get the user info record given the username, not that the username must exist else we'll have null exceptions
	 *
	 * @param username
	 * @return
	 */
	UserInfo getUserInfoByUsername(String username);

	WorkbenchUser getUserByUsername(String userName);

	UserInfo getUserInfoByResetToken(String token);

	UserInfo updateUserInfo(UserInfo userInfo);

	/**
	 * Increments the log in count.
	 *
	 * @param userId the user id
	 */
	void incrementUserLogInCount(int userId);

	/**
	 * Insert or update the specified {@link UserInfo} record.
	 *
	 * @param userDetails the user details
	 */
	void insertOrUpdateUserInfo(UserInfo userDetails);

	/**
	 * Returns list of roles that can be assigned to a new user
	 *
	 * @return
	 */
	List<Role> getAssignableRoles();

	/**
	 * Returns list of roles
	 *
	 * @return
	 */
	List<Role> getAllRoles();

	/**
	 * Return users with SUPERADMIN role
	 *
	 * @return
	 */
	List<WorkbenchUser> getSuperAdminUsers();

	/**
	 * Returns ProjectUserInfo List with the given project id and user ids
	 *
	 * @param projectId
	 * @param ids
	 * @return
	 */
	List<ProjectUserInfo> getProjectUserInfoByProjectIdAndUserIds(Long projectId, List<Integer> ids);

	/**
	 * Returns ProjectUserInfo with the given project id and user id
	 *
	 * @param projectId
	 * @param userId
	 * @return
	 */
	ProjectUserInfo getProjectUserInfoByProjectIdAndUserId(Long projectId, Integer userId);

}
