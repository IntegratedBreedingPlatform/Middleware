/*******************************************************************************
 * o * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.UserDefinedField;

import java.util.List;
import java.util.Map;

/**
 * This is the API for retrieving information about Germplasm Lists.
 *
 * @author Kevin Manansala, Mark Agarrado
 */
public interface GermplasmListManager {

	/**
	 * This method is deprecated. Please, use {@link org.generationcp.middleware.api.germplasmlist.GermplasmListService#getGermplasmListById(Integer)}
	 *
	 * Returns the GermplasmList identified by the given id.
	 *
	 * @param id - the listid of the GermplasmList
	 * @return Returns the GermplasmList POJO, null if no GermplasmList was retrieved.
	 */
	@Deprecated
	GermplasmList getGermplasmListById(Integer id);

	/**
	 * Returns all Germplasm list records.
	 *
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of GermplasmList POJOs
	 */
	List<GermplasmList> getAllGermplasmLists(int start, int numOfRows);

	/**
	 * Returns the total number of Germplasm Lists.
	 *
	 * @return The count of all germplasm lists.
	 */
	long countAllGermplasmLists();

	/**
	 * Returns all the Germplasm List records with names matching the given parameter.
	 *
	 * @param name
	 * @param programUUID
	 * @param start       - the starting index of the sublist of results to be returned
	 * @param numOfRows   - the number of rows to be included in the sublist of results to be returned
	 * @param operation   - can be equal or like
	 * @return List of GermplasmList POJOs
	 */
	List<GermplasmList> getGermplasmListByName(String name, String programUUID, int start, int numOfRows, Operation operation);

	/**
	 * Returns the number of Germplasm List records with names matching the given parameter.
	 *
	 * @param name
	 * @param operation can be Operation.EQUAL or Operation.LIKE
	 * @return The count of Germplasm lists based on the given name and operation
	 */
	long countGermplasmListByName(String name, Operation operation);

	/**
	 * Returns the germplasm lists that are associated with the specified GID.
	 *
	 * @param gid       - the Germplasm ID associated with the Germplasm Lists to be returned.
	 * @param start     - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of GermplasmList POJOs
	 */
	List<GermplasmList> getGermplasmListByGID(Integer gid, int start, int numOfRows);

	/**
	 * Returns the number of germplasm lists that are associated with the specified GID.
	 *
	 * @param gid - the Germplasm ID associated with the Germplasm Lists to be returned.
	 * @return The count of Germplasm Lists associated with the given Germplasm ID/
	 */
	long countGermplasmListByGID(Integer gid);

	/**
	 * Returns the germplasm list entries that belong to the list identified by the given id.
	 *
	 * @param id
	 * @return List of GermplasmListData POJOs
	 */
	List<GermplasmListData> getGermplasmListDataByListId(Integer id);

	/**
	 * Returns the number of germplasm list entries that belong to the list identified by the given id.
	 *
	 * @param id
	 * @return The count of Germplasm list data based on the given list ID
	 */
	long countGermplasmListDataByListId(Integer id);

