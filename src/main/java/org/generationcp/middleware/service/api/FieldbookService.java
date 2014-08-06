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

package org.generationcp.middleware.service.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.*;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.UserDefinedField;

// TODO: Auto-generated Javadoc
/**
 * This is the API for Fieldbook requirements.
 * 
 */
public interface FieldbookService {

    /**
     * Retrieves all the study details of the nurseries stored in local database.
     *
     * @return all local nursery details
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException;
    
    /**
     * Retrieves all the details of the trial studies stored in local database.
     *
     * @return  all local trial study details
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllLocalTrialStudyDetails() throws MiddlewareQueryException;
    
    /**
     * Gets the field map info (entries, reps, plots and counts) of the given trial.
     *
     * @param trialIdList the trial id list
     * @return the FieldMapCount object containing the counts
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList) throws MiddlewareQueryException;
    
    /**
     * Gets the field map info (entries, reps, plots and counts) of the given nursery.
     *
     * @param nurseryIdList the nursery id list
     * @return the FieldMapCount object containing the counts
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList) throws MiddlewareQueryException;
    
    
    /**
     * Retrieves all locations from central and local databases.
     *
     * @return List of location references
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllLocations()throws MiddlewareQueryException;
    
    /**
     * Gets the all breeding locations.
     *
     * @return the all breeding locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllBreedingLocations() throws MiddlewareQueryException;

    /**
     * Gets the all seed locations.
     *
     * @return the all seed locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllSeedLocations() throws MiddlewareQueryException;


    /**
     * Gets the all breeding methods.
     *
     * @param filterOutGenerative the filter out generative
     * @return All breeding methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllBreedingMethods(boolean filterOutGenerative) throws MiddlewareQueryException;

    /**
     * Gets the favorite breeding methods.
     *
     * @param methodIds the method ids
     * @param filterOutGenerative the filter out generative
     * @return the favorite breeding methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getFavoriteBreedingMethods(List<Integer> methodIds, boolean filterOutGenerative)  throws MiddlewareQueryException;
        
    
    /**
     * Save or update Field Map Properties like row, column, block, total rows, total columns, planting order.
     *
     * @param info the info
     * @param userId the user id
     * @param isNew the is new
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) throws MiddlewareQueryException;
    
    
    /**
     * Retrieve all field map labels in the block of the specified trial instance id.
     *
     * @param datasetId the dataset id
     * @param geolocationId the geolocation id
     * @return all field maps in block by trial instance id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId) throws MiddlewareQueryException;

    /**
     * Gets the dataset references.
     *
     * @param studyId the study id of the datasets
     * @return the dataset references belonging to the given study id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;
    
    /**
     * Gets the favorite location by project id.
     *
     * @param locationIds the location ids
     * @return the favorite locations based on the given project id
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getFavoriteLocationByProjectId(List<Long> locationIds)  throws MiddlewareQueryException;
    
    /**
     * Gets the study.
     *
     * @param studyId the study id
     * @return the Study corresponding to the given study id
     * @throws MiddlewareQueryException the middleware query exception
     */
    Study getStudy(int studyId) throws MiddlewareQueryException;

    /**
     * Returns the variable id given the property, scale, method, and role (P-S-M-R).
     *
     * @param property the property
     * @param scale the scale
     * @param method the method
     * @param role the role
     * @return the standard variable id by property scale method role
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role)
    throws MiddlewareQueryException;
    
    /**
     * Gets the next germplasm id.
     *
     * @return the next germplasm id
     * @throws MiddlewareQueryException the middleware query exception
     */
    int getNextGermplasmId() throws MiddlewareQueryException;
    
    /**
     * Gets the germplasm id by name.
     *
     * @param name the name
     * @return the germplasm id by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer getGermplasmIdByName(String name) throws MiddlewareQueryException;
    
    /**
     * Gets the data set.
     *
     * @param id the id
     * @return the data set
     * @throws MiddlewareQueryException the middleware query exception
     */
    Workbook getNurseryDataSet(int id) throws MiddlewareQueryException;

