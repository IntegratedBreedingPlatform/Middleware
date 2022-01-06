package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.ProgramMemberDto;
import org.generationcp.middleware.domain.workbench.UserSearchRequest;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.data.domain.Pageable;

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

	List<WorkbenchUser> getUsersByIds(List<Integer> userIds);

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

	Long countUsersByFullname(final String fullname);

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
	 * Return a List of {@link WorkbenchUser} records associated with a {@link Project}.
	 *
	 * @param projectId - the project id
	 * @return the List of {@link WorkbenchUser} records
	 */
	List<WorkbenchUser> getUsersByProjectId(Long projectId);

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
	 * @return Returns the the {@code WorkbenchUser} record added
	 */
	WorkbenchUser addUser(WorkbenchUser user);

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
	Integer createUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param userDto the user to update
	 * @return Returns the id of the {@code UserDto} record added
	 */
	Integer updateUser(UserDto userDto);

	/**
	 * Updates the user.
	 *
	 * @param user the user to update
	 * @return Returns the id of the {@code User} record updated
	 */
	void updateUser(WorkbenchUser user);

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

	List<UserDto> getUsersByPersonIds(final List<Integer> personIds);

	/**
	 * Gets the person by id.
	 *
	 * @param id - the id to match
	 * @return the person matching the given id
	 */
	Person getPersonById(int id);

	/**
	 * Returns the user's person name given the user id
	 * @param userId
	 * @return
	 */
	String getPersonNameForUserId(int userId);

	/**
	 * Returns the person name given the person id
	 * @param personId
	 * @return
	 */
	String getPersonNameForPersonId(int personId);

	List<Person> getPersonsByCrop(CropType cropType);

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
	 * @return Returns the {@code Person} record added
	 */
	Person addPerson(Person person);

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
	 * Return users with SUPERADMIN role
	 *
	 * @return
	 */
	List<WorkbenchUser> getSuperAdminUsers();

	/**
	 * Returns ProjectUserInfo with the given project id and user id
	 *
	 * @param projectId
	 * @param userId
	 * @return
	 */
	ProjectUserInfo getProjectUserInfoByProjectIdAndUserId(Long projectId, Integer userId);

	/**
	 * Saves or updates the ProjectUserInfo.
	 *
	 * @param projectUserInfo the project user info
	 * @return ProjectUserInfo
	 */
	void saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo);

	void saveCropPerson(CropPerson cropPerson);

	void removeCropPerson(CropPerson cropPerson);

	Map<Integer, String> getPersonNamesByPersonIds(List<Integer> personIds);

	WorkbenchUser getUserWithAuthorities(final String userName, final String cropName, final String programUuid);

	List<WorkbenchUser> getUsersWithRole(int id);

	List<Integer> getActiveUserIDsWithAccessToTheProgram(Long projectId);

	List<ProgramMemberDto> getProgramMembers(String programUUID, UserSearchRequest userSearchRequest, Pageable pageable);

	long countAllProgramMembers(String programUUID, final UserSearchRequest userSearchRequest);

	List<UserDto> getProgramMembersEligibleUsers(String programUUID, UserSearchRequest userSearchRequest, Pageable pageable);

	long countProgramMembersEligibleUsers(String programUUID, UserSearchRequest userSearchRequest);

}
