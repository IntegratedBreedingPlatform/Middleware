package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.dao.workbench.ProgramEligibleUsersSearchRequest;
import org.generationcp.middleware.dao.workbench.ProgramMembersSearchRequest;
import org.generationcp.middleware.domain.workbench.ProgramMemberDto;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
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
	WorkbenchUser getUserById(Integer userId);

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

	UserDto getUserByFullname(String fullname);

	Long countUsersByFullname(String fullname);

	Map<Integer, String> getUserIDFullNameMap(List<Integer> userIds);

	Map<Integer, String> getAllUserIDFullNameMap();

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
	 * Checks if a username exists.
	 *
	 * @param userName - the user name to check
	 * @return true, if is username exists
	 */
	boolean isUsernameExists(String userName);

	List<UserDto> getUsersByPersonIds(List<Integer> personIds);

	/**
	 * Gets the person by id.
	 *
	 * @param id - the id to match
	 * @return the person matching the given id
	 */
	Person getPersonById(int id);

	/**
	 * Returns the user's person name given the user id
	 *
	 * @param userId
	 * @return
	 */
	String getPersonNameForUserId(int userId);

	/**
	 * Returns the person name given the person id
	 *
	 * @param personId
	 * @return
	 */
	String getPersonNameForPersonId(int personId);

	List<Person> getPersonsByCrop(CropType cropType);

	/**
	 * Changes the password of the user.
	 *
	 * @param username - the username
	 * @param password - the new password
	 * @return true, if is user login is completed
	 */
	boolean changeUserPassword(String username, String password);

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

	Map<Integer, String> getPersonNamesByPersonIds(List<Integer> personIds);

	WorkbenchUser getUserWithAuthorities(String userName, String cropName, String programUuid);

	List<WorkbenchUser> getUsersWithRole(int id);

	List<ProgramMemberDto> getProgramMembers(String programUUID, ProgramMembersSearchRequest userSearchRequest, Pageable pageable);

	long countAllProgramMembers(String programUUID, ProgramMembersSearchRequest userSearchRequest);

	List<UserDto> getProgramMembersEligibleUsers(String programUUID, ProgramEligibleUsersSearchRequest searchRequest, Pageable pageable);

	long countProgramMembersEligibleUsers(String programUUID, ProgramEligibleUsersSearchRequest searchRequest);

	long countAllActiveUsers();

}
