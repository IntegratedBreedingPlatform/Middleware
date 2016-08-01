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

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListMetadata;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.pojos.UserDefinedField;

/**
 * This is the API for retrieving information about Germplasm Lists.
 *
 * @author Kevin Manansala, Mark Agarrado
 *
 */
public interface GermplasmListManager {

	/**
	 * Returns the GermplasmList identified by the given id.
	 *
	 * @param id - the listid of the GermplasmList
	 * @return Returns the GermplasmList POJO, null if no GermplasmList was retrieved.
	 */
	GermplasmList getGermplasmListById(Integer id);

	/**
	 * Returns all Germplasm list records.
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 *
	 * @return List of GermplasmList POJOs
	 */
	List<GermplasmList> getAllGermplasmLists(int start, int numOfRows);

	/**
	 * Returns all Germplasm list records.
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param instance - can either be Database.CENTRAL or Database.LOCAL
	 *
	 * @return List of GermplasmList POJOs
	 * @deprecated
	 */
	@Deprecated
	List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance);

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
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param operation - can be equal or like
	 * @return List of GermplasmList POJOs
	 */
	List<GermplasmList> getGermplasmListByName(String name, String programUUID, int start, int numOfRows, Operation operation);

	/**
	 * Returns all the Germplasm List records with names matching the given parameter.
	 *
	 * @param name
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param operation - can be equal or like
	 * @param instance - can either be Database.CENTRAL or Database.LOCAL
	 * @return List of GermplasmList POJOs
	 * @deprecated
	 */
	@Deprecated
	List<GermplasmList> getGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance);

	/**
	 * Returns the number of Germplasm List records with names matching the given parameter.
	 *
	 * @param name
	 * @param operation can be Operation.EQUAL or Operation.LIKE
	 * @return The count of Germplasm lists based on the given name and operation
	 */
	long countGermplasmListByName(String name, Operation operation);

	/**
	 * Returns the number of Germplasm List records with names matching the given parameter.
	 *
	 * @param name
	 * @param operation can be Operation.EQUAL or Operation.LIKE
	 * @param database - can either be Database.CENTRAL or Database.LOCAL
	 * @return The count of Germplasm lists based on the given name and operation
	 * @deprecated
	 */
	@Deprecated
	long countGermplasmListByName(String name, Operation operation, Database database);

	/**
	 * Returns the number of Germplasm List records that have the given status.
	 *
	 * @param status
	 * @param instance - can either be Database.CENTRAL or Database.LOCAL
	 * @return The count of Germplasm lists based on the given status.
	 */
	long countGermplasmListByStatus(Integer status, Database instance);

	/**
	 * Returns the germplasm lists that are associated with the specified GID.
	 *
	 * @param gid - the Germplasm ID associated with the Germplasm Lists to be returned.
	 * @param start - the starting index of the sublist of results to be returned
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
	 * @param entryId
	 * @return List of GermplasmListData POJOs
	 */
	GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId);

	/**
	 * Returns the germplasm list entry which is identified by the given parameters.
	 *
	 * @param listId
	 * @param lrecId
	 * @return List of GermplasmListData POJOs
	 */
	GermplasmListData getGermplasmListDataByListIdAndLrecId(Integer listId, Integer lrecId);

	/**
	 * Returns the germplasm list entries associated with the Germplasm identified by the given gid.
	 *
	 * @param gid
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 *
	 * @return List of GermplasmListData POJOs
	 */
	List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows);

	/**
	 * Returns the number of germplasm list entries associated with the Germplasm identified by the given gid.
	 *
	 * @param gid
	 * @return The count of Germplasm List data based on the given GID
	 */
	long countGermplasmListDataByGID(Integer gid);

	/**
	 * Returns the Top Level Germplasm List Folders present in the specified database.
	 *
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @param instance - can either be Database.CENTRAL or Database.LOCAL
	 * @return - List of GermplasmList POJOs
	 */
	List<GermplasmList> getAllTopLevelLists(int start, int numOfRows, Database instance);

	/**
	 * Returns the Top Level Germplasm List Folders present in the program of the specified database. Retrieval from the database is done by
	 * batch (as specified in batchSize) to reduce the load in instances where there is a large volume of top level folders to be retrieved.
	 * Though retrieval is by batch, this method still returns all of the top level folders as a single list.
	 *
	 * @param programUUID - the program UUID
	 * @param batchSize - the number of records to be retrieved per iteration
	 * @return - List of GermplasmList POJOs
	 */
	List<GermplasmList> getAllTopLevelListsBatched(String programUUID, int batchSize);

	/**
	 * Returns the number of Top Level Germplasm List Folders with the same or null program UUID.
	 *
	 * @param programUUID
	 * @return The count of all top level lists on the specified instance.
	 */
	long countAllTopLevelLists(String programUUID);

	/**
	 * Inserts a single {@code GermplasmList} object into the database.
	 *
	 * @param germplasmList - The {@code GermplasmList} object to be persisted to the database. Must be a valid {@code GermplasmList}
	 *        object.
	 * @return Returns the GermplasmList id that was assigned to the new GermplasmLists
	 */
	Integer addGermplasmList(GermplasmList germplasmList);

	/**
	 * Inserts a list of multiple {@code GermplasmList} objects into the database.
	 *
	 * @param germplasmLists - A list of {@code GermplasmList} objects to be persisted to the database. {@code GermplasmList} objects must
	 *        be valid.
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
	 *        valid.
	 * @return Returns the list of GermplasmList ids that were updated in the database.
	 */
	List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists);

	/**
	 * Service to delete all lists that belong to a program
	 *
	 * @param programUUID : string - the unique program key that denotes a program
	 * @return int : returns number of rows deleted from the DB
	 *
	 */
	public int deleteGermplasmListsByProgram(String programUUID);

	/**
	 * Removes the specified {@code GermplasmList} object from the database.
	 *
	 * @param germplasmList - The {@code GermplasmList} object to be removed from the database. Must be a valid {@code GermplasmList}
	 *        object.
	 * @return Returns the number of {@code GermplasmList} objects deleted from the database.
	 */
	int deleteGermplasmList(GermplasmList germplasmList);

	/**
	 * Removes the specified {@code GermplasmList} objects from the database.
	 *
	 * @param germplasmLists - A list of {@code GermplasmList} objects to be removed from the database. {@code GermplasmList} objects must
	 *        be valid.
	 * @return Returns the number of {@code GermplasmList} objects deleted from the database.
	 */
	int deleteGermplasmList(List<GermplasmList> germplasmLists);

	/**
	 * Removes the corresponding {@code GermplasmList} record and all related Data from the database given the List ID.
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List record to be deleted.
	 * @return Returns the number of {@code GermplasmList} records deleted from the database.
	 */
	int deleteGermplasmListByListId(Integer listId);

	/**
	 * Inserts a {@code GermplasmListData} object into the database.
	 *
	 * @param germplasmListData - The {@code GermplasmListData} object to be persisted to the database. Must be a valid
	 *        {@code GermplasmListData} object.
	 * @return Returns the id of {@code GermplasmListData} record inserted in the database.
	 */
	Integer addGermplasmListData(GermplasmListData germplasmListData);

	/**
	 * Inserts a list of multiple {@code GermplasmListData} objects into the database.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be persisted to the database. {@code GermplasmListData}
	 *        objects must be valid.
	 * @return Returns the ids of the {@code GermplasmListData} records inserted in the database.
	 */
	List<Integer> addGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Updates the database with the {@code GermplasmListData} object specified.
	 *
	 * @param germplasmListData - The {@code GermplasmListData} object to be updated in the database. Must be a valid
	 *        {@code GermplasmListData} object.
	 * @return Returns the number of {@code GermplasmListData} records updated in the database.
	 * @return Returns the id of the updated {@code GermplasmListData} record
	 */
	Integer updateGermplasmListData(GermplasmListData germplasmListData);

	/**
	 * Updates the database with the {@code GermplasmListData} objects specified.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be updated in the database. Must be valid
	 *        {@code GermplasmListData} objects.
	 * @return Returns the ids of the updated {@code GermplasmListData} records
	 */
	List<Integer> updateGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Removes the corresponding {@code GermplasmListData} record from the database given its List ID and Entry ID.
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List Data to be deleted.
	 * @param entryId - {@code GermplasmListData} Entry ID of the Germplasm List Data to be deleted.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId);

	/**
	 * Removes the corresponding {@code GermplasmListData} record from the database given its List ID and Entry ID.
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List Data to be deleted.
	 * @param lrecId - {@code GermplasmListData} Entry ID of the Germplasm List Data to be deleted.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListDataByListIdLrecId(Integer listId, Integer lrecId);

	/**
	 * Removes the corresponding {@code GermplasmListData} records from the database given their List ID.
	 *
	 * @param listId - {@code GermplasmList} ID of the Germplasm List Data records to be deleted.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListDataByListId(Integer listId);

	/**
	 * Removes the specified {@code GermplasmListData} object from the database.
	 *
	 * @param germplasmListData - The {@code GermplasmListData} object to be removed from the database. Must be a valid
	 *        {@code GermplasmListData} object.
	 * @return Returns the number of {@code GermplasmListData} objects deleted from the database.
	 */
	int deleteGermplasmListData(GermplasmListData germplasmListData);

	/**
	 * Removes the specified {@code GermplasmListData} objects from the database.
	 *
	 * @param germplasmListDatas - A list of {@code GermplasmListData} objects to be removed from the database. {@code GermplasmListData}
	 *        objects must be valid.
	 * @return Returns the number of {@code GermplasmListData} records deleted from the database.
	 */
	int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas);

	/**
	 * Returns a list of {@code GermplasmList} child records given a parent id.
	 *
	 * @param parentId - the ID of the parent to retrieve the child lists
	 * @param programUUID - the program UUID of the program where to retrieve the child lists
	 * @param start - the starting point to retrieve the results
	 * @param numOfRows - the number of rows from the starting point to be retrieved
	 * @return Returns a List of GermplasmList POJOs for the child lists
	 */
	List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, String programUUID, int start, int numOfRows);

	GermplasmList getLastSavedGermplasmListByUserId(Integer userID, String programUUID);

	/**
	 * Returns a list of {@code GermplasmList} child records given a parent id. Retrieval from the database is done by batch (as specified
	 * in batchSize) to reduce the load in instances where there is a large volume of child folders to be retrieved. Though retrieval is by
	 * batch, this method still returns all of the child folders as a single list.
	 *
	 * @param parentId - the ID of the parent to retrieve the child lists
	 * @param programUUID - the program UUID of the program where to retrieve the child lists
	 * @param batchSize - the number of records to be retrieved per iteration
	 * @return Returns a List of GermplasmList POJOs for the child lists
	 */
	List<GermplasmList> getGermplasmListByParentFolderIdBatched(Integer parentId, String programUUID, int batchSize);

	/**
	 * Returns the number of {@code GermplasmList} child records given a parent id.
	 *
	 * @param parentId the parent id
	 * @param programUUID the program UUID
	 * @return number of germplasm list child records of a parent record
	 */
	long countGermplasmListByParentFolderId(Integer parentId, String programUUID);

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
	 * Search for germplasm lists given a search term Q
	 *
	 * @param q string
	 * @param o operation
	 * @return - List of germplasm lists
	 */
	List<GermplasmList> searchForGermplasmList(String q, Operation o);

	/**
	 * Search for germplasm lists given a search term Q under a specific program
	 *
	 * @param q string
	 * @param programUUID string
	 * @param o operation
	 * @return - List of germplasm lists
	 */
	List<GermplasmList> searchForGermplasmList(String q, String programUUID, Operation o);

	/**
	 * Inserts or updates the ListDataProperty records (columns) corresponding to the ListDataColumn records
	 *
	 * @param listDataCollection
	 * @return the list of ListDataInfo objects with the listDataColumnId field of each ListDataColumn filled up if the corresponding
	 *         ListDataProperty object was successfully inserted or updated
	 */
	List<ListDataInfo> saveListDataColumns(List<ListDataInfo> listDataCollection);

	/**
	 * Retrieves list of distinct column names from ListDataProperty for given list Returns empty list if no related column found.
	 *
	 * @param listId - id of list to retrieve columns for
	 * @return GermplasmListNewColumnsInfo of the additional columns for the given list
	 */
	GermplasmListNewColumnsInfo getAdditionalColumnsForList(Integer listId);

	long countListDataProjectGermplasmListDataByListId(Integer id);

	/**
	 * Save list data properties
	 *
	 * @param listDataProps
	 * @return
	 */
	List<ListDataProperty> saveListDataProperties(List<ListDataProperty> listDataProps);

	/**
	 *
	 * @param listID
	 * @return
	 */
	List<ListDataProject> retrieveSnapshotListData(Integer listID);

	/**
	 *
	 * @param listID
	 * @return
	 */
	List<ListDataProject> retrieveSnapshotListDataWithParents(Integer listID);

	/**
	 *
	 * @param listID
	 * @return
	 */
	List<GermplasmListData> retrieveListDataWithParents(Integer listID);

	Integer retrieveDataListIDFromListDataProjectListID(Integer listDataProjectListID);

	/***
	 *
	 * @param listRef
	 * @return
	 */
	org.generationcp.middleware.pojos.GermplasmList getGermplasmListByListRef(Integer listRef);

	/**
	 * Retrieves metadata (such as count of entries, list owner) in one go for lists ids provide.
	 * This helps avoiding the need to query metadata in
	 * @param germplasmListParent ids for which we should retrieve metadata
	 */
	Map<Integer, GermplasmListMetadata> getGermplasmListMetadata(List<GermplasmList> germplasmListParent);

	List<GermplasmList> getAllGermplasmListsByProgramUUID(String currentProgramUUID);

	/**
	 * Returns the number of germplasm lists that are associated with the specified GID and programUUID.
	 *
	 * @param gid - the Germplasm ID associated with the Germplasm Lists to be returned.
	 * @param programUUID - the unique id of the the current program the user is in.
	 * @return The count of Germplasm Lists associated with the given Germplasm ID/
	 */
	long countGermplasmListByGIDandProgramUUID(Integer gid, String programUUID);

	/**
	 * Returns the germplasm lists that are associated with the specified GID and programUUID.
	 *
	 * @param gid - the Germplasm ID associated with the Germplasm Lists to be returned.
	 * @param start - start number of page
	 * @param numOfRows - number of rows per page
	 * @param programUUID - the unique id of the the current program the user is in.
	 * @return The count of Germplasm Lists associated with the given Germplasm ID/
	 */
	List<GermplasmList> getGermplasmListByGIDandProgramUUID(Integer gid, int start, int numOfRows, String programUUID);
}
