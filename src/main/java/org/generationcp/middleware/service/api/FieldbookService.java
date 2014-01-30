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
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;

// TODO: Auto-generated Javadoc
/**
 * This is the API for Fieldbook requirements.
 * 
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
}
