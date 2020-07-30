/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.service.api;

import com.google.common.base.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.exceptions.UnpermittedDeletionException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.*;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.CrossExpansionProperties;

import java.util.List;
import java.util.Map;

/**
 * This is the API for Fieldbook requirements.
 *
 */
public interface FieldbookService {

	/**
	 * Gets the field map info (entries, reps, plots and counts) of the given
	 * trial.
	 *
	 * @param trialIdList
	 *            the trial id list
	 *
	 * @return the FieldMapCount object containing the counts
	 */
	List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the field map info (entries, reps, plots and counts) of the given
	 * nursery.
	 *
	 * @param nurseryIdList
	 *            the nursery id list
	 * @return the FieldMapCount object containing the counts
	 */
	List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList, CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the all breeding locations.
	 *
	 * @return the all breeding locations
	 */
	List<Location> getAllBreedingLocations();

	/**
	 * Gets all breeding type locations (crop specific and program specific locations).
	 *
	 * @return the all breeding locations
	 */
	List<Location> getAllBreedingLocationsByProgramUUID(String programUUID);

	/**
	 * Gets the all seed locations.
	 *
	 * @return the all seed locations
	 */
	List<Location> getAllSeedLocations();

	/**
	 * Gets the all breeding methods.
	 *
	 * @param filterOutGenerative
	 *            the filter out generative
	 * @return All breeding methods
	 */
	List<Method> getAllBreedingMethods(boolean filterOutGenerative);

	/**
	 * Gets the breeding methods of generative type.
	 *
	 * @param programUUID
	 *            the filter out generative
	 * @return All breeding methods
	 */
	List<Method> getAllGenerativeMethods(final String programUUID);

	/**
	 * Gets the favorite breeding methods.
	 *
	 * @param methodIds
	 *            the method ids
	 * @param filterOutGenerative
	 *            the filter out generative
	 * @return the favorite breeding methods
	 */
	List<Method> getFavoriteBreedingMethods(List<Integer> methodIds, boolean filterOutGenerative);

