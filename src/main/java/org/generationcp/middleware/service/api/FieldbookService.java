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
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;

/**
 * This is the API for Fieldbook requirements.
 * 
 */
public interface FieldbookService {

    /**
     * Retrieves all the study details of the nurseries stored in local database.
     *
     * @return the all local nursery details
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException;
    
    /**
     * Retrieves all the details of the trial studies stored in local database.
     *
     * @return the all local trial study details
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
     * Save or update Field Map Properties like row, column, block, total rows, total columns, planting order.
     *
     * @param info the info
     * @param fieldmapUUID the fieldmap uuid
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, String fieldmapUUID) throws MiddlewareQueryException;
    
    
    /**
     * Retrieve all field map labels in the block of the specified trial instance id.
     *
     * @param datasetId the dataset id
     * @param geolocationId the geolocation id
     * @return the all field maps in block by trial instance id
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
     * Saves the measurement rows of a workbook as a local trial or nursery on the new CHADO schema.
     *
     * @param workbook that contains the measurement rows to save
     * @throws MiddlewareQueryException the middleware query exception
     */
    void saveMeasurementRows(Workbook workbook) throws MiddlewareQueryException;

    /**
     * Gets the all breeding methods.
     *
     * @return All breeding methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getAllBreedingMethods() throws MiddlewareQueryException;

    /**
     * Gets the favorite breeding methods.
     *
     * @param methodIds the method ids
     * @return the favorite breeding methods
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Method> getFavoriteBreedingMethods(List<Integer> methodIds)  throws MiddlewareQueryException;
        
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
     * @return the all persons
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<Person> getAllPersons() throws MiddlewareQueryException;
    
    int countPlotsWithPlantsSelectedofNursery(int nurseryId) throws MiddlewareQueryException;
}