    /**
     * Gets the data set.
     *
     * @param id the id
     * @return the data set
     * @throws MiddlewareQueryException the middleware query exception
     */
    Workbook getTrialDataSet(int id) throws MiddlewareQueryException;

    /**
     * Saves the measurement rows of a workbook as a local trial or nursery on the new CHADO schema.
     *
     * @param workbook that contains the measurement rows to save
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException;

    /**
     * Saves germplasm list advanced nursery types. This method saves the germplasms (and corresponding name) if not found in the database. 
     * ListData items are always added to the database, before saving the germplasm list.
     * 
     * Old Fieldbook Implementation:
     * 
     * call Save Listnms;
     * For each entry in the advance list table
     * if (gid != null) 
     *   germplasm = findByGid(gid)
     *   if (germplasm == null)
     *      germplasm = findByName(table.desig)
     *      
     *  if (germplasm != null) 
     *      call Save ListData using gid from germplasm.gid
     *  else 
     *      call Save Germplasm - note new gid generated
     *  call Save Names using NType = 1027, NVal = table.desig, NStat = 0
     *  call Save Names using NType = 1028, NVal = table.germplasmBCID, NStat = 1
     *  call Save Names using NType = 1029, NVal = table.cross, NStat = 0
     *  call Save ListData
     *
     * @param germplasms the germplasms to add - the key of the Map is the germplasm to add, 
     *                                      while the value is its corresponding name values
     * @param listDataItems the list data to add - the key of the Map is the germplasm 
     *                                      associated to the germplasm list data value
     * @param germplasmList the germplasm list to add
     * 
     * @return The id of the newly-created germplasm list
     * @throws MiddlewareQueryException the middleware query exception
     */
    Integer saveNurseryAdvanceGermplasmList(Map<Germplasm, List<Name>> germplasms
                , Map<Germplasm, GermplasmListData> listDataItems
                , GermplasmList germplasmList) 
            throws MiddlewareQueryException;
    
    /**
     * Used for retrieving the Cimmyt Wheat Germplasm name.
     *
     * @param gid the gid
     * @return the cimmyt wheat germplasm name by gid
     * @throws MiddlewareQueryException the middleware query exception
     */
    String getCimmytWheatGermplasmNameByGid(int gid) throws MiddlewareQueryException;

    /**
     * Used for retrieving the breeding method id given a method id.
     *
     * @param mid the mid
     * @return the breeding method by id
     * @throws MiddlewareQueryException the middleware query exception
     */
    Method getBreedingMethodById(int mid) throws MiddlewareQueryException;
    
    /**
     * Used for retrieving the germplasm given a germplasm id.
     *
     * @param gid the gid
     * @return the germplasm by gid
     * @throws MiddlewareQueryException the middleware query exception
     */
    Germplasm getGermplasmByGID(int gid) throws MiddlewareQueryException;
    
    /**
     * Get germplasm list by name.
     *
     * @param name the name
     * @return the germplasm list by name
     * @throws MiddlewareQueryException the middleware query exception
     */
    GermplasmList getGermplasmListByName(String name) throws MiddlewareQueryException;
    
    /**
     * Get All distinct values given a standard variable id.
     *
     * @param stdVarId the std var id
     * @return the distinct standard variable values
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<ValueReference> getDistinctStandardVariableValues(int stdVarId) throws MiddlewareQueryException;
    
    /**
     * Get all standard variables.
     *
     * @return the all standard variables
     * @throws MiddlewareQueryException the middleware query exception
     */
    Set<StandardVariable> getAllStandardVariables() throws MiddlewareQueryException;
    
