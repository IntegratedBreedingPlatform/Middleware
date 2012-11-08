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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.DatasetCondition;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.StudyInfo;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;

/**
 * This is the API for retrieving phenotypic data stored as Studies and
 * datasets.
 * 
 * @author Kevin Manansala
 * 
 */
public interface StudyDataManager{

    /**
     * Returns a List of GIDs identifying Germplasms exhibiting specific values
     * of traits as observed in studies. The search filters are composed of
     * combinations of trait, scale, method and value specified by the users.
     * 
     * The start and numOfRows will be used to limit results of the queries used
     * to retrieve the GIDs. They do not however depict the size of the List of
     * Integers returned by this function. It is recommended to check the size
     * of the List to get the actual count of GIDs it contains.
     * 
     * @param filters
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - the database instance to connect to: Database.CENTRAL,
     *            Database.LOCAL
     * @return
     * @throws MiddlewareQueryException
     */
    public List<Integer> getGIDSByPhenotypicData(List<TraitCombinationFilter> filters, int start, int numOfRows, Database instance)
            throws MiddlewareQueryException;

    /**
     * Returns the study records matching the given name
     * 
     * @param name
     *            - search string (pattern or exact match) for the name of the
     *            study
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE For LIKE operation, the parameter
     *            name may include the following: "%" - to indicate 0 or more
     *            characters in the pattern "_" - to indicate any single
     *            character in the pattern
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return List of Study POJOs
     * @throws MiddlewareQueryException
     */
    public List<Study> getStudyByName(String name, int start, int numOfRows, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the study records matching the given name
     * 
     * @param name
     *            - search string (pattern or exact match) for the name of the
     *            study
     * @param op
     *            - can be EQUAL like LIKE For LIKE operation, the parameter
     *            name may include the following: "%" - to indicate 0 or more
     *            characters in the pattern "_" - to indicate any single
     *            character in the pattern
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return number of Study records matching the given criteria
     * @throws MiddlewareQueryException
     */
    public long countStudyByName(String name, Operation op, Database instance) throws MiddlewareQueryException;


    /**
     * Returns the study records matching the given country
     * 
     * @param country
     *            - search string (pattern or exact match) for the country of the
     *            study
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE For LIKE operation, the parameter
     *            name may include the following: "%" - to indicate 0 or more
     *            characters in the pattern "_" - to indicate any single
     *            character in the pattern
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return List of Study POJOs
     * @throws MiddlewareQueryException
     */
    public List<Study> getStudyByCountry(String country, int start, int numOfRows, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the count of the study records of the given country
     * 
     * @param name
     *            - search string (pattern or exact match) for the country of the
     *            study
     * @param op
     *            - can be EQUAL like LIKE For LIKE operation, the parameter
     *            name may include the following: "%" - to indicate 0 or more
     *            characters in the pattern "_" - to indicate any single
     *            character in the pattern
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return number of Study records matching the given criteria
     * @throws MiddlewareQueryException
     */
    public long countStudyByCountry(String name, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the study records matching the given sdate
     * 
     * @param sdate
     *            - search string (exact match) for the sdate of the
     *            study
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return List of Study POJOs
     * @throws MiddlewareQueryException
     */
    public List<Study> getStudyBySDate(Integer sdate, int start, int numOfRows, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the study records matching the given sdate
     * 
     * @param sdate
     *            - search string (exact match) for the sdate of the
     *            study
     * @param op
     *            - can be EQUAL
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return number of Study records matching the given criteria
     * @throws MiddlewareQueryException
     */
    public long countStudyBySDate(Integer sdate, Operation op, Database instance) throws MiddlewareQueryException;
    
    /**
     * Returns the study records matching the given sdate
     * 
     * @param sdate
     *            - search string (exact match) for the sdate of the
     *            study
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return List of Study POJOs
     * @throws MiddlewareQueryException
     */
    public List<Study> getStudyByEDate(Integer edate, int start, int numOfRows, Operation op, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the study records matching the given edate
     * 
     * @param edate
     *            - search string (exact match) for the edate of the
     *            study
     * @param op
     *            - can be EQUAL
     * @param instance
     *            - can be CENTRAL or LOCAL
     * @return number of Study records matching the given criteria
     * @throws MiddlewareQueryException
     */
    public long countStudyByEDate(Integer edate, Operation op, Database instance) throws MiddlewareQueryException;
    
    /**
     * Retrieves a Study record of the given id
     * 
     * @param id
     * @return A Study POJO
     * @throws MiddlewareQueryException
     */
    public Study getStudyByID(Integer id) throws MiddlewareQueryException;

    /**
     * Returns a List of {@code Study} objects that are top-level studies, or
     * studies that do not have parent folders.
     * 
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param instance
     *            - can be Database.LOCAL or Database.CENTRAL
     * @return The list of all the top-level studies
     * @throws MiddlewareQueryException
     */
    public List<Study> getAllTopLevelStudies(int start, int numOfRows, Database instance) throws MiddlewareQueryException;

    /**
     * Returns the total number of top level studies.
     *
     * @return the int
     */
    public long countAllTopLevelStudies(Database instance) throws MiddlewareQueryException;
    
    /**
     * Returns the total number of studies belong to a study parent folder.
     *
     * @return the int
     */
    
    public long countAllStudyByParentFolderID(Integer parentFolderId,Database instance) throws MiddlewareQueryException;
    
    
    /**
     * Returns a List of {@code Study} objects that belong to the specified
     * Parent Folder ID.
     * 
     * @param parentFolderId
     *            - the parent folder's studyid
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return The list of all the studies belonging to the specified parent
     *         folder. Returns an empty list if there are no connections
     *         detected for both local and central instances.
     * @throws MiddlewareQueryException
     */
    public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns all Factor records which belong to the Study identified by the
     * given id.
     * 
     * @param studyId
     *            - id of the Study
     * @return List of Factor POJOs
     */
    public List<Factor> getFactorsByStudyID(Integer studyId) throws MiddlewareQueryException;

    /**
     * Returns all Variate records which belong to the Study identified by the
     * given id.
     * 
     * @param studyId
     *            - id of the Study
     * @return List of Variate POJOs
     */
    public List<Variate> getVariatesByStudyID(Integer studyId) throws MiddlewareQueryException;

    /**
     * Returns all the Effect records which belong to the Study identified by
     * the given id.
     * 
     * @param studyId
     *            - id of the Study
     * @return List of StudyEffect POJOs
     */
    public List<StudyEffect> getEffectsByStudyID(Integer studyId) throws MiddlewareQueryException;

    /**
     * Returns all the Representation records with the given effectId.
     * 
     * @param effectId
     * @return List of Representation POJOs
     */
    public List<Representation> getRepresentationByEffectID(Integer effectId) throws MiddlewareQueryException;

    /**
     * Returns all the Representation records with the given studyId.
     * 
     * @param studyId
     * @return List of Representation POJOs
     */
    public List<Representation> getRepresentationByStudyID(Integer studyId) throws MiddlewareQueryException;

    /**
     * Returns a List of {@code Factor} objects that belong to the specified
     * Representation ID.
     * 
     * @param representationId
     *            - the ID of the Representation
     * @return The list of all the factors belonging to the specified
     *         Representation. Returns an empty list if there are no connections
     *         detected for both local and central instances.
     * @throws MiddlewareQueryException
     */
    public List<Factor> getFactorsByRepresentationId(Integer representationId) throws MiddlewareQueryException;

    /**
     * Returns the number of OunitIDs that are associated to the specified
     * Representation ID.
     * 
     * @param representationId
     *            - the ID of the Representation
     * @return The number of all the OunitIDs associated to the specified
     *         Representation. Returns 0 if there are no connections detected
     *         for both local and central instances.
     * @throws MiddlewareQueryException
     */
    public long countOunitIDsByRepresentationId(Integer representationId) throws MiddlewareQueryException;

    /**
     * Returns a List of OunitIDs that are associated to the specified
     * Representation ID.
     * 
     * @param representationId
     *            - the ID of the Representation
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return The list of all the OunitIDs associated to the specified
     *         Representation. Returns an empty list if there are no connections
     *         detected for both local and central instances.
     * @throws MiddlewareQueryException
     */
    public List<Integer> getOunitIDsByRepresentationId(Integer representationId, int start, int numOfRows) throws MiddlewareQueryException;

    /**
     * Returns a List of {@code Variate} objects that belong to the specified
     * Representation ID.
     * 
     * @param representationId
     *            - the ID of the Representation
     * @return The list of all the variates belonging to the specified
     *         Representation. Returns an empty list if there are no connections
     *         detected for both local and central instances.
     * @throws MiddlewareQueryException
     */
    public List<Variate> getVariatesByRepresentationId(Integer representationId) throws MiddlewareQueryException;

    /**
     * Returns a list of NumericDataElements that represents the column values
     * for each specified ounitID (for each specified row).
     * 
     * @param ounitIdList
     *            - list of ounitIDs to get the corresponding column values. IDs
     *            in the list must be all from the Central DB or all from the
     *            Local DB.
     * @return The list of column values / NumericDataElements for the specified
     *         ounitIDs
     * @throws MiddlewareQueryException
     */
    public List<NumericDataElement> getNumericDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException;

    /**
     * Returns a list of CharacterDataElements that represents the column values
     * for each specified ounitID (for each specified row).
     * 
     * @param ounitIdList
     *            - list of ounitIDs to get the corresponding column values. IDs
     *            in the list must be all from the Central DB or all from the
     *            Local DB.
     * @return The list of column values / CharacterDataElements for the
     *         specified ounitIDs
     * @throws MiddlewareQueryException
     */
    public List<CharacterDataElement> getCharacterDataValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException;

    /**
     * Returns a list of NumericLevelElements that represents the column values
     * for each specified ounitID (for each specified row).
     * 
     * @param ounitIdList
     *            - list of ounitIDs to get the corresponding column values. IDs
     *            in the list must be all from the Central DB or all from the
     *            Local DB.
     * @return The list of column values / NumericLevelElements for the
     *         specified ounitIDs
     * @throws MiddlewareQueryException
     */
    public List<NumericLevelElement> getNumericLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException;

    /**
     * Returns a list of CharacterLevelElements that represents the column
     * values for each specified ounitID (for each specified row).
     * 
     * @param ounitIdList
     *            - list of ounitIDs to get the corresponding column values. IDs
     *            in the list must be all from the Central DB or all from the
     *            Local DB.
     * @return The list of column values / CharacterLevelElements for the
     *         specified ounitIDs
     * @throws MiddlewareQueryException
     */
    public List<CharacterLevelElement> getCharacterLevelValuesByOunitIdList(List<Integer> ounitIdList) throws MiddlewareQueryException;

    /**
     * Returns a list of DatasetCondition objects representing the Factors 
     * which have constant values in the dataset identified by the given 
     * representation ID.
     * 
     * @param representationId
     * @return
     * @throws MiddlewareQueryException
     */
    public List<DatasetCondition> getConditionsByRepresentationId(Integer representationId) throws MiddlewareQueryException;
    
    /**
     * Returns the main label given a value of the factorid column of a factor record.
     * 
     * @param factorid
     * @return
     * @throws MiddlewareQueryException
     */
    public String getMainLabelOfFactorByFactorId(Integer factorid) throws MiddlewareQueryException;
    
    /**
     * Returns the number of studies where the Germplasm, identified
     * by the given gid, is involved with. 
     * 
     * @param gid
     * @return
     * @throws MiddlewareQueryException
     */
    public long countStudyInformationByGID(Long gid) throws MiddlewareQueryException;
    
    /**
     * Returns a list of StudyInfo objects for the studies where the Germplasm, 
     * identified by the given gid, is involved with.
     *  
     * @param gid
     * @return
     * @throws MiddlewareQueryException
     */
    public List<StudyInfo> getStudyInformationByGID(Long gid) throws MiddlewareQueryException;
}
