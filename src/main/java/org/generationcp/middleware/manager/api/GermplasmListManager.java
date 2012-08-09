/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

/**
 * This is the API for retrieving information about Germplasm Lists.
 * 
 * @author Kevin Manansala, Mark Agarrado
 * 
 */
public interface GermplasmListManager{

    /**
     * Returns the GermplasmList identified by the given id.
     * 
     * @param id
     * @return GermplasmList POJO
     */
    public GermplasmList getGermplasmListById(Integer id);

    /**
     * Returns all Germplasm list records.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * 
     * @return List of GermplasmList POJOs
     * @throws QueryException
     */
    public List<GermplasmList> getAllGermplasmLists(int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Returns the total number of Germplasm Lists.
     * 
     * @return
     */
    public int countAllGermplasmLists();

    /**
     * Returns all the Germplasm List records with names matching the given
     * parameter.
     * 
     * @param name
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param operation
     *            - can be equal or like
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return List of GermplasmList POJOs
     * @throws QueryException
     */
    public List<GermplasmList> findGermplasmListByName(String name, int start, int numOfRows, Operation operation, Database instance)
            throws QueryException;

    /**
     * Returns the number of Germplasm List records with names matching the
     * given parameter.
     * 
     * @param name
     * @param operation
     * @return
     */
    public int countGermplasmListByName(String name, Operation operation);

    /**
     * Returns all the Germplasm List records that have the given status.
     * 
     * @param status
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * 
     * @return List of Germplasm POJOs
     * @throws QueryException
     */
    public List<GermplasmList> findGermplasmListByStatus(Integer status, int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Returns the number of Germplasm List records that have the given status.
     * 
     * @param status
     * @return
     */
    public int countGermplasmListByStatus(Integer status);

    /**
     * Returns the germplasm list entries that belong to the list identified by
     * the given id.
     * 
     * @param id
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of GermplasmListData POJOs
     */
    public List<GermplasmListData> getGermplasmListDataByListId(Integer id, int start, int numOfRows);

    /**
     * Returns the number of germplasm list entries that belong to the list
     * identified by the given id.
     * 
     * @param id
     * @return
     */
    public int countGermplasmListDataByListId(Integer id);

    /**
     * Returns the germplasm list entries that belong to the list identified by
     * the given id and have gids equal to the given parameter.
     * 
     * @param listId
     * @param gid
     * @return List of GermplasmListData POJOs
     */
    public List<GermplasmListData> getGermplasmListDataByListIdAndGID(Integer listId, Integer gid);

    /**
     * Returns the germplasm list entry which is identified by the given
     * parameters.
     * 
     * @param listId
     * @param entryId
     * @return List of GermplasmListData POJOs
     */
    public GermplasmListData getGermplasmListDataByListIdAndEntryId(Integer listId, Integer entryId);

    /**
     * Returns the germplasm list entries associated with the Germplasm
     * identified by the given gid.
     * 
     * @param gid
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * 
     * @return List of GermplasmListData POJOs
     * @throws QueryException
     */
    public List<GermplasmListData> getGermplasmListDataByGID(Integer gid, int start, int numOfRows) throws QueryException;

    /**
     * Returns the number of germplasm list entries associated with the
     * Germplasm identified by the given gid.
     * 
     * @param gid
     * @return
     */
    public int countGermplasmListDataByGID(Integer gid);

    /**
     * Returns the Top Level Germplasm List Folders present in the specified database.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return - List of GermplasmList POJOs
     * @throws QueryException
     */
    public List<GermplasmList> getTopLevelFolders(int start, int numOfRows, Database instance) throws QueryException;
    
    /**
     * Returns the number of Top Level Germplasm List Folders in the specified database.
     * 
     * @param instance
     *            - can either be Database.CENTRAL or Database.LOCAL
     * @return
     * @throws QueryException
     */
    public int countTopLevelFolders(Database instance) throws QueryException;

    /**
     * Inserts a single {@code GermplasmList} object into the database.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be persisted to the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the list of GermplasmList ids that were assigned to the new GermplasmLists
     * @throws QueryException
     */
    public Integer addGermplasmList(GermplasmList germplasmList) throws QueryException;

    /**
     * Inserts a list of multiple {@code GermplasmList} objects into the
     * database.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be persisted to
     *            the database. {@code GermplasmList} objects must be valid.
     * @return Returns the list of GermplasmList ids that were assigned to the new GermplasmLists
     * @throws QueryException
     */
    public List<Integer> addGermplasmList(List<GermplasmList> germplasmLists) throws QueryException;

    /**
     * Updates the database with the {@code GermplasmList} object specified.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be updated in the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the list of GermplasmList ids that were updated in the database.
     * @throws QueryException
     */
    public Integer updateGermplasmList(GermplasmList germplasmList) throws QueryException;

    /**
     * Updates the database with multiple {@code GermplasmList} objects
     * specified.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be updated in the
     *            database. {@code GermplasmList} objects must be valid.
     * @return Returns the list of GermplasmList ids that were updated in the database.
     * @throws QueryException
     */
    public List<Integer> updateGermplasmList(List<GermplasmList> germplasmLists) throws QueryException;

    /**
     * Removes the specified {@code GermplasmList} object from the database.
     * 
     * @param germplasmList
     *            - The {@code GermplasmList} object to be removed from the
     *            database. Must be a valid {@code GermplasmList} object.
     * @return Returns the number of {@code GermplasmList} objects deleted from
     *         the database.
     * @throws QueryException
     */
    public int deleteGermplasmList(GermplasmList germplasmList) throws QueryException;

    /**
     * Removes the specified {@code GermplasmList} objects from the database.
     * 
     * @param germplasmLists
     *            - A list of {@code GermplasmList} objects to be removed from
     *            the database. {@code GermplasmList} objects must be valid.
     * @return Returns the number of {@code GermplasmList} objects deleted from
     *         the database.
     * @throws QueryException
     */
    public int deleteGermplasmList(List<GermplasmList> germplasmLists) throws QueryException;

    /**
     * Removes the corresponding {@code GermplasmList} record and all related
     * Data from the database given the List ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List record to be
     *            deleted.
     * @return Returns the number of {@code GermplasmList} records deleted from
     *         the database.
     * @throws QueryException
     */
    public int deleteGermplasmListByListId(Integer listId) throws QueryException;

    /**
     * Inserts a {@code GermplasmListData} object into the database.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be persisted to the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the number of {@code GermplasmListData} records inserted
     *         in the database.
     * @throws QueryException
     */
    public int addGermplasmListData(GermplasmListData germplasmListData) throws QueryException;

    /**
     * Inserts a list of multiple {@code GermplasmListData} objects into the
     * database.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be persisted
     *            to the database. {@code GermplasmListData} objects must be
     *            valid.
     * @return Returns the number of {@code GermplasmListData} records inserted
     *         in the database.
     * @throws QueryException
     */
    public int addGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException;

    /**
     * Updates the database with the {@code GermplasmListData} object specified.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be updated in the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the number of {@code GermplasmListData} records updated
     *         in the database.
     * @throws QueryException
     */
    public int updateGermplasmListData(GermplasmListData germplasmListData) throws QueryException;

    /**
     * Updates the database with the {@code GermplasmListData} objects
     * specified.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be updated in
     *            the database. Must be valid {@code GermplasmListData} objects.
     * @return Returns the number of {@code GermplasmListData} records updated
     *         in the database.
     * @throws QueryException
     */
    public int updateGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException;

    /**
     * Removes the corresponding {@code GermplasmListData} record from the
     * database given its List ID and Entry ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List Data to be
     *            deleted.
     * @param entryId
     *            - {@code GermplasmListData} Entry ID of the Germplasm List
     *            Data to be deleted.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws QueryException
     */
    public int deleteGermplasmListDataByListIdEntryId(Integer listId, Integer entryId) throws QueryException;

    /**
     * Removes the corresponding {@code GermplasmListData} records from the
     * database given their List ID.
     * 
     * @param listId
     *            - {@code GermplasmList} ID of the Germplasm List Data records
     *            to be deleted.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws QueryException
     */
    public int deleteGermplasmListDataByListId(Integer listId) throws QueryException;

    /**
     * Removes the specified {@code GermplasmListData} object from the database.
     * 
     * @param germplasmListData
     *            - The {@code GermplasmListData} object to be removed from the
     *            database. Must be a valid {@code GermplasmListData} object.
     * @return Returns the number of {@code GermplasmListData} objects deleted
     *         from the database.
     * @throws QueryException
     */
    public int deleteGermplasmListData(GermplasmListData germplasmListData) throws QueryException;

    /**
     * Removes the specified {@code GermplasmListData} objects from the
     * database.
     * 
     * @param germplasmListDatas
     *            - A list of {@code GermplasmListData} objects to be removed
     *            from the database. {@code GermplasmListData} objects must be
     *            valid.
     * @return Returns the number of {@code GermplasmListData} records deleted
     *         from the database.
     * @throws QueryException
     */
    public int deleteGermplasmListData(List<GermplasmListData> germplasmListDatas) throws QueryException;

    /**
     * Returns a list of {@code GermplasmList} child records given a parent id.
     *
     * @param parentId the parent id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the germplasm list children
     * @throws QueryException the query exception
     */
    public List<GermplasmList> getGermplasmListByParentFolderId(Integer parentId, int start, int numOfRows) throws QueryException;
    
    /**
     * Returns the number of {@code GermplasmList} child records given a parent id.
     *
     * @param parentId the parent id
     * @return number of germplasm list child records of a parent record
     * @throws QueryException the query exception
     */
    public Long countGermplasmListByParentFolderId(Integer parentId) throws QueryException;
}