    /**
     * Get all distinct values given the PSMR combination.
     *
     * @param property the property
     * @param scale the scale
     * @param method the method
     * @param role the role
     * @return the distinct standard variable values
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<ValueReference> getDistinctStandardVariableValues(String property, String scale, String method, PhenotypicType role) 
    		throws MiddlewareQueryException;
    
    /**
     * Get a standard variable given an id.
     *
     * @param id the id
     * @return the standard variable
     * @throws MiddlewareQueryException the middleware query exception
     */
    StandardVariable getStandardVariable(int id) throws MiddlewareQueryException;
    
    
    /**
     * Gets the all nursery types.
     *
     * @return the all nursery types
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<ValueReference> getAllNurseryTypes() throws MiddlewareQueryException;
    
    /**
     * Gets the all persons.
     *
     * @return all persons
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Person> getAllPersons() throws MiddlewareQueryException;
    
    
    /**
     * Returns all Persons from local sorted by first-middle-last 
     * followed by all persons from local sorted by first-middle-last.
     *
     * @return the all persons ordered by local central
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException;

    
    /**
     * Count plots with plants selectedof nursery.
     *
     * @param nurseryId the nursery id
     * @param variateIds the variate ids
     * @return the count
     * @throws MiddlewareQueryException the middleware query exception
     */
    int countPlotsWithRecordedVariatesInDataset(int nurseryId, List<Integer> variateIds) throws MiddlewareQueryException;
    
    /**
     * Filter standard variables by mode.
     *
     * @param storedInIds the stored in ids
     * @param propertyIds the property ids
     * @param isRemoveProperties the is remove properties
     * @return the list
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds, List<Integer> propertyIds, boolean isRemoveProperties) throws MiddlewareQueryException;
    
    /**
     * Gets the nursery variable settings.
     *
     * @param id the id
     * @param isNursery the is nursery
     * @return the nursery variable settings
     * @throws MiddlewareQueryException the middleware query exception
     */
    //Workbook getNurseryVariableSettings(int id) throws MiddlewareQueryException;
    Workbook getStudyVariableSettings(int id, boolean isNursery) throws MiddlewareQueryException;
    
    /**
     * Gets the germplasms.
     *
     * @param gids the gids
     * @return the germplasms
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Germplasm> getGermplasms(List<Integer> gids) throws MiddlewareQueryException;
    
    /**
     * Gets the all field locations.
     *
     * @param locationId the location id
     * @return the all field locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllFieldLocations(int locationId) throws MiddlewareQueryException;
    
    /**
     * Gets the all block locations.
     *
     * @param fieldId the field id
     * @return  all block locations
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllBlockLocations(int fieldId) throws MiddlewareQueryException;
       
    /**
     * Gets the block information.
     *
     * @param blockId the block id
     * @return the block information
     * @throws MiddlewareQueryException the middleware query exception
     */
    FieldmapBlockInfo getBlockInformation(int blockId) throws MiddlewareQueryException;
    
    /**
     * Gets the all fields.
     *
     * @return  all fields
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Location> getAllFields()throws MiddlewareQueryException;
    
    /**
     * Adds the field location.
     *
     * @param fieldName the field name
     * @param parentLocationId the parentlocation id
     * @param currentUserId the current user id
     * @return the id
     * @throws MiddlewareQueryException the middleware query exception
     */
    int addFieldLocation(String fieldName, Integer parentLocationId, Integer currentUserId)throws MiddlewareQueryException;
    
    /**
     * Adds the block location.
     *
     * @param blockName the block name
     * @param parentFieldId the parent field id
     * @param currentUserId the current user id
     * @return the id
     * @throws MiddlewareQueryException the middleware query exception
     */
    int addBlockLocation(String blockName, Integer parentFieldId, Integer currentUserId)throws MiddlewareQueryException;
    
    /**
     * Get all field maps in the same block.
     *
     * @param blockId the block id
     * @return the field maps in the given block
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId)
            throws MiddlewareQueryException;
    
    /**
     * Get all Treatment Levels.
     *
     * @return all treatment levels
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StandardVariableReference> getAllTreatmentLevels(List<Integer> hiddenFields) throws MiddlewareQueryException;
    
    /**
     * Fetch all the possible pairs of the treatment level variable.
     *
     * @param cvTermId the cv term id
     * @param propertyId the property id
     * @return list of all possible treatment pairs
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StandardVariable> getPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields) throws MiddlewareQueryException;

    /**
     * Returns the study type.
     *
     * @param studyId the study id
     * @return the study type
     * @throws MiddlewareQueryException the middleware query exception
     */
    TermId getStudyType(int studyId) throws MiddlewareQueryException;
    