	/**
	 * Save or update Field Map Properties like row, column, block, total rows,
	 * total columns, planting order.
	 *
	 * @param info
	 *            the info
	 * @param userId
	 *            the user id
	 * @param isNew
	 *            the is new
	 */
	void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew);

	/**
	 * Retrieve all field map labels in the block of the specified trial
	 * instance id.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @param geolocationId
	 *            the geolocation id
	 * @return all field maps in block by trial instance id
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId,
			CrossExpansionProperties crossExpansionProperties);

	/**
	 * Gets the dataset references.
	 *
	 * @param studyId
	 *            the study id of the datasets
	 * @return the dataset references belonging to the given study id
	 */
	List<DatasetReference> getDatasetReferences(int studyId);

	/**
	 * Gets the favorite location by project id.
	 *
	 * @param locationIds
	 *            the location ids
	 * @param filtered
	 * @return the favorite locations based on the given project id
	 */
	List<Location> getFavoriteLocationByLocationIDs(List<Integer> locationIds, Boolean filtered);

	/**
	 * Gets the study.
	 *
	 * @param studyId
	 *            the study id
	 * @return the Study corresponding to the given study id
	 */
	Study getStudy(int studyId);

	/**
	 * Returns the variable id given the property, scale, method, and role
	 * (P-S-M-R).
	 *
	 * @param property
	 *            the property
	 * @param scale
	 *            the scale
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @return the standard variable id by property scale method role
	 */
	Integer getStandardVariableIdByPropertyScaleMethodRole(String property, String scale, String method, PhenotypicType role);

	/**
	 * Given a workbook already loaded which does not load observations now - this is a helper method to trigger
	 * loading the observations collection IF AND WHEN NEEDED. This method is a
	 * stop gap mecahnism to lazy load the observations collection until we can
	 * gradually refactor all code so that entire set of observations (plots)
	 * data is not required to be loaded in session. This method should only be
	 * invoked at a point in process where entire observations (plots)
	 * collection with measurements is required due to the way rest of the
	 * process code is written. For large Nurseries and trials this method is
	 * not yet performance tuned. Memory footprint of the overall application
	 * can be severly impacted if this method is used without consideration for
	 * performance at scale. So please be very careful and think it through
	 * before using this method.
	 */
	boolean loadAllObservations(final Workbook workbook);

	/**
	 * Saves germplasm list advanced nursery types. This method saves the
	 * germplasms (and corresponding name) if not found in the database.
	 * ListData items are always added to the database, before saving the
	 * germplasm list.
	 *
	 * Old Fieldbook Implementation:
	 *
	 * call Save Listnms; For each entry in the advance list table if (gid !=
	 * null) germplasm = findByGid(gid) if (germplasm == null) germplasm =
	 * findByName(table.desig)
	 *
	 * if (germplasm != null) call Save ListData using gid from germplasm.gid
	 * else call Save Germplasm - note new gid generated call Save Names using
	 * NType = 1027, NVal = table.desig, NStat = 0 call Save Names using NType =
	 * 1028, NVal = table.germplasmBCID, NStat = 1 call Save Names using NType =
	 * 1029, NVal = table.cross, NStat = 0 call Save ListData
	 *
	 * @param germplasms
	 *            the germplasms to add - the key of the Map is the germplasm to
	 *            add, while the value is its corresponding name values
	 * @param listDataItems
	 *            the list data to add - the key of the Map is the germplasm
	 *            associated to the germplasm list data value
	 * @param germplasmList
	 *            the germplasm list to add
	 *
	 * @return The id of the newly-created germplasm list
	 */

	Integer saveNurseryAdvanceGermplasmList(List<Pair<Germplasm, List<Name>>> germplasms,
			List<Pair<Germplasm, GermplasmListData>> listDataItems, GermplasmList germplasmList,
			List<Pair<Germplasm, List<Attribute>>> germplasmAttributes);

	/**
	 * Used for retrieving the breeding method id given a method id.
	 *
	 * @param mid
	 *            the mid
	 * @return the breeding method by id
	 */
	Method getBreedingMethodById(int mid);

	/**
	 * Used for retrieving the germplasm given a germplasm id.
	 *
	 * @param gid
	 *            the gid
	 * @return the germplasm by gid
	 */
	Germplasm getGermplasmByGID(int gid);

	/**
	 * Get germplasm list by name.
	 *
	 * @param name
	 *            the name
	 * @param programUUID
	 *            unique id of the program
	 * @return the germplasm list by name
	 */
	GermplasmList getGermplasmListByName(String name, String programUUID);

	/**
	 * Get a standard variable given an id. After the first read, the variable
	 * is cached in memory.
	 *
	 * @param id
	 *            the id
	 * @param programUUID
	 *            unique id of the program
	 * @return the standard variable
	 */
	StandardVariable getStandardVariable(int id, String programUUID);

	/**
	 * Count plots with plants selectedof nursery.
	 *
	 * @param nurseryId
	 *            the nursery id
	 * @param variateIds
	 *            the variate ids
	 * @return the count
	 */
	int countPlotsWithRecordedVariatesInDataset(int nurseryId, List<Integer> variateIds);

	/**
	 * Filter standard variables by mode.
	 *
	 * @param storedInIds
	 *            the stored in ids
	 * @param propertyIds
	 *            the property ids
	 * @param isRemoveProperties
	 *            the is remove properties
	 * @return the list
	 */
	List<StandardVariableReference> filterStandardVariablesByMode(List<Integer> storedInIds, List<Integer> propertyIds,
			boolean isRemoveProperties);

	/**
	 * Gets the nursery variable settings.
	 *
	 * @param id
	 *            the id
	 * @return the nursery variable settings
	 */

	Workbook getStudyVariableSettings(int id);
	/**
	 * Gets the germplasms.
	 *
	 * @param gids
	 *            the gids
	 * @return the germplasms
	 */
	List<Germplasm> getGermplasms(List<Integer> gids);

	/**
	 * Gets the all field locations.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all field locations
	 */
	List<Location> getAllFieldLocations(int locationId);

	/**
	 * Gets the all block locations.
	 *
	 * @param fieldId
	 *            the field id
	 * @return all block locations
	 */
	List<Location> getAllBlockLocations(int fieldId);

	/**
	 * Gets the block information.
	 *
	 * @param blockId
	 *            the block id
	 * @return the block information
	 */
	FieldmapBlockInfo getBlockInformation(int blockId);

	/**
	 * Gets the all fields.
	 *
	 * @return all fields
	 */
	List<Location> getAllFields();

	/**
	 * Adds the field location.
	 *
	 * @param fieldName
	 *            the field name
	 * @param parentLocationId
	 *            the parentlocation id
	 * @param currentUserId
	 *            the current user id
	 * @return the id
	 */
	int addFieldLocation(String fieldName, Integer parentLocationId, Integer currentUserId);

	/**
	 * Adds the block location.
	 *
	 * @param blockName
	 *            the block name
	 * @param parentFieldId
	 *            the parent field id
	 * @param currentUserId
	 *            the current user id
	 * @return the id
	 */
	int addBlockLocation(String blockName, Integer parentFieldId, Integer currentUserId);

	/**
	 * Get all field maps in the same block.
	 *
	 * @param blockId
	 *            the block id
	 * @return the field maps in the given block
	 */
	List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId);

	/**
	 * Fetch all the possible pairs of the treatment level variable.
	 *
	 * @param cvTermId
	 *            the cv term id
	 * @param propertyId
	 *            the property id
	 * @return list of all possible treatment pairs
	 */
	List<StandardVariable> getPossibleTreatmentPairs(int cvTermId, int propertyId, List<Integer> hiddenFields);

	/**
	 * Get lOcation by id.
	 *
	 * @param id
	 *            the id
	 * @return the location by id
	 */
	Location getLocationById(int id);

	/**
	 * get the dataset id of the measurement dataset of the study.
	 *
	 * @param studyId
	 *            the study id
	 * @return the measurement dataset id
	 */
	int getMeasurementDatasetId(int studyId);

	/**
	 * count the number of observations.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @return the long
	 */
	long countObservations(int datasetId);

	/**
	 * Counts the number of stocks.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @return the long
	 */
	long countStocks(int datasetId);

	/**
	 * Determines if fieldmap exists.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @return true, if successful
	 */
	boolean hasFieldMap(int datasetId);

	/**
	 * Gets the germplasm list by id.
	 *
	 * @param listId
	 *            the list id
	 * @return the germplasm list by id
	 */
	GermplasmList getGermplasmListById(Integer listId);

	/**
	 * Gets the owner.
	 *
	 * @param userId
	 *            the user id
	 * @return the owner
	 */
	String getOwnerListName(Integer userId);

	/**
	 * Get study details.
	 *
	 * @param studyId
	 *            the study id
	 * @return the study details
	 */
	StudyDetails getStudyDetails(int studyId);

	/**
	 * Get the block id of a particular trial instance in a dataset.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @param trialInstance
	 *            the trial instance
	 * @return the block id
	 */
	String getBlockId(int datasetId, Integer trialInstance);

	/**
	 * Gets the folder name by id.
	 *
	 * @param folderId
	 *            the folder id
	 * @return the folder name by id
	 */
	String getFolderNameById(Integer folderId);

	/**
	 * Check if study has measurement data.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @param variateIds
	 *            the variate ids
	 * @return true, if successful
	 */
	boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds);

	/**
	 * Count the number of variates with data.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @param variateIds
	 *            the variate ids
	 * @return the int
	 */
	int countVariatesWithData(int datasetId, List<Integer> variateIds);

	/**
	 * Get germplasms by name.
	 *
	 * @param name
	 *            the name
	 * @return the germplasm ids by name
	 */
	List<Integer> getGermplasmIdsByName(String name);

	/**
	 * Add Germplasm Name.
	 *
	 * @param nameValue
	 *            the name value
	 * @param gid
	 *            the gid
	 * @param userId
	 *            the user id
	 * @param nameTypeId
	 *            the name type id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the integer
	 */
	Integer addGermplasmName(String nameValue, int gid, int userId, int nameTypeId, int locationId, Integer date);

	/**
	 * Adds a new Germplasm.
	 *
	 * @param nameValue
	 *            the name value
	 * @param userId
	 *            the user id
	 * @return the integer
	 */
	Integer addGermplasm(String nameValue, int userId);

	/**
	 * Adds the germplasm.
	 *
	 * @param germplasm
	 *            the germplasm
	 * @param name
	 *            the name
	 * @return the integer
	 */
	Integer addGermplasm(Germplasm germplasm, Name name);

	/**
	 *
	 * @param germplasmTriples
	 * @return
	 */
	List<Integer> addGermplasm(List<Triple<Germplasm, Name, List<Progenitor>>> germplasmTriples);

	/**
	 * Get an id from the project table that matches the name (regardless if
	 * it's a study or a folder).
	 *
	 * @param name
	 *            the name
	 * @param programUUID
	 *            the program UUID
	 * @return the project id by name
	 */
	Integer getProjectIdByNameAndProgramUUID(String name, String programUUID);

	/**
	 * Returns the stanadard variale given the PSMR combination.
	 *
	 * @param property
	 *            the property
	 * @param scale
	 *            the scale
	 * @param method
	 *            the method
	 * @param role
	 *            the role
	 * @return the measurement variable by property scale method and role
	 */
	MeasurementVariable getMeasurementVariableByPropertyScaleMethodAndRole(String property, String scale, String method,
			PhenotypicType role, String programUUID);

	void setTreatmentFactorValues(List<TreatmentVariable> treatmentFactors, int measurementDatasetID);

	/**
	 * Return the measurement rows of a given dataset.
	 *
	 * @param datasetId
	 *            the dataset id
	 * @return the complete dataset
	 */
	Workbook getCompleteDataset(int datasetId);

	/**
	 * Gets the germplasm name types.
	 *
	 * @return the germplasm name types
	 */
	List<UserDefinedField> getGermplasmNameTypes();

	/**
	 * Returns a map of Gid, and list of Names.
	 *
	 * @param gids
	 *            the gids
	 * @return the names by gids
	 */
	Map<Integer, List<Name>> getNamesByGids(List<Integer> gids);

	/**
	 * Count germplasm list data by list id.
	 *
	 * @param listId
	 *            the list id
	 * @return the int
	 */
	int countGermplasmListDataByListId(Integer listId);

	/**
	 * Gets the method by code.
	 *
	 * @param code
	 *            the code
	 * @param programUUID
	 *            unique id of the program
	 * @return the method by code
	 */
	Method getMethodByCode(String code, String programUUID);

	/**
	 * Gets the method by id.
	 *
	 * @param id
	 *            the id
	 * @return the method by id
	 */
	Method getMethodById(int id);

	/**
	 * Gets the method by name.
	 *
	 * @param name
	 *            the name
	 * @return the method by name
	 */
	Method getMethodByName(String name);

	/*
	 * Deletes a study (logical delete).
	 */
	void deleteStudy(int studyId, Integer currentUserId) throws UnpermittedDeletionException;

	/**
	 * Gets the favorite project location ids.
	 *
	 * @param programUUID
	 *            - unique id of program
	 * @return the favorite project location ids
	 */
	List<Integer> getFavoriteProjectLocationIds(String programUUID);

	/**
	 * Gets the favorite project methods.
	 *
	 * @param programUUID
	 *            - unique id of program
	 * @return the favorite project methods
	 */
	List<Integer> getFavoriteProjectMethods(String programUUID);

	/**
	 * Returns germplasm lists by project id.
	 *
	 * @param projectId
	 * @param type
	 *            list type
	 * @return List of GermplasmList objects under the given project id and type
	 */
	List<GermplasmList> getGermplasmListsByProjectId(int projectId, GermplasmListType type);

	/**
	 * Saves germplasm list crosses types. ListData items are always added to
	 * the database, before saving the germplasm list.
	 *
	 * @param listDataItems
	 *            the list data to add - the key of the Map is the germplasm
	 *            associated to the germplasm list data value
	 * @param germplasmList
	 *            the germplasm list to add
	 * @param isApplyNewGroupToPreviousCrosses
	 *
	 * @return The id of the newly-created germplasm list
	 */
	Integer saveGermplasmList(List<Pair<Germplasm, GermplasmListData>> listDataItems, GermplasmList germplasmList,
			boolean isApplyNewGroupToPreviousCrosses);

	void saveStudyColumnOrdering(Integer studyId, List<Integer> orderedTermIds);

	boolean setOrderVariableByRank(Workbook workbook);

	/**
	 * Gets the StandardVariable by Name
	 *
	 * @param name
	 *            of the Standard Varible
	 * @param programUUID
	 *            unique id of the program
	 **/
	StandardVariable getStandardVariableByName(String name, String programUUID);

	List<StandardVariableReference> filterStandardVariablesByIsAIds(List<StandardVariableReference> standardReferences,
			List<Integer> isAIds);

	Location getLocationByName(String locationName, Operation op);

	/**
	 * Updates germplasm list crosses types. ListData items are always updated
	 * in the database, before saving the germplasm list.
	 *
	 * @param listDataItems
	 *            the list data to add - the key of the Map is the germplasm
	 *            associated to the germplasm list data value
	 * @param germplasmList
	 *            the germplasm list to add
	 *
	 * @return The id of the newly-created germplasm list
	 */
	Integer updateGermplasmList(List<Pair<Germplasm, GermplasmListData>> listDataItems, GermplasmList germplasmList);

	List<Location> getFavoriteLocationByLocationIDs(List<Integer> locationIds);

	List<Method> getFavoriteMethods(List<Integer> methodIds, Boolean filterOutGenerative);

	List<Location> getLocationsByProgramUUID(String programUUID);

	/**
	 *
	 * @param filterOutGenerative
	 * @return all no-bulking methods filtering by type = 'GEN' when filterOutGenerative is true
	 */
	List<Method> getAllNoBulkingMethods(boolean filterOutGenerative);

	/**
	 * Gets the favorite project methods.
	 *
	 * @param programUUID
	 *            - unique id of program
	 * @return the no bulking favorite project methods
	 */
	List<Method> getFavoriteProjectNoBulkingMethods(final String programUUID);

	/**
	 *
	 * @param programUUID
	 * @return All generative and no bulking method
	 */
	List<Method> getAllGenerativeNoBulkingMethods(final String programUUID);

	Workbook getStudyDataSet(int studyID);

	Workbook getStudyByNameAndProgramUUID(String studyName, String programUUID);

	Optional<StudyReference> getStudyReferenceByNameAndProgramUUID(String studyName, String programUUID);

	/**
	 * Save experimental Design
	 *
	 * @param workbook
	 * @param programUUID the program UUID
	 * @param crop
	 */
	void saveExperimentalDesign(final Workbook workbook, final String programUUID, final CropType crop);


	/**
	 * Save workbook variables and Observations
	 *
	 * @param workbook
	 */
	void saveWorkbookVariablesAndObservations(final Workbook workbook);

}
