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

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;

/**
 * This is the API for Fieldbook requirements.
 * 
 * 
 */
public interface FieldbookService {

    /**
     * Retrieves all the study details of the nurseries stored in local database.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException;
    
    /**
     * Retrieves all the details of the trial studies stored in local database.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    List<StudyDetails> getAllLocalTrialStudyDetails() throws MiddlewareQueryException;
    
    /**
     * Gets the field map info (entries, reps, plots and counts) of the given trial. 
     * 
     * @param trialId the id of the trial to retrieve the count from
     * @return the FieldMapCount object containing the counts
     */
    List<FieldMapInfo> getFieldMapInfoOfTrial(List<Integer> trialIdList) throws MiddlewareQueryException;
    
    /**
     * Gets the field map info (entries, reps, plots and counts) of the given nursery. 
     * 
     * @param nurseryId the id of the nursery to retrieve the count from
     * @return the FieldMapCount object containing the counts
     */
    List<FieldMapInfo> getFieldMapInfoOfNursery(List<Integer> nurseryIdList) throws MiddlewareQueryException;
    
    
    /**
     * Retrieves all locations from central and local databases.
     * 
     * @return List of location references
     * @throws MiddlewareQueryException
     */
    List<Location> getAllLocations()throws MiddlewareQueryException;
    

    /**
     * Save or update Field Map Properties like row, column, block, total rows, total columns, planting order.
     * 
     * @throws MiddlewareQueryException
     */
    void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, String fieldmapUUID) throws MiddlewareQueryException;
    
    
    /**
     * Retrieve all field map labels in the block of the specified trial instance id.
     * @param geolocationId
     * @throws MiddlewareQueryException
     */
    List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId) throws MiddlewareQueryException;

            
    
    //TODO remove this, this is just for testing
    int getGeolocationId(int projectId) throws MiddlewareQueryException;
    List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException;
    
    List<Location> getFavoriteLocationByProjectId(List<Long> locationIds)  throws MiddlewareQueryException;
    Study getStudy(int studyId) throws MiddlewareQueryException ;

}
