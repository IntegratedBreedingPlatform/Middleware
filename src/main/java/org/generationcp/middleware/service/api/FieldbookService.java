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

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

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

}