	/**
	 * Returns the germplasm list entries that belong to the list identified by the given id and have gids equal to the given parameter.
	 *
	 * @param listId
	 * @param gid
	 * @return List of GermplasmListData POJOs
	 */
	List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid);

	/**
	 * Returns the germplasm list entry which is identified by the given parameters.
	 *
	 * @param listId
	 * @param lrecId
	 * @return List of GermplasmListData POJOs
	 */
	GermplasmListData getGermplasmListDataByListIdAndLrecId(Integer listId, Integer lrecId);

	/**
	 * Returns the Top Level Germplasm List Folders present in the program of the specified database. Retrieval from the database is done by
	 * batch (as specified in batchSize) to reduce the load in instances where there is a large volume of top level folders to be retrieved.
	 * Though retrieval is by batch, this method still returns all of the top level folders as a single list.
	 *
	 * @param programUUID - the program UUID
	 * @return - List of GermplasmList POJOs
	 */
	List<GermplasmList> getAllTopLevelLists(String programUUID);

	/**
	 * Inserts a single {@code GermplasmList} object into the database.
	 *
	 * @param germplasmList - The {@code GermplasmList} object to be persisted to the database. Must be a valid {@code GermplasmList}
	 *                      object.
	 * @return Returns the GermplasmList id that was assigned to the new GermplasmLists
	 */
	Integer addGermplasmList(GermplasmList germplasmList);

	/**
	 * Inserts a list of multiple {@code GermplasmList} objects into the database.
	 *
	 * @param germplasmLists - A list of {@code GermplasmList} objects to be persisted to the database. {@code GermplasmList} objects must
	 *                       be valid.
	 * @return Returns the list of GermplasmList ids that were assigned to the new GermplasmLists
	 */
	List<Integer> addGermplasmList(List<GermplasmList> germplasmLists);

	/**
	 * Updates the database with the {@code GermplasmList} object specified.
	 *
	 * @param germplasmList - The {@code GermplasmList} object to be updated in the database. Must be a valid {@code GermplasmList} object.
	 * @return Returns the list of GermplasmList ids that were updated in the database.
	 */
	Integer updateGermplasmList(GermplasmList germplasmList);

	/**
	 * Updates the database with multiple {@code GermplasmList} objects specified.
	 *
	 * @param germplasmLists - A list of {@code GermplasmList} objects to be updated in the database. {@code GermplasmList} objects must be
	 *                       valid.
	 * @return Returns the list of GermplasmList ids that were updated in the database.
	 */
	List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists);

	/**
	 * Service to delete all lists that belong to a program
	 *
	 * @param programUUID : string - the unique program key that denotes a program
	 * @return int : returns number of rows deleted from the DB
	 */
	int deleteGermplasmListsByProgram(String programUUID);

	/**
	 * Removes the specified {@code GermplasmList} object from the database.
	 *
	 * @param germplasmList - The {@code GermplasmList} object to be removed from the database. Must be a valid {@code GermplasmList}
	 *                      object.
	 * @return Returns the number of {@code GermplasmList} objects deleted from the database.
	 */
	int deleteGermplasmList(GermplasmList germplasmList);

	/**
	 * Removes the specified {@code GermplasmList} objects from the database.
	 *
	 * @param germplasmLists - A list of {@code GermplasmList} objects to be removed from the database. {@code GermplasmList} objects must
	 *                       be valid.
	 * @return Returns the number of {@code GermplasmList} objects deleted from the database.
	 */
	int deleteGermplasmList(List<GermplasmList> germplasmLists);

	/**
	 * Removes the corresponding {@code GermplasmList} record and all related Data from the database given the List ID.
	 *
	 * <b>WARNING</b>: this method physically removes records from the DB<br/>
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List record to be deleted.
	 * @return Returns the number of {@code GermplasmList} records deleted from the database.
	 */
	int deleteGermplasmListByListIdPhysically(Integer listId);

	/**
	 * Inserts a {@code GermplasmListData} object into the database.
	 *
	 * @param germplasmListData - The {@code GermplasmListData} object to be persisted to the database. Must be a valid
	 *                          {@code GermplasmListData} object.
	 * @return Returns the id of {@code GermplasmListData} record inserted in the database.
	 */
	Integer addGermplasmListData(GermplasmListData germplasmListData);

	/**
	 * This method is deprecated. Please, use {@link org.generationcp.middleware.api.germplasmlist.GermplasmListService#addGermplasmListData(List)}
	 *
	 * Inserts a list of multiple {@code GermplasmListData} objects into the database.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be persisted to the database. {@code GermplasmListData}
	 *                           objects must be valid.
	 * @return Returns the ids of the {@code GermplasmListData} records inserted in the database.
	 */
	@Deprecated
	List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Updates the database with the {@code GermplasmListData} objects specified.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be updated in the database. Must be valid
	 *                           {@code GermplasmListData} objects.
	 * @return Returns the ids of the updated {@code GermplasmListData} records
	 */
	List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Removes the corresponding {@code GermplasmListData} records from the database given their List ID.
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List Data records to be deleted.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListDataByListId(Integer listId);

	/**
	 * Removes the specified {@code GermplasmListData} objects from the database.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be removed from the database. {@code GermplasmListData}
	 *                           objects must be valid.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Returns a list of {@code GermplasmList} child records given a parent id.
	 *
	 * @param parentId    - the ID of the parent to retrieve the child lists
	 * @param programUUID - the program UUID of the program where to retrieve the child lists
	 * @return Returns a List of GermplasmList POJOs for the child lists
	 */
	List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, String programUUID);

	/**
	 * Returns a list of {@code GermplasmList} child records given a parent id.
	 *
	 * @param parentId    - the ID of the parent to retrieve the child lists
	 * @return Returns a List of GermplasmList POJOs for the child lists
	 */
	List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId);

	GermplasmList getLastSavedGermplasmListByUserId(Integer userID, String programUUID);

	/**
	 * Return a List of UserDefinedField POJOs representing records from the udflds table of IBDB which are the types of germplasm lists.
	 *
	 * @return List of UserDefinedField values of the germplasm list types
	 */
	List<UserDefinedField> getGermplasmListTypes();

	/**
	 * Return a List of UserDefinedField POJOs representing records from the udflds table of IBDB which are the types of germplasm names.
	 *
	 * @return List of UserDefinedField values of the germplasm name types
	 */
	List<UserDefinedField> getGermplasmNameTypes();

	/**
	 * @param listID
	 * @return
	 */
	List<GermplasmListData> retrieveGermplasmListDataWithParents(Integer listID);

	List<GermplasmList> getAllGermplasmListsByProgramUUID(String currentProgramUUID);

	/**
	 * @param listIds a list of listid for which we want the corresponding germplasm list
	 * @return the resultant germplasm list
	 */
	List<GermplasmList> getAllGermplasmListsByIds(List<Integer> listIds);

	void populateGermplasmListCreatedByName(List<GermplasmList> germplasmLists);

	Map<Integer, ListMetadata> getGermplasmListMetadata(List<GermplasmList> listIds);

}