    /**
     * Returns list of root or top-level folders from specified database.
     *
     * @param instance Can be CENTRAL or LOCAL
     * @return List of Folder POJOs or empty list if none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException;

    /**
     * Returns list of children of a folder given its ID. Retrieves from central
     * if the given ID is positive, otherwise retrieves from local.
     *
     * @param folderId The id of the folder to match
     * @return List of AbstractNode (FolderNode, StudyNode) POJOs or empty list
     * if none found
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException;
    /**
     * Check if the given id is an existing study.
     *
     * @param id the id
     * @return true, if is study
     * @throws MiddlewareQueryException the middleware query exception
     */
	boolean isStudy(int id) throws MiddlewareQueryException;

    /**
     * Get lOcation by id.
     *
     * @param id the id
     * @return the location by id
     * @throws MiddlewareQueryException the middleware query exception
     */
    Location getLocationById(int id) throws MiddlewareQueryException;
    
    /**
     * Get person by id.
     *
     * @param id the id
     * @return the person by id
     * @throws MiddlewareQueryException the middleware query exception
     */
    Person getPersonById(int id) throws MiddlewareQueryException;
    
	/**
	 * get the dataset id of the measurement dataset of the study.
	 *
	 * @param studyId the study id
	 * @param studyName the study name
	 * @return the measurement dataset id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int getMeasurementDatasetId(int studyId, String studyName) throws MiddlewareQueryException;
	
	/**
	 * count the number of observations.
	 *
	 * @param datasetId the dataset id
	 * @return the long
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countObservations(int datasetId) throws MiddlewareQueryException;
	
	/**
	 * Counts the number of stocks.
	 *
	 * @param datasetId the dataset id
	 * @return the long
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countStocks(int datasetId) throws MiddlewareQueryException;
	
	/**
	 * Determines if fieldmap exists.
	 *
	 * @param datasetId the dataset id
	 * @return true, if successful
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean hasFieldMap(int datasetId) throws MiddlewareQueryException;
	
	/**
	 * Gets the germplasm list by id.
	 *
	 * @param listId the list id
	 * @return the germplasm list by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	GermplasmList getGermplasmListById(Integer listId) throws MiddlewareQueryException;
	
	/**
	 * Gets the owner.
	 *
	 * @param userId the user id
	 * @return the owner
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getOwnerListName(Integer userId)  throws MiddlewareQueryException;
	
	/**
	 * Get study details.
	 *
	 * @param database the database
	 * @param studyType the study type
	 * @param studyId the study id
	 * @return the study details
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StudyDetails getStudyDetails(Database database, StudyType studyType, int studyId) throws MiddlewareQueryException;
	
	/**
	 * Get the block id of a particular trial instance in a dataset.
	 *
	 * @param datasetId the dataset id
	 * @param trialInstance the trial instance
	 * @return the block id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getBlockId(int datasetId, String trialInstance) throws MiddlewareQueryException;
	
	/**
	 * Gets the folder name by id.
	 *
	 * @param folderId the folder id
	 * @return the folder name by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	String getFolderNameById(Integer folderId) throws MiddlewareQueryException;
	
	/**
	 * Returns true if all instances in the study has fieldmap.
	 *
	 * @param studyId the study id
	 * @return true, if successful
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean checkIfStudyHasFieldmap(int studyId) throws MiddlewareQueryException;
	
	/**
	 * Builds the Trial Observations from the trial dataset id.
	 *
	 * @param trialDatasetId the trial dataset id
	 * @param factorList the factor list
	 * @param variateList the variate list
	 * @return the list
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList, List<MeasurementVariable> variateList)
			throws MiddlewareQueryException;
	
	/**
	 * Check if study has measurement data.
	 *
	 * @param datasetId the dataset id
	 * @param variateIds the variate ids
	 * @return true, if successful
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException;
	
	/**
	 * Count the number of variates with data.
	 *
	 * @param datasetId the dataset id
	 * @param variateIds the variate ids
	 * @return the int
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException;
	
	/**
	 * Delete observations of study.
	 *
	 * @param datasetId the dataset id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	void deleteObservationsOfStudy(int datasetId) throws MiddlewareQueryException;

	/**
	 * Get germplasms by name.
	 *
	 * @param name the name
	 * @return the germplasm ids by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Integer> getGermplasmIdsByName(String name) throws MiddlewareQueryException;
	
	/**
	 * Add Germplasm Name.
	 *
	 * @param nameValue the name value
	 * @param gid the gid
	 * @param userId the user id
	 * @param nameTypeId the name type id
	 * @param locationId the location id
	 * @param date the date
	 * @return the integer
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasmName(String nameValue, int gid, int userId, int nameTypeId,int locationId, Integer date) throws MiddlewareQueryException;
	
	/**
	 * Adds a new Germplasm.
	 *
	 * @param nameValue the name value
	 * @param userId the user id
	 * @return the integer
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasm(String nameValue, int userId) throws MiddlewareQueryException;
	
	/**
	 * Adds the germplasm.
	 *
	 * @param germplasm the germplasm
	 * @param name the name
	 * @return the integer
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer addGermplasm(Germplasm germplasm, Name name) throws MiddlewareQueryException;
	
	/**
	 * Get an id from the project table that matches the name (regardless if it's a study or a folder).
	 *
	 * @param name the name
	 * @return the project id by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Integer getProjectIdByName(String name) throws MiddlewareQueryException;
	
	/**
	 * Returns the stanadard variale given the PSMR combination.
	 *
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @param role the role
	 * @return the measurement variable by property scale method and role
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(String property, String scale, String method, PhenotypicType role) 
			throws MiddlewareQueryException;

    public void setTreatmentFactorValues(List<TreatmentVariable> treatmentFactors, int measurementDatasetID) throws MiddlewareQueryException;
	
	/**
	 * Return the measurement rows of a given dataset.
	 *
	 * @param datasetId the dataset id
	 * @param isTrial the is trial
	 * @return the complete dataset
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Workbook getCompleteDataset(int datasetId, boolean isTrial) throws MiddlewareQueryException;
	
	/**
	 * Gets the germplasm name types.
	 *
	 * @return the germplasm name types
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<UserDefinedField> getGermplasmNameTypes() throws MiddlewareQueryException;
	
	/**
	 * Returns a map of Gid, and list of Names.
	 *
	 * @param gids the gids
	 * @return the names by gids
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<Integer, List<Name>> getNamesByGids(List<Integer> gids) throws MiddlewareQueryException;

    /**
	 * Count germplasm list data by list id.
	 *
	 * @param listId the list id
	 * @return the int
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	int countGermplasmListDataByListId(Integer listId) throws MiddlewareQueryException;
	
	/**
	 * Gets the method by code.
	 *
	 * @param code the code
	 * @return the method by code
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByCode(String code) throws MiddlewareQueryException;
	
	/**
	 * Gets the method by id.
	 *
	 * @param id the id
	 * @return the method by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodById(int id) throws MiddlewareQueryException;
	
	/**
	 * Gets the method by name.
	 *
	 * @param name the name
	 * @return the method by name
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Method getMethodByName(String name) throws MiddlewareQueryException;

	/*
	 * Deletes a study (logical delete).
	 */
	void deleteStudy(int studyId) throws MiddlewareQueryException;
	 /**
     * Gets the favorite project location ids.
     *
     * @param projectId the project id
     * @return the favorite project location ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Long> getFavoriteProjectLocationIds() throws MiddlewareQueryException;
    
    /**
     * Gets the favorite project methods.
     *
     * @param projectId the project id
     * @return the favorite project methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Integer> getFavoriteProjectMethods() throws MiddlewareQueryException;
}
