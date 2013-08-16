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

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;


public interface CrossStudyDataManager{

    /**
     * Retrieves a list of all trial environments.
     * 
     * @return TrialEnvironments
     * @throws MiddlewareQueryException
     */
    TrialEnvironments getAllTrialEnvironments() throws MiddlewareQueryException;
    
    /**
     * Get all environment properties given a list of environments.
     * 
     * @param trialEnvtIds
     * @return a List of Environment Properties
     * @throws MiddlewareQueryException
     */
    List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> trialEnvtIds) throws MiddlewareQueryException;
    
    /**
     * Get all studies given a list of environments.
     * 
     * @param environmentIds
     * @return a list of Study References
     * @throws MiddlewareQueryException
     */
    List<StudyReference> getStudiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException;
    
}
